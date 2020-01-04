/*
 * Copyright 2019 Aleksei Balan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package info.ab.telegram;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.telegram.api.TLConfig;
import org.telegram.api.TLDcOption;
import org.telegram.api.dialog.TLDialog;
import org.telegram.api.functions.help.TLRequestHelpGetConfig;
import org.telegram.api.functions.messages.TLRequestMessagesGetDialogs;
import org.telegram.api.functions.messages.TLRequestMessagesGetPinnedDialogs;
import org.telegram.api.input.peer.TLInputPeerSelf;
import org.telegram.api.message.TLMessage;
import org.telegram.api.messages.TLMessagesPeerDialogs;
import org.telegram.api.messages.dialogs.TLAbsDialogs;
import org.telegram.bot.kernel.KernelComm;
import org.telegram.bot.kernel.TelegramBot;
import org.telegram.bot.kernel.engine.MemoryApiState;
import org.telegram.bot.structure.BotConfig;
import org.telegram.bot.structure.IUser;
import org.telegram.bot.structure.LoginStatus;
import org.telegram.tl.TLVector;

import javax.annotation.PostConstruct;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class TelegramService {

  public static final LegacyLogger LEGACY_LOGGER = new LegacyLogger();

  @Value("${telegram.api.id}")
  private Integer apiKey;
  @Value("${telegram.api.hash}")
  private String apiHash;

  @Value("${telegram.phoneNumber}")
  private String phoneNumber;
  @Value("${telegram.peerId:#{null}}")
  private Integer peerId;

  @Value("${telegram.dc.id}")
  private Integer dcId;
  @Value("${telegram.dc.host}")
  private String dcHost;
  @Value("${telegram.dc.port}")
  private Integer dcPort;

  private TelegramBot service;
  private final MemoryDatabase db = new MemoryDatabase();

  @SneakyThrows
  @PostConstruct
  public void postConstruct() {
    org.telegram.mtproto.log.Logger.registerInterface(LEGACY_LOGGER);
    org.telegram.api.engine.Logger.registerInterface(LEGACY_LOGGER);

    BotConfig authConfig = new AuthConfig(phoneNumber);
    MemoryApiState apiState = new MemoryApiState(authConfig.getAuthfile());
    //apiState.getObj().getDcInfos().clear();
    //apiState.getObj().getDcInfos().add(new TLDcInfo(0, dcId, dcHost, dcPort, 0));
    //apiState.setPrimaryDc(dcId);
    //apiState.reset(); // will write the file

    KernelComm kernelComm = new KernelComm(apiKey, apiState);
    kernelComm.init();
    TLConfig config = kernelComm.getApi().doRpcCallNonAuth(new TLRequestHelpGetConfig());
    //apiState.getObj().getDcInfos().add(new TLDcInfo(dcOption.getFlags(), dcOption.getId(), dcOption.getIpAddress(), dcOption.getPort(), 0));
    config.getDcOptions().stream().map(TLDcOption::getIpAddress).forEach(log::info);

    service = new TelegramBot(authConfig, new UpdateBuilder(db), apiKey, apiHash);
    // reset loggers back after instantiating TelegramBot
    org.telegram.mtproto.log.Logger.registerInterface(LEGACY_LOGGER);
    org.telegram.api.engine.Logger.registerInterface(LEGACY_LOGGER);

    LoginStatus loginStatus = service.init();
    if (loginStatus == LoginStatus.CODESENT) {
      System.out.println("INPUT CODE FOR " + phoneNumber + ":");
      String authCode = new Scanner(System.in).nextLine().trim();
      boolean isLogged = service.getKernelAuth().setAuthCode(authCode);
      if (isLogged) {
        loginStatus = LoginStatus.ALREADYLOGGED;
      }
    }
    if (loginStatus != LoginStatus.ALREADYLOGGED) {
      throw new IllegalStateException();
    }
    service.startBot();

    // save users from the contact list
    TLRequestMessagesGetDialogs requestMessagesGetDialogs = new TLRequestMessagesGetDialogs();
    requestMessagesGetDialogs.setOffsetPeer(new TLInputPeerSelf());
    TLAbsDialogs messagesPeerDialogs = service.getKernelComm().doRpcCallSync(requestMessagesGetDialogs);
    messagesPeerDialogs.getUsers().forEach(db::putUser);
  }

  @SneakyThrows
  public String request(String input) {

    int peerId;
    if (this.peerId == null) {
      // take the pinned user
      TLRequestMessagesGetPinnedDialogs requestMessagesGetPinnedDialogs = new TLRequestMessagesGetPinnedDialogs();
      TLMessagesPeerDialogs messagesPeerDialogs = service.getKernelComm().doRpcCallSync(requestMessagesGetPinnedDialogs);
      messagesPeerDialogs.getUsers().forEach(db::putUser); // save users in the database, important
      TLVector<TLDialog> dialogs = messagesPeerDialogs.getDialogs();
      Assert.isTrue(dialogs.size() == 1, "Only one dialog must be pinned");
      peerId = dialogs.get(0).getPeer().getId();
    } else {
      peerId = this.peerId;
    }

    db.getQueue().clear();

    IUser iUser = db.getUserById(peerId);
    Assert.notNull(iUser, "Contact not found");
    service.getKernelComm().sendMessage(iUser, input);

    TLMessage output;
    do {
      // in five seconds the system will start sending retries so we will wait no more than three
      output = db.getQueue().poll(3, TimeUnit.SECONDS);
    } while (output != null && output.getFromId() != peerId);
    if (output != null) service.getKernelComm().performMarkAsRead(iUser, output.getId());

    return output == null ? "" : output.getMessage();
  }
}

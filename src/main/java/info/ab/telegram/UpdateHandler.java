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

import lombok.extern.slf4j.Slf4j;
import org.telegram.api.message.TLAbsMessage;
import org.telegram.api.message.TLMessage;
import org.telegram.api.peer.TLPeerUser;
import org.telegram.api.update.TLUpdateNewMessage;
import org.telegram.api.updates.TLUpdateShortMessage;
import org.telegram.api.user.TLAbsUser;
import org.telegram.bot.handlers.DefaultUpdatesHandler;
import org.telegram.bot.kernel.IKernelComm;
import org.telegram.bot.kernel.differenceparameters.IDifferenceParametersService;

import java.util.List;

@Slf4j
public class UpdateHandler extends DefaultUpdatesHandler {

  private final MemoryDatabase db;

  public UpdateHandler(IKernelComm kernelComm, IDifferenceParametersService differenceParametersService, MemoryDatabase databaseManager) {
    super(kernelComm, differenceParametersService, databaseManager);
    this.db = databaseManager;
  }

  @Override
  public void onTLAbsMessageCustom(TLAbsMessage message) {
    TLMessage tlMessage = (TLMessage) message; // exception is ok
    log.debug("{} -> {}: {}", tlMessage.getFromId(), tlMessage.getToId().getId(), tlMessage.getMessage());
    db.getQueue().add(tlMessage);
  }

  @Override
  public void onTLUpdateNewMessageCustom(TLUpdateNewMessage update) {
    onTLAbsMessageCustom(update.getMessage());
  }

  @Override
  public void onTLUpdateShortMessageCustom(TLUpdateShortMessage update) {
    TLMessage message = new TLMessage();
    message.setId(update.getId());
    message.setFromId(update.getUserId());
    message.setToId(new TLPeerUser());
    message.setMessage(update.getMessage());
    onTLAbsMessageCustom(message);
  }

  @Override
  public void onUsersCustom(List<TLAbsUser> users) {
    users.forEach(db::putUser);
  }

}

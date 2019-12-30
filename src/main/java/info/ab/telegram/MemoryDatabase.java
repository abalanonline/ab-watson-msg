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

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.Nullable;
import org.telegram.api.message.TLMessage;
import org.telegram.api.user.TLAbsUser;
import org.telegram.api.user.TLUser;
import org.telegram.bot.kernel.database.DatabaseManager;
import org.telegram.bot.structure.Chat;
import org.telegram.bot.structure.IUser;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

@Slf4j
public class MemoryDatabase implements DatabaseManager {

  @Getter
  private final HashMap<Integer, int[]> differencesData = new HashMap<>();

  @Getter
  private final BlockingQueue<TLMessage> queue = new LinkedBlockingDeque<>();

  private final HashMap<Integer, IUser> users = new HashMap<>();

  @Override
  public @Nullable Chat getChatById(int i) {
    throw new NotImplementedException("");
  }

  @Override
  public @Nullable IUser getUserById(int i) {
    IUser user = users.get(i);
    log.trace("---- user " + i + " => " + (user == null ? null : user.getUserHash()));
    return user;
  }

  public void putUser(TLAbsUser user) {
    putUser(new User(user.getId(), user instanceof TLUser ? ((TLUser) user).getAccessHash() : null));
  }

  public void putUser(IUser user) {
    users.put(user.getUserId(), user);
    log.trace("---- user " + user.getUserId() + " <= " + user.getUserHash());
  }

  @Override
  public boolean updateDifferencesData(int i, int i1, int i2, int i3) {
    return null != differencesData.put(i, new int[]{i1, i2, i3});
  }
}

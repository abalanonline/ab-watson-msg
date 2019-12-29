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

@Slf4j
public class LegacyLogger implements org.telegram.mtproto.log.LogInterface, org.telegram.api.engine.LoggerInterface {

  @Override
  public void w(String tag, String message) {
    log.warn("{}: {}", tag, message);
  }

  @Override
  public void d(String tag, String message) {
    log.debug("{}: {}", tag, message);
  }

  @Override
  public void e(String tag, String message) {
    log.error("{}: {}", tag, message);
  }

  @Override
  public void e(String tag, Throwable t) {
    log.error(tag, t);
  }

}

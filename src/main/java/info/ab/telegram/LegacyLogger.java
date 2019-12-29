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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LegacyLogger implements org.telegram.mtproto.log.LogInterface, org.telegram.api.engine.LoggerInterface {

  public final String CLASS_NAME = this.getClass().getName();

  private final Set<String> excludedClassNames = Stream.of(CLASS_NAME, "org.telegram.mtproto.log.Logger", "org.telegram.api.engine.Logger").collect(Collectors.toCollection(HashSet::new));

  final Map<String, Logger> loggers = new HashMap<>();

  private Logger getLogger() {
    StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
    Optional<StackTraceElement> callingClass = Arrays.stream(stackTrace).skip(1).filter(e -> !excludedClassNames.contains(e.getClassName())).findAny();
    String callingClassName = callingClass.map(StackTraceElement::getClassName).orElse(CLASS_NAME);

    return loggers.computeIfAbsent(callingClassName, LoggerFactory::getLogger);
  }

  @Override
  public void w(String tag, String message) {
    getLogger().warn("{}: {}", tag, message);
  }

  @Override
  public void d(String tag, String message) {
    getLogger().debug("{}: {}", tag, message);
  }

  @Override
  public void e(String tag, String message) {
    getLogger().error("{}: {}", tag, message);
  }

  @Override
  public void e(String tag, Throwable t) {
    getLogger().error(tag, t);
  }

}

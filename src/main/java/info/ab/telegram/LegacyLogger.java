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
import java.util.logging.Level;

public class LegacyLogger implements org.telegram.mtproto.log.LogInterface, org.telegram.api.engine.LoggerInterface {

  public static final String FORMAT = "{}: {}";

  public final String CLASS_NAME = this.getClass().getName();

  private final Set<String> excludedClassNames;

  final Map<String, Logger> loggers = new HashMap<>();

  public LegacyLogger() {
    excludedClassNames = new HashSet<>();
    excludedClassNames.add(CLASS_NAME);
    excludedClassNames.add("org.telegram.mtproto.log.Logger");
    excludedClassNames.add("org.telegram.api.engine.Logger");
    excludedClassNames.add("org.telegram.bot.services.BotLogger");
  }

  private Logger getLogger() {
    StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
    Optional<StackTraceElement> callingClass = Arrays.stream(stackTrace).skip(1).filter(e -> !excludedClassNames.contains(e.getClassName())).findAny();
    String callingClassName = callingClass.map(StackTraceElement::getClassName).orElse(CLASS_NAME);

    return loggers.computeIfAbsent(callingClassName, LoggerFactory::getLogger);
  }

  @Override
  public void w(String tag, String message) {
    getLogger().warn(FORMAT, tag, message);
  }

  @Override
  public void d(String tag, String message) {
    getLogger().debug(FORMAT, tag, message);
  }

  @Override
  public void e(String tag, String message) {
    getLogger().error(FORMAT, tag, message);
  }

  @Override
  public void e(String tag, Throwable t) {
    getLogger().error(tag, t);
  }

  public void log(Level level, String tag, String message) {
    if (Level.FINEST.equals(level)) getLogger().trace(FORMAT, tag, message);
    if (Level.FINER.equals(level)) getLogger().debug(FORMAT, tag, message);
    if (Level.FINE.equals(level)) getLogger().debug(FORMAT, tag, message);
    if (Level.CONFIG.equals(level)) getLogger().info(FORMAT, tag, message);
    if (Level.INFO.equals(level)) getLogger().info(FORMAT, tag, message);
    if (Level.WARNING.equals(level)) getLogger().warn(FORMAT, tag, message);
    if (Level.SEVERE.equals(level)) getLogger().error(FORMAT, tag, message);
  }

  public void log(Level level, String message, Throwable throwable) {
    if (Level.FINEST.equals(level)) getLogger().trace(message, throwable);
    if (Level.FINER.equals(level)) getLogger().debug(message, throwable);
    if (Level.FINE.equals(level)) getLogger().debug(message, throwable);
    if (Level.CONFIG.equals(level)) getLogger().info(message, throwable);
    if (Level.INFO.equals(level)) getLogger().info(message, throwable);
    if (Level.WARNING.equals(level)) getLogger().warn(message, throwable);
    if (Level.SEVERE.equals(level)) getLogger().error(message, throwable);
  }

}

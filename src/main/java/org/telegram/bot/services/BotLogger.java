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

package org.telegram.bot.services;

import info.ab.telegram.LegacyLogger;

import java.util.logging.Level;

/**
 * BotLogger created files in static initializer section and I could not find a way to disable it
 * except making a copy of this class.
 */
public class BotLogger {

  public static final LegacyLogger L = new LegacyLogger();

  public static void setLevel(Level level) {
    // level can be set in application.yaml file like this
    // logging.level.org.telegram: DEBUG
    // logging.level.org.telegram.bot: WARN
  }

  public static void log(Level level, String tag, String msg) {
    L.log(level, tag, msg);
  }

  public static void severe(String tag, String msg) {
    L.log(Level.SEVERE, tag, msg);
  }

  public static void warn(String tag, String msg) {
    warning(tag, msg);
  }

  public static void debug(String tag, String msg) {
    fine(tag, msg);
  }

  public static void error(String tag, String msg) {
    severe(tag, msg);
  }

  public static void trace(String tag, String msg) {
    finer(tag, msg);
  }

  public static void warning(String tag, String msg) {
    L.log(Level.WARNING, tag, msg);
  }

  public static void info(String tag, String msg) {
    L.log(Level.INFO, tag, msg);
  }

  public static void config(String tag, String msg) {
    L.log(Level.CONFIG, tag, msg);
  }

  public static void fine(String tag, String msg) {
    L.log(Level.FINE, tag, msg);
  }

  public static void finer(String tag, String msg) {
    L.log(Level.FINER, tag, msg);
  }

  public static void finest(String tag, String msg) {
    L.log(Level.FINEST, tag, msg);
  }

  public static void log(Level level, String tag, Throwable throwable) {
    L.log(level, String.format("[%s] Exception", tag), throwable);
  }

  public static void log(Level level, String tag, String msg, Throwable thrown) {
    L.log(level, msg, thrown);
  }

  public static void severe(String tag, Throwable throwable) {
    log(Level.SEVERE, tag, throwable);
  }

  public static void warning(String tag, Throwable throwable) {
    log(Level.WARNING, tag, throwable);
  }

  public static void info(String tag, Throwable throwable) {
    log(Level.INFO, tag, throwable);
  }

  public static void config(String tag, Throwable throwable) {
    log(Level.CONFIG, tag, throwable);
  }

  public static void fine(String tag, Throwable throwable) {
    log(Level.FINE, tag, throwable);
  }

  public static void finer(String tag, Throwable throwable) {
    log(Level.FINER, tag, throwable);
  }

  public static void finest(String tag, Throwable throwable) {
    log(Level.FINEST, tag, throwable);
  }

  public static void warn(String tag, Throwable throwable) {
    warning(tag, throwable);
  }

  public static void debug(String tag, Throwable throwable) {
    fine(tag, throwable);
  }

  public static void error(String tag, Throwable throwable) {
    severe(tag, throwable);
  }

  public static void trace(String tag, Throwable throwable) {
    finer(tag, throwable);
  }

  public static void severe(String msg, String tag, Throwable throwable) {
    log(Level.SEVERE, tag, msg, throwable);
  }

  public static void warning(String msg, String tag, Throwable throwable) {
    log(Level.WARNING, tag, msg, throwable);
  }

  public static void info(String msg, String tag, Throwable throwable) {
    log(Level.INFO, tag, msg, throwable);
  }

  public static void config(String msg, String tag, Throwable throwable) {
    log(Level.CONFIG, tag, msg, throwable);
  }

  public static void fine(String msg, String tag, Throwable throwable) {
    log(Level.FINE, tag, msg, throwable);
  }

  public static void finer(String msg, String tag, Throwable throwable) {
    log(Level.FINER, tag, msg, throwable);
  }

  public static void finest(String msg, String tag, Throwable throwable) {
    log(Level.FINEST, msg, throwable);
  }

  public static void warn(String msg, String tag, Throwable throwable) {
    log(Level.WARNING, tag, msg, throwable);
  }

  public static void debug(String msg, String tag, Throwable throwable) {
    log(Level.FINE, tag, msg, throwable);
  }

  public static void error(String msg, String tag, Throwable throwable) {
    log(Level.SEVERE, tag, msg, throwable);
  }

  public static void trace(String msg, String tag, Throwable throwable) {
    log(Level.FINER, tag, msg, throwable);
  }

}

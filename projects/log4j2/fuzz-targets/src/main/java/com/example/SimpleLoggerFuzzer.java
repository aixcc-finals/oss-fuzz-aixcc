// Copyright 2025 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
////////////////////////////////////////////////////////////////////////////////

package com.example;

import java.nio.charset.StandardCharsets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

final class SimpleLoggerFuzzer {

  /**
   * This is the logger.
   */
  private static final Logger LOGGER =
      LogManager.getLogger(SimpleLoggerFuzzer.class);

  private SimpleLoggerFuzzer() {
  }

  /**
   * Entry-point for jazzer.
   * @param bytes
   * @throws Exception
   */
  public static void fuzzerTestOneInput(final byte[] bytes) throws Exception {
    String msg = new String(bytes, StandardCharsets.UTF_8);
    LOGGER.warn(msg);
  }
}

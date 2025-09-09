/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;

import org.apache.tika.cli.TikaCLI;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

/**
 * Adds harness for recursive unpacking feature.
 */
public final class TikaAppRUnpackerFuzzer {

  /**
   * Temp dir.
   */
  private static Path tmpDir;

  /**
   * Extract dir.
   */
  private static Path extractDir;

  static {
    try {
      tmpDir = Files.createTempDirectory("expander-tmp");
      extractDir = tmpDir.resolve("output/q/r/s/t");
      Path target = tmpDir.resolve("output/q/r/jazzer-traversal");
      System.setProperty("jazzer.file_path_traversal_target",
          target.toAbsolutePath().toString());
    } catch (IOException e) {
      throw new RuntimeException("couldn't create tmp dir", e);
    }
  }

  private TikaAppRUnpackerFuzzer() {
    //private constructor
  }

  private static void setUp() {
    try {
      if (!Files.isDirectory(tmpDir)) {
        Files.createDirectories(tmpDir);
      }
    } catch (IOException e) {
      throw new RuntimeException("Couldn't create tmp dir: "
          + tmpDir.toAbsolutePath());
    }
  }

  private static void tearDown() throws IOException {
    if (Files.isDirectory(tmpDir)) {
      FileUtils.deleteDirectory(tmpDir.toFile());
    }
  }

  /**
   * main.
   * @param args
   * @throws Exception
   */
  public static void main(final String[] args) throws Exception {
    byte[] bytes = Files.readAllBytes(Paths.get(args[0]));
    parseOne(bytes);
  }

  /**
   * Jazzer entry-point.
   *
   * @param bytes
   * @throws Exception
   */
  public static void fuzzerTestOneInput(final byte[] bytes) throws Exception {
    parseOne(bytes);
  }

  private static synchronized void parseOne(final byte[] bytes)
      throws Exception {
    setUp();
    Path input = tmpDir.resolve("input.bin");
    //limit to loading only two parsers -- zip and text
    Path tikaConfig = tmpDir.resolve("tika-config.xml");
    try {
      Files.write(input, bytes);
      writeConfig(tikaConfig);
      TikaCLI.main(
          new String[]{"-Z",
              "--config=" + tikaConfig.toAbsolutePath(),
              "--extract-dir=" + extractDir.toAbsolutePath(),
              input.toAbsolutePath().toString()});
    } catch (TikaException | IOException | SAXException e) {
      //swallow
    } finally {
      tearDown();
    }
  }

  private static void writeConfig(final Path tikaConfig) throws IOException {
    String xml = """
        <?xml version="1.0" encoding="UTF-8"?>
          <properties>
            <parsers>
              <parser class="org.apache.tika.parser.pkg.PackageParser"/>
              <parser class="org.apache.tika.parser.txt.TXTParser"/>
            </parsers>
          </properties>
        """;
    Files.writeString(tikaConfig, xml, StandardCharsets.UTF_8);
  }
}

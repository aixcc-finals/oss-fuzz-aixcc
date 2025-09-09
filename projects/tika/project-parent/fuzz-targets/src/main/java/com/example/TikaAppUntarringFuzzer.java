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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;


/**
 * Harness for the untarring option when it exists.
 */
public final class TikaAppUntarringFuzzer {

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

  private TikaAppUntarringFuzzer() {
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
   * @throws Throwable
   */
  public static void main(final String[] args) throws Throwable {
    byte[] bytes = Files.readAllBytes(Paths.get(args[0]));
    parseOne(bytes);
  }

  /**
   * Jazzer entrypoint.
   * @param bytes
   * @throws Exception
   */
  public static void fuzzerTestOneInput(final byte[] bytes) throws Exception {
    try {
      parseOne(bytes);
    } catch (TikaException | IOException | SAXException
             | IllegalArgumentException e) {
      //e.printStackTrace();
    }
  }

  private static void parseOne(final byte[] bytes) throws IOException,
      SAXException, TikaException {
    Class untarringClazz = null;

    try {
      untarringClazz = Class.forName("org.apache.tika.cli.TikaUntar");
    } catch (ClassNotFoundException e) {
      return;
    }
    setUp();
    Path input = tmpDir.resolve("input.bin");
    Files.write(input, bytes);
    String[] params =
        new String[]{input.toAbsolutePath().toString(),
            extractDir.toAbsolutePath().toString()};
    try {
      Method m = untarringClazz.getMethod("main", String[].class);
      m.invoke(null, (Object) params);
    } catch (InvocationTargetException e) {
      Throwable t = e.getCause();
      if (t instanceof Error) {
        throw (Error) t;
      } else if (t instanceof RuntimeException) {
        throw (RuntimeException) t;
      }
      //swallow
    } catch (ReflectiveOperationException e) {
      throw new RuntimeException("something went very, very wrong", e);
    } finally {
      tearDown();
    }
  }
}

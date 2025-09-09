package org.apache.poi;
// Copyright 2024 Google LLC
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

import org.apache.poi.poifs.dev.POIFSDump;

/**
 * Fuzzer for POIFSDump.
 */
public final class POIPOIFSDumpFuzzer {

  /**
   * tmpDir.
   */
  private static Path tmpDir;
  /**
   * extractDir.
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

  /**
   * private constructor.
   */
  private POIPOIFSDumpFuzzer() {
    //don't allow public constructor
  }

  /**
   * Jazzer entry point.
   *
   * @param data data
   * @throws IOException IOException
   */
  public static void fuzzerTestOneInput(final byte[] data)
      throws IOException {
    try {
      Path src = extractDir.resolve("input.bin");
      if (!Files.isDirectory(extractDir)) {
        Files.createDirectories(extractDir);
      }
      Files.write(src, data);
      POIFSDump.main(new String[]{src.toAbsolutePath().toString()});
    } catch (IllegalArgumentException | IOException ignored) {
      //ignore
    } finally {
      try (Stream<Path> files = Files.walk(tmpDir)) {
        files.sorted(Comparator.reverseOrder())
            .map(Path::toFile).forEach(File::delete);
      } catch (IOException e) {
        //swallow
      }
    }
  }
}

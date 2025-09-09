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

import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import java.io.ByteArrayInputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.compress.archivers.examples.Expander;
import org.apache.commons.io.FileUtils;

public class ExpanderFuzzer extends BaseTests {
    static Path TMP_DIR;
    static Path EXTRACT_DIR;

    static {
        try {
            TMP_DIR = Files.createTempDirectory("expander-tmp");
            EXTRACT_DIR = TMP_DIR.resolve("output/q/r/s/t");
            Path target = TMP_DIR.resolve("output/q/r/jazzer-traversal");
            System.setProperty("jazzer.file_path_traversal_target",
                    target.toAbsolutePath().toString());
        } catch (IOException e) {
            throw new RuntimeException("couldn't create tmp dir", e);
        }
    }

    public static void fuzzerTestOneInput(byte[] data) throws IOException {
        try {
            Path src = TMP_DIR.resolve("input.bin");
            if (! Files.isDirectory(TMP_DIR)) {
                Files.createDirectories(TMP_DIR);
            }
            Files.write(src, data);
            Expander expander = new Expander();
            expander.expand(src, EXTRACT_DIR);
        } catch (IllegalArgumentException|IOException ignored) {
            //ignore
        } finally {
          try {
              FileUtils.deleteDirectory(TMP_DIR.toFile());
          } catch (IOException e) {
                //swallow
          }
        }
    }
}

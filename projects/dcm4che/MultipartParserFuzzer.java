// Copyright 2023 Google LLC
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
///////////////////////////////////////////////////////////////////////////
import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import org.dcm4che3.mime.MultipartParser;
import org.dcm4che3.mime.MultipartInputStream;

public class MultipartParserFuzzer {
  private static class MultipartHandler implements MultipartParser.Handler {
    MultipartHandler() throws IOException {
      // Do nothing
    }

    @Override
    public void bodyPart(int partNumber, MultipartInputStream in) throws IOException {
        
    }
  }
  
  public static void fuzzerTestOneInput(FuzzedDataProvider data) {
    try {
      String boundary = data.consumeString(500);
      byte[] b = data.consumeRemainingAsBytes();
      InputStream is = new ByteArrayInputStream(b);
      new MultipartParser(boundary).parse(is, new MultipartHandler());
      
    } catch (IOException e) {
      // Known exception
    }
  }
}

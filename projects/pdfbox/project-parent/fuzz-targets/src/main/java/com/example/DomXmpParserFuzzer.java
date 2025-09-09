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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.DublinCoreSchema;
import org.apache.xmpbox.schema.XMPSchema;
import org.apache.xmpbox.xml.DomXmpParser;

class DomXmpParserFuzzer {

    static {
        LogManager.getLogManager().reset();
    }

    public static void fuzzerTestOneInput(FuzzedDataProvider data) {

        byte [] bytes = data.consumeRemainingAsBytes();

        try (InputStream is = new ByteArrayInputStream(bytes)) {
            try (PDDocument pdDocument = Loader.loadPDF(new RandomAccessReadBuffer(is))) {
                PDDocumentCatalog catalog = pdDocument.getDocumentCatalog();
                if (catalog == null) {
                    return;
                }
                PDMetadata pdMetadata = catalog.getMetadata();
                if (pdMetadata == null) {
                    return;
                }
                try (InputStream xmpStream = pdMetadata.exportXMPMetadata()) {
                    DomXmpParser domXmpParser = new DomXmpParser();
                    XMPMetadata xmp = domXmpParser.parse(xmpStream);
                    DublinCoreSchema dublinCoreSchema = xmp.getDublinCoreSchema();

                } catch (org.apache.xmpbox.xml.XmpParsingException e) {
                }
            }
        } catch (IOException | RuntimeException e) {
            //swallow
        }
    }
}

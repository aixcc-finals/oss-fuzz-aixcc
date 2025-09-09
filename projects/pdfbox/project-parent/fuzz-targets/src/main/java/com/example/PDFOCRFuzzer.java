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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.io.File;
import java.io.IOException;

import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.Files;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.util.logging.LogManager;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;

public class PDFOCRFuzzer {

    static {
        LogManager.getLogManager().reset();
    }

    private static final long timeoutMillisPerPage = 20000;
    private static int startPage = 1;
    private static int endPage = 1;
    public static void fuzzerTestOneInput(FuzzedDataProvider data) throws Exception
    {
        try
        {
            Class.forName("org.apache.pdfbox.ocr.OCRStreamEngine");
        }
        catch (ClassNotFoundException e)
        {
            return;
        }
        Path tmp = null;
        try
        {
            tmp = Files.createTempDirectory("pdfbox-ocr-");
            Path input = tmp.resolve("input.pdf");
            //write the bytes to a tmp file
            Files.write(input, data.consumeRemainingAsBytes());
            try (PDDocument doc = Loader.loadPDF(input.toFile()))
            {
                //use reflection in case the class is not available
                Class clazz = Class.forName("org.apache.pdfbox.ocr.OCRStreamEngine");
                Constructor ctor = clazz.getConstructor(PDDocument.class);
                Object streamEngine = ctor.newInstance(doc);

                Method timeoutSetter = streamEngine.getClass().getMethod(
                        "setTimeoutMillisPerPage", long.class);
                timeoutSetter.invoke(streamEngine, timeoutMillisPerPage);

                Method getText = streamEngine.getClass().getDeclaredMethod("getText", int.class, int.class);

                String txt = (String) getText.invoke(streamEngine, startPage, endPage);

                /** if this class is in the project, skip the reflection
                 * and remove the reflection exception catch block below
                OCRStreamEngine ocrStreamEngine = OCRStreamEngine(doc);
                ocrStreamEngine.setTimeoutMillisPerPage(timeoutMillisPerPage);
                String txt = ocrStreamEngine.getText(startPage, endPage);
                 **/
            }
            catch (IllegalAccessException | NoSuchMethodException | InstantiationException | ClassNotFoundException e)
            {
                //these should be show stoppers and represent a bug in reflection that we
                //want to see immediately
                throw new RuntimeException(e);
            }
            catch (InvocationTargetException e)
            {
                //this is just a regular exception thrown during invoke
                //unwrap it, and rethrow
                Throwable cause = e.getCause();
                if (cause != null && cause instanceof Exception) {
                    throw (Exception) cause;
                } else {
                    throw e;
                }
            }
        }
        catch (IOException | RuntimeException e) {
            //illegal state can happen from a page that is fuzzed out of existence:
            // '1-based index not found: 1'
        }
        finally
        {
            if (tmp == null)
            {
                return;
            }
            Files.walkFileTree(tmp, new SimpleFileVisitor<Path>()
            {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                        throws IOException
                {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc)
                        throws IOException
                {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }
}
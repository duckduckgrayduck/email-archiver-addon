/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* $Id: PDFLibraryTestSuite.java 1901776 2022-06-09 10:54:29Z ssteiner $ */

package org.apache.fop.pdf;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import org.apache.fop.render.pdf.extensions.PDFEmbeddedFileAttachmentTestCase;

/**
 * Test suite for FOP's utility classes.
 */
@RunWith(Suite.class)
@SuiteClasses({
        PDFArrayTestCase.class,
        PDFDictionaryTestCase.class,
        PDFNumberTestCase.class,
        PDFObjectTestCase.class,
        PDFNameTestCase.class,
        AbstractPDFStreamTestCase.class,
        PDFDestsTestCase.class,
        PDFDocumentTestCase.class,
        PDFNullTestCase.class,
        PDFNumsArrayTestCase.class,
        PDFRectangleTestCase.class,
        PDFReferenceTestCase.class,
        PDFResourcesTestCase.class,
        VersionTestCase.class,
        VersionControllerTestCase.class,
        PDFRootTestCase.class,
        PDFFileSpecTestCase.class,
        PDFEmbeddedFileAttachmentTestCase.class
})
public class PDFLibraryTestSuite {
}

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

/* $Id: TTFFile.java 1904293 2022-09-27 07:04:02Z ssteiner $ */

package org.apache.fop.fonts.truetype;

import java.io.IOException;

/**
 * Reads a TrueType file or a TrueType Collection.
 * The TrueType spec can be found at the Microsoft.
 * Typography site: http://www.microsoft.com/truetype/
 */
public class TTFFile extends OpenFont {

    public TTFFile() {
        this(true, false);
    }

    /**
     * Constructor
     * @param useKerning true if kerning data should be loaded
     * @param useAdvanced true if advanced typographic tables should be loaded
     */
    public TTFFile(boolean useKerning, boolean useAdvanced) {
        super(useKerning, useAdvanced);
    }

    /**
     * Read the "name" table.
     * @throws IOException In case of a I/O problem
     */
    protected void readName() throws IOException {
        seekTab(fontFile, OFTableName.NAME, 2);
        int i = fontFile.getCurrentPos();
        int n = fontFile.readTTFUShort();
        int j = fontFile.readTTFUShort() + i - 2;
        i += 2 * 2;

        while (n-- > 0) {
            // getLogger().debug("Iteration: " + n);
            fontFile.seekSet(i);
            final int platformID = fontFile.readTTFUShort();
            final int encodingID = fontFile.readTTFUShort();
            final int languageID = fontFile.readTTFUShort();

            int k = fontFile.readTTFUShort();
            int l = fontFile.readTTFUShort();

            if (((platformID == 1 || platformID == 3)
                    && (encodingID == 0 || encodingID == 1))) {
                fontFile.seekSet(j + fontFile.readTTFUShort());
                String txt;
                if (platformID == 3) {
                    txt = fontFile.readTTFString(l, encodingID);
                } else {
                    txt = fontFile.readTTFString(l);
                }

                if (log.isDebugEnabled()) {
                    log.debug(platformID + " "
                            + encodingID + " "
                            + languageID + " "
                            + k + " " + txt);
                }
                switch (k) {
                case 0:
                    if (notice.length() == 0) {
                        notice = txt;
                    }
                    break;
                case 1: //Font Family Name
                case 16: //Preferred Family
                    familyNames.add(txt);
                    break;
                case 2:
                    if (subFamilyName.length() == 0) {
                        subFamilyName = txt;
                    }
                    break;
                case 4:
                    if (fullName.length() == 0 || (platformID == 3 && languageID == 1033)) {
                        fullName = txt;
                    }
                    break;
                case 6:
                    if (postScriptName.length() == 0) {
                        postScriptName = txt;
                    }
                    break;
                default:
                    break;
                }
            }
            i += 6 * 2;
        }
    }

    /**
     * Read the "glyf" table to find the bounding boxes.
     * @throws IOException In case of a I/O problem
     */
    private void readGlyf() throws IOException {
        OFDirTabEntry dirTab = dirTabs.get(OFTableName.GLYF);
        if (dirTab == null) {
            throw new IOException("glyf table not found, cannot continue");
        }
        for (int i = 0; i < (numberOfGlyphs - 1); i++) {
            if (mtxTab[i].getOffset() != mtxTab[i + 1].getOffset()) {
                fontFile.seekSet(dirTab.getOffset() + mtxTab[i].getOffset());
                fontFile.skip(2);
                final int[] bbox = {
                    fontFile.readTTFShort(),
                    fontFile.readTTFShort(),
                    fontFile.readTTFShort(),
                    fontFile.readTTFShort()};
                mtxTab[i].setBoundingBox(bbox);
            } else {
                mtxTab[i].setBoundingBox(mtxTab[0].getBoundingBox());
            }
        }

        long n = (dirTabs.get(OFTableName.GLYF)).getOffset();
        for (int i = 0; i < numberOfGlyphs; i++) {
            if ((i + 1) >= mtxTab.length
                    || mtxTab[i].getOffset() != mtxTab[i + 1].getOffset()) {
                if (lastLoca != 0 && lastLoca == mtxTab[i].getOffset()) {
                    break;
                }
                fontFile.seekSet(n + mtxTab[i].getOffset());
                fontFile.skip(2);
                final int[] bbox = {
                    fontFile.readTTFShort(),
                    fontFile.readTTFShort(),
                    fontFile.readTTFShort(),
                    fontFile.readTTFShort()};
                mtxTab[i].setBoundingBox(bbox);
            } else {
                final int bbox0 = mtxTab[0].getBoundingBox()[0];
                final int[] bbox = {bbox0, bbox0, bbox0, bbox0};
                mtxTab[i].setBoundingBox(bbox);
            }
            if (log.isTraceEnabled()) {
                log.trace(mtxTab[i].toString(this));
            }
        }
    }

    @Override
    protected void updateBBoxAndOffset() throws IOException {
        readIndexToLocation();
        readGlyf();
    }

    /**
     * Read the "loca" table.
     * @throws IOException In case of a I/O problem
     */
    protected final void readIndexToLocation()
            throws IOException {
        if (!seekTab(fontFile, OFTableName.LOCA, 0)) {
            throw new IOException("'loca' table not found, happens when the font file doesn't"
                    + " contain TrueType outlines (trying to read an OpenType CFF font maybe?)");
        }
        for (int i = 0; i < numberOfGlyphs; i++) {
            mtxTab[i].setOffset(locaFormat == 1 ? fontFile.readTTFULong()
                                 : (fontFile.readTTFUShort() << 1));
        }
        lastLoca = (locaFormat == 1 ? fontFile.readTTFULong()
                    : (fontFile.readTTFUShort() << 1));
    }

    /**
     * Gets the last location of the glyf table
     * @return The last location as a long
     */
    public long getLastGlyfLocation() {
        return lastLoca;
    }

    @Override
    protected void initializeFont(FontFileReader in) throws IOException {
        fontFile = in;
    }
}

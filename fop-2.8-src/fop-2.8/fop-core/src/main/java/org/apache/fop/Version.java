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

/* $Id: Version.java 1556164 2014-01-07 09:51:25Z vhennebert $ */

package org.apache.fop;

/**
 * This class is used to evaluate the version information contained in the Manifest of FOP's JAR.
 * Note that this class can only find the version information if it's in the org.apache.fop package
 * as this package equals the one specified in the manifest.
 */
public final class Version {

    private Version() { }

    /**
     * Get the version of FOP
     * @return the version string
     */
    public static String getVersion() {
        String version = null;
        Package jarinfo = Version.class.getPackage();
        if (jarinfo != null) {
            version = jarinfo.getImplementationVersion();
        }
        if (version == null) {
            //Fallback if FOP is used in a development environment
            // CSOFF: LineLength
            String headURL = "$HeadURL: https://svn.apache.org/repos/asf/xmlgraphics/fop/branches/fop-2_8/fop-core/src/main/java/org/apache/fop/Version.java $";
            // CSON: LineLength
            version = headURL;
            final String pathPrefix = "/xmlgraphics/fop/";
            int pos = version.indexOf(pathPrefix);
            if (pos >= 0) {
                version = version.substring(pos + pathPrefix.length() - 1, version.length() - 2);
                pos = version.indexOf("/src/");
                version = version.substring(1, pos);
                version = " " + version;
            } else {
                version = "";
            }
            version = "SVN" + version;
        }
        return version;
    }

}

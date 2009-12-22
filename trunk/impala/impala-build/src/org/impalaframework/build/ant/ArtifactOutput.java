/*
 * Copyright 2007-2008 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.impalaframework.build.ant;

import java.io.File;

public class ArtifactOutput extends ArtifactDescription {

    private File srcFile;
    
    private File sourceSrcFile;

    public File getSrcFile() {
        return srcFile;
    }

    public void setSrcFile(File outputFile) {
        this.srcFile = outputFile;
    }

    public File getSourceSrcFile() {
        return sourceSrcFile;
    }

    public void setSourceSrcFile(File sourceOutputFile) {
        this.sourceSrcFile = sourceOutputFile;
    }
    
    public File getOutputLocation(File organisationDirectory, boolean sources) {
        return getOutputLocation(organisationDirectory, ".jar", sources);
    }
    
    public File getOutputLocation(File organisationDirectory, String extension) {
        return getOutputLocation(organisationDirectory, extension, false);
    }
    
    private File getOutputLocation(File organisationDirectory, String extension, boolean sources) {
        final String outputLocation = this.getArtifact() + "/" 
            + this.getVersion() + "/" 
            + this.getArtifact() 
            + "-" 
            + this.getVersion() 
            + (sources ? "-sources" : "")
            + extension;
        return new File(organisationDirectory, outputLocation);
    }
    
}

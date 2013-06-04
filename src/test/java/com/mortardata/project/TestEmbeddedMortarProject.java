/*
 * Copyright 2013 Mortar Data Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mortardata.project;

import java.io.File;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.junit.RepositoryTestCase;
import org.eclipse.jgit.util.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.mortardata.util.Files;

public class TestEmbeddedMortarProject extends RepositoryTestCase {

    private File rootPath;
    private Git git;
    
    private File controlscripts;
    private File pigscripts;
    private File macros;
    private File udfs;
    private File fixtures;
    
    @Before
    public void setUp() throws Exception {
        super.setUp();
        
        // setup a blank existing project
        this.rootPath = Files.createTempDirectory();
        
        this.controlscripts = new File(this.rootPath, "controlscripts");
        this.pigscripts = new File(this.rootPath, "pigscripts");
        this.macros = new File(this.rootPath, "macros");
        this.udfs = new File(this.rootPath, "udfs");
        this.fixtures = new File(this.rootPath, "fixtures");
        
        File[] dirs = {this.pigscripts, this.controlscripts,
                this.macros, this.udfs, this.fixtures};
        for (File dir : dirs) {
            FileUtils.mkdir(dir);
        }
        
        this.git = new Git(this.db);        
    }
    
    @Test
    public void testDeployToMortar() {
        Assert.assertTrue(this.pigscripts.exists());
        
    }
}

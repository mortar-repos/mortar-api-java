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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.InitCommand;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.junit.RepositoryTestCase;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.util.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.mortardata.git.GitUtil;
import com.mortardata.util.Files;

public class TestEmbeddedMortarProject extends RepositoryTestCase {

    private File rootPath;
    private Git git;

    private File controlscripts;
    private File pigscripts;
    private File macros;
    private File udfs;
    private File fixtures;
    private UsernamePasswordCredentialsProvider fakeCP;
    private File mirrorPath;
    private String remoteURLGit;
    private String remoteURLHttps;

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

        // write the .mortar-project-remote
        this.remoteURLGit = "git@github.com:mortardata/mortar-api-java.git";
        this.remoteURLHttps = "https://github.com/mortardata/mortar-api-java.git";
        this.write(new File(this.rootPath,
                EmbeddedMortarProject.MORTAR_PROJECT_REMOTE_FILENAME),
                this.remoteURLGit);

        File[] dirs = { this.pigscripts, this.controlscripts, this.macros,
                this.udfs, this.fixtures };
        for (File dir : dirs) {
            FileUtils.mkdir(dir);
        }

        // setup an empty git repo
        this.mirrorPath = Files.createTempDirectory();
        this.git = new InitCommand().setBare(false)
                .setDirectory(this.mirrorPath).call();

        // do a commit
        this.write(new File(this.mirrorPath, ".gitkeeptest"), "");
        this.git.add().addFilepattern(".gitkeeptest").call();
        this.git.commit().setMessage("initial commit")
                .setAuthor("unitest", "unittest").call();

        this.fakeCP = new UsernamePasswordCredentialsProvider("foo", "bar");
    }

    @Test
    public void testSetupGitMirror() throws IOException, GitAPIException {
        Git gitSpy = spy(this.git);

        PushCommand pushMock = mock(PushCommand.class);
        when(gitSpy.push()).thenReturn(pushMock);
        when(pushMock.setRemote(anyString())).thenReturn(pushMock);
        when(pushMock.setCredentialsProvider(any(CredentialsProvider.class)))
                .thenReturn(pushMock);
        when(pushMock.setRefSpecs(any(RefSpec.class))).thenReturn(pushMock);

        String commiter = "fake_committer";
        EmbeddedMortarProject e = new EmbeddedMortarProject(this.rootPath, this.remoteURLHttps);
        e.setupGitMirror(gitSpy, this.fakeCP, commiter);

        // ensure that we pushed to the remote URL
        verify(pushMock).setRemote(this.remoteURLHttps);
        verify(pushMock).call();

        // ensure that the mirror exists with a .gitkeep
        Assert.assertTrue(new File(this.mirrorPath, ".gitkeep").exists());

        // ensure that the mirror is at master
        Assert.assertEquals("master", this.git.getRepository().getBranch());
    }

    @Test
    public void testSyncEmbeddedProjectWithMirrorMissingManifest()
            throws IOException, GitAPIException {

        EmbeddedMortarProject e = new EmbeddedMortarProject(this.rootPath, this.remoteURLHttps);
        try {
            e.syncEmbeddedProjectWithMirror(this.git, this.fakeCP, "master",
                    "fake_committer");
            Assert.fail("Expected to raise exception for missing .mortar-project-manifest");
        } catch (IOException ie) {

        }
    }

    @Test
    public void testSyncEmbeddedProjectWithMirror() throws IOException, GitAPIException {
        
        EmbeddedMortarProject e = new EmbeddedMortarProject(this.rootPath, this.remoteURLHttps);
        // write a project manifest
        String[] manifestDirs = {"pigscripts", "controlscripts", "udfs", "fixtures"};
        writeManifestFile(manifestDirs);
        
        e.syncEmbeddedProjectWithMirror(this.git, this.fakeCP, "master", "fake_committer");
        Assert.assertEquals("master", this.git.getRepository().getBranch());
        
        // add a new file in a directory that is synced
        String pigscriptFilename = "foo.pig";
        String pigscriptContents = "My new pigscript";
        File originalPigscriptFile = new File(this.pigscripts, pigscriptFilename);
        write(originalPigscriptFile, pigscriptContents);
        
        // sync
        e.syncEmbeddedProjectWithMirror(this.git, this.fakeCP, "master", "fake_committer");
        
        // ensure the new file exists with the right contents
        File mirrorPigscriptFile = new File(new File(this.mirrorPath, "pigscripts"), pigscriptFilename);
        Assert.assertTrue(mirrorPigscriptFile.exists());
        Assert.assertEquals(pigscriptContents, read(mirrorPigscriptFile));
        
        // ensure that it is checked in
        assertCleanTree();
        
        // remove the file from the project
        Assert.assertTrue(originalPigscriptFile.delete());
        
        // sync
        e.syncEmbeddedProjectWithMirror(this.git, this.fakeCP, "master", "fake_committer");
        
        // ensure it is gone
        Assert.assertFalse(mirrorPigscriptFile.exists());
        assertCleanTree();
    }
    

    @Test
    public void testSyncEmbeddedProjectWithMirrorOnNewBranch() throws IOException, GitAPIException {
        EmbeddedMortarProject e = new EmbeddedMortarProject(this.rootPath, this.remoteURLHttps);
        // write a project manifest
        String[] manifestDirs = {"pigscripts", "controlscripts", "udfs", "fixtures"};
        writeManifestFile(manifestDirs);
        
        // add a new file in a directory that is synced
        String pigscriptFilename = "foo.pig";
        String pigscriptContents = "My new pigscript";
        File originalPigscriptFile = new File(this.pigscripts, pigscriptFilename);
        write(originalPigscriptFile, pigscriptContents);
        
        // sync with a non-master branch
        e.syncEmbeddedProjectWithMirror(this.git, this.fakeCP, "my_new_branch", "fake_committer");
        Assert.assertEquals("my_new_branch", this.git.getRepository().getBranch());
        assertCleanTree();
        
        // ensure the new file exists with the right contents
        File mirrorPigscriptFile = new File(new File(this.mirrorPath, "pigscripts"), pigscriptFilename);
        Assert.assertTrue(mirrorPigscriptFile.exists());
        Assert.assertEquals(pigscriptContents, read(mirrorPigscriptFile));
    }
    
    @Test
    public void testSyncEmbeddedProjectWithMirrorUsesManifest() throws IOException, GitAPIException {
        EmbeddedMortarProject e = new EmbeddedMortarProject(this.rootPath, this.remoteURLHttps);
        // write a project manifest
        String[] manifestDirs = {"pigscripts", "controlscripts", "udfs", "fixtures"};
        writeManifestFile(manifestDirs);
        
        // drop a file in a data directory, outside of the manifest
        File dataDir = new File(this.rootPath, "data");
        FileUtils.mkdir(dataDir);
        File dataFile = new File(dataDir, "mydata.txt");
        write(dataFile, "some data that should not be synced");
        e.syncEmbeddedProjectWithMirror(this.git, this.fakeCP, "master", "fake_committer");
        
        File mirrorDataDir = new File(this.mirrorPath, "data");
        Assert.assertFalse(mirrorDataDir.exists());
        assertCleanTree();
    }

    protected void assertCleanTree() throws GitAPIException {
        Status status = this.git.status().call();
        Assert.assertTrue("Expected no files in the status; got: " + status.toString(),
                status.isClean());
    }
    
    protected void writeManifestFile(String[] dirs) throws IOException {
        StringBuilder output = new StringBuilder();
        for (String dir : dirs) {
            output.append(dir + "\n");
        }
        // write a project manifest
        write(new File(this.rootPath,
                EmbeddedMortarProject.MORTAR_PROJECT_MANIFEST_FILENAME),
                output.toString());
    }

}

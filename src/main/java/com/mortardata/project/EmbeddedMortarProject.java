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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mortardata.git.GitUtil;
import com.mortardata.util.Files;

/**
 * Tools for deploying Embedded Mortar Projects to the
 * Mortar Service (via github).
 * 
 * @see <a href="http://bit.ly/11Ha0iY" target="_blank">
 * Using Your Own Source Control</a>
 */
public class EmbeddedMortarProject {
    
    final Logger logger = LoggerFactory.getLogger(EmbeddedMortarProject.class);
    
    /**
     * Default target branch to push to in mirror repo.
     */
    public static final String DEPLOY_TARGET_BRANCH_DEFAULT = "master";
    
    /**
     * Filename in Embedded Mortar Project that contains list of files/directories to sync.
     */
    public static final String MORTAR_PROJECT_MANIFEST_FILENAME = ".mortar-project-manifest";
    
    /**
     * Filename in Embedded Mortar Project that contains git remote for Mortar git mirror. 
     */
    public static final String MORTAR_PROJECT_REMOTE_FILENAME = ".mortar-project-remote";
    
    private File rootPath;
    private GitUtil gitUtil;

    private String gitMirrorURL;
    
    /**
     * Construct a representation of an Embedded Mortar Project.
     * 
     * @param rootPath Path on the file system where the project code lives
     */
    public EmbeddedMortarProject(File rootPath) {
        this(rootPath, null);
    }

    /**
     * Construct a representation of an Embedded Mortar Project.
     * 
     * @param rootPath Path on the file system where the project code lives
     * @param gitMirrorURL URL of the Mortar private mirror repository
     * (if null, looked up in the .mortar-project-remote file) 
     */
    EmbeddedMortarProject(File rootPath, String gitMirrorURL) {
        this.rootPath = rootPath;
        this.gitUtil = new GitUtil();
        this.gitMirrorURL = gitMirrorURL;
    }

    /**
     * Deploy the code in this EmbeddedMortarProject to Mortar onto 
     * the master branch.
     * 
     * @param githubUsername Username for github user associated with Mortar 
     * account (used to sync code to backing Mortar github repo)
     * @param githubPassword Password for github user associated with Mortar 
     * account (used to sync code to backing Mortar github repo)
     * @throws IOException if unable to sync code to Mortar
     */
    public void deployToMortar(String githubUsername, String githubPassword) throws IOException {
        deployToMortar(githubUsername, githubPassword, DEPLOY_TARGET_BRANCH_DEFAULT);
    }
    
    /**
     * Deploy the code in this EmbeddedMortarProject to Mortar onto 
     * a specified branch.
     * 
     * @param githubUsername Username for github user associated with Mortar 
     * account (used to sync code to Mortar's mirror github repo)
     * @param githubPassword Password for github user associated with Mortar 
     * account (used to sync code to Mortar's mirror github repo)
     * @param targetBranch target branch to which deployment should go in 
     * Mortar's mirror github repo
     * @throws IOException
     */
    public void deployToMortar(String githubUsername, String githubPassword, String targetBranch)
            throws IOException {
        deployToMortar(githubUsername, githubPassword, targetBranch, 
                Files.createTempDirectory());
    }
    
    /**
     * Get the URL of the backing git mirror for this EmbeddedMortarProject.
     * 
     * @return https URL of git mirror  
     * @throws IOException if unable to load git mirror URL from .mortar-project-remote 
     */
    public String getGitMirrorURL() throws IOException {
        if (this.gitMirrorURL == null) {
            this.gitMirrorURL = loadGitMirrorURL();
        }
        return this.gitMirrorURL;
    }
    
    /**
     * Get the embedded project manifest file.
     * 
     * @return
     */
    public File getManifestFile() {
        return new File(this.rootPath, MORTAR_PROJECT_MANIFEST_FILENAME);
    }
    
    void deployToMortar(String githubUsername, String githubPassword, 
            String targetBranch, File mirrorPath) throws IOException {
        // validate mirrorPath
        if (!mirrorPath.exists()) {
            // create the directory
            if (!mirrorPath.mkdirs()) {
                throw new IOException("Unable to make directory for mirrorPath at " +
                        mirrorPath);
            }
            
        } else {
            if (!(mirrorPath.isDirectory())) {
                throw new IOException("mirrorPath must be a directory, found a file at " +
                        mirrorPath);
            }
            if (!Files.isEmpty(mirrorPath)) {
                throw new IOException("mirrorPath must be an empty directory, " + 
                        "found contents in " + mirrorPath);
            }
        }
        
        logger.debug("Using temporary mirror path " + mirrorPath);
        
        CredentialsProvider cp = new UsernamePasswordCredentialsProvider(
                githubUsername, githubPassword);
        try {
            Git git = cloneGitMirror(mirrorPath, cp);
            setupGitMirror(git, cp, githubUsername);
            syncEmbeddedProjectWithMirror(git, cp, targetBranch, githubUsername);
            syncProjectMirrorWithMortarGit(git, cp, targetBranch);
        } catch (GitAPIException e) {
            throw new IOException("Error processing git command", e);
        }
    }
    
    
    Git cloneGitMirror(File mirrorPath, CredentialsProvider cp) 
            throws GitAPIException, IOException {
        logger.debug("git clone " + getGitMirrorURL());
        return new CloneCommand()
                .setDirectory(mirrorPath)
                .setURI(getGitMirrorURL())
                .setCredentialsProvider(cp)
                .setCloneAllBranches(true)
                .call();
    }
    
    void setupGitMirror(Git gitMirror, CredentialsProvider cp, String committer) 
            throws IOException, GitAPIException {
        
        // checkout master as base branch
        this.gitUtil.checkout(gitMirror, "master");
        
        // touch .gitkeep to ensure we have something in the repo
        File gitKeep = new File(gitMirror.getRepository().getWorkTree(), ".gitkeep");
        logger.debug("Touching " + gitKeep);
        Files.touch(gitKeep);

        // add it
        logger.debug("git add .");
        gitMirror.add().addFilepattern(".").call();

        // commit it
        logger.debug("git commit");
        gitMirror.commit()
            .setCommitter(committer, committer)
            .setMessage("mortar development initial commit")
            .call();

        // push it
        logger.info("Pushing initialization commit to mortar github mirror repo " +
                getGitMirrorURL());
        gitMirror.push()
            .setRemote(getGitMirrorURL())
            .setCredentialsProvider(cp)
            .setRefSpecs(new RefSpec("master"))
            .call();
    }
    
    void syncEmbeddedProjectWithMirror(Git gitMirror, CredentialsProvider cp, 
            String targetBranch, String committer) 
            throws GitAPIException, IOException {
        
        // checkout the target branch
        gitUtil.checkout(gitMirror, targetBranch);
        
        // clear out the mirror directory contents (except .git and .gitkeep)
        File localBackingGitRepoPath = gitMirror.getRepository().getWorkTree();
        for (File f : 
             FileUtils.listFilesAndDirs(
                localBackingGitRepoPath, 
                FileFilterUtils.notFileFilter(FileFilterUtils.nameFileFilter(".gitkeep")),
                FileFilterUtils.notFileFilter(FileFilterUtils.nameFileFilter(".git")))) {
            if (!f.equals(localBackingGitRepoPath)) {
                logger.debug("Deleting existing mirror file" + f.getAbsolutePath());
                FileUtils.deleteQuietly(f);
            }
        }
        
        // copy everything from the embedded project
        List<File> manifestFiles = getFilesAndDirsInManifest();
        for (File fileToCopy : manifestFiles) {
            if (!fileToCopy.exists()) {
                logger.warn("Can't find file or directory " + 
                    fileToCopy.getCanonicalPath() + " referenced in manifest file.  Ignoring.");
            } else if (fileToCopy.isDirectory()) {
                FileUtils.copyDirectoryToDirectory(fileToCopy, localBackingGitRepoPath);
            } else {
                FileUtils.copyFileToDirectory(fileToCopy, localBackingGitRepoPath);
            }
        }
        
        // add everything
        logger.debug("git add .");
        gitMirror.add()
                .addFilepattern(".")
                .call();
        
        // remove missing files (deletes)
        logger.debug("git add -u .");
        gitMirror.add()
                .setUpdate(true)
                .addFilepattern(".")
                .call();
        
        // commit it
        logger.debug("git commit");
        gitMirror.commit()
            .setCommitter(committer, committer)
            .setMessage("mortar development snapshot commit")
            .call();
    }
    
    void syncProjectMirrorWithMortarGit(Git localBackingGitRepo, CredentialsProvider cp, 
            String targetBranch) throws GitAPIException, IOException {
        // push
        logger.info("Pushing updated code to Mortar github mirror repo " + getGitMirrorURL() 
                + " on target branch " + targetBranch);
        localBackingGitRepo.push()
            .setRemote(getGitMirrorURL())
            .setCredentialsProvider(cp)
            .setRefSpecs(new RefSpec(targetBranch))
            .call();
    }
   
    String loadGitMirrorURL() throws IOException {
        if (!((this.rootPath != null) && this.rootPath.exists() && this.rootPath.isDirectory())) {
            throw new IOException("Project root path must be an existing directory.  Got: " + 
                    this.rootPath);
        }
        
        File mortarProjectRemoteFile = new File(this.rootPath, MORTAR_PROJECT_REMOTE_FILENAME);
        if (!mortarProjectRemoteFile.exists()) {
            throw new IOException("A " + MORTAR_PROJECT_REMOTE_FILENAME + 
                    " file must exist under the " + 
                    "project root. No file found at " + mortarProjectRemoteFile);
        }
        
        String mortarProjectRemoteGitData = 
                FileUtils.readFileToString(mortarProjectRemoteFile, "UTF-8");
        return gitUtil.convertGithubSshURLToHttpsURL(mortarProjectRemoteGitData.trim());
        
    }
    
    /**
     * Get the files and directories referenced in the project manifest file.
     * 
     * @return List<File> of files and directories
     * @throws IOException
     */
    List<File> getFilesAndDirsInManifest() throws IOException {
        
        File manifestFile = getManifestFile();
        if (!manifestFile.exists()) {
            throw new IOException("Unable to find mortar project manifest file " +
                    "in embedded project; expected it at " + 
                    manifestFile.getCanonicalPath() + 
                    ". Please create a newline-separated list of directories to sync there.");
        }
        
        List<File> manifestFilesAndDirs = new ArrayList<File>();
        for (String filename : FileUtils.readLines(manifestFile, "UTF-8")) {
            if (filename.trim().length() > 0) {
                manifestFilesAndDirs.add(new File(this.rootPath, filename.trim()));
            }
        }
        
        return manifestFilesAndDirs;
    }

    @Override
    public String toString() {
        return "EmbeddedMortarProject [rootPath=" + rootPath + "]";
    }

}

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
package com.mortardata.git;

import java.util.List;

import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilities for accessing git (via jgit).
 */
public class GitUtil {
    
    final Logger logger = LoggerFactory.getLogger(GitUtil.class);
    
    /**
     * Get all branches in a git repo.
     * 
     * @param git Git repository
     * 
     * @return List<Ref> all branches in the repo
     * @throws GitAPIException if error running git command
     */
    public List<Ref> getBranches(Git git) throws GitAPIException {
        return git.branchList()
                .setListMode(ListMode.ALL)
                .call();
    }

    /**
     * Checkout a branch, tracking remote branch if it exists.
     *  
     * @param git Git repository
     * @param branch Name of branch to checkout
     * @throws GitAPIException if error running git command
     */
    public void checkout(Git git, String branch) throws GitAPIException {
        List<Ref> branches = getBranches(git);
        
        CheckoutCommand checkout = 
                git.checkout()
                    .setName(branch);
        
        String localBranchRefName = "refs/heads/" + branch;
        String remoteBranchName = "origin/" + branch;
        String remoteBranchRefName = "refs/remotes/" + remoteBranchName;
        if (containsRef(branches, localBranchRefName)) {
            logger.debug("git checkout " + branch);
            // local branch exists: no create branch
            checkout
                .setCreateBranch(false);
        } else if (containsRef(branches, remoteBranchRefName)) {
            logger.debug("git checkout --set-upstream -b " + branch + " " + remoteBranchName);
            // remote branch exists: track existing branch
            checkout
                .setCreateBranch(true)
                .setUpstreamMode(SetupUpstreamMode.SET_UPSTREAM)
                .setStartPoint(remoteBranchName);
        } else {
            // no remote branch exists: create a new branch, no tracking
            logger.debug("git checkout -b " + branch);
            checkout
                .setCreateBranch(true);
        }
        
        checkout.call();
    }

    /**
     * Convert an SSH-style github URL to an https-style URL.
     * 
     * @param githubSSHURL URL in SSH format (e.g. git@github.com:mortardata/mortar.git)
     * @return URL converted to HTTPS format (e.g. https://github.com/mortardata/mortar.git)
     */
    public String convertGithubSshURLToHttpsURL(String githubSSHURL) {
        // "git@github.com:organization/repo.git"
        // to
        // "https://github.com/organization/repo.git"
        String repo = githubSSHURL.substring(
                        githubSSHURL.indexOf(":") + 1);
        return "https://github.com/" + repo;
    }

    boolean containsRef(List<Ref> refs, String name) {
        for (Ref ref : refs) {
            if ((ref.getName() != null) && 
                 ref.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

}

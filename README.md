# Mortar API for Java

[Mortar](http://www.mortardata.com/) is a platform as a service for Hadoop. With Mortar, you can run jobs on Hadoop using Apache Pig and Python without any special training.

The Mortar API for Java lets you interact with the [Mortar API](http://help.mortardata.com/reference/api/api_version_2), running jobs and querying their status.  It also lets you deploy Mortar project code to the Mortar service.

[![Build Status](https://travis-ci.org/mortardata/mortar-api-java.png?branch=master)](https://travis-ci.org/mortardata/mortar-api-java)

## Installation

You can install mortar-api-java from maven via:

```xml
<dependency>
  <groupId>com.mortardata.api</groupId>
  <artifactId>mortar-api-java</artifactId>
  <version>0.1</version>
</dependency>
```

## Deploying Embedded Mortar Projects

[Embedded Mortar Projects](http://help.mortardata.com/reference/mortar_project_reference/using_your_own_source_control) allow you to keep your Mortar Project within your own source control repository.  You can use any type of repository you like--git, svn, or others.

Behind the scenes, Mortar provides a mirrored, private git repository stored at github.  mortar-api-java lets you deploy your code to the mirror repository with one command.

When you're ready to deploy your Embedded Mortar Project to Mortar, first ensure that you've checked out the code locally at the version you'd like to deploy.  Then, run:

```java
import java.io.File;

import com.mortardata.project.EmbeddedMortarProject;

// ...

// local path where my embedded mortar project lives
File pathToEmbeddedMortarProject = new File("/home/me/path/to/embedded/mortar/project");

// github account associated with my Mortar account (to sync to backing github repo)
String githubUsername = "myGithubUsername";
String githubPassword = "myGithubPassword";

// deploy embedded mortar project to Mortar (on branch master)
EmbeddedMortarProject project = new EmbeddedMortarProject(pathToEmbeddedMortarProject);
String gitHash = project.deployToMortar(githubUsername, githubPassword);
```

## Running a Job

To run a Mortar job with the API, do:

```java
import com.mortardata.api.v2.API;
import com.mortardata.api.v2.Jobs;

// ...

// credentials
String email = "my-mortar-account-email@mydomain.com";
String apiKey = "my-mortar-api-key";

// project to run
String projectName = "my-project-name";

// pigscript or controlscript to run
String scriptName = "pigscripts/my-pigscript.pig";

// branch or commit hash I want to run
String codeVersion = gitHash;

// cluster size in number of nodes
Integer clusterSize = 2;

// run the job
Jobs jobs = new Jobs(new API(email, apiKey));
JobRequest jobRequest = new JobRequest(projectName, scriptName, codeVersion, clusterSize);
String jobId = jobs.postJob(jobRequest);

// wait for job completion
JobStatus finalJobStatus = jobs.blockUntilJobComplete(jobId);
```

## Javadoc

[Javadoc documentation](http://mortardata.github.io/mortar-api-java) is available on github-pages.

/*
 * Copyright (c) 2014 Kyle Lieber
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.github.klieber.phantomjs.download;

import com.github.klieber.phantomjs.archive.PhantomJSArchive;
import com.github.klieber.phantomjs.locate.RepositoryDetails;
import com.github.klieber.phantomjs.util.ArtifactBuilder;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class RepositoryDownloader implements Downloader {

  private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryDownloader.class);

  private final static String UNABLE_TO_RESOLVE = "Unable to resolve artifact.";

  private final static String RESOLVED_ARTIFACT = "Resolved artifact {} from {}";

  private final ArtifactBuilder artifactBuilder;
  private final RepositoryDetails repositoryDetails;

  public RepositoryDownloader(ArtifactBuilder artifactBuilder,
                              RepositoryDetails repositoryDetails) {
    this.artifactBuilder = artifactBuilder;
    this.repositoryDetails = repositoryDetails;
  }

  @Override
  public File download(PhantomJSArchive archive) throws DownloadException {
    ArtifactRequest request = new ArtifactRequest();
    request.setArtifact(artifactBuilder.createArtifact(archive));
    request.setRepositories(repositoryDetails.getRemoteRepositories());

    try {
      ArtifactResult result = repositoryDetails.getRepositorySystem().resolveArtifact(
          repositoryDetails.getRepositorySystemSession(),
          request);
      LOGGER.info(RESOLVED_ARTIFACT, result.getArtifact().getFile(), result.getRepository());
      return result.getArtifact().getFile();
    } catch(ArtifactResolutionException e) {
      throw new DownloadException(UNABLE_TO_RESOLVE, e);
    }
  }
}

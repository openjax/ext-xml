/* Copyright (c) 2008 FastJAX
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * You should have received a copy of The MIT License (MIT) along with this
 * program. If not, see <http://opensource.org/licenses/MIT/>.
 */

package org.fastjax.maven.plugin.xml;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashSet;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.fastjax.io.Files;
import org.fastjax.net.URLs;
import org.fastjax.util.Dates;
import org.fastjax.xml.OfflineValidationException;
import org.fastjax.xml.sax.Validator;
import org.xml.sax.SAXException;

@Mojo(name="validate", defaultPhase=LifecyclePhase.COMPILE)
@Execute(goal="validate")
public final class ValidatorMojo extends XmlMojo {
  @Override
  public void executeMojo(final LinkedHashSet<URL> urls) throws MojoExecutionException, MojoFailureException {
    final File recordDir = new File(directory, "validator");
    recordDir.mkdirs();

    try {
      for (final URL url : urls) {
        final File recordFile = new File(recordDir, URLs.getName(url));
        final String filePath = URLs.isFile(url) ? Files.relativePath(CWD.getAbsoluteFile(), new File(url.getFile()).getAbsoluteFile()) : url.toExternalForm();
        long lastModified = -1;
        if (recordFile.exists() && recordFile.lastModified() >= (lastModified = URLs.getLastModified(url)) && recordFile.lastModified() < lastModified + Dates.MILLISECONDS_IN_DAY) {
          getLog().info("Pre-validated: " + filePath);
        }
        else {
          try {
            getLog().info("   Validating: " + filePath);

            Validator.validate(url, offline);
            if (!recordFile.createNewFile())
              recordFile.setLastModified(lastModified != -1 ? lastModified : URLs.getLastModified(url));
          }
          catch (final OfflineValidationException e) {
            if (!offline)
              throw e;
          }
          catch (final SAXException e) {
            final StringBuilder builder = new StringBuilder("\nURL: " + url.toExternalForm() + "\nReason: " + e.getMessage() + "\n");
            for (final Throwable t : e.getSuppressed())
              builder.append("        ").append(t.getMessage()).append("\n");

            final MojoFailureException exception = new MojoFailureException("Failed to validate xml.", "", builder.toString());
            exception.initCause(e);
            throw exception;
          }
        }
      }
    }
    catch (final OfflineValidationException e) {
      throw new MojoFailureException(e.getMessage(), e);
    }
    catch (final IOException e) {
      throw new MojoExecutionException(e.getMessage(), e);
    }
  }
}
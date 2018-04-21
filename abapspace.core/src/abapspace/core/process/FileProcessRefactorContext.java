/*
 * MIT License
 *
 * Copyright (c) 2018 mnemotron
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package abapspace.core.process;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.logging.log4j.Logger;

import abapspace.core.context.InterfaceContext;
import abapspace.core.exception.FileProcessException;
import abapspace.core.exception.TargetDirectoryNotCreatedException;
import abapspace.core.exception.TargetFileContentNotWrittenException;

public class FileProcessRefactorContext implements InterfaceFileProcess {

    private Map<String, Map<String, InterfaceContext>> contextMap;
    private Logger log;
    private ResourceBundle messages;
    private File targetDir;
    private File sourceDir;

    public FileProcessRefactorContext(Logger log, ResourceBundle messages, File sourceDir, File targetDir,
	    Map<String, Map<String, InterfaceContext>> contextMap) {
	this.log = log;
	this.messages = messages;
	this.contextMap = contextMap;
	this.targetDir = targetDir;
	this.sourceDir = sourceDir;
    }

    @Override
    public void processFile(File sourceFile, StringBuffer contextBuffer) throws FileProcessException {

	String locContext = contextBuffer.toString();

	Map<String, InterfaceContext> locIContextMap = this.contextMap.get(sourceFile.getAbsolutePath());

	Iterator<Map.Entry<String, InterfaceContext>> locICMIterator = locIContextMap.entrySet().iterator();

	this.log.info(
		MessageFormat.format(this.messages.getString("refactor.file.source"), sourceFile.getAbsolutePath()));

	while (locICMIterator.hasNext()) {
	    Map.Entry<String, InterfaceContext> locV = locICMIterator.next();

	    this.log.info(MessageFormat.format(this.messages.getString("refactor.object"),
		    locV.getValue().getObject(), locV.getValue().getReplacement()));

	    locContext = locContext.replaceAll(locV.getValue().getObject(), locV.getValue().getReplacement());
	}

	try {
	    this.saveTargetFile(this.sourceDir, sourceFile, this.targetDir, locContext);
	} catch (TargetDirectoryNotCreatedException | TargetFileContentNotWrittenException e) {
	    throw new FileProcessException(e.getMessage(), e);
	}
    }

    private void saveTargetFile(File sourceDir, File sourceFile, File targetDir, String context)
	    throws TargetDirectoryNotCreatedException, TargetFileContentNotWrittenException {

	BufferedWriter locBW = null;
	File locTargetFile = null;
	String locTargetPath = sourceFile.getAbsolutePath();

	locTargetPath = locTargetPath.replaceAll(sourceDir.getAbsolutePath(), targetDir.getAbsolutePath());

	locTargetFile = new File(locTargetPath);

	this.log.info(
		MessageFormat.format(this.messages.getString("refactor.file.target"), locTargetFile.getAbsolutePath()));

	try {
	    Files.createDirectories(locTargetFile.toPath().getParent());
	} catch (IOException e) {
	    throw new TargetDirectoryNotCreatedException(
		    this.messages.getString("TargetDirectoryNotCreatedException") + locTargetFile.toPath().getParent(),
		    e);
	}

	try {
	    locBW = new BufferedWriter(new FileWriter(locTargetFile));
	    locBW.write(context);
	} catch (IOException e) {
	    throw new TargetFileContentNotWrittenException(
		    this.messages.getString("TargetFileContentNotWrittenException") + locTargetFile.getAbsolutePath(),
		    e);
	} finally {
	    if (locBW != null) {
		try {
		    locBW.close();
		} catch (IOException e) {
		    this.log.error(this.messages.getString("FileProcessRefactorContext_BW_NotClosed"), e);
		}
	    }
	}

    }
}

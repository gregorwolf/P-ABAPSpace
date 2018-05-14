package abapspace.core.context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import abapspace.core.exception.FileProcessException;
import abapspace.core.exception.SourceDirectoryNotFoundException;
import abapspace.core.exception.TargetDirectoryNotCreatedException;
import abapspace.core.exception.TargetDirectoryNotFoundException;
import abapspace.core.exception.TargetFileContentNotWrittenException;
import abapspace.core.log.LogEventManager;
import abapspace.core.log.LogType;
import abapspace.core.messages.MessageManager;
import abapspace.core.process.InterfaceFileProcess;

public class ContextFile extends File implements InterfaceFileProcess {

    private static final long serialVersionUID = 6780539753747307909L;

    private ContextManager contextManager;
    private Map<String, InterfaceContext> contextMap;

    public ContextFile(String pathname, ContextManager contextManager) {
	super(pathname);
	this.contextManager = contextManager;
	this.contextMap = new HashMap<String, InterfaceContext>();
    }

    @Override
    public void collectContext() throws FileProcessException {

	LogEventManager.fireLog(LogType.INFO,
		MessageManager.getMessage("collect.context.file") + this.getAbsolutePath());

	// file name
	// TODO file name collect

	// file content
	try {
	    StringBuffer locSB = this.getContextBuffer();

	    this.contextMap = processFileSearch(locSB.toString(), this.contextMap,
		    this.contextManager.getContextList());
	} catch (CloneNotSupportedException e) {
	    throw new FileProcessException(
		    MessageManager.getMessage("exception.fileProcessCollectContext.cloneNotSupported"), e);
	} catch (IOException e) {
	    throw new FileProcessException(MessageManager.getMessageFormat(
		    "exception.fileProcessCollectContext.FileNotReachable" + this.getAbsolutePath()), e);
	}
    }

    @Override
    public void refactorContext() throws FileProcessException {

	LogEventManager.fireLog(LogType.INFO,
		MessageFormat.format(MessageManager.getMessage("refactor.file.source"), this.getAbsolutePath()));

	try {
	    StringBuffer locSB = this.getContextBuffer();
	    String locContext = locSB.toString();

	    Iterator<Map.Entry<String, InterfaceContext>> locICMIterator = this.contextMap.entrySet().iterator();

	    while (locICMIterator.hasNext()) {
		Map.Entry<String, InterfaceContext> locV = locICMIterator.next();

		LogEventManager.fireLog(LogType.INFO, MessageManager.getMessageFormat("refactor.object",
			locV.getValue().getObject(), locV.getValue().getReplacement()));

		locContext = locContext.replaceAll(locV.getValue().getObject(), locV.getValue().getReplacement());
	    }

	    this.saveTargetFile(this.contextManager.getPreset().getFileSourceDir(), this,
		    this.contextManager.getPreset().getFileTargetDir(), locContext);

	} catch (IOException e) {
	    throw new FileProcessException(MessageManager.getMessageFormat(
		    "exception.fileProcessRefactorContext.fileNotReachable" + this.getAbsolutePath()), e);
	} catch (TargetDirectoryNotCreatedException e) {
	    throw new FileProcessException(e.getMessage(), e);
	} catch (TargetFileContentNotWrittenException e) {
	    throw new FileProcessException(e.getMessage(), e);
	} catch (SourceDirectoryNotFoundException e) {
	    throw new FileProcessException(e.getMessage(), e);
	} catch (TargetDirectoryNotFoundException e) {
	    throw new FileProcessException(e.getMessage(), e);
	}
    }

    private Map<String, InterfaceContext> processFileSearch(String fileContextString,
	    Map<String, InterfaceContext> iContextMap, List<InterfaceContext> contextList)
	    throws CloneNotSupportedException {

	for (InterfaceContext iContext : contextList) {

	    for (Matcher m = Pattern.compile(iContext.getRegex(), Pattern.CASE_INSENSITIVE)
		    .matcher(fileContextString); m.find();) {

		String locGroup1 = m.group(1); // group 1: namespace + object ID
		String locGroup2 = m.group(2); // group 2: object name
		String locObject = locGroup1 + locGroup2;

		InterfaceContext locIContext = iContext.clone();

		LogEventManager.fireLog(LogType.INFO,
			MessageManager.getMessageFormat("collect.context.object", locObject, m.start(), m.end()));

		if (iContextMap.containsKey(locObject)) {
		    continue;
		}

		locIContext.setObject(new String[] { locGroup1, locGroup2 });

		iContextMap.put(locObject, locIContext);
	    }

	}

	return iContextMap;
    }

    private StringBuffer getContextBuffer() throws IOException {

	FileReader locFR = null;
	BufferedReader locBR = null;
	StringBuffer locSB = new StringBuffer();
	int locInt;

	try {
	    locFR = new FileReader(this);
	    locBR = new BufferedReader(locFR);

	    while ((locInt = locBR.read()) != -1) {
		locSB.append((char) locInt);

	    }
	} finally {

	    if (locBR != null) {
		locBR.close();
	    }

	    if (locFR != null) {
		locFR.close();
	    }
	}

	return locSB;
    }

    private void saveTargetFile(File sourceDir, File sourceFile, File targetDir, String context)
	    throws TargetDirectoryNotCreatedException, TargetFileContentNotWrittenException {

	BufferedWriter locBW = null;
	File locTargetFile = null;
	String locTargetPath = sourceFile.getAbsolutePath();

	locTargetPath = locTargetPath.replaceAll(sourceDir.getAbsolutePath(), targetDir.getAbsolutePath());

	locTargetFile = new File(locTargetPath);

	LogEventManager.fireLog(LogType.INFO,
		MessageManager.getMessageFormat("refactor.file.target", locTargetFile.getAbsolutePath()));

	try {
	    Files.createDirectories(locTargetFile.toPath().getParent());
	} catch (IOException e) {
	    throw new TargetDirectoryNotCreatedException(MessageManager.getMessage("TargetDirectoryNotCreatedException")
		    + locTargetFile.toPath().getParent(), e);
	}

	try {
	    locBW = new BufferedWriter(new FileWriter(locTargetFile));
	    locBW.write(context);
	} catch (IOException e) {
	    throw new TargetFileContentNotWrittenException(
		    MessageManager.getMessage("TargetFileContentNotWrittenException") + locTargetFile.getAbsolutePath(),
		    e);
	} finally {
	    if (locBW != null) {
		try {
		    locBW.close();
		} catch (IOException e) {
		    LogEventManager.fireLog(LogType.ERROR,
			    MessageManager.getMessage("FileProcessRefactorContext_BW_NotClosed"), e);
		}
	    }
	}
    }

    @Override
    public boolean checkMaxNameLength() {

	final boolean[] locValid = new boolean[] { true };

	this.contextMap.forEach((objectIdent, iContext) -> {
	    ContextCheckMaxNameLength locCheck = iContext.checkMaxNameLengthForReplacement();

	    if (!locCheck.isValid()) {
		LogEventManager.fireLog(LogType.ERROR,
			MessageManager.getMessageFormat("check.maxNameLength", iContext.getObject(),
				iContext.getReplacement(), locCheck.getMaxNameLength(),
				locCheck.getActualNameLength()));
		locValid[0] = false;
	    }
	});

	return locValid[0];
    }

    @Override
    public Map<String, InterfaceContext> getContextMap() {
	return this.contextMap;
    }

    @Override
    public void setContextMap(Map<String, InterfaceContext> contextMap) {

	contextMap.forEach((objectIdent, iContext) -> {
	    if (this.contextMap.containsKey(objectIdent)) {
		this.contextMap.get(objectIdent).setReplacement(iContext.getReplacement());
	    }
	});

    }

}

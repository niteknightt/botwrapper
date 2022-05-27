package niteknightt.common;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class UciIoLogger {

    public static String fileNameStartText = "ucilog_";
    public static String fileNameExtension = ".log";
    public static String fileNameFull = ".log";

    public static final String INPUT_MARKER = "\"in\"";
    public static final String OUTPUT_MARKER = "\"out\"";
    public static final char DELIMITER = ';';
    public static final char NEWLINE = '\n';
    public static final char DOUBLEQUOTE = '"';
    public static final String REPLACEMENT_TEXT_FOR_NEWLINE = "[newline]";
    
    protected BufferedWriter _fileWriter;
    protected long _currentLogId;
    protected long _currentInputLogId;
    protected long _currentIsReadyLogId;

    public UciIoLogger() {
        init();
    }

    protected void init() {
        fileNameFull = new StringBuilder()
            .append(Common.RESOURCE_PATH)
            .append(fileNameStartText)
            .append(Helpers.formatDateForFilename(new Date()))
            .append(fileNameExtension)
            .toString();
        try {
            _fileWriter = new BufferedWriter(new FileWriter(fileNameFull));
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to open uci log file for writing: " + fileNameFull);
        }

        _currentLogId = Helpers.getNextLogId();
        _currentInputLogId = 0;
    }

    protected void write(String gameId, String text, String ioMarker, long referenceLogId) {
        StringBuilder sb = new StringBuilder(text.trim());
        for (int i = sb.length() - 1; i >= 0; --i) {
            if (sb.charAt(i) == NEWLINE) {
                sb.deleteCharAt(i);
                if (i != sb.length() && i != 0) {
                    sb.insert(i, REPLACEMENT_TEXT_FOR_NEWLINE);
                }
            }
        }
        if (sb.length() == 0) {
            return;
        }

        StringBuilder lineSb = new StringBuilder()
            .append(Helpers.formatDateForLog(new Date()))
            .append(DELIMITER)
            .append(_currentLogId)
            .append(DELIMITER)
            .append(referenceLogId)
            .append(DELIMITER)
            .append(DOUBLEQUOTE)
            .append(gameId)
            .append(DOUBLEQUOTE)
            .append(DELIMITER)
            .append(ioMarker)
            .append(DELIMITER)
            .append(DOUBLEQUOTE)
            .append(sb.toString())
            .append(DOUBLEQUOTE)
            .append(NEWLINE);

        try {
            _fileWriter.write(lineSb.toString());
            _fileWriter.flush();
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to write to uci log file: " + fileNameFull);
        }

        ++_currentLogId;
    }

    public void writeInput(String gameId, String text) {
        if (text.equals("isready")) {
            _currentIsReadyLogId = _currentLogId;
        }
        else {
            _currentInputLogId = _currentLogId;
        }
        write(gameId, text, INPUT_MARKER, 0);
    }

    public void writeOutput(String gameId, String text) {
        long referenceId = 0;
        if (text.equals("readyok")) {
            referenceId = _currentIsReadyLogId;
        }
        else {
            referenceId = _currentInputLogId;
        }
        write(gameId, text, OUTPUT_MARKER, referenceId);
    }

    public void close() {
        try {
            _fileWriter.close();
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to close uci log file: " + fileNameFull);
        }
    }
}

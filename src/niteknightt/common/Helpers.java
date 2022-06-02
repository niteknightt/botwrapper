package niteknightt.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import niteknightt.gameplay.Enums;

public class Helpers {
    
    private static long lastLogId;

    public static long LOG_ID_INCREMENT = 100000l;
    public static String LAST_LOG_ID_FILE_NAME = "lastLogId.txt";
    public static SimpleDateFormat formatForFilename = new SimpleDateFormat("yyyyMMddHHmmss");
    public static SimpleDateFormat formatForLog = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public static void initLog() {
        loadLastLogId();
    }

    private static void loadLastLogId() {
        String fileNameFull = new StringBuilder()
        .append(Common.RESOURCE_PATH)
        .append(LAST_LOG_ID_FILE_NAME)
        .toString();
        
        File f = new File(fileNameFull);
        if(!f.exists() || f.isDirectory()) {
            lastLogId = 1;
            saveLastLogId();
            return;
        }

        try {
            var fileReader = new BufferedReader(new FileReader(fileNameFull));
            lastLogId = Long.parseLong(fileReader.readLine());
            fileReader.close();
        }
        catch (IOException e) {
            throw new RuntimeException("Error while reading last log ID: " + fileNameFull);
        }
    }

    private static void saveLastLogId() {
        String fileNameFull = new StringBuilder()
        .append(Common.RESOURCE_PATH)
        .append(LAST_LOG_ID_FILE_NAME)
        .toString();

        try {
            var fileWriter = new BufferedWriter(new FileWriter(fileNameFull));
            fileWriter.write(Long.toString(lastLogId));
            fileWriter.close();
        }
        catch (IOException e) {
            throw new RuntimeException("Error while reading last log ID: " + fileNameFull);
        }
    }

    public static String formatDateForFilename(Date date) {
        return formatForFilename.format(date); 
    }
    
    public static String formatDateForLog(Date date) {
        return formatForLog.format(date); 
    }

    public static long getNextLogId() {
        lastLogId += LOG_ID_INCREMENT;
        saveLastLogId();
        return lastLogId;
    }

    public static Enums.PieceType letterToPieceType(char letter) {
        switch (letter) {
            case 'N':
                return Enums.PieceType.KNIGHT;
            case 'B':
                return Enums.PieceType.BISHOP;
            case 'R':
                return Enums.PieceType.ROOK;
            case 'Q':
                return Enums.PieceType.QUEEN;
            case 'K':
                return Enums.PieceType.KING;
            default:
                throw new RuntimeException("Failed to find piece type for letter " + letter);
        }
    }
}

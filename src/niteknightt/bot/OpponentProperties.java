package niteknightt.bot;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import com.google.gson.Gson;

import niteknightt.gameplay.Enums;

public class OpponentProperties {
    public String id;
    public Enums.EngineAlgorithm algorithm;

    public static String RESOURCE_FILE_PATH = "resources/";
    public static String OPPONENT_PROPS_FILE_NAME = RESOURCE_FILE_PATH + "opponents.json";

    public static List<OpponentProperties> _allProps;

    public static List<OpponentProperties> getAllProps() {
        if (_allProps == null) {
            _loadAllProps();
        }
        return _allProps;
    }

    public static OpponentProperties getForHuman(String humanId) {
        if (_allProps == null) {
            _loadAllProps();
        }

        for (int i = 0; i < _allProps.size(); ++i) {
            OpponentProperties props = _allProps.get(i);
            if (props.id.equals(humanId)) {
                return props;
            }
        }
        return null;
    }

    public static void createOrUpdateAlgorithmForHuman(String humanId, Enums.EngineAlgorithm algorithm) {
        OpponentProperties props = new OpponentProperties();
        props.id = humanId;
        props.algorithm = algorithm;
        createOrUpdateProperties(props);
    }

    public static void createOrUpdateProperties(OpponentProperties newprops) {
        for (int i = 0; i < _allProps.size(); ++i) {
            OpponentProperties props = _allProps.get(i);
            if (props.id.equals(newprops.id)) {
                props.algorithm = newprops.algorithm;
                _saveAllProps();
                return;
            }
        }

        _allProps.add(newprops);
        _saveAllProps();
    }

    protected static void _loadAllProps() {
        Gson gson = new Gson();
        try {
            Reader reader = Files.newBufferedReader(Paths.get(OPPONENT_PROPS_FILE_NAME));
            _allProps = new LinkedList<OpponentProperties>(Arrays.asList(gson.fromJson(reader, OpponentProperties[].class)));
            reader.close();
        }
        catch (IOException e) {
            Logger.error("Exception while reading " + OPPONENT_PROPS_FILE_NAME + ": " + e.toString());
            _allProps = new ArrayList<OpponentProperties>();
            _saveAllProps();
        }
    }

    protected static void _saveAllProps() {
        Gson gson = new Gson();
        try {
            Writer writer = Files.newBufferedWriter(Paths.get(OPPONENT_PROPS_FILE_NAME));
            gson.toJson(_allProps, writer);
            writer.close();
        }
        catch (IOException e) {
            Logger.error("Exception while writing " + OPPONENT_PROPS_FILE_NAME + ": " + e.toString());
        }
    }
}

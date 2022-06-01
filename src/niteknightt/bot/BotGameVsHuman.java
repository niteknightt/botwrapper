package niteknightt.bot;

import niteknightt.lichessapi.LichessApiException;
import niteknightt.lichessapi.LichessChallenge;
import niteknightt.lichessapi.LichessInterface;
import niteknightt.gameplay.Enums;

public class BotGameVsHuman extends BotGame {
    
    public static Enums.EngineAlgorithm DEFAULT_ALGORITHM = Enums.EngineAlgorithm.BEST_MOVE;

    public BotGameVsHuman(LichessChallenge challenge) {
        super(challenge);
    }

    @Override
    protected void _performPregameTasks() {
        _setAlgorithmFromChallengerProps();
        _writeWelcomToChallenger();
    }

    /**
     * Checks if there is a saved algorithm for this challenger.
     * If there is, and it is not NONE, use it for the engine.
     * If it is NONE, use the default algorithm for the engine.
     * If there are no properties for this challenger, create properties
     * and set the algorithm in the properties to NONE while using the
     * default algorithm for the engine.
     */
    protected void _setAlgorithmFromChallengerProps() {
        OpponentProperties props = OpponentProperties.getForOpponent(_challenge.challenger.id);
        if (props != null && props.algorithm != Enums.EngineAlgorithm.NONE) {
            _setAlgorithm(props.algorithm);
        }
        else {
            _setAlgorithm(DEFAULT_ALGORITHM);

            if (props == null) {
                OpponentProperties.createOrUpdateAlgorithmForOpponent(_challenge.challenger.id, Enums.EngineAlgorithm.NONE);
            }
        }
    }

    protected void _writeWelcomToChallenger() {
        OpponentProperties props = OpponentProperties.getForOpponent(_challenge.challenger.id);
        try {
            if (props != null && props.algorithm != Enums.EngineAlgorithm.NONE) {
                LichessInterface.writeChat(_gameId, "Welcome back " + _challenge.challenger.id + "! I will be using algorithm " + _algorithm + " since that is what you have set up already.");
            }
            else {
                if (props == null) {
                    LichessInterface.writeChat(_gameId, "Welcome " + _challenge.challenger.id + "! Since you have never played me before, I will be using algorithm " + _algorithm + " which is my default algorithm.");
                }
                else {
                    LichessInterface.writeChat(_gameId, "Welcome back " + _challenge.challenger.id + "! I will be using algorithm " + _algorithm + ", my default algorithm, since you haven't set up an algorithm yet.");
                }
            }
        }
        catch (LichessApiException e) { }
    }

    @Override
    protected void _performPostmoveTasks() {
        if (_algorithm == Enums.EngineAlgorithm.INSTRUCTIVE) {
            if (!Instructor.reviewLastHumanMove(this)) {
                setGameState(Enums.GameState.ERROR);
            }
        }
    }

    /**
     * Handles text received from the challenger in the chat.
     * 
     * @param text the text that the challenger wrote in the chat.
     */
    @Override
    protected void _handleChatFromChallenger(String text) {
        if (text.startsWith("algo")) {
            // Only do this part if the chat line starts with "algo"
            String remainingText = text.substring("algo".length());
            try {
                int algoCode = Integer.parseInt(remainingText.trim());
                if (algoCode < 0 || algoCode > 3) {
                    Logger.info("Invalid algo choice: " + text);
                    LichessInterface.writeChat(_gameId, "Sorry, only algos 0, 1, 2, and 3 are working so far.");
                    return;
                }
                Enums.EngineAlgorithm requestedAlgorithm = Enums.EngineAlgorithm.fromValue(algoCode);
                OpponentProperties.createOrUpdateAlgorithmForOpponent(_challenge.challenger.id, requestedAlgorithm);
                Enums.EngineAlgorithm prevAlgoritm = _algorithm;
                if (requestedAlgorithm == Enums.EngineAlgorithm.NONE) {
                    _setAlgorithm(DEFAULT_ALGORITHM);
                    if (prevAlgoritm != _algorithm) {
                        LichessInterface.writeChat(_gameId, "Switched to the default algorithm, " + DEFAULT_ALGORITHM + ", because the user set algoritm choice to NONE");
                    }
                }
                else {
                    _setAlgorithm(requestedAlgorithm);
                    if (prevAlgoritm != _algorithm) {
                        LichessInterface.writeChat(_gameId, "Switched to the " + _algorithm + " algorithm because the user requested it");
                    }
                }
                Logger.info("User sent valid algo choice: " + text);
            }
            catch (LichessApiException ex) { }
            catch (NumberFormatException e) {
                Logger.info("Failed to parse algo choice: " + text);
                try {
                    LichessInterface.writeChat(_gameId, "Yeah, I couldn't understand that. But we're cool, right?");
                }
                catch (LichessApiException ex2) { }
            }
        }
    }

    @Override
    protected void _performPostgameTasks() {
        // Do nothing.
    }

}

package niteknightt.bot;

import java.util.*;
import niteknightt.gameplay.*;
import niteknightt.lichessapi.*;

public class Game implements Runnable {

    public static Enums.EngineAlgorithm DEFAULT_ALGORITHM_FOR_HUMAN = Enums.EngineAlgorithm.BEST_MOVE;
    public static Enums.EngineAlgorithm DEFAULT_ALGORITHM_FOR_BOT = Enums.EngineAlgorithm.BEST_MOVE;

    protected String _gameId;
    protected Enums.GameState _gameState;
    protected LichessChallenge _challenge;
    protected Enums.Color _engineColor;
    protected Enums.Color _challengerColor;
    protected Board _board;
    protected StockfishClient _stockfishClient = new StockfishClient();
    protected List<Move> _moves = new ArrayList<Move>();
    protected int _numMovesPlayedByChallenger;
    protected int _numMovesPlayedByEngine;
    protected Date _askedForAlgorithmTime = new Date();
    protected Random _random = new Random();
    protected Enums.EngineAlgorithm _algorithm;
    protected List<OpponentProperties> _opponentProerties;
    protected Date _lastGameStateUpdate;
    protected EngineMoveSelector _moveSelector;
    protected boolean _isChallengerHuman;
    protected boolean _isChallengerBot;

    public Game(String gameId, LichessChallenge challenge) {
        _gameId = gameId;
        _challenge = challenge;
        initGame();
    }

    /**
     * Initializes data structures after accepting a challenge from another Lichess user.
     */
    protected void initGame() {
        setGameState(Enums.GameState.CREATED);

        if (_challenge.challenger.title != null && _challenge.challenger.title.equals("BOT")) {
            _isChallengerHuman = false;
        }
        else {
            _isChallengerHuman = true;
        }
        _isChallengerBot = !_isChallengerHuman;

        _stockfishClient.init(5000l);
        _stockfishClient.startGame();

        _board = new Board();
        _board.setupStartingPosition();

        _numMovesPlayedByEngine = 0;
        _numMovesPlayedByChallenger = 0;
    }

    /**
     * Initializes game items after receiving the full game state from Lichess.
     * This should happen once per game after starting up the bot.
     */
    protected void _handleReceivingFullGameState(String whitePlayerId, String blackPlayerId) {
        setAlgorithmFromChallengerProps();
        writeWelcomeForBots();

        if (blackPlayerId.equals("niteknighttbot")) {
            _engineColor = Enums.Color.BLACK;
            _challengerColor = Enums.Color.WHITE;
        }
        else {
            _engineColor = Enums.Color.WHITE;
            _challengerColor = Enums.Color.BLACK;
        }
        setGameState(Enums.GameState.FULL_STATE_RECEIVED);
    }

    /**
     * Handle receiving a challenger's move, and verify that our internal state of the game
     * matches Lichess's state.
     * 
     * @param status the status of the game according to Lichess.
     * @param moves list of all moves played in the game according to Lichess.
     */
    protected void _handleReceivingIncrementalGameState(LichessEnums.GameStatus status, String moves) {

        // Log information about the game state received.
        if (status.equals(LichessEnums.GameStatus.ABORTED) ||
            status.equals(LichessEnums.GameStatus.DRAW) ||
            status.equals(LichessEnums.GameStatus.MATE) ||
            status.equals(LichessEnums.GameStatus.OUT_OF_TIME) ||
            status.equals(LichessEnums.GameStatus.RESIGN) ||
            status.equals(LichessEnums.GameStatus.STALEMATE) ||
            status.equals(LichessEnums.GameStatus.TIMEOUT) ||
            status.equals(LichessEnums.GameStatus.UNKNOWN_FINISH)){
                Logger.info("Received game-ending event -- not doing any moves");
                return;
        }

        // Verify that the moves in the game state match the moves we have recorded.
        List<Move> stateMoves = Move.parseListOfUCIMoves(moves, _board);

        boolean movesEqual = true;
        for (int i = 0; i < _moves.size(); ++i) {
            if (!_moves.get(i).equals(stateMoves.get(i))) {
                movesEqual = false;
            }
        }
        if (!movesEqual) {
            Logger.error("Game state moves do not match board moves");
            Logger.error(Move.printMovesToString("Game state moves", stateMoves));
            Logger.error(Move.printMovesToString("Board moves", _moves));
            setGameState(Enums.GameState.ERROR);
        }

        // Make the challenger's move that is in the game state, if there is one.
        if (_board.whosTurnToGo() == _challengerColor && stateMoves.size() == _moves.size() + 1) {
            Move currentMove = stateMoves.get(stateMoves.size() - 1);
            if (!_board._isMoveLegal(currentMove)) {
                Logger.error("Challenger move is not legal: " + currentMove);
                setGameState(Enums.GameState.ERROR);
            }
            else {
                if (!_board.handleMoveForGame(currentMove)) {
                    Logger.error("Board says current move did not work");
                    setGameState(Enums.GameState.ERROR);
                }
                _moves.add(currentMove);
                ++_numMovesPlayedByChallenger;
            }
        }
    }

    /**
     * Handles text received from the challenger in the chat.
     * 
     * @param text the text that the challenger wrote in the chat.
     */
    protected void _handleChatFromChallenger(String text) {
        if (text.startsWith("algo") && _isChallengerHuman) {
            // Only do this part if the chat line starts with "algo"
            // and if the challenger is an actual human, not a bot.
            String remainingText = text.substring("algo".length());
            try {
                int algoCode = Integer.parseInt(remainingText.trim());
                if (algoCode < 0 || algoCode > 2) {
                    Logger.info("Invalid algo choice: " + text);
                    LichessInterface.writeChat(_gameId, "Sorry, only algos 0, 1, and 2 are working so far.");
                    return;
                }
                Enums.EngineAlgorithm requestedAlgorithm = Enums.EngineAlgorithm.fromValue(algoCode);
                OpponentProperties.createOrUpdateAlgorithmForOpponent(_challenge.challenger.id, requestedAlgorithm);
                Enums.EngineAlgorithm prevAlgoritm = _algorithm;
                if (requestedAlgorithm == Enums.EngineAlgorithm.NONE) {
                    _setAlgorithm(DEFAULT_ALGORITHM_FOR_HUMAN);
                    if (prevAlgoritm != _algorithm) {
                        LichessInterface.writeChat(_gameId, "Switched to the default algorithm, " + DEFAULT_ALGORITHM_FOR_HUMAN + ", because the user set algoritm choice to NONE");
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
            catch (NumberFormatException e) {
                Logger.info("Failed to parse algo choice: " + text);
                LichessInterface.writeChat(_gameId, "Yeah, I couldn't understand that. But we're cool, right?");
            }
        }
    }

    /**
     * Checks if there is a saved algorithm for this challenger.
     * If there is, and it is not NONE, use it for the engine.
     * If it is NONE, use the default algorithm for the engine.
     * If there are no properties for this challenger, create properties
     * and set the algorithm in the properties to NONE while using the
     * default algorithm for the engine.
     */
    protected void setAlgorithmFromChallengerProps() {
        OpponentProperties props = OpponentProperties.getForOpponent(_challenge.challenger.id);
        if (props != null && props.algorithm != Enums.EngineAlgorithm.NONE) {
            _setAlgorithm(props.algorithm);
            if (_isChallengerHuman) {
                LichessInterface.writeChat(_gameId, "Welcome back " + _challenge.challenger.id + "! I will be using algorithm " + _algorithm + " since that is what you have set up already.");
            }
        }
        else {
            if (_isChallengerBot) {
                _setAlgorithm(DEFAULT_ALGORITHM_FOR_BOT);
            }
            else {
                _setAlgorithm(DEFAULT_ALGORITHM_FOR_HUMAN);
            }

            if (props == null) {
                OpponentProperties.createOrUpdateAlgorithmForOpponent(_challenge.challenger.id, Enums.EngineAlgorithm.NONE);
                if (_isChallengerHuman) {
                    LichessInterface.writeChat(_gameId, "Welcome " + _challenge.challenger.id + "! Since you have never played me before, I will be using algorithm " + _algorithm + " which is my default algorithm.");
                }
            }
            else {
                if (_isChallengerHuman) {
                    LichessInterface.writeChat(_gameId, "Welcome back " + _challenge.challenger.id + "! I will be using algorithm " + _algorithm + ", my default algorithm, since you haven't set up an algorithm yet.");
                }
            }
        }
    }

    protected void writeWelcomeForBots() {
        if (_isChallengerHuman) {
            return;
        }
        LichessInterface.writeChat(_gameId, "Hi! You're playing against @niteknighttbot, a bot created by @niteknightt!");
        LichessInterface.writeChat(_gameId, "GLHF!");
    }

    protected void writeGoodbyeForBots() {
        if (_isChallengerHuman) {
            return;
        }
        LichessInterface.writeChat(_gameId, "GG!");
    }

    /**
     * Selects a move for the engine to play, and plays it.
     */
    protected void _playEngineMove() {

        Move engineMove = null;
        
        try {
            engineMove = _moveSelector.selectMove(_board);
        }
        catch (MoveSelectorException e) {
            LichessInterface.writeChat(_gameId, "Sorry dude, I'm quitting this game because I'm not sure how to play right now.");
            LichessInterface.resignGame(_gameId);
            setGameState(Enums.GameState.ABORTED);
            return;
        }

        if (engineMove == null) {
            Logger.info("Ending game internally because no legal moves for engine");
            LichessInterface.writeChat(_gameId, "Looks like I have no legal moves, but Lichess hasn't informed me yet that the game is over. Weird.");
            setGameState(Enums.GameState.ENDED_INTERNALLY);
            return;
        }

        if (!_board.handleMoveForGame(engineMove)) {
            Logger.error("Something went wrong. The computer says the engine's move is not legal. Ending game. Check log.");
            LichessInterface.writeChat(_gameId, "The computer won't let me make the move I want to make. Looks like I have to quit. Sorry.");
            setGameState(Enums.GameState.ERROR);
        }

        _moves.add(engineMove);
        ++_numMovesPlayedByEngine;
        LichessInterface.makeMove(_gameId, engineMove._uciFormat);
    }

    /**
     * Main loop of the game.
     */
    public void run() {

        while (!done()) {

            if (gameState() == Enums.GameState.FULL_STATE_RECEIVED) {
                if (_board.whosTurnToGo() == _engineColor) {
                    _playEngineMove();
                }
            }
            else if (gameState() == Enums.GameState.ERROR) {
                // This state can occur if any unexpected situation is caught by the program.
                // Only the program sets this state.
                Logger.info("Aborting game because gamestate is error");
                LichessInterface.writeChat(_gameId, "Hey guess what? Some sort of error happened in my program and I have to quit the game. Sorry!");
                LichessInterface.resignGame(_gameId);
                setGameState(Enums.GameState.ABORTED);
            }
            else if (gameState() == Enums.GameState.EXTERNAL_FORCED_END) {
                // This might happen if the bot manager decided it has to kill the game.
                Logger.info("Aborting game because got external force end");
                LichessInterface.writeChat(_gameId, "OMG! I've been instructed by the program to quit the gane. Bye for now.");
                LichessInterface.resignGame(_gameId);
                setGameState(Enums.GameState.ABORTED);
            }
            else if (gameState() == Enums.GameState.CREATED ||
                     gameState() == Enums.GameState.STARTED_BY_EVENT ||
                     gameState() == Enums.GameState.ENDED_INTERNALLY) {
                        // These are states that should only be very temporary. So if we are
                        // stuck in one of them, apparently something bad has happened and
                        // we should kill the game.
                        // CREATED -- challenge has been received but GAME_START event not yet received
                        // STARTED_BY_EVENT -- GAME_START event received but full game state data not yet received
                        // ENDED_INTERNALLY -- program detected game end (mate, etc.) but GAME_FINISH event not yet received
                        Date now = new Date();
                        long diffInMillies = Math.abs(now.getTime() - _lastGameStateUpdate.getTime());
                        if (diffInMillies > 15000) {
                            Logger.info("Aborting game because gamestate is CREATED or STARTED_BY_EVENT or ENDED_INTERNALLY for 15 seconds");
                            LichessInterface.writeChat(_gameId, "I zoned out for just a second and now I'm totally confused about this game. I gotta go.");
                            LichessInterface.resignGame(_gameId);
                            setGameState(Enums.GameState.ABORTED);
                        }
            }

            try { Thread.sleep(100); } catch (InterruptedException interruptException) { }
        }
        writeGoodbyeForBots();
    }

    /**
     * Handles receiving a "gameFull" state from the game state stream.
     * @param event the event data of the state.
     */
    public void sendFullGameState(LichessGameFullEvent event) {
        Logger.info("Received full game state");
        Logger.debug("Moves in event: " + event.state.moves);
        _handleReceivingFullGameState(event.white.id, event.black.id);
    }

    /**
     * Handles receiving a "gameState" state from the game state stream.
     * @param event the event data of the state.
     */
    public void sendCurrentGameState(LichessGameStateEvent event) {
        Logger.info("Received current game state with status " + event.status);
        Logger.debug("Moves in event: " + event.moves);
        _handleReceivingIncrementalGameState(event.status, event.moves);
    }

    /**
     * Handles receiving a "chatLine" state from the game state stream.
     * @param event the event data of the state.
     */
    public void sendChatMessage(LichessChatLineEvent event) {
        if (event.username.equals("niteknighttbot")) {
            Logger.info("Received chat message that the bot sent");
            return;
        }
        Logger.info("Received chat message from challenger");
        _handleChatFromChallenger(event.text);
    }

    public Enums.GameState gameState() { return _gameState; }
    public void setGameState(Enums.GameState gameState ) { _gameState = gameState; _lastGameStateUpdate = new Date(); }

    public boolean done() {
        if (gameState() == Enums.GameState.FINISHED_BY_EVENT || gameState() == Enums.GameState.ABORTED) {
            Logger.info("Ending main loop because game state is " + gameState());
            return true;
        }
        return false;
    }

    protected void _setAlgorithm(Enums.EngineAlgorithm algorithm) {
        _algorithm = algorithm;
        _setMoveSelector();
    }

    protected void _setMoveSelector() {
        if (_algorithm == Enums.EngineAlgorithm.BEST_MOVE || _algorithm == Enums.EngineAlgorithm.WORST_MOVE) {
            _moveSelector = new BestWorstMoveSelector(_random, _algorithm, _stockfishClient);
        }
        else if (_algorithm == Enums.EngineAlgorithm.INSTRUCTIVE) {
            _moveSelector = new InstructiveMoveSelector(_random, _algorithm, _stockfishClient);
        }
        else {
            Logger.info("Aborting game because I could not choose a move selector for algorithm " + _algorithm);
            LichessInterface.writeChat(_gameId, "This is embarrassing. I can't figure out which method to use to select moves, so I have to quit the game. Sorry!");
            LichessInterface.resignGame(_gameId);
            setGameState(Enums.GameState.ABORTED);

        }
    }

    public String gameId() { return _gameId; }

    public void setStarted() {
        setGameState(Enums.GameState.STARTED_BY_EVENT);
    }

    public void setFinished() {
        setGameState(Enums.GameState.FINISHED_BY_EVENT);
    }

    public void forceEnd() {
        setGameState(Enums.GameState.EXTERNAL_FORCED_END);
    }

}

package niteknightt.bot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import niteknightt.gameplay.Move;
import niteknightt.bot.moveselectors.BestWorstMoveSelector;
import niteknightt.bot.moveselectors.InstructiveMoveSelector;
import niteknightt.bot.moveselectors.JustTheBestMoveSelector;
import niteknightt.bot.moveselectors.MoveSelector;
import niteknightt.gameplay.Board;
import niteknightt.gameplay.Enums;
import niteknightt.lichessapi.LichessApiException;
import niteknightt.lichessapi.LichessChallenge;
import niteknightt.lichessapi.LichessChatLineEvent;
import niteknightt.lichessapi.LichessEnums;
import niteknightt.lichessapi.LichessGameFullEvent;
import niteknightt.lichessapi.LichessGameStateEvent;
import niteknightt.lichessapi.LichessInterface;

public abstract class BotGame implements Runnable {
    
    protected String _gameId;
    protected Enums.GameState _gameState;
    protected LichessChallenge _challenge;
    protected StockfishClient _stockfishClient = new StockfishClient();
    protected Board _board;
    protected int _numMovesPlayedByChallenger;
    protected int _numMovesPlayedByEngine;
    protected Date _lastGameStateUpdate;
    protected Enums.Color _engineColor;
    protected Enums.Color _challengerColor;
    protected Enums.EngineAlgorithm _algorithm;
    protected MoveSelector _moveSelector;
    protected Random _random = new Random();
    protected List<Move> _moves = new ArrayList<Move>();


    public static BotGame createGameForChallenge(LichessChallenge challenge) {
        if (challenge.challenger.title != null && challenge.challenger.title.equals("BOT")) {
            return new BotGameVsBot(challenge);
        }
        else {
            return new BotGameVsHuman(challenge);
        }
    }

    public BotGame(LichessChallenge challenge) {
        _challenge = challenge;
        _gameId = challenge.id;
        initGame();
    }

    /**
     * Initializes data structures after accepting a challenge from another Lichess user.
     */
    protected void initGame() {
        setGameState(Enums.GameState.CREATED);

        _stockfishClient.init(5000l, _gameId);
        _stockfishClient.startGame();

        _board = new Board();
        _board.setupStartingPosition();

        _numMovesPlayedByEngine = 0;
        _numMovesPlayedByChallenger = 0;
    }

    public Enums.GameState gameState() { return _gameState; }
    public void setGameState(Enums.GameState gameState ) { _gameState = gameState; _lastGameStateUpdate = new Date(); }

    /**
     * Initializes game items after receiving the full game state from Lichess.
     * This should happen once per game after starting up the bot.
     */
    protected void _handleReceivingFullGameState(String whitePlayerId, String blackPlayerId) {
        if (blackPlayerId.equals("niteknighttbot")) {
            _engineColor = Enums.Color.BLACK;
            _challengerColor = Enums.Color.WHITE;
        }
        else {
            _engineColor = Enums.Color.WHITE;
            _challengerColor = Enums.Color.BLACK;
        }
        setGameState(Enums.GameState.FULL_STATE_RECEIVED);

        _performPregameTasks();
    }

    protected abstract void _performPregameTasks();

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
        else if (_algorithm == Enums.EngineAlgorithm.JUST_THE_BEST) {
            _moveSelector = new JustTheBestMoveSelector(_random, _algorithm, _stockfishClient);
        }
        else {
            Logger.info("Aborting game because I could not choose a move selector for algorithm " + _algorithm);
            _handleErrorInGame(true, Enums.GameState.ABORTED, "This is embarrassing. I can't figure out which method to use to select moves, so I have to quit the game. Sorry!");
        }
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
/*
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
*/
        // Make the challenger's move that is in the game state, if there is one.
        String moveSRs[] = moves.split(" ");
        if (_board.whosTurnToGo() == _challengerColor && moveSRs.length == _moves.size() + 1) {
            Move currentMove = new Move(moveSRs[moveSRs.length - 1], _board);
            //Move currentMove = stateMoves.get(stateMoves.size() - 1);
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

                _performPostmoveTasks();
            }
        }
    }

    protected abstract void _performPostmoveTasks();

    public Board getBoard() { return _board; }

    public Move getLastMove() { return _moves.get(_moves.size() - 1); }

    public MoveSelector getMoveSelector() { return _moveSelector; }

    public String getGameId() { return _gameId; }

    /**
     * Handles text received from the challenger in the chat.
     * 
     * @param text the text that the challenger wrote in the chat.
     */
    protected abstract void _handleChatFromChallenger(String text);

    /**
     * Selects a move for the engine to play, and plays it.
     */
    protected void _playEngineMove() {

        Move engineMove = null;
        
        try {
            engineMove = _moveSelector.selectMove(_board);
        }
        catch (MoveSelectorException e) {
            _handleErrorInGame(true, Enums.GameState.ABORTED, "Sorry dude, I'm quitting this game because I'm not sure how to play right now.");
            return;
        }

        if (engineMove == null) {
            Logger.info("Ending game internally because no legal moves for engine");
            _handleErrorInGame(false, Enums.GameState.ENDED_INTERNALLY, "Looks like I have no legal moves, but Lichess hasn't informed me yet that the game is over. Weird.");
            return;
        }

        if (!_board.handleMoveForGame(engineMove)) {
            Logger.error("Something went wrong. The computer says the engine's move is not legal. Ending game. Check log.");
            _handleErrorInGame(false, Enums.GameState.ERROR, "The computer won't let me make the move I want to make. Looks like I have to quit. Sorry.");
        }

        _moves.add(engineMove);
        ++_numMovesPlayedByEngine;
        try {
            LichessInterface.makeMove(_gameId, engineMove.uciFormat());
        }
        catch (LichessApiException e) {
            Logger.error("Caught LichessApiException while sending move to Lichess");
            _handleErrorInGame(false, Enums.GameState.ERROR, "I want to make a move, but can't seem to send it to Lichess. Gotta quit.");
        }
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
                _handleErrorInGame(true, Enums.GameState.ABORTED, "Hey guess what? Some sort of error happened in my program and I have to quit the game. Sorry!");
            }
            else if (gameState() == Enums.GameState.EXTERNAL_FORCED_END) {
                // This might happen if the bot manager decided it has to kill the game.
                Logger.info("Aborting game because got external force end");
                _handleErrorInGame(true, Enums.GameState.ABORTED, "OMG! I've been instructed by the program to quit the gane. Bye for now.");
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
                            Logger.error("Resigning game because gamestate is CREATED or STARTED_BY_EVENT or ENDED_INTERNALLY for 15 seconds");
                            _handleErrorInGame(true, Enums.GameState.ABORTED, "I zoned out for just a second and now I'm totally confused about this game. I gotta go.");
                        }
            }

            try { Thread.sleep(100); } catch (InterruptedException interruptException) { }
        }

        _performPostgameTasks();
    }

    protected void _handleErrorInGame(boolean doQuitGame, Enums.GameState newGameState, String textForChat) {

        try {
            if (textForChat != null) {
                LichessInterface.writeChat(_gameId, textForChat);
            }
        }
        catch (LichessApiException e) { }
        try {
            if (doQuitGame) {
                LichessInterface.resignGame(_gameId);
            }
        }
        catch (LichessApiException e) { }

        setGameState(newGameState);
    }

    public boolean done() {
        if (gameState() == Enums.GameState.FINISHED_BY_EVENT || gameState() == Enums.GameState.ABORTED) {
            Logger.info("Ending main loop because game state is " + gameState());
            return true;
        }
        return false;
    }

    protected abstract void _performPostgameTasks();

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

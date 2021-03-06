package niteknightt.bot;

import java.util.*;

import niteknightt.lichessapi.LichessApiException;
import niteknightt.lichessapi.LichessEnums;
import niteknightt.lichessapi.LichessEvent;
import niteknightt.lichessapi.LichessInterface;

public class BotManager implements Runnable {

    public static int MAX_CONCURRENT_CHALLENGES = 5;

    protected Map<String, BotGame> _games;
    protected Map<String, Thread> _gameThreads;
    protected Map<String, Thread> _gameStateReaderThreads;
    protected Map<String, Date> _endedGames;
    protected LichessEventReader _eventReader;
    protected Thread _eventReaderThread;
    protected boolean _done;

    protected int _numRunningChallenges;

    public BotManager() {
    }

    public void setNotDone() { _done = false; }
    public void setDone() { _done = true; }
    public boolean done() { return _done; }

    public void init() {
        _games = new HashMap<String, BotGame>();
        _gameThreads = new HashMap<String, Thread>();
        _gameStateReaderThreads = new HashMap<String, Thread>();
        _endedGames = new HashMap<String, Date>();
        _numRunningChallenges = 0;
        _eventReader = new LichessEventReader(this);
        _eventReaderThread = new Thread(_eventReader);
        _eventReaderThread.start();
        setNotDone();
    }

    public void run() {
        init();

        while (!done()) {
            
            // Remove any completed games.
            for (Map.Entry<String, BotGame> entry : _games.entrySet()) {
                String gameId = entry.getKey();
                BotGame game = entry.getValue();
                if (game.done()) {
                    _endedGames.put(gameId, new Date());
                    _games.remove(gameId);
                    --_numRunningChallenges;
                    Logger.info("Identified game ID " + gameId + " as done and removed it -- currently there are " + _numRunningChallenges + " running games.");
                }
            }

            // Check if any old completed games still has an open thread.
            for (Map.Entry<String, Date> entry : _endedGames.entrySet()) {
                String gameId = entry.getKey();
                Date timeEnded = entry.getValue();

                Thread gameThread = _gameThreads.get(gameId);
                if (gameThread != null && gameThread.isAlive()) {
                    Date now = new Date();
                    long diffInMillies = Math.abs(now.getTime() - timeEnded.getTime());
                    if (diffInMillies >= 30) {
                        Logger.error("Game " + gameId + " has a game thread that is still alive " + diffInMillies + " milliseconds after the game ended.");
                    }
                }
                _gameThreads.remove(gameId);

                Thread gameStateReaderThread = _gameStateReaderThreads.get(gameId);
                if (gameStateReaderThread != null && gameStateReaderThread.isAlive()) {
                    Date now = new Date();
                    long diffInMillies = Math.abs(now.getTime() - timeEnded.getTime());
                    if (diffInMillies >= 30) {
                        Logger.error("Game " + gameId + " has a game state reader thread that is still alive " + diffInMillies + " milliseconds after the game ended.");
                    }
                }
                _gameStateReaderThreads.remove(gameId);
            }

            try { Thread.sleep(500); } catch (InterruptedException interruptException) { }
        }

        Logger.info("Closing BotManager");

        _eventReader.setDone();
        if (_eventReaderThread.isAlive()) {
            try { _eventReaderThread.join(15000); } catch (InterruptedException ex) {
                Logger.warning("Failed to join event reader thread");
            }
        }
        else {
            Logger.warning("Event reader thread is already dead");
        }

        // Remove any completed games.
        for (Map.Entry<String, BotGame> entry : _games.entrySet()) {
            String gameId = entry.getKey();
            BotGame game = entry.getValue();
            if (!game.done()) {
                game.forceEnd();
                Logger.info("Forcing end to game " + gameId);
            }
        }

        try { Thread.sleep(500); } catch (InterruptedException interruptException) { }
    }

    public void handleChallenge(LichessEvent event) {
        try {
            if (!event.challenge.variant.key.equals(LichessEnums.VariantKey.STANDARD)) {
                System.out.println("INFO: Declining challenge ID " + event.challenge.id + " because it was not standard chess.");
                LichessInterface.declineChallenge(event.challenge.id);
                return;
            }
            if (_numRunningChallenges >= MAX_CONCURRENT_CHALLENGES) {
                System.out.println("INFO: Declining challenge ID " + event.challenge.id + " because there are too many games in progress.");
                LichessInterface.declineChallenge(event.challenge.id);
                return;
            }
            if ((event.challenge.challenger.title == null || !event.challenge.challenger.title.equals("BOT")) &&
                event.challenge.rated) {
                    // Rated challenge from human - decline
                    System.out.println("INFO: Declining challenge ID " + event.challenge.id + " because it is a rated challenge from a human.");
                    LichessInterface.declineChallenge(event.challenge.id);
                    return;
            }
        }
        catch (LichessApiException e) {
            Logger.error("Got LichessApiException while trying to decline challenge");
        }

        ++_numRunningChallenges;

        BotGame game = BotGame.createGameForChallenge(event.challenge);
        Thread gameThread = new Thread(game);
        gameThread.start();

        _games.put(event.challenge.id, game);
        _gameThreads.put(event.challenge.id, gameThread);

        try {
            LichessInterface.acceptChallenge(event.challenge.id);
        }
        catch (LichessApiException e) {
            Logger.error("Got LichessApiException while trying to accept challenge");
            game.setFinished();
            return;
        }

        Logger.info("Accepted challenge ID " + event.challenge.id + " -- currently there are " + _numRunningChallenges + " running games");
    }

    public void handleChallengeCanceled(LichessEvent event) {
        if (_games.containsKey(event.challenge.id)) {
            BotGame game = _games.get(event.challenge.id);
            game.forceEnd();
            Logger.info("Canceled challenge ID " + event.challenge.id + " -- currently there are " + _numRunningChallenges + " running games");
        }
        else {
            Logger.error("Got cancelation of challenge ID " + event.challenge.id + " but it is not in the list of games");
        }
    }

    public void handleChallengeDeclined(LichessEvent event) {
        Logger.error("Got challenge declined for challenge ID " + event.challenge.id + " -- don't know what to do with this");
    }

    public void handleGameStart(LichessEvent event) {
        if (_games.containsKey(event.game.id)) {
            BotGame game = _games.get(event.game.id);
            game.setStarted();

            LichessGameStateReader gameStateReader = new LichessGameStateReader(game);
            Thread gameStateReaderThread = new Thread(gameStateReader);
            gameStateReaderThread.start();
            _gameStateReaderThreads.put(event.game.id, gameStateReaderThread);
    
            Logger.info("Got game start event for game ID " + event.game.id);
        }
        else {
            Logger.error("Got game start event for game ID " + event.game.id + " but it is not in the list of games");
            try {
                LichessInterface.writeChat(event.game.id, "Lichess tells me I'm supposed to play this game, but I don't recognize it, so I am resigning. Bye.");
            }
            catch (LichessApiException e) { }
            try {
                LichessInterface.resignGame(event.game.id);
            }
            catch (LichessApiException e) {
                Logger.warning("Got LichessApiException while trying to resign a game that I could not start");
            }
        }
    }

    public void handleGameFinish(LichessEvent event) {
        if (_games.containsKey(event.game.id)) {
            _games.get(event.game.id).setFinished();
            Logger.info("Got game finish event for game ID " + event.game.id);
        }
        else {
            Logger.error("Got game finish event for game ID " + event.game.id + " but it is not in the list of games");
            try {
                LichessInterface.writeChat(event.game.id, "Lichess tells me I'm supposed to finish this game, but I don't recognize it, so I am resigning. Bye.");
            }
            catch (LichessApiException e) { }
            try {
                LichessInterface.resignGame(event.game.id);
            }
            catch (LichessApiException e) {
                Logger.warning("Got LichessApiException while trying to resign a game that I could not finish");
            }
        }
    }
}

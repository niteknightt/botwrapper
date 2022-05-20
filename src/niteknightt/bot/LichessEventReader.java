package niteknightt.bot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.Queue;

import com.google.gson.Gson;

import niteknightt.lichessapi.LichessEvent;
import niteknightt.lichessapi.LichessInterface;
import niteknightt.lichessapi.LichessEnums;

public class LichessEventReader implements Runnable {

    protected boolean _done = false;
    protected BotManager _botManager;
    protected Queue<LichessEvent> _events = new LinkedList<LichessEvent>();
    
    public boolean done() { return _done; }
    public void setDone() { _done = true; }
    public void setNotDone() { _done = false; }

    public LichessEventReader(BotManager botManager) {
        _botManager = botManager;
    }

    public LichessEvent peekQueue() {
        return _events.peek();
    }

    public LichessEvent popQueue() {
        return _events.remove();
    }

    public boolean isQueueEmpty() {
        return _events.isEmpty();
    }

    public void handleEventString(String eventString) {
        LichessEvent event = null;

        try {
            event = new Gson().fromJson(eventString, LichessEvent.class);
        }
        catch (Exception e) {
            System.out.println("Exception while parsing event -- the event string is below");
            System.out.println(eventString);
            System.out.println(e.getMessage());
            System.out.println(e.getStackTrace());
        }

        if (event == null) {
            System.out.println("ERROR: Event is null -- event string is below");
            System.out.println(eventString);
            return;
        }

        if (event.eventType == null) {
            System.out.println("ERROR: Event type is null -- event string is below");
            System.out.println(eventString);
            return;
        }

        if (event.eventType.equals(LichessEnums.EventType.CHALLENGE)) {
            _botManager.handleChallenge(event);
        }
        else if (event.eventType.equals(LichessEnums.EventType.CHALLENGE_CANCELED)) {
            _botManager.handleChallengeCanceled(event);
        }
        else if (event.eventType.equals(LichessEnums.EventType.CHALLENGE_DECLINED)) {
            _botManager.handleChallengeDeclined(event);
        }
        else if (event.eventType.equals(LichessEnums.EventType.GAME_START)) {
            _botManager.handleGameStart(event);
        }
        else if (event.eventType.equals(LichessEnums.EventType.GAME_FINISH)) {
            _botManager.handleGameFinish(event);
        }
        else {
            System.out.println("ERROR: Unknown event type received: " + event.eventType);
        }
    }

    public void run() {
        URL url;
        URLConnection conn;
        try {
            url = new URL(LichessInterface.LICHESS_API_ENDPOINT_BASE + "stream/event");
        }
        catch (MalformedURLException e) {
            throw new RuntimeException("Got MalformedURLException for this URL: " + LichessInterface.LICHESS_API_ENDPOINT_BASE + "stream/event");
        }
        try {
            conn = url.openConnection();
        }
        catch (IOException e) {
            throw new RuntimeException("Got IOException for this URL: " + LichessInterface.LICHESS_API_ENDPOINT_BASE + "stream/event");
        }

        conn.setRequestProperty (LichessInterface.AUTH_KEY_TEXT, LichessInterface.AUTH_VALUE_TEXT);
        
        while (!done()) {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null && !done())
                    if (!inputLine.trim().isEmpty())
                        handleEventString(inputLine);
                in.close();
                try { Thread.sleep(5000); } catch (InterruptedException e) { }
            }
            catch (IOException e) {
                throw new RuntimeException("Got IOException while reading from this URL: " + LichessInterface.LICHESS_API_ENDPOINT_BASE + "stream/event");
            }
        }
        Logger.info("Ending event reader thread");
    }

}

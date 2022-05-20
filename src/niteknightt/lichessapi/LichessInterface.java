package niteknightt.lichessapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class LichessInterface {

    public static String LICHESS_API_ENDPOINT_BASE = "https://lichess.org/api/";
    public static String AUTH_KEY_TEXT = "Authorization";
    public static String AUTH_VALUE_TEXT = "Bearer lip_NkdnIybjkl2B8A1Ue85k";
    public static HttpClient client = HttpClient.newHttpClient();
 
    public static String doHttpSyncGet(String endpoint) {
        try {
            //HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(LICHESS_API_ENDPOINT_BASE + endpoint))
                    .GET()
                    .setHeader(AUTH_KEY_TEXT, AUTH_VALUE_TEXT)
                    .build();
    
            HttpResponse<String> response =  client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response == null || response.body() == null) {
                System.out.println("Received null or empty response while calling sync get endpoint " + endpoint);
                return null;
            }

            return response.body();
        }
        catch (Exception e) {
            System.out.println("Exception while calling sync get endpoint " + endpoint);
            System.out.println(e.getMessage());
            System.out.println(e.getStackTrace());
            return null;
        }
    }

    public static void doHttpSyncPostNoBody(String endpoint) {
        URL url;
        try {
            url = new URL(LICHESS_API_ENDPOINT_BASE + endpoint);
        }
        catch (MalformedURLException e) {
            throw new RuntimeException("Got MalformedURLException for this URL: " + LICHESS_API_ENDPOINT_BASE + endpoint);
        }

        HttpURLConnection conn;
        try {
            conn = (HttpURLConnection)url.openConnection();
        }
        catch (IOException e) {
            throw new RuntimeException("Got IOException for this URL: " + LICHESS_API_ENDPOINT_BASE + endpoint);
        }

        try {
            conn.setRequestMethod("POST");
        }
        catch (ProtocolException e) {
            throw new RuntimeException("Got ProtocolException for POST to this URL: " + LICHESS_API_ENDPOINT_BASE + endpoint);
        }

        conn.setRequestProperty (LichessInterface.AUTH_KEY_TEXT, LichessInterface.AUTH_VALUE_TEXT);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(0));
        conn.setDoOutput(true);

        try {
            Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            for (int c; (c = in.read()) >= 0;)
                System.out.print((char)c);

            StringBuilder sb = new StringBuilder();
            for (int c; (c = in.read()) >= 0;)
                sb.append((char)c);
            //String response = sb.toString();
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Got UnsupportedEncodingException for this URL: " + LICHESS_API_ENDPOINT_BASE + endpoint);
        }
        catch (IOException e) {
            throw new RuntimeException("Got IOException for this URL: " + LICHESS_API_ENDPOINT_BASE + endpoint);
        }
    }

    public static void doHttpSyncPost(String endpoint) {
        doHttpSyncPost(endpoint, null);
    }

    public static void doHttpSyncPost(String endpoint, Map<String, String> params) {
        boolean hasBody = (params != null && !params.isEmpty());

        URL url;
        try {
            url = new URL(LICHESS_API_ENDPOINT_BASE + endpoint);
        }
        catch (MalformedURLException e) {
            throw new RuntimeException("Got MalformedURLException for this URL: " + LICHESS_API_ENDPOINT_BASE + endpoint);
        }

        byte[] postDataBytes = null;
        if (hasBody) {
            try {
                StringBuilder postData = new StringBuilder();
                for (Map.Entry<String,String> param : params.entrySet()) {
                    if (postData.length() != 0) postData.append('&');
                    postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }
                postDataBytes = postData.toString().getBytes("UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                throw new RuntimeException("Got UnsupportedEncodingException for this URL: " + LICHESS_API_ENDPOINT_BASE + endpoint);
            }
        }

        HttpURLConnection conn;
        try {
            conn = (HttpURLConnection)url.openConnection();
        }
        catch (IOException e) {
            throw new RuntimeException("Got IOException for this URL: " + LICHESS_API_ENDPOINT_BASE + endpoint);
        }

        try {
            conn.setRequestMethod("POST");
        }
        catch (ProtocolException e) {
            throw new RuntimeException("Got ProtocolException for POST to this URL: " + LICHESS_API_ENDPOINT_BASE + endpoint);
        }

        conn.setRequestProperty (LichessInterface.AUTH_KEY_TEXT, LichessInterface.AUTH_VALUE_TEXT);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        if (hasBody) {
            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        }
        else {
            conn.setRequestProperty("Content-Length", String.valueOf(0));
        }

        conn.setDoOutput(true);

        if (hasBody) {
            try {
                conn.getOutputStream().write(postDataBytes);
            }
            catch (IOException e) {
                throw new RuntimeException("Got IOException for this URL: " + LICHESS_API_ENDPOINT_BASE + endpoint);
            }
        }

        try {
            Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            in.getClass(); // This line here just to avoid compiler warning.
            
            //for (int c; (c = in.read()) >= 0;)
            //    System.out.print((char)c);

            //StringBuilder sb = new StringBuilder();
            //for (int c; (c = in.read()) >= 0;)
            //    sb.append((char)c);
            //String response = sb.toString();
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Got UnsupportedEncodingException for this URL: " + LICHESS_API_ENDPOINT_BASE + endpoint);
        }
        catch (IOException e) {
            throw new RuntimeException("Got IOException for this URL: " + LICHESS_API_ENDPOINT_BASE + endpoint);
        }
    }

    public static LichessApiObject httpSyncGetWrapper(String endpoint, Class<?> aclass) {
        String response = doHttpSyncGet(endpoint);

        try {
            return (LichessApiObject)new Gson().fromJson(response, aclass);
        }
        catch (Exception e) {
            System.out.println("Exception while parsing response to sync get endpoint " + endpoint + " using class " + aclass);
            System.out.println(e.getMessage());
            System.out.println(e.getStackTrace());
            return null;
        }
    }

    public static LichessProfile getProfile() {
        return (LichessProfile)httpSyncGetWrapper("account", LichessProfile.class);
    }    

    public static void acceptChallenge(String challengeId) {
        doHttpSyncPost("challenge/" + challengeId + "/accept");
    }    

    public static void declineChallenge(String challengeId) {
        doHttpSyncPost("challenge/" + challengeId + "/decline");
    }

    public static void makeMove(String gameId, String move) {
        doHttpSyncPost("bot/game/" + gameId + "/move/" + move);
    }    

    public static void abortGame(String gameId) {
        doHttpSyncPost("bot/game/" + gameId + "/abort");
    }

    public static void resignGame(String gameId) {
        doHttpSyncPost("bot/game/" + gameId + "/resign");
    }

    public static void writeChat(String gameId, String text) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("room", "player");
        params.put("text", text);
        doHttpSyncPost("bot/game/" + gameId + "/chat", params);
    }

    public static LichessEvent getEvent() {
        return (LichessEvent)httpSyncGetWrapper("stream/event", LichessEvent.class);
    }

    public static void getEvent1() {
        URL url;
        boolean done = false;
        HttpURLConnection con = null;
        try {
            url = new URL(LICHESS_API_ENDPOINT_BASE + "stream/event" + "?access_token=lip_NkdnIybjkl2B8A1Ue85k");
            con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty (AUTH_KEY_TEXT, AUTH_VALUE_TEXT);
            con.setRequestMethod("GET");
            int respCode = con.getResponseCode();
            if (respCode == HttpURLConnection.HTTP_OK) {
                while (!done) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
                    while (br. ready()) {
                        String sr = br.readLine();
                        System.out.println("Event: " + sr);
                    }
                    br.close();
                    Thread.sleep(5000);
                }
                //Scanner s = new Scanner(con.getInputStream()).useDelimiter("\\A");
                //String result = s.hasNext() ? s.next() : "";
            }
        }
        catch (Exception e) {
            System.out.print(e);
        }
    }

    public static void getEvent2() {
        boolean done = false;
        try {
            URL oracle = new URL(LICHESS_API_ENDPOINT_BASE + "stream/event" + "?access_token=lip_NkdnIybjkl2B8A1Ue85k");
            URLConnection yc = oracle.openConnection();
            yc.setRequestProperty (AUTH_KEY_TEXT, AUTH_VALUE_TEXT);
            while (!done) {
                BufferedReader in = new BufferedReader(new InputStreamReader(
                                            yc.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) 
                    System.out.println(inputLine);
                in.close();
            }
            Thread.sleep(5000);
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    public static LichessChallenge checkForChallenge() {
        try {
            //HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://lichess.org/api/challenge"))
                    .GET() // GET is default
                    .setHeader("Authorization", "Bearer lip_NkdnIybjkl2B8A1Ue85k")
                    .build();
    

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(response -> {

                if (response.body() == null) {
                    System.out.println("Respponse body null");
                }
                System.out.println("Respponse body unnull");
                JsonObject jobj = new Gson().fromJson(response.body(), JsonObject.class);
                if (jobj.has("type")) {
                    String eventType = jobj.get("type").getAsString();
                    if (eventType.equals("challenge")) {
                        String challengeStr = jobj.get("challenge").getAsString();
                        LichessChallenge challenge = new Gson().fromJson(challengeStr, LichessChallenge.class);
                        System.out.println(challenge);
                    }
                }

            });

/*
            CompletableFuture<HttpResponse<String>> response =
                    client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    
            String result = response.thenApply(HttpResponse::body).get(15, TimeUnit.SECONDS);
    
            System.out.println(result);
*/
/*
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.body() == null) {
                return null;
            }
            JsonObject jobj = new Gson().fromJson(response.body(), JsonObject.class);
            if (jobj.has("type")) {
                String eventType = jobj.get("type").getAsString();
                if (eventType.equals("challenge")) {
                    String challengeStr = jobj.get("challenge").getAsString();
                    LichessChallenge challenge = new Gson().fromJson(challengeStr, LichessChallenge.class);
                    return challenge;
                }
            }
            */
            return null;
        } catch (Exception e) {
            System.out.println("Exception: " + e);
            return null;
        }
    }    

    public static void getGameState() {
        try {
            //HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://lichess.org/api/board/game/stream/XfUeCzukk3lm"))
                    .GET() // GET is default
                    .setHeader("Authorization", "Bearer lip_NkdnIybjkl2B8A1Ue85k")
                    .build();
    

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(response -> {

                if (response.body() == null) {
                    System.out.println("Respponse body null");
                }
                System.out.println("Respponse body unnull");
                JsonObject jobj = new Gson().fromJson(response.body(), JsonObject.class);
                if (jobj.has("type")) {
                    String eventType = jobj.get("type").getAsString();
                    if (eventType.equals("challenge")) {
                        String challengeStr = jobj.get("challenge").getAsString();
                        LichessChallenge challenge = new Gson().fromJson(challengeStr, LichessChallenge.class);
                        System.out.println(challenge);
                    }
                }

            });

/*
            CompletableFuture<HttpResponse<String>> response =
                    client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    
            String result = response.thenApply(HttpResponse::body).get(15, TimeUnit.SECONDS);
    
            System.out.println(result);
*/
/*
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.body() == null) {
                return null;
            }
            JsonObject jobj = new Gson().fromJson(response.body(), JsonObject.class);
            if (jobj.has("type")) {
                String eventType = jobj.get("type").getAsString();
                if (eventType.equals("challenge")) {
                    String challengeStr = jobj.get("challenge").getAsString();
                    LichessChallenge challenge = new Gson().fromJson(challengeStr, LichessChallenge.class);
                    return challenge;
                }
            }
            */
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
    }    

}

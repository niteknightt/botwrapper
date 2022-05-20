package niteknightt.lichessapi;

import java.util.*;

import com.google.gson.annotations.SerializedName;

public class LichessProfile extends LichessApiObject {
    public String id;
    public String username;
    public boolean online;

    // Possible keys: "chess960", "atomic", "racingKings", "ultraBullet", "blitz", "kingOfTheHill", "bullet", "correspondence", "horde", "puzzle", "classical", "rapid", "storm"
    @SerializedName("perfs")
    public Map<String, LichessProfilePerf> profilePerfs;
    
    public long createdAt;
    public boolean disabled;
    public boolean tosViolation;
    @SerializedName("profile")
    public LichessPersonalProfile personalProfile;
    public long seenAt;
    public boolean patron;
    public boolean verified;
    public LichessPlayTime playTime;
    public String title;
    public String url;
    public String playing;
    public int completionRate;

    // Possible keys: "all", "rated", "ai", "draw", "drawH", "loss", "lossH", "win", "winH", "bookmark", "playing", "import", "me"
    @SerializedName("count")
    public Map<String, Integer> counts;
    
    public boolean streaming;
    public boolean followable;
    public boolean following;
    public boolean blocking;
    public boolean followsYou;
}

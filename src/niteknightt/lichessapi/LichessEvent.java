package niteknightt.lichessapi;

import com.google.gson.annotations.SerializedName;

public class LichessEvent extends LichessApiObject {
    @SerializedName("type")
    public LichessEnums.EventType eventType;
    public LichessGameEventInfo game; // Only for events GAME_START, GAME_FINISH
    public LichessChallenge challenge; // Only for events CHALLENGE, CHALLENGE_CANCELED, CHALLENGE_DECLINED
}

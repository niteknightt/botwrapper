package niteknightt.lichessapi;

import com.google.gson.annotations.SerializedName;

public class LichessChallenge extends LichessApiObject {
    public String id;
    public String url;
    public String status;
    public LichessChallengeUser challenger;
    @SerializedName("destUser")
    public LichessChallengeUser challengee;
    public LichessVariant variant;
    public boolean rated;
    public LichessEnums.Speed speed;
    public LichessTimeControl timeControl;
    public LichessEnums.Color color;
    public LichessEnums.Direction direction;
    public String initialFen;
    public String declineReason;
}

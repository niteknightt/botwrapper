package niteknightt.lichessapi;

import com.google.gson.annotations.SerializedName;

public class LichessGameFullEvent {
    @SerializedName("type")
    public LichessEnums.GameStateType gameStateType;
    public String id;
    public LichessVariant variant;
    public LichessClock clock;
    public LichessEnums.Speed speed;
    public LichessPerf perf;
    public boolean rated;
    public long createdAt;
    public LichessGameEventPlayer white;
    public LichessGameEventPlayer black;
    public String initialFen;
    public LichessGameStateEvent state;
    public String tournamentId;
}

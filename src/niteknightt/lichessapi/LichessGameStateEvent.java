package niteknightt.lichessapi;

import com.google.gson.annotations.SerializedName;

public class LichessGameStateEvent {
    @SerializedName("type")
    public LichessEnums.GameStateType gameStateType;
    public String moves;
    public int wtime;
    public int btime;
    public int winc;
    public int binc;
    public LichessEnums.GameStatus status;
    public String winner;
    public boolean wdraw;
    public boolean bdraw;
    public boolean wtakeback;
    public boolean btakeback;
}

package niteknightt.lichessapi;

import com.google.gson.annotations.SerializedName;

public class LichessChatLineEvent {
    @SerializedName("type")
    public LichessEnums.GameStateType gameStateType;
    public LichessEnums.Room room;
    public String username;
    public String text;
}

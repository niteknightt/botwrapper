package niteknightt.lichessapi;

import com.google.gson.annotations.SerializedName;

public class LichessEnums {

    public enum Color {
        @SerializedName("white")
        WHITE,
        @SerializedName("black")
        BLACK,
        @SerializedName("random")
        RANDOM
     }

     public enum Speed {
        @SerializedName("ultraBullet")
        ULTRA_BULLET,
        @SerializedName("bullet")
        BULLET,
        @SerializedName("blitz")
        BLITZ,
        @SerializedName("rapid")
        RAPID,
        @SerializedName("classical")
        CLASSICAL,
        @SerializedName("correspondence")
        CORRESPONDENCE
     }

     public enum Direction {
        @SerializedName("in")
        IN,
        @SerializedName("out")
        OUT
     }

     public enum Title {
        @SerializedName("GM")
        GM,
        @SerializedName("WGM")
        WGM,
        @SerializedName("IM")
        IM,
        @SerializedName("WIM")
        WIM,
        @SerializedName("FM")
        FM,
        @SerializedName("WFM")
        WFM,
        @SerializedName("NM")
        NM,
        @SerializedName("CM")
        CM,
        @SerializedName("WCM")
        WCM,
        @SerializedName("WNM")
        WNM,
        @SerializedName("LM")
        LM,
        @SerializedName("BOT")
        BOT
     }

     public enum GameSource {
        @SerializedName("lobby")
         LOBBY,
         @SerializedName("friend")
         FRIEND,
         @SerializedName("ai")
         AI,
         @SerializedName("api")
         API,
         @SerializedName("tournament")
         TOURNAMENT,
         @SerializedName("position")
         POSITION,
         @SerializedName("import")
         IMPORT,
         @SerializedName("importlive")
         IMPORT_LIVE,
         @SerializedName("simul")
         SIMUL,
         @SerializedName("relay")
         RELAY,
         @SerializedName("pool")
         POOL,
         @SerializedName("swiss")
         SWISS
     }

    public enum EventType {
        @SerializedName("gameStart")
        GAME_START,
        @SerializedName("gameFinish")
        GAME_FINISH,
        @SerializedName("challenge")
        CHALLENGE,
        @SerializedName("challengeCanceled")
        CHALLENGE_CANCELED,
        @SerializedName("challengeDeclined")
        CHALLENGE_DECLINED
    }

    public enum GameStateType {
        @SerializedName("gameFull")
        GAME_FULL,
        @SerializedName("gameState")
        GAME_STATE,
        @SerializedName("chatLine")
        CHAT_LINE
    }

    public enum VariantKey {
        @SerializedName("standard")
        STANDARD,
        @SerializedName("chess960")
        CHESS_960,
        @SerializedName("crazyhouse")
        CRAZY_HOUSE,
        @SerializedName("antichess")
        ANTICHESS,
        @SerializedName("atomic")
        ATOMIC,
        @SerializedName("horde")
        HORDE,
        @SerializedName("kingOfTheHill")
        KING_OF_THE_HILL,
        @SerializedName("racingKings")
        RACING_KINGS,
        @SerializedName("threeCheck")
        THREE_CHECK,
        @SerializedName("fromPosition")
        FROM_POSITION
    }

    public enum GameStatus {
        @SerializedName("created")
        CREATED,
        @SerializedName("started")
        STARTED,
        @SerializedName("aborted")
        ABORTED,
        @SerializedName("mate")
        MATE,
        @SerializedName("resign")
        RESIGN,
        @SerializedName("stalemate")
        STALEMATE,
        @SerializedName("timeout")
        TIMEOUT,
        @SerializedName("draw")
        DRAW,
        @SerializedName("outoftime")
        OUT_OF_TIME,
        @SerializedName("cheat")
        CHEAT,
        @SerializedName("noStart")
        NO_START,
        @SerializedName("unknownFinish")
        UNKNOWN_FINISH,
        @SerializedName("variantEnd")
        VARIANT_END
    }

    public enum Room {
        @SerializedName("player")
        PLAYER,
        @SerializedName("spectator")
        SPECTATOR
    }
}

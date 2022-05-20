package niteknightt.gameplay;

public class Enums {
    
    public enum Color {
        WHITE(0), BLACK(1);
    
        private final int value;

        private Color(int value) {
            if (value < 0  || value > 1) {
                throw new RuntimeException("Attempt to create Color object with invalid value: " + value);
            }
            this.value = value;
        }
    
        public int getValue() {
            return value;
        }
    
        public static String getSR(Color color) {
            return (color == Color.WHITE) ? "WHITE" : "BLACK";
        }
    
        public static Color oppositeColor(Color color) {
            return (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
        }
    }
    
    public enum PieceType {
        BLANK, PAWN, ROOK, KNIGHT, BISHOP, QUEEN, KING
    }

    public enum CastleSide {
        QUEENSIDE(0), KINGSIDE(1);
    
        private final int value;

        private CastleSide(int value) {
            if (value < 0  || value > 1) {
                throw new InvalidEnumValueException("Attempt to create CastleSide object with invalid value: " + value);
            }
            this.value = value;
        }
    
        public int getValue() {
            return value;
        }
    
    }

    public enum Algorithm {
        NONE, RANDOM, MINIMAX
    }

    public enum MinOrMax {
        MIN, MAX
    }

    public enum BotStatus {
        INIT,
        END
    }

    public enum GameState {
        CREATED,
        STARTED_BY_EVENT,
        FULL_STATE_RECEIVED,
        ERROR,
        ABORTED,
        EXTERNAL_FORCED_END,
        ENDED_INTERNALLY,
        FINISHED_BY_EVENT
    }

    public enum ChatState {
        NONE,
        ASKED_FOR_ALGORITHM,
        ALGORITHM_ANSWERED,
        ALGORITHM_NOT_ANSWERED
    }

    public enum AlgoState {
        NONE,
        SET_TO_DEFAULT,
        READ_FROM_FILE,
        SENT_BY_USER,
        IGNORED_BY_USER
    }

    public enum EngineAlgorithm {
        NONE(0), BEST_MOVE(1), WORST_MOVE(2), INSTRUCTIVE(3);
    
        private final int value;

        private EngineAlgorithm(int value) {
            if (value < 0  || value > 3) {
                throw new InvalidEnumValueException("Attempt to create EngineAlgorithm object with invalid value: " + value);
            }
            this.value = value;
        }
    
        public int getValue() {
            return value;
        }
    
        public static EngineAlgorithm fromValue(int value) {
            for (EngineAlgorithm type : values()) {
                if (type.getValue() == value) {
                    return type;
                }
            }
            throw new InvalidEnumValueException("Attempt to create EngineAlgorithm object with invalid value: " + value);
        }
    }

    public enum LogLevel {
        ERROR,
        WARNING,
        INFO,
        DEBUG
    }
}


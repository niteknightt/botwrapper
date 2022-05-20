package niteknightt.gameplay;

import java.util.*;

public class Position {
    public int col;
    public int row;

    public Position() {
        this.col = -1;
        this.row = -1;
    }

    public Position(int col, int row) {
        this.col = col;
        this.row = row;
    }

    public static Position uciToInternal(String uci) {
        Position pos = new Position();
        pos.col = uci.charAt(0) - 'a';
        pos.row = uci.charAt(1) - '1';
        return pos;
    }

    public static String internalToUci(Position pos) {
        char col = (char)('a' + pos.col);
        char row = (char)('1' + pos.row);
        String uci = "" + col + row;
        return uci;
    }

    public static int internalToIndex(Position pos) {
        return pos.row * 8 + pos.col;
    }

    public static Position indexToInternal(int index) {
        Position pos = new Position();
        pos.col = index % 8;
        pos.row = index / 8;
        return pos;
    }

    public static int uciToIndex(String uci) {
        Position pos = uciToInternal(uci);
        return internalToIndex(pos);
    }

    public static String indexToUci(int index) {
        Position pos = indexToInternal(index);
        return internalToUci(pos);
    }

    public static boolean areSquaresTouching(Position pos1, Position pos2) {
        return (Math.abs(pos1.col - pos2.col) <= 1 &&  Math.abs(pos1.row - pos2.row) <= 1 &&
                !(pos1.col == pos2.col && pos1.row == pos2.row));
    }

    public static boolean areSquaresOnSameColumn(Position pos1, Position pos2) {
        return (pos1.col == pos2.col);
    }

    public static boolean areSquaresOnSameRow(Position pos1, Position pos2) {
        return (pos1.row == pos2.row);
    }

    public static boolean areSquaresOnSameDiagonalUp(Position pos1, Position pos2) {
        return (pos1.col - pos2.col == pos1.row - pos2.row);
    }

    public static boolean areSquaresOnSameDiagonalDown(Position pos1, Position pos2) {
        return ((pos1.col - pos2.col) == -(pos1.row - pos2.row));
    }

    public static boolean areSquaresKnightsMoveFromEachOther(Position pos1, Position pos2) {
        return ((Math.abs(pos1.col - pos2.col) == 2 &&  Math.abs(pos1.row - pos2.row) == 1) ||
                (Math.abs(pos1.col - pos2.col) == 1 &&  Math.abs(pos1.row - pos2.row) == 2));
    }

    public static List<Position> getSquaresWherePawnCanAttack(Position pos, Enums.Color attackerColor) {
        List<Position> positions = new ArrayList<Position>();

        if (attackerColor == Enums.Color.BLACK) {
            int row = pos.row + 1;
            if (row <= 7) {
                int col = pos.col - 1;
                if (col >= 0) {
                    positions.add(new Position(col, row));
                }
                col = pos.col + 1;
                if (col <= 7) {
                    positions.add(new Position(col, row));
                }
            }
        }
        else {
            int row = pos.row - 1;
            if (row >= 0) {
                int col = pos.col - 1;
                if (col >= 0) {
                    positions.add(new Position(col, row));
                }
                col = pos.col + 1;
                if (col <= 7) {
                    positions.add(new Position(col, row));
                }
            }
        }

        return positions;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Position)) {
            return false;
        }

        Position casted = (Position)other;
        return (col == casted.col && row == casted.row);
    }

    @Override
    public String toString() {
        return Position.internalToUci(this);
    }
}

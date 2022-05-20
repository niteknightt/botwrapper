package niteknightt.gameplay;

import java.util.*;

public abstract class Piece {
    
    public Piece(Enums.PieceType pieceType, Enums.Color color, int col, int row, Board board) {
        _pieceType = pieceType;
        _color = color;
        _position = new Position(col, row);
        _board = board;
    }

    public Piece(Enums.PieceType pieceType, Board board) {
        _pieceType = pieceType;
        _board = board;
    }

    public void setColor(Enums.Color val) { _color = val; }
    public void setPosition(int col, int row) { _position.col = col; _position.row = row; }
    public Position position() { return _position; }
    public boolean isBlankPiece() { return false; } // virtual
    public Enums.PieceType pieceType() { return _pieceType; }
    public Enums.Color color() { return _color; }
    public abstract void getPossibleMoves(List<Move> possibleMoves);
    public abstract boolean calculateHasLegalMove();
    public abstract boolean isAttackingSquare(Position square);
    public void setPieceType(Enums.PieceType val) { _pieceType = val; }

    public Piece clone(Board board) {
        return Piece.createPiece(_pieceType, _color, _position.col, _position.row, board);
    }

    public static Piece createPiece(Enums.PieceType pieceType, Enums.Color color, int col, int row, Board board) {
        if (pieceType == Enums.PieceType.PAWN) {
            return new Pawn(color, col, row, board);
        }
        if (pieceType == Enums.PieceType.ROOK) {
            return new Rook(color, col, row, board);
        }
        if (pieceType == Enums.PieceType.BISHOP) {
            return new Bishop(color, col, row, board);
        }
        if (pieceType == Enums.PieceType.KNIGHT) {
            return new Knight(color, col, row, board);
        }
        if (pieceType == Enums.PieceType.QUEEN) {
            return new Queen(color, col, row, board);
        }
        if (pieceType == Enums.PieceType.KING) {
            return new King(color, col, row, board);
        }
        if (pieceType == Enums.PieceType.BLANK) {
            return new BlankPiece(color, col, row, board);
        }
        throw new RuntimeException("Incorrect type of piece to create");
    }
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        if (_color == Enums.Color.WHITE) {
            sb.append("white ");
        }
        else {
            sb.append("black ");
        }
        if (_pieceType == Enums.PieceType.PAWN) {
            sb.append("pawn on ");
        }
        else if (_pieceType == Enums.PieceType.KNIGHT) {
            sb.append("knight on ");
        }
        else if (_pieceType == Enums.PieceType.BISHOP) {
            sb.append("bishop on ");
        }
        else if (_pieceType == Enums.PieceType.ROOK) {
            sb.append("rook on ");
        }
        else if (_pieceType == Enums.PieceType.QUEEN) {
            sb.append("queen on ");
        }
        else if (_pieceType == Enums.PieceType.KING) {
            sb.append("king on ");
        }
        sb.append(_position.toString());

        return sb.toString();
    }

    public static void CreateBlankPiece(Board board) { GenericBlankPiece = Piece.createPiece(Enums.PieceType.BLANK, Enums.Color.WHITE, 0, 0, board); }

    public static Piece GetBlankPiece() { return GenericBlankPiece; }

    protected static Piece GenericBlankPiece;
    protected Enums.Color _color;
    protected Position _position;
    protected Enums.PieceType _pieceType;
    protected Board _board;
}

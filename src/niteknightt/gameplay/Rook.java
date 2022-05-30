package niteknightt.gameplay;

import java.util.*;

public class Rook extends Piece {

    public Rook(Board board) {
        super(Enums.PieceType.ROOK, board);
    }

    public Rook(Enums.Color color, int col, int row, Board board) {
        super(Enums.PieceType.ROOK, color, col, row, board);
    }

    @Override
    public void getPossibleMoves(List<Move> possibleMoves) {
        int targetRow = _position.row + 1;
        while (targetRow < 8) {
            Position target = new Position(_position.col, targetRow);
            if (_board.isSquarePieceColor(target, _color)) {
                break;
            }
            Move move = new Move(_position, target, _board);
            if (_board.testMoveForLegality(move)) {
                possibleMoves.add(move);
            }
            if (!_board.isSquareEmpty(target)) {
                break;
            }
            ++targetRow;
        }
        targetRow = _position.row - 1;
        while (targetRow > -1) {
            Position target = new Position(_position.col, targetRow);
            if (_board.isSquarePieceColor(target, _color)) {
                break;
            }
            Move move = new Move(_position, target, _board);
            if (_board.testMoveForLegality(move)) {
                possibleMoves.add(move);
            }
            if (!_board.isSquareEmpty(target)) {
                break;
            }
            --targetRow;
        }
        int targetCol = _position.col + 1;
        while (targetCol < 8) {
            Position target = new Position(targetCol, _position.row);
            if (_board.isSquarePieceColor(target, _color)) {
                break;
            }
            Move move = new Move(_position, target, _board);
            if (_board.testMoveForLegality(move)) {
                possibleMoves.add(move);
            }
            if (!_board.isSquareEmpty(target)) {
                break;
            }
            ++targetCol;
        }
        targetCol = _position.col - 1;
        while (targetCol > -1) {
            Position target = new Position(targetCol, _position.row);
            if (_board.isSquarePieceColor(target, _color)) {
                break;
            }
            Move move = new Move(_position, target, _board);
            if (_board.testMoveForLegality(move)) {
                possibleMoves.add(move);
            }
            if (!_board.isSquareEmpty(target)) {
                break;
            }
            --targetCol;
        }
    }
    
    @Override
    public boolean calculateHasLegalMove() {
        int targetRow = _position.row + 1;
        while (targetRow < 8) {
            Position target = new Position(_position.col, targetRow);
            if (_board.isSquarePieceColor(target, _color)) {
                break;
            }
            Move move = new Move(_position, target, _board);
            if (_board.testMoveForLegality(move)) {
                return true;
            }
            if (!_board.isSquareEmpty(target)) {
                break;
            }
            ++targetRow;
        }
        targetRow = _position.row - 1;
        while (targetRow > -1) {
            Position target = new Position(_position.col, targetRow);
            if (_board.isSquarePieceColor(target, _color)) {
                break;
            }
            Move move = new Move(_position, target, _board);
            if (_board.testMoveForLegality(move)) {
                return true;
            }
            if (!_board.isSquareEmpty(target)) {
                break;
            }
            --targetRow;
        }
        int targetCol = _position.col + 1;
        while (targetCol < 8) {
            Position target = new Position(targetCol, _position.row);
            if (_board.isSquarePieceColor(target, _color)) {
                break;
            }
            Move move = new Move(_position, target, _board);
            if (_board.testMoveForLegality(move)) {
                return true;
            }
            if (!_board.isSquareEmpty(target)) {
                break;
            }
            ++targetCol;
        }
        targetCol = _position.col - 1;
        while (targetCol > -1) {
            Position target = new Position(targetCol, _position.row);
            if (_board.isSquarePieceColor(target, _color)) {
                break;
            }
            Move move = new Move(_position, target, _board);
            if (_board.testMoveForLegality(move)) {
                return true;
            }
            if (!_board.isSquareEmpty(target)) {
                break;
            }
            --targetCol;
        }
    
        return false;
    }

    @Override
    public boolean isAttackingSquare(Position square) {
        if (square.equals(_position)) {
            return false;
        }
        if (Position.areSquaresOnSameColumn(_position, square)) {
            if (_board.canSquaresSeeEachOtherOnColumn(_position, square)) {
                return true;
            }
        }
        if (Position.areSquaresOnSameRow(_position, square)) {
            if (_board.canSquaresSeeEachOtherOnRow(_position, square)) {
                return true;
            }
        }
        return false;
    }
    
    

}

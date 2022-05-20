package niteknightt.gameplay;

import java.util.*;

public class Queen extends Piece {

    public Queen(Board board) {
        super(Enums.PieceType.QUEEN, board);
    }

    public Queen(Enums.Color color, int col, int row, Board board) {
        super(Enums.PieceType.QUEEN, color, col, row, board);
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
        targetCol = _position.col + 1;
        targetRow = _position.row + 1;
        while (targetCol < 8 && targetRow < 8) {
            Position target = new Position(targetCol, targetRow);
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
            ++targetRow;
        }
        targetCol = _position.col - 1;
        targetRow = _position.row - 1;
        while (targetCol > -1 && targetRow > -1) {
            Position target = new Position(targetCol, targetRow);
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
            --targetRow;
        }
        targetCol = _position.col + 1;
        targetRow = _position.row - 1;
        while (targetCol < 8 && targetRow > -1) {
            Position target = new Position(targetCol, targetRow);
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
            --targetRow;
        }
        targetCol = _position.col - 1;
        targetRow = _position.row + 1;
        while (targetCol > -1 && targetRow < 8) {
            Position target = new Position(targetCol, targetRow);
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
            ++targetRow;
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
        targetCol = _position.col + 1;
        targetRow = _position.row + 1;
        while (targetCol < 8 && targetRow < 8) {
            Position target = new Position(targetCol, targetRow);
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
            ++targetRow;
        }
        targetCol = _position.col - 1;
        targetRow = _position.row - 1;
        while (targetCol > -1 && targetRow > -1) {
            Position target = new Position(targetCol, targetRow);
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
            --targetRow;
        }
        targetCol = _position.col + 1;
        targetRow = _position.row - 1;
        while (targetCol < 8 && targetRow > -1) {
            Position target = new Position(targetCol, targetRow);
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
            --targetRow;
        }
        targetCol = _position.col - 1;
        targetRow = _position.row + 1;
        while (targetCol > -1 && targetRow < 8) {
            Position target = new Position(targetCol, targetRow);
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
            ++targetRow;
        }
    
        return false;
    }
    
    @Override
    public boolean isAttackingSquare(Position square) {
        if (square == _position) {
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
        if (Position.areSquaresOnSameDiagonalUp(_position, square)) {
            if (_board.canSquaresSeeEachOtherOnDiagonalUp(_position, square)) {
                return true;
            }
        }
        if (Position.areSquaresOnSameDiagonalDown(_position, square)) {
            if (_board.canSquaresSeeEachOtherOnDiagonalDown(_position, square)) {
                return true;
            }
        }
        return false;
    }
    
}

package niteknightt.gameplay;

import java.util.*;

public class King extends Piece {

    public King(Board board) {
        super(Enums.PieceType.KING, board);
    }

    public King(Enums.Color color, int col, int row, Board board) {
        super(Enums.PieceType.KING, color, col, row, board);
    }

    @Override
    public void getPossibleMoves(List<Move> possibleMoves) {
        List<Position> potentialTargets = new ArrayList<Position>();
    
        if (_position.row < 7) {
            potentialTargets.add(new Position(_position.col, _position.row + 1));
            if (_position.col < 7) {
                potentialTargets.add(new Position(_position.col + 1, _position.row + 1));
            }
            if (_position.col > 0) {
                potentialTargets.add(new Position(_position.col - 1, _position.row + 1));
            }
        }
        if (_position.row > 0) {
            potentialTargets.add(new Position(_position.col, _position.row - 1));
            if (_position.col < 7) {
                potentialTargets.add(new Position(_position.col + 1, _position.row - 1));
            }
            if (_position.col > 0) {
                potentialTargets.add(new Position(_position.col - 1, _position.row - 1));
            }
        }
        if (_position.col < 7) {
            potentialTargets.add(new Position(_position.col + 1, _position.row));
        }
        if (_position.col > 0) {
            potentialTargets.add(new Position(_position.col - 1, _position.row));
        }
    
        int baseRow = (_color == Enums.Color.WHITE ? 0 : 7);
        if (_board.castlingRights(_color, Enums.CastleSide.KINGSIDE)) {
            if (!_board.isCheck() && _board.isSquareEmpty(new Position(5, baseRow)) && !_board.isSquareAttacked(new Position(5, baseRow), Enums.Color.oppositeColor(_color)) && _board.isSquareEmpty(new Position(6, baseRow)) && !_board.isSquareAttacked(new Position(6, baseRow), Enums.Color.oppositeColor(_color))) {
                potentialTargets.add(new Position(6, baseRow));
            }
        }
        if (_board.castlingRights(_color, Enums.CastleSide.QUEENSIDE)) {
            if (!_board.isCheck() && _board.isSquareEmpty(new Position(3, baseRow)) && !_board.isSquareAttacked(new Position(3, baseRow), Enums.Color.oppositeColor(_color)) && _board.isSquareEmpty(new Position(2, baseRow)) && !_board.isSquareAttacked(new Position(2, baseRow), Enums.Color.oppositeColor(_color)) && _board.isSquareEmpty(new Position(1, baseRow))) {
                potentialTargets.add(new Position(2, baseRow));
            }
        }
    
        for (Position target : potentialTargets) {
            Move move = new Move(_position, target, _board);
            if ((_board.isSquareEmpty(target) || !_board.isSquarePieceColor(target, _color)) /* && !_board->isSquareAttacked(target, oppositeColor(_color)) */ && _board.testMoveForLegality(move)) {
                possibleMoves.add(move);
            }
        }
    }
    
    @Override
    public boolean calculateHasLegalMove() {
        if (_position.row < 7) {
            Position target = new Position(_position.col, _position.row + 1);
            if ((_board.isSquareEmpty(target) || !_board.isSquarePieceColor(target, _color)) && _board.testMoveForLegality(new Move(_position, target, _board))) {
                return true;
            }
            if (_position.col < 7) {
                Position target1 = new Position(_position.col + 1, _position.row + 1);
                if ((_board.isSquareEmpty(target1) || !_board.isSquarePieceColor(target1, _color)) && _board.testMoveForLegality(new Move(_position, target1, _board))) {
                    return true;
                }
            }
            if (_position.col > 0) {
                Position target1 = new Position(_position.col - 1, _position.row + 1);
                if ((_board.isSquareEmpty(target1) || !_board.isSquarePieceColor(target1, _color)) && _board.testMoveForLegality(new Move(_position, target1, _board))) {
                    return true;
                }
            }
        }
        if (_position.row > 0) {
            Position target = new Position(_position.col, _position.row - 1);
            if ((_board.isSquareEmpty(target) || !_board.isSquarePieceColor(target, _color)) && _board.testMoveForLegality(new Move(_position, target, _board))) {
                return true;
            }
            if (_position.col < 7) {
                Position target1 = new Position(_position.col + 1, _position.row - 1);
                if ((_board.isSquareEmpty(target1) || !_board.isSquarePieceColor(target1, _color)) && _board.testMoveForLegality(new Move(_position, target1, _board))) {
                    return true;
                }
            }
            if (_position.col > 0) {
                Position target1 = new Position(_position.col - 1, _position.row - 1);
                if ((_board.isSquareEmpty(target1) || !_board.isSquarePieceColor(target1, _color)) && _board.testMoveForLegality(new Move(_position, target1, _board))) {
                    return true;
                }
            }
        }
        if (_position.col < 7) {
            Position target = new Position(_position.col + 1, _position.row);
            if ((_board.isSquareEmpty(target) || !_board.isSquarePieceColor(target, _color)) && _board.testMoveForLegality(new Move(_position, target, _board))) {
                return true;
            }
        }
        if (_position.col > 0) {
            Position target = new Position(_position.col - 1, _position.row);
            if ((_board.isSquareEmpty(target) || !_board.isSquarePieceColor(target, _color)) && _board.testMoveForLegality(new Move(_position, target, _board))) {
                return true;
            }
        }
    
        int baseRow = (_color == Enums.Color.WHITE ? 0 : 7);
        if (_board.castlingRights(_color, Enums.CastleSide.KINGSIDE)) {
            if (!_board.isCheck() && _board.isSquareEmpty(new Position(5, baseRow)) && !_board.isSquareAttacked(new Position(5, baseRow), Enums.Color.oppositeColor(_color)) && _board.isSquareEmpty(new Position(6, baseRow)) && !_board.isSquareAttacked(new Position(6, baseRow), Enums.Color.oppositeColor(_color))) {
                if (_board.testMoveForLegality(new Move(_position, new Position(6, baseRow), _board))) {
                    return true;
                }
            }
        }
        if (_board.castlingRights(_color, Enums.CastleSide.QUEENSIDE)) {
            if (!_board.isCheck() && _board.isSquareEmpty(new Position(3, baseRow)) && !_board.isSquareAttacked(new Position(3, baseRow), Enums.Color.oppositeColor(_color)) && _board.isSquareEmpty(new Position(2, baseRow)) && !_board.isSquareAttacked(new Position(2, baseRow), Enums.Color.oppositeColor(_color)) && _board.isSquareEmpty(new Position(1, baseRow))) {
                if (_board.testMoveForLegality(new Move(_position, new Position(2, baseRow), _board))) {
                    return true;
                }
            }
        }
    
        return false;
    }

    @Override
    public boolean isAttackingSquare(Position square) {
        if (square == _position) {
            return false;
        }
        return Position.areSquaresTouching(_position, square);
    }
}

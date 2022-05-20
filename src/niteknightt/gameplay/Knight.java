package niteknightt.gameplay;

import java.util.*;

public class Knight extends Piece {

    public Knight(Board board) {
        super(Enums.PieceType.KNIGHT, board);
    }

    public Knight(Enums.Color color, int col, int row, Board board) {
        super(Enums.PieceType.KNIGHT, color, col, row, board);
    }

    @Override
    public void getPossibleMoves(List<Move> possibleMoves) {
        List<Position> potentialTargets = new ArrayList<Position>();

        if (_position.col + 1 < 8 && _position.row + 2 < 8) {
            potentialTargets.add(new Position(_position.col + 1, _position.row + 2));
        }
        if (_position.col + 1 < 8 && _position.row - 2 > -1) {
            potentialTargets.add(new Position(_position.col + 1, _position.row - 2));
        }
        if (_position.col + 2 < 8 && _position.row + 1 < 8) {
            potentialTargets.add(new Position(_position.col + 2, _position.row + 1));
        }
        if (_position.col + 2 < 8 && _position.row - 1 > -1) {
            potentialTargets.add(new Position(_position.col + 2, _position.row - 1));
        }
        if (_position.col - 1 > -1 && _position.row + 2 < 8) {
            potentialTargets.add(new Position(_position.col - 1, _position.row + 2));
        }
        if (_position.col - 1 > -1 && _position.row - 2 > -1) {
            potentialTargets.add(new Position(_position.col - 1, _position.row - 2));
        }
        if (_position.col - 2 > -1 && _position.row + 1 < 8) {
            potentialTargets.add(new Position(_position.col - 2, _position.row + 1));
        }
        if (_position.col - 2 > -1 && _position.row - 1 > -1) {
            potentialTargets.add(new Position(_position.col - 2, _position.row - 1));
        }

        for (Position target : potentialTargets) {
            Move move = new Move(_position, target, _board);
            //MAINLOG("Potential move: " << move->algebraicFormat())
            //MAINLOG("isSquareEmpty = " << _board->isSquareEmpty(target))
            //MAINLOG("isSquarePieceColor = " << _board->isSquarePieceColor(target, _color))
            //MAINLOG("testMoveForLegality = " << _board->testMoveForLegality(move))
            if ((_board.isSquareEmpty(target) || !_board.isSquarePieceColor(target, _color)) && _board.testMoveForLegality(move)) {
                possibleMoves.add(move);
            }
        }
    }

    @Override
    public boolean calculateHasLegalMove() {
        if (_position.col + 1 < 8 && _position.row + 2 < 8) {
            Position target = new Position(_position.col + 1, _position.row + 2);
            if ((_board.isSquareEmpty(target) || !_board.isSquarePieceColor(target, _color)) && _board.testMoveForLegality(new Move(_position, target, _board))) {
                return true;
            }
        }
        if (_position.col + 1 < 8 && _position.row - 2 > -1) {
            Position target = new Position(_position.col + 1, _position.row - 2);
            if ((_board.isSquareEmpty(target) || !_board.isSquarePieceColor(target, _color)) && _board.testMoveForLegality(new Move(_position, target, _board))) {
                return true;
            }
        }
        if (_position.col + 2 < 8 && _position.row + 1 < 8) {
            Position target = new Position(_position.col + 2, _position.row + 1);
            if ((_board.isSquareEmpty(target) || !_board.isSquarePieceColor(target, _color)) && _board.testMoveForLegality(new Move(_position, target, _board))) {
                return true;
            }
        }
        if (_position.col + 2 < 8 && _position.row - 1 > -1) {
            Position target = new Position(_position.col + 2, _position.row - 1);
            if ((_board.isSquareEmpty(target) || !_board.isSquarePieceColor(target, _color)) && _board.testMoveForLegality(new Move(_position, target, _board))) {
                return true;
            }
        }
        if (_position.col - 1 > -1 && _position.row + 2 < 8) {
            Position target = new Position(_position.col - 1, _position.row + 2);
            if ((_board.isSquareEmpty(target) || !_board.isSquarePieceColor(target, _color)) && _board.testMoveForLegality(new Move(_position, target, _board))) {
                return true;
            }
        }
        if (_position.col - 1 > -1 && _position.row - 2 > -1) {
            Position target = new Position(_position.col - 1, _position.row - 2);
            if ((_board.isSquareEmpty(target) || !_board.isSquarePieceColor(target, _color)) && _board.testMoveForLegality(new Move(_position, target, _board))) {
                return true;
            }
        }
        if (_position.col - 2 > -1 && _position.row + 1 < 8) {
            Position target = new Position(_position.col - 2, _position.row + 1);
            if ((_board.isSquareEmpty(target) || !_board.isSquarePieceColor(target, _color)) && _board.testMoveForLegality(new Move(_position, target, _board))) {
                return true;
            }
        }
        if (_position.col - 2 > -1 && _position.row - 1 > -1) {
            Position target = new Position(_position.col - 2, _position.row - 1);
            if ((_board.isSquareEmpty(target) || !_board.isSquarePieceColor(target, _color)) && _board.testMoveForLegality(new Move(_position, target, _board))) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isAttackingSquare(Position square) {
        if (square == _position) {
            return false;
        }
        return Position.areSquaresKnightsMoveFromEachOther(_position, square);
    }

}

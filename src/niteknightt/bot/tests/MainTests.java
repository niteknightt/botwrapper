package niteknightt.bot.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import niteknightt.gameplay.Board;
import niteknightt.gameplay.Move;

public class MainTests {
    
    @Test
    void something(){
        Board board = new Board();
        board.setupStartingPosition();
        assertTrue(board.handleMoveForGame(new Move("d2d4", board)));
        assertTrue(board.handleMoveForGame(new Move("g8f6", board)));
        assertTrue(board.handleMoveForGame(new Move("c2c4", board)));
        assertTrue(board.handleMoveForGame(new Move("e7e6", board)));
        assertTrue(board.handleMoveForGame(new Move("g2g3", board)));
        assertTrue(board.handleMoveForGame(new Move("d7d5", board)));
        assertTrue(board.handleMoveForGame(new Move("f1g2", board)));
        assertTrue(board.handleMoveForGame(new Move("d5c4", board)));
        assertTrue(board.handleMoveForGame(new Move("g1f3", board)));
        assertTrue(board.handleMoveForGame(new Move("b8c6", board)));
        assertTrue(board.handleMoveForGame(new Move("d1a4", board)));
        assertTrue(board.handleMoveForGame(new Move("f8b4", board)));
        assertTrue(board.handleMoveForGame(new Move("c1d2", board)));
        assertTrue(board.handleMoveForGame(new Move("f6d5", board)));
        assertTrue(board.handleMoveForGame(new Move("d2b4", board)));
        assertTrue(board.handleMoveForGame(new Move("d5b4", board)));
        assertTrue(board.handleMoveForGame(new Move("a2a3", board)));
        assertTrue(board.handleMoveForGame(new Move("b7b5", board)));
        assertTrue(board.handleMoveForGame(new Move("a4b5", board)));
        assertTrue(board.handleMoveForGame(new Move("b4c2", board)));
        assertTrue(board.handleMoveForGame(new Move("e1f1", board)));
        assertTrue(board.handleMoveForGame(new Move("c8d7", board)));
        assertTrue(board.handleMoveForGame(new Move("a1a2", board)));
        assertTrue(board.handleMoveForGame(new Move("c2d4", board)));
        assertTrue(board.handleMoveForGame(new Move("b5c4", board)));
        assertTrue(board.handleMoveForGame(new Move("e6e5", board)));
        assertTrue(board.handleMoveForGame(new Move("f3d4", board)));
        assertTrue(board.handleMoveForGame(new Move("c6d4", board)));
        assertTrue(board.handleMoveForGame(new Move("c4d5", board)));
        assertTrue(board.handleMoveForGame(new Move("d7e6", board)));
        assertTrue(board.handleMoveForGame(new Move("d5a8", board)));
        assertTrue(board.handleMoveForGame(new Move("d8a8", board)));
        assertTrue(board.handleMoveForGame(new Move("g2a8", board)));
        assertTrue(board.handleMoveForGame(new Move("e6a2", board)));
        assertTrue(board.handleMoveForGame(new Move("b1c3", board)));
        assertTrue(board.handleMoveForGame(new Move("e8e7", board)));
        assertTrue(board.handleMoveForGame(new Move("c3a2", board)));
        assertTrue(board.handleMoveForGame(new Move("h8a8", board)));
        assertTrue(board.handleMoveForGame(new Move("e2e3", board)));
        assertTrue(board.handleMoveForGame(new Move("d4b3", board)));
        assertTrue(board.handleMoveForGame(new Move("f1e2", board)));
        assertTrue(board.handleMoveForGame(new Move("a7a5", board)));
        assertTrue(board.handleMoveForGame(new Move("h1d1", board)));
        assertTrue(board.handleMoveForGame(new Move("c7c6", board)));
        assertTrue(board.handleMoveForGame(new Move("e2d3", board)));
        assertTrue(board.handleMoveForGame(new Move("a8d8", board)));
        assertTrue(board.handleMoveForGame(new Move("d3c2", board)));
        assertTrue(board.handleMoveForGame(new Move("d8d1", board)));
        assertTrue(board.handleMoveForGame(new Move("c2d1", board)));
        assertTrue(board.handleMoveForGame(new Move("b3c5", board)));
        assertTrue(board.handleMoveForGame(new Move("d1c2", board)));
        assertTrue(board.handleMoveForGame(new Move("c5e4", board)));
        assertTrue(board.handleMoveForGame(new Move("f2f4", board)));
        assertTrue(board.handleMoveForGame(new Move("e7d6", board)));
        assertTrue(board.handleMoveForGame(new Move("b2b4", board)));
        assertTrue(board.handleMoveForGame(new Move("e5f4", board)));
        assertTrue(board.handleMoveForGame(new Move("e3f4", board)));
        assertTrue(board.handleMoveForGame(new Move("a5b4", board)));
        assertTrue(board.handleMoveForGame(new Move("a2b4", board)));
        assertTrue(board.handleMoveForGame(new Move("c6c5", board)));
        assertTrue(board.handleMoveForGame(new Move("b4d3", board)));
        assertTrue(board.handleMoveForGame(new Move("f7f6", board)));
        assertTrue(board.handleMoveForGame(new Move("f4f5", board)));
        assertTrue(board.handleMoveForGame(new Move("c5c4", board)));
        assertTrue(board.handleMoveForGame(new Move("d3f4", board)));
        assertTrue(board.handleMoveForGame(new Move("d6c6", board)));
        assertTrue(board.handleMoveForGame(new Move("f4e6", board)));
        assertTrue(board.handleMoveForGame(new Move("g7g5", board)));
        assertTrue(board.handleMoveForGame(new Move("f5g6", board)));
        System.out.println("Done");
    }

    @Test
    void testEnPassantInLegalMoves() {
        Board board = new Board();
        board.setupFromFen("8/7p/2k1Np2/5Pp1/2p1n3/P5P1/2K4P/8 w - g6 0 35");
        List<Move> moves = board.getLegalMoves();
        boolean foundExpectedMove = false;
        for (Move move : moves) {
            if (move.uciFormat().equals("f5g6")) {
                foundExpectedMove = true;
                break;
            }
        }

        assertTrue(foundExpectedMove);
    }
}

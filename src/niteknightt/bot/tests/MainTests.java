package niteknightt.bot.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import niteknightt.gameplay.Board;
import niteknightt.gameplay.Move;
import niteknightt.gameplay.NotationConverter;

public class MainTests {
    
    @Test
    void gameThatFailedToFindEnPassant(){
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

    @Test
    void gameThatFailedToFindAnyMove(){
        Board board = new Board();
        board.setupStartingPosition();
        assertTrue(board.handleMoveForGame(new Move("d2d4", board)));
        assertTrue(board.handleMoveForGame(new Move("g8f6", board)));
        assertTrue(board.handleMoveForGame(new Move("c2c4", board)));
        assertTrue(board.handleMoveForGame(new Move("g7g6", board)));
        assertTrue(board.handleMoveForGame(new Move("c1g5", board)));
        assertTrue(board.handleMoveForGame(new Move("f6e4", board)));
        assertTrue(board.handleMoveForGame(new Move("g5f4", board)));
        assertTrue(board.handleMoveForGame(new Move("c7c5", board)));
        assertTrue(board.handleMoveForGame(new Move("d1c2", board)));
        assertTrue(board.handleMoveForGame(new Move("f7f5", board)));
        assertTrue(board.handleMoveForGame(new Move("f2f3", board)));
        assertTrue(board.handleMoveForGame(new Move("d8a5", board)));
        assertTrue(board.handleMoveForGame(new Move("b1d2", board)));
        assertTrue(board.handleMoveForGame(new Move("e4f6", board)));
        assertTrue(board.handleMoveForGame(new Move("d4d5", board)));
        assertTrue(board.handleMoveForGame(new Move("d7d6", board)));
        assertTrue(board.handleMoveForGame(new Move("e2e3", board)));
        assertTrue(board.handleMoveForGame(new Move("f8g7", board)));
        assertTrue(board.handleMoveForGame(new Move("g1h3", board)));
        assertTrue(board.handleMoveForGame(new Move("b7b5", board)));
        assertTrue(board.handleMoveForGame(new Move("c4b5", board)));
        assertTrue(board.handleMoveForGame(new Move("b8d7", board)));
        assertTrue(board.handleMoveForGame(new Move("f1c4", board)));
        assertTrue(board.handleMoveForGame(new Move("d7b6", board)));
        assertTrue(board.handleMoveForGame(new Move("e1g1", board)));
        assertTrue(board.handleMoveForGame(new Move("f6d5", board)));
        assertTrue(board.handleMoveForGame(new Move("a2a4", board)));
        assertTrue(board.handleMoveForGame(new Move("b6c4", board)));
        assertTrue(board.handleMoveForGame(new Move("d2c4", board)));
        assertTrue(board.handleMoveForGame(new Move("a5b4", board)));
        assertTrue(board.handleMoveForGame(new Move("a1c1", board)));
        assertTrue(board.handleMoveForGame(new Move("c8e6", board)));
        assertTrue(board.handleMoveForGame(new Move("b2b3", board)));
        assertTrue(board.handleMoveForGame(new Move("a7a6", board)));
        assertTrue(board.handleMoveForGame(new Move("e3e4", board)));
        assertTrue(board.handleMoveForGame(new Move("d5c3", board)));
        assertTrue(board.handleMoveForGame(new Move("b5b6", board)));
        assertTrue(board.handleMoveForGame(new Move("e6c4", board)));
        assertTrue(board.handleMoveForGame(new Move("b3c4", board)));
        assertTrue(board.handleMoveForGame(new Move("b4c4", board)));
        assertTrue(board.handleMoveForGame(new Move("g1h1", board)));
        assertTrue(board.handleMoveForGame(new Move("c4a4", board)));
        assertTrue(board.handleMoveForGame(new Move("c2d2", board)));
        assertTrue(board.handleMoveForGame(new Move("a4d4", board)));
        assertTrue(board.handleMoveForGame(new Move("d2e1", board)));
        assertTrue(board.handleMoveForGame(new Move("f5e4", board)));
        assertTrue(board.handleMoveForGame(new Move("h3g5", board)));
        assertTrue(board.handleMoveForGame(new Move("e8d7", board)));
        assertTrue(board.handleMoveForGame(new Move("c1c3", board)));
        assertTrue(!board.getLegalMoves().isEmpty());
        System.out.println("Done");
    }

    @Test
    void gameThatSaidFenDidNotMatch(){
        Board board = new Board();
        board.setupFromFen("8/3r2p1/R5k1/5p2/5BP1/8/5K2/8 b - - 1 168");
        assertEquals("8/3r2p1/R5k1/5p2/5BP1/8/5K2/8 b - - 1 168", board.getFen());
        Move move = new Move("d7d6", board);
        assertTrue(board.handleMoveForGame(move));
        System.out.println("Done");
    }

    @Test
    void checkAlgebraicTranslationOfAllMoves() {
        String fen = "r1bqk2r/1ppp2pp/pbn5/3nPpB1/2Bp4/1QP2N2/PP1N1PPP/R3K2R w KQkq f6 0 10";
        Board board = new Board();
        board.setupFromFen(fen);
        List<Move> legalMoves = board.getLegalMoves();
        NotationConverter converter = new NotationConverter(board);
        for (Move move : legalMoves) {
            converter.handleAlgebraicNotation(move.algebraicFormat());
            assertEquals(move.source().col, converter.sourcecol());
            assertEquals(move.source().row, converter.sourcerow());
            assertEquals(move.target().col, converter.targetcol());
            assertEquals(move.target().row, converter.targetrow());
        }
    }
}

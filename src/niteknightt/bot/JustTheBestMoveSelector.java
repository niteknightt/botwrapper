package niteknightt.bot;

import java.util.*;

import niteknightt.gameplay.Board;
import niteknightt.gameplay.Enums;
import niteknightt.gameplay.Move;

public class JustTheBestMoveSelector extends EngineMoveSelector {

    public JustTheBestMoveSelector(Random random, Enums.EngineAlgorithm algorithm, StockfishClient stockfishClient) {
        super(random, algorithm, stockfishClient);
    }

    public Move selectMove(Board board) throws MoveSelectorException {
        List<Move> legalMoves = board.getLegalMoves();
        Logger.debug(Move.printMovesToString("These are the legal moves", legalMoves));

        String bestMoveUciFormat = "";

        if (legalMoves.size() == 0) {
            Logger.info("Ending game internally because no legal moves for engine");
            return null;
        }

        if (legalMoves.size() == 1) {
            bestMoveUciFormat =  legalMoves.get(0)._uciFormat;
            Logger.debug("Best move for lack of choice: " + bestMoveUciFormat);
        }
        else {
            _stockfishClient.setPosition(board._fen);
            try {
                Date beforeCall = new Date();
                bestMoveUciFormat = _stockfishClient.calcBestMove(5000);
                Date afterCall = new Date();
                long callTime = Math.abs(afterCall.getTime() - beforeCall.getTime());
                Logger.info("justthebest;depth=10;moveNumber=" + board.getFullMoveNumber() + ";numPieces=" + board.getNumPiecesOnBoard() + ";numLegalMoves=" + legalMoves.size() + ";timeMs=" + callTime);
            }
            catch (Exception ex) {
                Logger.error("Exception while calling calcBestMove: " + ex.toString());
            }
            if (bestMoveUciFormat == null || bestMoveUciFormat.length() == 0) {
                Logger.error("Failed to get best move from stockfish");
                throw new RuntimeException("Failed to get best move from stockfish");
            }
        }
        Move engineMove = new Move(bestMoveUciFormat, board);
        return engineMove;
    }
    
}

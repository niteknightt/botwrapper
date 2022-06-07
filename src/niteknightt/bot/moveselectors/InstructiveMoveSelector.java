package niteknightt.bot.moveselectors;

import java.util.*;

import niteknightt.bot.Logger;
import niteknightt.bot.MoveSelectorException;
import niteknightt.bot.MoveWithEval;
import niteknightt.bot.StockfishClient;
import niteknightt.gameplay.Board;
import niteknightt.gameplay.Enums;
import niteknightt.gameplay.Move;

public class InstructiveMoveSelector extends MoveSelector {
    
    public InstructiveMoveSelector(Random random, Enums.EngineAlgorithm algorithm, StockfishClient stockfishClient) {
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
            bestMoveUciFormat =  legalMoves.get(0).uciFormat();
            Logger.debug("Best move for lack of choice: " + bestMoveUciFormat);
        }
        else {
            _stockfishClient.setPosition(board.getFen());
            List<MoveWithEval> movesWithEval = new ArrayList<MoveWithEval>();
            try {
                Date beforeCall = new Date();
                movesWithEval = _stockfishClient.calcMoves(board.getLegalMoves().size(), 2000, board.whosTurnToGo());
                Date afterCall = new Date();
                long callTime = Math.abs(afterCall.getTime() - beforeCall.getTime());
                Logger.info("instructive;depth=10;moveNumber=" + board.getFullMoveNumber() + ";numPieces=" + board.getNumPiecesOnBoard() + ";numLegalMoves=" + legalMoves.size() + ";timeMs=" + callTime);
            }
            catch (Exception ex) {
                Logger.error("Exception while calling calcMoves: " + ex.toString());
            }
            if (movesWithEval.size() == 0) {
                Logger.error("Zero moves from stockfish even though there are legal moves");
                int index = _random.nextInt(legalMoves.size());
                bestMoveUciFormat = legalMoves.get(index).uciFormat();
                Logger.debug("Best random move: " + bestMoveUciFormat);
            }
            else {
                if (movesWithEval.size() != legalMoves.size()) {
                    Logger.error("Number of moves from stockfish (" + movesWithEval.size() + ") is not the same as number of legal moves (" + legalMoves.size() + ")");
                }
                int closestIndex = -1;
                double diff = 1000.0;
                for (int i = 0; i < movesWithEval.size(); ++i) {
                    if (Math.abs(movesWithEval.get(i).eval) < diff) {
                        diff = Math.abs(movesWithEval.get(i).eval);
                        closestIndex = i;
                    }
                }
                bestMoveUciFormat = movesWithEval.get(closestIndex).uci;
                Logger.debug("Best move to keep close to eval zero: " + bestMoveUciFormat);
            }
        }
        Move engineMove = new Move(bestMoveUciFormat, board);
        return engineMove;
    }

    public List<MoveWithEval> getAllMoves(Board board) throws MoveSelectorException {
        List<Move> legalMoves = board.getLegalMoves();
        Logger.debug(Move.printMovesToString("These are the legal moves", legalMoves));

        String bestMoveUciFormat = "";

        if (legalMoves.size() == 0) {
            return null;
        }

        if (legalMoves.size() == 1) {
            bestMoveUciFormat =  legalMoves.get(0).uciFormat();
            MoveWithEval moveWithEval = new MoveWithEval();
            moveWithEval.eval = -1000.0;
            moveWithEval.ismate = false;
            moveWithEval.matein = 0;
            moveWithEval.uci = bestMoveUciFormat;
            return Arrays.asList(moveWithEval);
        }
        else {
            _stockfishClient.setPosition(board.getFen());
            List<MoveWithEval> movesWithEval = new ArrayList<MoveWithEval>();
            try {
                Date beforeCall = new Date();
                movesWithEval = _stockfishClient.calcMoves(board.getLegalMoves().size(), 2000, board.whosTurnToGo());
                Date afterCall = new Date();
                long callTime = Math.abs(afterCall.getTime() - beforeCall.getTime());
                Logger.info("instructive;depth=10;moveNumber=" + board.getFullMoveNumber() + ";numPieces=" + board.getNumPiecesOnBoard() + ";numLegalMoves=" + legalMoves.size() + ";timeMs=" + callTime);
            }
            catch (Exception ex) {
                Logger.error("Exception while calling calcMoves: " + ex.toString());
            }
            if (movesWithEval.size() == 0) {
                Logger.error("Zero moves from stockfish even though there are legal moves");
                int index = _random.nextInt(legalMoves.size());
                bestMoveUciFormat = legalMoves.get(index).uciFormat();
                MoveWithEval moveWithEval = new MoveWithEval();
                moveWithEval.eval = -1000.0;
                moveWithEval.ismate = false;
                moveWithEval.matein = 0;
                moveWithEval.uci = bestMoveUciFormat;
                return Arrays.asList(moveWithEval);
            }
            else {
                if (movesWithEval.size() != legalMoves.size()) {
                    Logger.error("Number of moves from stockfish (" + movesWithEval.size() + ") is not the same as number of legal moves (" + legalMoves.size() + ")");
                }
                return movesWithEval;
            }
        }
    }
    
}

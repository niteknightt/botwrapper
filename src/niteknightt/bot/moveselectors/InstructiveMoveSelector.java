package niteknightt.bot.moveselectors;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
            bestMoveUciFormat =  legalMoves.get(0)._uciFormat;
            Logger.debug("Best move for lack of choice: " + bestMoveUciFormat);
        }
        else {
            _stockfishClient.setPosition(board._fen);
            List<MoveWithEval> movesWithEval = new ArrayList<MoveWithEval>();
            try {
                movesWithEval = _stockfishClient.calcMoves(board.getLegalMoves().size(), 2000, board.whosTurnToGo());
            }
            catch (Exception ex) {
                Logger.error("Exception while calling calcMoves: " + ex.toString());
            }
            if (movesWithEval.size() == 0) {
                Logger.error("Zero moves from stockfish even though there are legal moves");
                int index = _random.nextInt(legalMoves.size());
                bestMoveUciFormat = legalMoves.get(index)._uciFormat;
                Logger.debug("Best random move: " + bestMoveUciFormat);
            }
            else {
                if (movesWithEval.size() != legalMoves.size()) {
                    Logger.error("Number of moves from stockfish (" + movesWithEval.size() + ") is not the same as number of legal moves (" + legalMoves.size() + ")");
                }
                if (_algorithm == Enums.EngineAlgorithm.BEST_MOVE) {
                    bestMoveUciFormat = movesWithEval.get(0).uci;
                }
                else if (_algorithm == Enums.EngineAlgorithm.WORST_MOVE) {
                    bestMoveUciFormat = movesWithEval.get(movesWithEval.size()-1).uci;
                }
                else {
                    Logger.error("Algorithm is not set correctly to play game -- value is " + _algorithm);
                    throw new MoveSelectorException();
                }
                Logger.debug("Best move from multiPV stockfish: " + bestMoveUciFormat);
            }
        }
        Move engineMove = new Move(bestMoveUciFormat, board);
        return engineMove;
    }
    
}
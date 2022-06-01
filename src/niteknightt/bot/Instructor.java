package niteknightt.bot;

import java.util.List;

import niteknightt.gameplay.Board;
import niteknightt.gameplay.Move;
import niteknightt.lichessapi.LichessApiException;
import niteknightt.lichessapi.LichessInterface;

public class Instructor {
    
    public static double BORDER_BETWEEN_MUCH_BETTER_AND_A_LITTLE_BETTER = 1.0;
    public static double BORDER_BETWEEN_A_LITTLE_BETTER_AND_BASICALLY_EQUAL = 0.25;

    public static boolean reviewLastHumanMove(BotGameVsHuman game) {
        List<MoveWithEval> movesAvailableForChallenger = null;

        Board gameBoardClone;
        Move lastMove;

        try {
            gameBoardClone = game.getBoard().clone();
            lastMove = game.getLastMove();
            gameBoardClone.undoMoveForGame(lastMove);
            movesAvailableForChallenger = game.getMoveSelector().getAllMoves(gameBoardClone);
        }
        catch (Exception e) {
            Logger.error("Failed to get available moves for challenger");
            return false;
        }

        int numMovesBetter = 0;
        int numMovesMuchBetter = 0;
        int numMovesALittleBetter = 0;
        int numMovesBasicallyEqual = 0;
        int numMovesEqualOrWorse = 0;

        int challengerMoveIndex = -1;

        // First find the challenger move and eval in the list of legal moves.
        for (int i = 0; i < movesAvailableForChallenger.size(); ++i) {
            MoveWithEval availableMove = movesAvailableForChallenger.get(i);
            if (availableMove.uci.equals(lastMove.uciFormat())) {
                challengerMoveIndex = i;
                break;
            }
        }

        // Handle when the move is not found.
        if (challengerMoveIndex == -1) {
            Logger.error("Failed to find challenger move in list of available moves");
            return false;
        }

        MoveWithEval challengerMoveWithEval = movesAvailableForChallenger.get(challengerMoveIndex);
        boolean bestMove = false;

        if (challengerMoveIndex == 0) {
            bestMove = true;
        }
        else {
            numMovesBetter = challengerMoveIndex;
            for (int i = 0; i < numMovesBetter; ++i) {
                MoveWithEval availableMove = movesAvailableForChallenger.get(i);
                double evalDiff = Math.abs(availableMove.eval - challengerMoveWithEval.eval);
                if (evalDiff > BORDER_BETWEEN_MUCH_BETTER_AND_A_LITTLE_BETTER) {
                    ++numMovesMuchBetter;
                }
                else if (evalDiff > BORDER_BETWEEN_A_LITTLE_BETTER_AND_BASICALLY_EQUAL) {
                    ++numMovesALittleBetter;
                }
                else {
                    ++numMovesBasicallyEqual;
                }
            }
            numMovesEqualOrWorse = movesAvailableForChallenger.size() - numMovesBetter;
        }

        StringBuilder sb = new StringBuilder();
        if (bestMove) {
            sb.append("That was the best move!");
        }
        else if (numMovesMuchBetter == 1) {
            sb.append("There was a much better move you could have made (" + new Move(movesAvailableForChallenger.get(0).uci, gameBoardClone).algebraicFormat() + ").");
            if (challengerMoveWithEval.continuation.length > 0) {
                Move move = new Move(challengerMoveWithEval.uci, gameBoardClone);
                gameBoardClone.handleMoveForGame(move);
                sb.append(" I might now play " + new Move(challengerMoveWithEval.continuation[0], gameBoardClone).algebraicFormat());
                gameBoardClone.undoSingleAnalysisMove(move);
            }
        }
        else if (numMovesMuchBetter > 1) {
            sb.append("There were much better moves you could have made (such as " + new Move(movesAvailableForChallenger.get(0).uci, gameBoardClone).algebraicFormat() + ").");
            if (challengerMoveWithEval.continuation.length > 0) {
                Move move = new Move(challengerMoveWithEval.uci, gameBoardClone);
                gameBoardClone.handleMoveForGame(move);
                sb.append(" I might now play " + new Move(challengerMoveWithEval.continuation[0], gameBoardClone).algebraicFormat());
                gameBoardClone.undoSingleAnalysisMove(move);
            }
        }
        else  if (numMovesALittleBetter == 1) {
            sb.append("There was a slightly better move you could have made (" + new Move(movesAvailableForChallenger.get(0).uci, gameBoardClone).algebraicFormat() + ").");
        }
        else if (numMovesALittleBetter > 1) {
            sb.append("There were slightly better moves you could have made (such as " + new Move(movesAvailableForChallenger.get(0).uci, gameBoardClone).algebraicFormat() + ").");
        }
        else {
            sb.append("That was one of the best moves.");
        }

        try{
            LichessInterface.writeChat(game.getGameId(), sb.toString());
        }
        catch (LichessApiException e) {
            Logger.error("Got LichessApiException while trying to write move evaluation to chat");
        }

        return true;
    }
}

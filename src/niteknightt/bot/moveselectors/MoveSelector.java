package niteknightt.bot.moveselectors;

import java.util.List;
import java.util.Random;

import niteknightt.bot.MoveSelectorException;
import niteknightt.bot.MoveWithEval;
import niteknightt.bot.StockfishClient;
import niteknightt.gameplay.Board;
import niteknightt.gameplay.Enums;
import niteknightt.gameplay.Move;

public abstract class MoveSelector {
    protected Random _random;
    protected StockfishClient _stockfishClient;
    protected Enums.EngineAlgorithm _algorithm;

    public MoveSelector(Random random, Enums.EngineAlgorithm algorithm, StockfishClient stockfishClient) {
        _random = random;
        _stockfishClient = stockfishClient;
        _algorithm = algorithm;
    }

    public abstract List<MoveWithEval> getAllMoves(Board board)
        throws MoveSelectorException;

    public abstract Move selectMove(Board board)
        throws MoveSelectorException;
}

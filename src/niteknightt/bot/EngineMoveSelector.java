package niteknightt.bot;

import java.util.Random;

import niteknightt.gameplay.Board;
import niteknightt.gameplay.Enums;
import niteknightt.gameplay.Move;

public abstract class EngineMoveSelector {
    protected Random _random;
    protected StockfishClient _stockfishClient;
    protected Enums.EngineAlgorithm _algorithm;

    public EngineMoveSelector(Random random, Enums.EngineAlgorithm algorithm, StockfishClient stockfishClient) {
        _random = random;
        _stockfishClient = stockfishClient;
        _algorithm = algorithm;
    }

    public abstract Move selectMove(Board board)
        throws MoveSelectorException;
}

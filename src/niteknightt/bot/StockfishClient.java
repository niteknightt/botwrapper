package niteknightt.bot;

import java.io.*;
import java.util.*;

import net.andreinc.neatchess.client.*;
import net.andreinc.neatchess.client.model.Move;
import niteknightt.gameplay.Enums;

// Most of this code is from: https://www.andreinc.net/2021/04/22/writing-a-universal-chess-interface-client-in-java

public class StockfishClient {

    private Process process = null;
    private BufferedReader reader = null;
    private OutputStreamWriter writer = null;
    UCI uci;
    String fen;
    long defaultTimeout;
    boolean startGameFlag = false;

    public StockfishClient() { }

    public void init(long defaultTimeout) {
        this.defaultTimeout = defaultTimeout;
        uci = new UCI(defaultTimeout);
        uci.startStockfish();
    }

    public void startGame() {
        // do nothing.
        startGameFlag = true;
        uci.uciNewGame();
    }

    public void setPosition(String fen) {
        this.fen = fen;
        uci.positionFen(fen);
    }

    public List<MoveWithEval> calcMoves(int numMoves, long timeoutMs, Enums.Color colorToMove) {
        uci.setOption("MultiPV", Integer.valueOf(numMoves).toString());
        var analysis = uci.analysis(6).getResultOrThrow();
        var moves = analysis.getAllMoves();
        List<MoveWithEval> movesWithEval = new ArrayList<MoveWithEval>();
        for (Map.Entry<Integer,Move> entry : moves.entrySet()) {
            String uciFormat = entry.getValue().getLan();
            Double eval = entry.getValue().getStrength().getScore();
            int matein = 0;
            if (entry.getValue().getStrength().isForcedMate()) {
                matein = entry.getValue().getStrength().getMateIn();
            }
            int breakpoint = -1;
            for (int i = 0; i < movesWithEval.size(); ++i) {
                MoveWithEval moveFromList = movesWithEval.get(i);
                if (colorToMove == Enums.Color.WHITE) {
                    if (matein > 0) {
                        if (!moveFromList.ismate) {
                            breakpoint = i;
                            break;
                        }
                        else if (moveFromList.matein > matein) {
                            breakpoint = i;
                            break;
                        }
                    }
                    else {
                        if (!moveFromList.ismate) {
                            if (colorToMove == Enums.Color.WHITE && moveFromList.eval < eval.doubleValue()) {
                                breakpoint = i;
                                break;
                            }
                            if (colorToMove == Enums.Color.BLACK && moveFromList.eval > eval.doubleValue()) {
                                breakpoint = i;
                                break;
                            }
                        }
                    }
                }
            }
            MoveWithEval newmove = new MoveWithEval();
            newmove.uci = uciFormat;
            newmove.eval = eval;
            newmove.matein = matein;
            newmove.ismate = (matein > 0);
            if (breakpoint == -1) {
                movesWithEval.add(newmove);
            }
            else {
                movesWithEval.add(breakpoint, newmove);
            }
        }

        return movesWithEval;
    }

    public String calcBestMove(long timeoutMs) {
//        var uci = new UCI(defaultTimeout);
//        uci.startStockfish();
//        if (startGameFlag) {
//            uci.uciNewGame();
//        }
//        uci.positionFen(fen);
        var result = uci.bestMove(10).getResultOrThrow();
        return result.getCurrent();
    }

    public void start(String cmd) {
        ProcessBuilder pb = new ProcessBuilder(cmd);
        try {
            this.process = pb.start();
            this.reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            this.writer = new OutputStreamWriter(process.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        if (this.process.isAlive()) {
            this.process.destroy();
        }
        try {
            reader.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

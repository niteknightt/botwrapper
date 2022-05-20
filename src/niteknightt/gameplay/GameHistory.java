package niteknightt.gameplay;

import java.util.Stack;

public class GameHistory {
    
    public void addMove(Move move, String fen) {
        _fens.push(fen);
        _moves.push(move);
    }
    
    public void addCapturedPiece(Piece piece) {
        _capturedPieces.push(piece);
    }

    public void addPromotedPawn(Piece piece) {
        _promotedPawns.push(piece);
    }

    public void undoLastMove() {
        _fens.pop();

        Move move = _moves.peek();
        if (move.isCapture() || move.isEnPassant()) {
            _capturedPieces.pop();
        }
        if (move.isPromotion()) {
            _promotedPawns.pop();
        }
   
       _moves.pop();
    }

    public boolean isEmpty() {
        return _fens.empty();
    }

    public boolean hasLastFen() {
        return !_fens.empty();
    }

    public String lastFen() {
        if (_fens.empty()) {
            throw new RuntimeException("Requested last FEN from history but there are no FENs");
        }
        return _fens.peek();
    }

    public boolean hasLastMove() {
        return !_moves.empty();
    }

    public Move lastMove() {
        if (_moves.empty()) {
            throw new RuntimeException("Requested last move from history but there are no moves");
        }
        return _moves.peek();
    }

    public boolean hasLastCapturedPiece() {
        return !_capturedPieces.empty();
    }

    public Piece lastCapturedPiece() {
        if (_capturedPieces.empty()) {
            throw new RuntimeException("Requested last captured piece from history but there are no captured pieces");
        }
        return _capturedPieces.peek();
    }

    public boolean hasLastPromotedPawn() {
        return !_promotedPawns.empty();
    }

    public Piece lastPromotedPawn() {
        if (_promotedPawns.empty()) {
            throw new RuntimeException("Requested last promoted pawn from history but there are no promoted pawns");
        }
        return _promotedPawns.peek();
    }

    public void clear() {
        while (!_fens.empty())
            _fens.pop();
        while (!_moves.empty())
            _moves.pop();
        while (!_capturedPieces.empty())
            _capturedPieces.pop();
        while (!_promotedPawns.empty())
            _promotedPawns.pop();
    }

    public void addStartupFen(String fen) {
        _fens.push(fen);
    }
    public GameHistory clone() {
        GameHistory clone = new GameHistory();
        //std::shared_ptr<GameHistory> clone = std::make_shared<GameHistory>();
        clone.setFens((Stack<String>)_fens.clone());
        clone.setMoves((Stack<Move>)_moves.clone());
        clone.setCapturedPieces((Stack<Piece>)_capturedPieces.clone());
        clone.setPromotedPawns((Stack<Piece>)_promotedPawns.clone());
        return clone;
    }

    // Methods for clone
    public Stack<String> getFens() { return _fens; }
    public void setFens(Stack<String> val) { _fens = val; }
    public Stack<Move> getMoves() { return _moves; }
    public void setMoves(Stack<Move> val) { _moves = val; }
    public Stack<Piece> getCapturedPieces() { return _capturedPieces; }
    public void setCapturedPieces(Stack<Piece> val) { _capturedPieces = val; }
    public Stack<Piece> getPromotedPawns() { return _promotedPawns; }
    public void setPromotedPawns(Stack<Piece> val) { _promotedPawns = val; }

    public static void copy_reverse(Stack<String> source, Stack<String> dest) {

    }

    public static void CopyStack(Stack<String> source, Stack<String> target) {

    }

    protected Stack<String> _fens = new Stack<String>();
    protected Stack<Move> _moves = new Stack<Move>();
    protected Stack<Piece> _capturedPieces = new Stack<Piece>();
    protected Stack<Piece> _promotedPawns = new Stack<Piece>();
    
}

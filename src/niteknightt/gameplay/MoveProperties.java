package niteknightt.gameplay;

public class MoveProperties {
    protected Move _move; //yes
    protected Enums.Color _color = Enums.Color.WHITE; // implicit from move
    protected boolean _isCastle; // implicit from move
    protected Enums.CastleSide _castleSide; // implicit from move
    protected boolean _isPromotion; // implicit from move
    protected Enums.PieceType _promotionResult = Enums.PieceType.BLANK; // implicit from move
    protected boolean _isCapture; //yes
    protected Enums.PieceType _pieceTypeCaptured = Enums.PieceType.BLANK; //yes
    protected boolean _ruinedCastling[] = new boolean[2]; //yes
    protected boolean _isEnPassant; // implicit from move
    protected boolean _ruinedEnPassant; //yes
    protected Position _ruinedEnPassantTarget = new Position(); //yes
    protected boolean _enabledEnPassant; //yes
    protected Position _enabledEnPassantTarget = new Position(); //yes
    protected boolean _isCheck; // implicit from move
    protected boolean _isMate; // implicit from move
    protected boolean _isStalemate; // implicit from move
    protected String _fenBefore; //yes
    protected String _fenAfter; //yes
    protected int _halfMoveClockBefore; //yes
    protected int _halfMoveClockAfter; //yes
    protected int _fullMoveNumberBefore; //yes
    protected int _fullMoveNumberAfter; //yes

    public void setMove(Move move) {
        _move = move;
        _color = move.color();
        _isCastle = move.isCastle();
        _castleSide = move.castleSide();
        _isPromotion = move.isPromotion();
        _promotionResult = move.promotionResult();
        _isEnPassant = move.isEnPassant();
        _isCheck = move.isCheck();
        _isMate = move.isMate();
        //_isStalemate = move.isStalemate();
    }

    public void setRuinedCastling(Enums.CastleSide castleSide, boolean val) { _ruinedCastling[castleSide.getValue()] = val; }
    public void setFenBefore(String fen) { _fenBefore = fen; }
    public void setFenAfter(String fen) { _fenAfter = fen; }
    public void setRuinedEnPassant(boolean val) { _ruinedEnPassant = val; }
    public void setRuinedEnPassantTarget(Position pos) { _ruinedEnPassantTarget = pos; }
    public void setEnabledEnPassant(boolean val) { _enabledEnPassant = val; }
    public void setEnabledEnPassantTarget(Position pos) { _enabledEnPassantTarget = pos; }
    public void setHalfMoveClockBefore(int val) { _halfMoveClockBefore = val; }
    public void setHalfMoveClockAfter(int val) { _halfMoveClockAfter = val; }
    public void setFullMoveNumberBefore(int val) { _fullMoveNumberBefore = val; }
    public void setFullMoveNumberAfter(int val) { _fullMoveNumberAfter = val; }
    public void setIsCapture(boolean val) { _isCapture = val; }
    public void setPieceTypeCaptured(Enums.PieceType pieceType) { _pieceTypeCaptured = pieceType; }
    public void setIsPromotion(boolean val) { _isPromotion = val; }

    public Move move() { return _move; }
    public boolean enabledEnPassant() { return _enabledEnPassant; }
    public boolean ruinedEnPassant() { return _ruinedEnPassant; }
    public Position ruinedEnPassantTarget() { return _ruinedEnPassantTarget; }
    public int halfMoveClockBefore() { return _halfMoveClockBefore; }
    public boolean ruinedCastling(int index) { return _ruinedCastling[index]; }
    public String fenBefore() { return _fenBefore; }
}

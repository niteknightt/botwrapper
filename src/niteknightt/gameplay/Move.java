package niteknightt.gameplay;

import java.util.*;

import niteknightt.gameplay.Enums.PieceType;

public class Move {

    public Move() {

    }

    public Move(Position source, Position target, PieceType promotionResult, Board board) {
        _board = board;
        _source = new Position(source.col, source.row);
        _target = new Position(target.col, target.row);
        _promotionResult = promotionResult;
        _isPromotion = (promotionResult != PieceType.BLANK);
        _init();
    }

//    public Move(Position source, Position target, Board board) {
//        this(source, target, PieceType.BLANK, board);
//    }

    public Move(String moveText, Board board) {
        _board = board;
        _init(moveText);
    }
        
    public Position source() { return _source; }
    public Position target() { return _target; }
    public Enums.Color color() { return _color; }
    public Enums.PieceType pieceType() { return _pieceType; }
    public boolean isCapture() { return _isCapture; }
    public Enums.PieceType pieceTypeCaptured() { return _pieceTypeCaptured; }
    public boolean isPromotion() { return _isPromotion; }
    public Enums.PieceType promotionResult() { return _promotionResult; }
    public boolean isCastle() { return _isCastle; }
    public boolean isEnPassant() { return _isEnPassant; }
    public Enums.CastleSide castleSide() { return _castleSide; }
    public boolean isCheck() { return _isCheck; }
    public boolean isMate() { return _isMate; }
    //public boolean isStalemate() { return _isStalemate; }

    public String uciFormat() {
        if (_uciFormat == null || _uciFormat.trim().isEmpty()) {
            _calculateUciFormat();
        }
        return _uciFormat;
    }

    public String algebraicFormat() {
        if (_algebraicFormat == null || _algebraicFormat.trim().isEmpty()) {
            _calculateAlgebraicFormat();
        }
        return _algebraicFormat;
    }

    protected void _init() {
        _init("");
    }

    protected void _init(String text) {
        if (text == null || text.trim().isEmpty()) {
            _initFromPosition();
        }
        else if (_isTextUciFormat(text)) {
            _initFromUci(text);
        }
        else {
            _initFromAlgebraic(text);
        }
    }

    protected void _calculateRemainingInternalItems() {
        _findColor();
        _findPieceType();
        _calculateIsCaptureAndPieceTypeCaptured();
        _calculateIsCastleAndCastleSide();
        _calculateIsEnPassant();
    }

    protected void _initFromPosition() {
        _calculateSourceAndTargetFromPosition();
        _calculateRemainingInternalItems();
        if ((_board.isSquareEmpty(_target) || !_board.isSquarePieceColor(_target, _color)) && _board.testMoveForLegality(this)) {
            _calculateOtherInfoFromPosition();
        }
    }

    protected void _initFromUci(String uciFormat) {
        _uciFormat = uciFormat;
        _calculateSourceAndTargetFromUci();
        _calculateRemainingInternalItems();
        if ((_board.isSquareEmpty(_target) || !_board.isSquarePieceColor(_target, _color)) && _board.testMoveForLegality(this)) {
            _calculateOtherInfoFromUci();
        }
    }

    protected void _initFromAlgebraic(String algebraicFormat) {
        _algebraicFormat = algebraicFormat;
        _calculateSourceAndTargetFromAlgebraic();
        _calculateRemainingInternalItems();
        if ((_board.isSquareEmpty(_target) || !_board.isSquarePieceColor(_target, _color)) && _board.testMoveForLegality(this)) {
            _calculateOtherInfoFromAlgebraic();
        }
    }

    protected boolean _isTextUciFormat(String text) {
        if (text == null) {
            throw new RuntimeException("Received null text when initializing move");
        }

        if (text.length() < 4) {
            return false;
        }

        if (text.charAt(0) >= 'a' && text.charAt(0) <= 'h' &&
            text.charAt(2) >= 'a' && text.charAt(2) <= 'h' &&
            text.charAt(1) >= '1' && text.charAt(1) <= '8' &&
            text.charAt(3) >= '1' && text.charAt(3) <= '8') {
                return true;
        }

        return false;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Move)) {
            return false;
        }

        Move casted = (Move)other;
        return (_source.equals(casted.source()) && _target.equals(casted.target()));
    }

    protected void _findColor() {
        _color = _board.pieceAt(_source).color();
    }

    protected void _findPieceType() {
        _pieceType = _board.pieceAt(_source).pieceType();
    }

    protected void _calculateIsCaptureAndPieceTypeCaptured() {
        _isCapture = (!_board.pieceAt(_target).isBlankPiece() && _board.pieceAt(_target).color() != _board.pieceAt(_source).color());
        if (_isCapture) {
            _pieceTypeCaptured = _board.pieceAt(_target).pieceType();
        }
    }

    protected void _calculateIsCastleAndCastleSide() {
        _isCastle = (_pieceType == Enums.PieceType.KING && _source.row == _target.row && Math.abs(_source.col - _target.col) > 1);
        if (_isCastle) {
            _castleSide = (_target.col == 6 ? Enums.CastleSide.KINGSIDE : Enums.CastleSide.QUEENSIDE);
        }
    }

    protected void _calculateIsEnPassant() {
        _isEnPassant = (_pieceType == Enums.PieceType.PAWN && _board.hasEnPassantTarget() && _board.getEnPassantTarget().equals(_target));
    }

    protected void _calculateSourceAndTargetFromPosition() {

    }

    protected void _calculateOtherInfoFromPosition() {
        // This is the only place in this class where the board
        // is modified, if only temporarily.
        CheckCheckmateStalemate result = _board.testMoveForCheckCheckmateStalemate(this);
        _isMate = result.checkmate;
        _isCheck = result.check;
        _isStalemate = result.stalemate;
    }

    protected void _calculateSourceAndTargetFromUci() {
        _source = Position.uciToInternal(_uciFormat.substring(0, 2));
        _target = Position.uciToInternal(_uciFormat.substring(2, 4));
    }

    protected void _calculateOtherInfoFromUci() {
        if (_uciFormat.length() == 5) {
            _isPromotion = true;
            if (_uciFormat.charAt(4) == 'q') {
                _promotionResult = Enums.PieceType.QUEEN;
            }
            else if (_uciFormat.charAt(4) == 'b') {
                _promotionResult = Enums.PieceType.BISHOP;
            }
            else if (_uciFormat.charAt(4) == 'n' || _uciFormat.charAt(4) == 'k') {
                _promotionResult = Enums.PieceType.KNIGHT;
            }
            else if (_uciFormat.charAt(4) == 'r') {
                _promotionResult = Enums.PieceType.ROOK;
            }
        }
        else {
            _isPromotion = false;
            _promotionResult = Enums.PieceType.BLANK;
        }

        // This is the only place in this class where the board
        // is modified, if only temporarily.
        CheckCheckmateStalemate result = _board.testMoveForCheckCheckmateStalemate(this);
        _isMate = result.checkmate;
        _isCheck = result.check;
        _isStalemate = result.stalemate;
    }

    protected void _calculateSourceAndTargetFromAlgebraic() {
        NotationConverter converter = new NotationConverter(_board);
        converter.handleAlgebraicNotation(_algebraicFormat);
        _source = new Position(converter.sourcecol(), converter.sourcerow());
        _target = new Position(converter.targetcol(), converter.targetrow());
    }

    protected void _calculateOtherInfoFromAlgebraic() {
        NotationConverter converter = new NotationConverter(_board);
        converter.handleAlgebraicNotation(_algebraicFormat);
        _isPromotion = converter.isPromotion();
        _promotionResult = converter.promotionResult();
        _isCheck = converter.isCheck();
        _isMate = converter.isMate();
    }

    protected void _calculateUciFormat() {
        _uciFormat = "";
        _uciFormat += (char)('a' + _source.col);
        _uciFormat += (char)('1' + _source.row);
        _uciFormat += (char)('a' + _target.col);
        _uciFormat += (char)('1' + _target.row);
        if (_isPromotion) {
            if (_promotionResult == Enums.PieceType.QUEEN) {
                _uciFormat += 'q';
            }
            else if (_promotionResult == Enums.PieceType.ROOK) {
                _uciFormat += 'r';
            }
            else if (_promotionResult == Enums.PieceType.BISHOP) {
                _uciFormat += 'b';
            }
            else if (_promotionResult == Enums.PieceType.KNIGHT) {
                _uciFormat += 'n';
            }
        }
    }

    protected void _calculateAlgebraicFormat() {
        _algebraicFormat = new String();
        if (_pieceType == Enums.PieceType.KING) {
            _algebraicFormat += 'K';
        }
        else if (_pieceType == Enums.PieceType.QUEEN) {
            _algebraicFormat += 'Q';
        }
        else if (_pieceType == Enums.PieceType.ROOK) {
            _algebraicFormat += 'R';
        }
        else if (_pieceType == Enums.PieceType.BISHOP) {
            _algebraicFormat += 'B';
        }
        else if (_pieceType == Enums.PieceType.KNIGHT) {
            _algebraicFormat += 'N';
        }
        else if (_pieceType == Enums.PieceType.PAWN) {
            if (_isCapture || _isEnPassant) {
                _algebraicFormat += (char)('a' + _source.col);
            }
        }
        if (_pieceType != Enums.PieceType.PAWN && _pieceType != Enums.PieceType.KING) {
            List<Piece> allAttackers = _board.getAllAttackerOfTypeOnSquare(_pieceType, _color, _target);
            boolean sameRank = false;
            boolean sameFile = false;
            for (Piece attacker : allAttackers) {
                if (attacker.position().row == _source.row && attacker.position().col != _source.col) {
                    sameRank = true;
                }
                if (attacker.position().col == _source.col && attacker.position().row != _source.row) {
                    sameFile = true;
                }
            }
            if (sameRank) {
                _algebraicFormat += (char)('a' + _source.col);
            }
            if (sameFile) {
                _algebraicFormat += (char)('1' + _source.row);
            }
        }
        if (_isCapture || _isEnPassant) {
            _algebraicFormat += 'x';
        }
        _algebraicFormat += (char)('a' + _target.col);
        _algebraicFormat += (char)('1' + _target.row);
        if (_isPromotion) {
            _algebraicFormat += '=';
            if (_promotionResult == Enums.PieceType.QUEEN) {
                _algebraicFormat += 'Q';
            }
            else if (_promotionResult == Enums.PieceType.ROOK) {
                _algebraicFormat += 'R';
            }
            else if (_promotionResult == Enums.PieceType.BISHOP) {
                _algebraicFormat += 'B';
            }
            else if (_promotionResult == Enums.PieceType.KNIGHT) {
                _algebraicFormat += 'N';
            }
            else {
                _algebraicFormat += 'X';
            }
        }
        if (_isCheck) {
            _algebraicFormat += '+';
        }
        if (_isMate) {
            _algebraicFormat += '#';
        }
    }

    public static List<Move> parseListOfUCIMoves(String uciMoves, Board board) {
        List<Move> moves = new ArrayList<Move>();
        String moveSRs[] = uciMoves.split(" ");
        for (int i = 0; i < moveSRs.length; ++i) {
            Move move = new Move(moveSRs[i], board);
            moves.add(move);
        }

        return moves;
    }

    public static String printMovesToString(String prefix, List<Move> moves) {
        StringBuilder sb = new StringBuilder();
        sb.append(prefix);
        sb.append(":");
        for (Move move : moves) {
            sb.append(" ");
            sb.append(move.toString());
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return _algebraicFormat;
    }

    protected Position _source = new Position();
    protected Position _target = new Position();
    protected Board _board = new Board();
    protected Enums.Color _color = Enums.Color.WHITE;
    protected Enums.PieceType _pieceType = Enums.PieceType.BLANK;
    protected boolean _isCapture;
    protected Enums.PieceType _pieceTypeCaptured = Enums.PieceType.BLANK;
    protected boolean _isPromotion;
    protected Enums.PieceType _promotionResult = Enums.PieceType.BLANK;
    protected boolean _isCastle;
    protected Enums.CastleSide _castleSide = Enums.CastleSide.KINGSIDE;
    protected String _uciFormat = new String();
    protected String _algebraicFormat = new String();
    protected boolean _isEnPassant;
    protected boolean _isCheck;
    protected boolean _isMate;
    protected boolean _isStalemate;
    
}

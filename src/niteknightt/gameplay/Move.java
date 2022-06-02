package niteknightt.gameplay;

import java.util.*;

import niteknightt.gameplay.Enums.PieceType;

public class Move {

    public Move() {

    }

    public Move(Position source, Position target, Board board) {
        _board = board;
        _source = new Position(source.col, source.row);
        _target = new Position(target.col, target.row);
        _promotionResult = Enums.PieceType.BLANK;
        _ruinedEnPassant = false;
        _enabledEnPassant = false;
        _calculateUciFormat();
        _findColor();
        _findPieceType();
        _calculateIsCapture();
        _calculateIsPromotion();
        _calculateIsCastle();
        _calculateIsEnPassant();
        _calculateAlgebraicFormat();
    }

    public Move(String uciFormat, Board board) {
        _board = board;
        _uciFormat = uciFormat;
        _ruinedEnPassant = false;
        _enabledEnPassant = false;
        _calculateInternalValuesWithUci();
        _findColor();
        _findPieceType();
        _calculateIsCapture();
        _calculateIsCastle();
        _calculateIsEnPassant();
        _calculateAlgebraicFormat();
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
    public void setRuinedEnPassant(boolean val) { _ruinedEnPassant = val; }
    public void setRuinedEnPassantTarget(Position pos) { _ruinedEnPassantTarget = pos; }
    public void setEnabledEnPassant(boolean val) { _enabledEnPassant = val; }
    public void setEnabledEnPassantTarget(Position pos) { _enabledEnPassantTarget = pos; }
    public Enums.CastleSide castleSide() { return _castleSide; }
    public String uciFormat() { return _uciFormat; }
    public String algebraicFormat() { return _algebraicFormat; }
    public boolean ruinedCastling(int index) { return _ruinedCastling[index]; }
    public boolean ruinedEnPassant() { return _ruinedEnPassant; }
    public Position ruinedEnPassantTarget() { return _ruinedEnPassantTarget; }
    public boolean enabledEnPassant() { return _enabledEnPassant; }
    public Position enabledEnPassantTarget() { return _enabledEnPassantTarget; }

    protected void _init(String text, Board board) {
        if (_isTextUciFormat(text)) {
            _initFromUci(text, board);
        }
        else {
            _initFromAlgebraic(text, board);
        }
    }

    protected void _initFromUci(String uciFormat, Board board) {
        _board = board;
        _uciFormat = uciFormat;
        _ruinedEnPassant = false;
        _enabledEnPassant = false;
        _calculateInternalValuesWithUci();
        _findColor();
        _findPieceType();
        _calculateIsCapture();
        _calculateIsCastle();
        _calculateIsEnPassant();
        _calculateAlgebraicFormat();
    }

    protected void _initFromAlgebraic(String algebraicFormat, Board board) {

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

    void setPromotionResult(Enums.PieceType val) {
        if (!_isPromotion) {
            //MAINLOG("ERROR: Setting promotion result for move that is not promotion: " << _algebraicFormat)
            throw new RuntimeException("ERROR: Setting promotion result for move that is not promotion");
        }
    
        _promotionResult = val;
        _calculateUciFormat();
        _calculateAlgebraicFormat();
    }

    void setRuinedCastling(Enums.CastleSide castleSide, boolean val) { _ruinedCastling[castleSide.getValue()] = val; }

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

    protected void _calculateIsCapture() {
        _isCapture = (!_board.pieceAt(_target).isBlankPiece() && _board.pieceAt(_target).color() != _board.pieceAt(_source).color());
        if (_isCapture) {
            _pieceTypeCaptured = _board.pieceAt(_target).pieceType();
        }
    }

    protected void _calculateIsPromotion() {
        _isPromotion = (_pieceType == Enums.PieceType.PAWN && (_target.row == 0 || _target.row == 7));
    }

    protected void _calculateIsCastle() {
        _isCastle = (_pieceType == Enums.PieceType.KING && _source.row == _target.row && Math.abs(_source.col - _target.col) > 1);
        if (_isCastle) {
            _castleSide = (_target.col == 6 ? Enums.CastleSide.KINGSIDE : Enums.CastleSide.QUEENSIDE);
        }
    }

    protected void _calculateIsEnPassant() {
        _isEnPassant = (_pieceType == Enums.PieceType.PAWN && _board.hasEnPassantTarget() && _board.getEnPassantTarget().equals(_target));
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
        else {
            _promotionResult = Enums.PieceType.BLANK;
        }
    }

    protected void _calculateInternalValuesWithUci() {
        _source = Position.uciToInternal(_uciFormat.substring(0, 2));
        _target = Position.uciToInternal(_uciFormat.substring(2, 4));
        
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
    }

    protected void _calculateInternalValuesWithAlgebraic() {
        _isCheck = false;
        _isMate = false;
        _isCastle = false;
        _castleSide = Enums.CastleSide.KINGSIDE;
        _isPromotion = false;
        _promotionResult = PieceType.BLANK;

        NotationConverter converter = new NotationConverter(_board);
        converter.handleAlgebraicNotation(_algebraicFormat);
        String text = _algebraicFormat;
        int charsToIgnore = 0;
        if (_algebraicFormat.endsWith("+")) {
            _isCheck = true;
        }
        else if (_algebraicFormat.endsWith("#")) {
            _isMate = true;
        }
        if (_algebraicFormat.startsWith("O-O-O")) {
            _isCastle = true;
            _castleSide = Enums.CastleSide.QUEENSIDE;
        }
        else if (_algebraicFormat.startsWith("O-O")) {
            _isCastle = true;
            _castleSide = Enums.CastleSide.KINGSIDE;
        }

        _color = _board.whosTurnToGo();

        if (_isCastle) {
            if (_castleSide == Enums.CastleSide.KINGSIDE) {
                if (_color == Enums.Color.WHITE) {
                    _source = new Position(4, 0);
                    _target = new Position(6, 0);
                }
                else {
                    _source = new Position(4, 7);
                    _target = new Position(6, 7);
                }
            }
            else {
                if (_color == Enums.Color.WHITE) {
                    _source = new Position(4, 0);
                    _target = new Position(2, 0);
                }
                else {
                    _source = new Position(4, 7);
                    _target = new Position(2, 7);
                }
            }
            return;
        }

        int numExtraCharsAtEnd = 0;
        if (_isMate || _isCheck) {
            ++numExtraCharsAtEnd;
        }
        if (_isPromotion) {
            numExtraCharsAtEnd += 2;
        }

        char firstChar = _algebraicFormat.charAt(0);
        char secondChar = _algebraicFormat.charAt(1);
        if (firstChar >= 'a' && firstChar <= 'h') {
            // Pawn move
            if (secondChar == 'x') {
                // Capture with pawn
                int capturedRank = Character.getNumericValue(_algebraicFormat.charAt(3));
                int increment = (_color == Enums.Color.WHITE ? -1 : 1);
                _source = new Position(firstChar - 'a', capturedRank - 1 + increment);
                _target = new Position(_algebraicFormat.charAt(2) - 'a', capturedRank - 1);
            }
            else {
                // Pawn push
                int targetRank = Character.getNumericValue(secondChar);
                _target = new Position(firstChar - 'a', targetRank - 1);
                int increment = (_color == Enums.Color.WHITE ? -1 : 1);
                boolean done = false;
                boolean found = false;
                int foundRank = -1;
                int rankToCheck = targetRank + increment;
                while (!done) {
                    if (Math.abs(rankToCheck - targetRank) > 2 || rankToCheck == 0 || rankToCheck == 9) {
                        break;
                    }
                    Position posToCheck = new Position(firstChar - 'a', rankToCheck - 1);
                    Piece pieceAtPos = _board.pieceAt(posToCheck);
                    if (pieceAtPos.pieceType() != Enums.PieceType.BLANK) {
                        if (pieceAtPos.pieceType() != Enums.PieceType.PAWN) {
                            break;
                        }
                        if (pieceAtPos.color() != _color) {
                            break;
                        }
                        found = true;
                        foundRank = rankToCheck;
                        break;
                    }
                }
                if (!found) {
                    throw new RuntimeException("Failed to find original position of pawn push");
                }
                if (foundRank - targetRank == 2 && foundRank != 7) {
                    throw new RuntimeException("Found pawn push 2 squares but original position not on 7th rank");
                }
                if (targetRank - foundRank == 2 && foundRank != 2) {
                    throw new RuntimeException("Found pawn push 2 squares but original position not on 2nd rank");
                }
                _source = new Position(firstChar - 'a', foundRank - 1);
            }
        }
        else if (firstChar == 'N') {
            char thirdChar = _algebraicFormat.charAt(2);
            if (thirdChar >= 'a' && thirdChar <= 'h') {
                // Both knights could have made the move

            }
            if (_algebraicFormat.length() >= 4) {
                char fourthChar = _algebraicFormat.charAt(3);

            }
        }



        int lastCharPosition = _algebraicFormat.length() - 1 - charsToIgnore;
        char lastChar = _algebraicFormat.charAt(lastCharPosition);
        char penultimateChar = _algebraicFormat.charAt(lastCharPosition - 1);
        if (penultimateChar == '=' &&
            (lastChar == 'B' || lastChar == 'N' || lastChar == 'R' || lastChar == 'Q')) {
                _isPromotion = true;
                charsToIgnore += 2;
        }

        _source = Position.uciToInternal(_uciFormat.substring(0, 2));
        _target = Position.uciToInternal(_uciFormat.substring(2, 4));
        
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
                //_algebraicFormat += 'a';
                //_algebraicFormat += _source.col;
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
    public String _uciFormat = new String();
    protected String _algebraicFormat = new String();
    protected boolean _ruinedCastling[] = new boolean[2];
    protected boolean _isEnPassant;
    protected boolean _ruinedEnPassant;
    protected Position _ruinedEnPassantTarget = new Position();
    protected boolean _enabledEnPassant;
    protected Position _enabledEnPassantTarget = new Position();
    protected boolean _isCheck;
    protected boolean _isMate;
    
}

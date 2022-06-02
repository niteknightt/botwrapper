package niteknightt.gameplay;
import java.util.*;

import niteknightt.bot.Logger;
import niteknightt.gameplay.Enums.PieceType;


public class Board {
    
    protected List<Piece> _pieces = new ArrayList<Piece>();
    protected Enums.Color _whosTurnToGo = Enums.Color.WHITE;
    protected boolean _hasEnPassantTarget;
    protected Position _enPassantTarget = new Position();
    protected int _halfMoveClock;
    protected int _fullMoveNumber;
    protected boolean _isCheck;
    protected String _fen;
    protected GameHistory _gameHistory = new GameHistory();
    protected List<Move> _legalMoves = new ArrayList<Move>();
    protected boolean _hasLegalMove;
    protected boolean _castlingRights[] = new boolean[4];
    protected boolean _castled[] = new boolean[2];
    protected int _prevHalfMoveClock;

    public Board() {

    }

    public void setupStartingPosition() {
        setupFromFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    }

    public void setupFromFen(String fen) {
        //MAINLOG("Setting up board from FEN: " << fen)

        _pieces.clear();
        _gameHistory.clear();
        _gameHistory.addStartupFen(fen);
        
        Piece.CreateBlankPiece(this); // TODO: This should not be necessary -- the Piece class should automatically create the BlankPiece object.
        _pieces.clear();
        for (int i = 0; i < 64; ++i) {
            _pieces.add(Piece.GetBlankPiece());
        }
        
        int pos = 0;
        int col = 0;
        int row = 7;
        char currChar = fen.charAt(pos);
        while (currChar == ' ') {
            currChar = fen.charAt(++pos);
        }
        while (currChar != ' ') {
            if (currChar == 'r') {
                _pieces.set(Position.internalToIndex(new Position(col, row)),  Piece.createPiece(Enums.PieceType.ROOK, Enums.Color.BLACK, col, row, this));
                ++col;
            }
            else if (currChar == 'n') {
                _pieces.set(Position.internalToIndex(new Position(col, row)), Piece.createPiece(Enums.PieceType.KNIGHT, Enums.Color.BLACK, col, row, this));
                ++col;
            }
            else if (currChar == 'b') {
                _pieces.set(Position.internalToIndex(new Position(col, row)), Piece.createPiece(Enums.PieceType.BISHOP, Enums.Color.BLACK, col, row, this));
                ++col;
            }
            else if (currChar == 'q') {
                _pieces.set(Position.internalToIndex(new Position(col, row)), Piece.createPiece(Enums.PieceType.QUEEN, Enums.Color.BLACK, col, row, this));
                ++col;
            }
            else if (currChar == 'k') {
                _pieces.set(Position.internalToIndex(new Position(col, row)), Piece.createPiece(Enums.PieceType.KING, Enums.Color.BLACK, col, row, this));
                ++col;
            }
            else if (currChar == 'p') {
                _pieces.set(Position.internalToIndex(new Position(col, row)), Piece.createPiece(Enums.PieceType.PAWN, Enums.Color.BLACK, col, row, this));
                ++col;
            }
            else if (currChar == 'R') {
                _pieces.set(Position.internalToIndex(new Position(col, row)), Piece.createPiece(Enums.PieceType.ROOK, Enums.Color.WHITE, col, row, this));
                ++col;
            }
            else if (currChar == 'N') {
                _pieces.set(Position.internalToIndex(new Position(col, row)), Piece.createPiece(Enums.PieceType.KNIGHT, Enums.Color.WHITE, col, row, this));
                ++col;
            }
            else if (currChar == 'B') {
                _pieces.set(Position.internalToIndex(new Position(col, row)), Piece.createPiece(Enums.PieceType.BISHOP, Enums.Color.WHITE, col, row, this));
                ++col;
            }
            else if (currChar == 'Q') {
                _pieces.set(Position.internalToIndex(new Position(col, row)), Piece.createPiece(Enums.PieceType.QUEEN, Enums.Color.WHITE, col, row, this));
                ++col;
            }
            else if (currChar == 'K') {
                _pieces.set(Position.internalToIndex(new Position(col, row)), Piece.createPiece(Enums.PieceType.KING, Enums.Color.WHITE, col, row, this));
                ++col;
            }
            else if (currChar == 'P') {
                _pieces.set(Position.internalToIndex(new Position(col, row)), Piece.createPiece(Enums.PieceType.PAWN, Enums.Color.WHITE, col, row, this));
                ++col;
            }
            else if (currChar == '/') {
                --row;
                col = 0;
            }
            else if (currChar >= '1' && currChar <= '8') {
                col += (currChar - '0');
            }
            currChar = fen.charAt(++pos);
        }
        currChar = fen.charAt(++pos);
        if (currChar == 'w') {
            _whosTurnToGo = Enums.Color.WHITE;
        } else {
            _whosTurnToGo = Enums.Color.BLACK;
        }
        currChar = fen.charAt(++pos);
        currChar = fen.charAt(++pos);
        setCastlingRights(Enums.Color.WHITE, Enums.CastleSide.KINGSIDE, false);
        setCastlingRights(Enums.Color.WHITE, Enums.CastleSide.QUEENSIDE, false);
        setCastlingRights(Enums.Color.BLACK, Enums.CastleSide.KINGSIDE, false);
        setCastlingRights(Enums.Color.BLACK, Enums.CastleSide.QUEENSIDE, false);
        while (currChar != ' ') {
            if (currChar == 'K') {
                setCastlingRights(Enums.Color.WHITE, Enums.CastleSide.KINGSIDE, true);
            }
            else if (currChar == 'k') {
                setCastlingRights(Enums.Color.BLACK, Enums.CastleSide.KINGSIDE, true);
            }
            else if (currChar == 'Q') {
                setCastlingRights(Enums.Color.WHITE, Enums.CastleSide.QUEENSIDE, true);
            }
            else if (currChar == 'q') {
                setCastlingRights(Enums.Color.BLACK, Enums.CastleSide.QUEENSIDE, true);
            }
            currChar = fen.charAt(++pos);
        }

        currChar = fen.charAt(++pos);
        if (currChar == '-') {
            setHasEnPassantTarget(false);
            setEnPassantTarget(null);
            currChar = fen.charAt(++pos);
        }
        else {
            String algebraicTarget = "" + currChar;
            currChar = fen.charAt(++pos);
            algebraicTarget += currChar;
            Position enPassantTargetPos = Position.uciToInternal(algebraicTarget);
            setHasEnPassantTarget(true);
            setEnPassantTarget(enPassantTargetPos);
            currChar = fen.charAt(++pos);
        }

        currChar = fen.charAt(++pos);
        String halfMoveClockText = "";
        while (currChar != ' ') {
            halfMoveClockText += currChar;
            currChar = fen.charAt(++pos);
        }
        setHalfMoveClock(Integer.parseInt(halfMoveClockText));

        currChar = fen.charAt(++pos);
        String fullMoveNumberText = "";
        while (currChar != ' ') {
            fullMoveNumberText += currChar;
            if (pos == fen.length() - 1) {
                break;
            }
            currChar = fen.charAt(++pos);
        }
        setFullMoveNumber(Integer.parseInt(fullMoveNumberText));

        _calculateFen();
        _calculateLegalMoves();    
        _calculateHasLegalMove();
    }

    public boolean handleMoveForGame(Move move) {
        //MAINLOG("Handling move for full analysis " << move.algebraicFormat())
        if (!_isMoveLegal(move)) {
            return false;
        }
        _handleMove(move);
        _calculateLegalMoves();
        _calculateHasLegalMove();
        return true;
    }

    public void undoSingleAnalysisMove(Move move) {
        //MAINLOG("Undoing single analysis move " << move.algebraicFormat())
        _undoMove(move);
        _calculateHasLegalMove();
    }

    public void undoMoveForGame(Move move) {
        //MAINLOG("Undoing single analysis move " << move.algebraicFormat())
        _undoMove(move);
        _calculateLegalMoves();    
        _calculateHasLegalMove();
    }

    public boolean isSquareAttacked(Position square, Enums.Color attackerColor) {
        for (Piece piece : _pieces) {
            if (piece.pieceType() == Enums.PieceType.BLANK || piece.color() != attackerColor) {
                continue;
            }
            if (piece.isAttackingSquare(square)) {
                return true;
            }
        }
        return false;
    }

    public boolean canSquaresSeeEachOtherOnColumn(Position pos1, Position pos2) {
        int col = pos1.col;
        int startRow = Math.min(pos1.row, pos2.row);
        int endRow = Math.max(pos1.row, pos2.row);
    
        for (int i = startRow + 1; i < endRow; ++i) {
            if (_pieces.get(Position.internalToIndex(new Position(col, i))).pieceType() != Enums.PieceType.BLANK) {
                return false;
            }
        }
        return true;
    }

    public boolean canSquaresSeeEachOtherOnRow(Position pos1, Position pos2) {
        int row = pos1.row;
        int startCol = Math.min(pos1.col, pos2.col);
        int endCol = Math.max(pos1.col, pos2.col);
    
        for (int i = startCol + 1; i < endCol; ++i) {
            if (_pieces.get(Position.internalToIndex(new Position(i, row))).pieceType() != Enums.PieceType.BLANK) {
                return false;
            }
        }
        return true;
    }

    public boolean canSquaresSeeEachOtherOnDiagonalUp(Position pos1, Position pos2) {
        Position startPos = pos1;
        Position endPos = pos2;
        if (pos2.col < pos1.col) {
            startPos = pos2;
            endPos = pos1;
        }
    
        Position curPos = new Position(startPos.col + 1, startPos.row + 1);
        while (curPos.col < endPos.col && curPos.row < endPos.row) {
            if (_pieces.get(Position.internalToIndex(curPos)).pieceType() != Enums.PieceType.BLANK) {
                return false;
            }
            curPos = new Position(curPos.col + 1, curPos.row + 1);
        }
        return true;
    }

    public boolean canSquaresSeeEachOtherOnDiagonalDown(Position pos1, Position pos2) {
        Position startPos = pos1;
        Position endPos = pos2;
        if (pos2.col < pos1.col) {
            startPos = pos2;
            endPos = pos1;
        }
    
        Position curPos = new Position(startPos.col + 1, startPos.row - 1);
        while (curPos.col < endPos.col && curPos.row > endPos.row) {
            if (_pieces.get(Position.internalToIndex(curPos)).pieceType() != Enums.PieceType.BLANK) {
                return false;
            }
            curPos = new Position(curPos.col + 1, curPos.row - 1);
        }
        return true;
    }

    public boolean isSquareEmpty(Position pos) {
        return (_pieces.get(Position.internalToIndex(pos)).pieceType() == Enums.PieceType.BLANK);
    }

    public boolean isSquarePieceColor(Position pos, Enums.Color color) {
        return (!isSquareEmpty(pos) && _pieces.get(Position.internalToIndex(pos)).color() == color);
    }

    public boolean testMoveForLegality(Move move) {
        if (_pieces.get(Position.internalToIndex(move.target())).pieceType() == Enums.PieceType.KING) {
            // Cannot capture a king!
            return false;
        }
    
        //MAINLOG("Handling move for legality test " << move.algebraicFormat())
        _handleMove(move);
    
        boolean result = !_scanForCheck(true); // Check if the player that just moved is in check, which would make the move illegal.
    
        //MAINLOG("Undoing legality test move " << move.algebraicFormat())
        _undoMove(move);
    
        return result;
    }

    public Piece pieceAt(Position position) {
        return _pieces.get(Position.internalToIndex(position));
    }

    public List<Piece> getAllAttackerOfTypeOnSquare(Enums.PieceType pieceType, Enums.Color color, Position target) {
        List<Piece> attackers = new ArrayList<Piece>();
        for (Piece piece : _pieces) {
            if (piece.pieceType() != pieceType || piece.color() != color) {
                continue;
            }
            if (piece.isAttackingSquare(target)) {
                attackers.add(piece);
            }
        }
    
        return attackers;
    }

    public boolean isCheckmate() {
        if (!_isCheck) {
            return false;
        }
        return !_hasLegalMove;
    }

    public boolean isStalemate() {
        if (_isCheck) {
            return false;
        }
        return !_hasLegalMove;
    }

    public List<Move> getLegalMoves() {
        return _legalMoves;
    }

    public boolean _isMoveLegal(Move move) {
        for (Move legalMove : _legalMoves) {
            if (legalMove.equals(move)) {
                return true;
            }
        }

        return false;
    }
    protected void _calculateLegalMoves() {
        _legalMoves.clear();
        for (Piece piece : _pieces) {
            if (piece.pieceType() == Enums.PieceType.BLANK || piece.color() != _whosTurnToGo) {
                continue;
            }
    
            List<Move> pieceMoves = new ArrayList<Move>();
            piece.getPossibleMoves(pieceMoves);
            _legalMoves.addAll(pieceMoves);
        }
    
        _hasLegalMove = (_legalMoves.size() > 0);
    
        //MAINLOG_NNL("List of legal response moves (" << _legalMoves.size() << "):")
        //for (Move move : _legalMoves) {
        //    MAINLOG_NNL(" " << move.algebraicFormat())
        //}
        //MAINLOG("")
    }

    protected void _calculateHasLegalMove() {
        for (Piece piece : _pieces) {
            if (piece.pieceType() == Enums.PieceType.BLANK || piece.color() != _whosTurnToGo) {
                continue;
            }
    
            if (piece.calculateHasLegalMove()) {
                _hasLegalMove = true;
                return;
            }
    
            _hasLegalMove = false;
        }
    }

    public boolean castlingRights(Enums.Color color, Enums.CastleSide castleSide) {
        return _castlingRights[color.getValue() * 2 + castleSide.getValue()];
    }

    public void setCastlingRights(Enums.Color color, Enums.CastleSide castleSide, boolean val) {
        _castlingRights[color.getValue() * 2 + castleSide.getValue()] = val;
    }

    public Board clone() {
        Board clone = new Board();
        clone.addAllPieces(_pieces);
        clone.setWhosTurnToGo(_whosTurnToGo);
        clone.setHasEnPassantTarget(_hasEnPassantTarget);
        if (_hasEnPassantTarget) {
            clone.setEnPassantTarget(_enPassantTarget);
        }
        clone.setHalfMoveClock(_halfMoveClock);
        clone.setFullMoveNumber(_fullMoveNumber);
        clone.setIsCheck(_isCheck);
        clone.setFen(_fen);
        clone.setGameHistory(_gameHistory.clone());
        clone.addAllLegalMoves(_legalMoves);
        clone.setHasLegalMove(_hasLegalMove);
        clone.setCastlingRights(_castlingRights);
        clone.setCastled(_castled);
        clone.setPrevHalfMoveClock(_prevHalfMoveClock);
    
        return clone;
    }

    public List<Piece> pieces() { return _pieces; }

    public Position getEnPassantTarget() { return _enPassantTarget; }

    public boolean hasEnPassantTarget() { return _hasEnPassantTarget; }

    public boolean isCheck() { return _isCheck; }

    public String getFen() { return _fen; }

    public Enums.Color whosTurnToGo() { return _whosTurnToGo; }

    public int getFullMoveNumber() { return _fullMoveNumber; }

    public int getNumPiecesOnBoard() {
        int total = 0;
        for (Piece piece : _pieces) {
            if (piece.pieceType() != Enums.PieceType.BLANK) {
                ++total;
            }
        }
        return total;
    }

    public void addAllPieces(List<Piece> pieces) {
        _pieces.clear();
        for (Piece piece : pieces) {
            if (piece.pieceType() == Enums.PieceType.BLANK) {
                _pieces.add(piece);
            }
            else {
                Piece newPiece = piece.clone(this);
                _pieces.add(newPiece);
            }
        }
    }

    public void setWhosTurnToGo(Enums.Color color) { _whosTurnToGo = color; }

    public void setHasEnPassantTarget(boolean hasTarget) { _hasEnPassantTarget = hasTarget; }

    public void setEnPassantTarget(Position target) { _enPassantTarget = target; }

    public void setHalfMoveClock(int clock) { _halfMoveClock = clock; }

    public void setFullMoveNumber(int num) { _fullMoveNumber = num; }

    public void setIsCheck(boolean check) { _isCheck = check; }

    public void setFen(String fenStr) { _fen = fenStr; }

    public void setGameHistory(GameHistory history) { _gameHistory = history; }

    public void addAllLegalMoves(List<Move> moves) { moves.addAll(_legalMoves); }

    public void setHasLegalMove(boolean hasLegal) { _hasLegalMove = hasLegal; }

    public void setCastlingRights(boolean[] val) { for (int i = 0; i < 4; ++i) { _castlingRights[i] = val[i]; }}

    public void setCastled(boolean[] val) { for (int i = 0; i < 2; ++i) { _castled[i] = val[i]; }}

    public void setPrevHalfMoveClock(int clock) { _prevHalfMoveClock = clock; }

    public Piece getKing(Enums.Color color) {
        for (Piece piece : _pieces) {
            if (piece.pieceType() == Enums.PieceType.KING && piece.color() == color) {
                return piece;
            }
        }
        throw new RuntimeException("Failed to find king of color " + color.toString());
    }
    protected void _handleMove(Move move) {
        Position startPos = move.source();
        Position endPos = move.target();
        int startIndex = Position.internalToIndex(startPos);
        int endIndex = Position.internalToIndex(endPos);
    
        Piece movedPiece = _pieces.get(startIndex);
    
        move.setRuinedCastling(Enums.CastleSide.KINGSIDE, false);
        move.setRuinedCastling(Enums.CastleSide.QUEENSIDE, false);
    
        if (move.isCastle() || move.pieceType() == Enums.PieceType.KING) {
            for (int i = 0; i < 2; ++i) {
                if (castlingRights(move.color(), Enums.CastleSide.values()[i])) {
                    setCastlingRights(move.color(), Enums.CastleSide.values()[i], false);
                    move.setRuinedCastling(Enums.CastleSide.values()[i], true);
                }
            }
        }
        else if (move.pieceType() == Enums.PieceType.ROOK) {
            int baseRow = (move.color() == Enums.Color.WHITE ? 0 : 7);
            if (move.source().row == baseRow && (move.source().col == 0 || move.source().col == 7)) {
                Enums.CastleSide castleSide = (move.source().col == 0 ? Enums.CastleSide.QUEENSIDE : Enums.CastleSide.KINGSIDE);
                if (castlingRights(move.color(), castleSide)) {
                    setCastlingRights(move.color(), castleSide, false);
                    move.setRuinedCastling(castleSide, true);
                }
            }
        }
    
        if (_hasEnPassantTarget) {
            move.setRuinedEnPassant(true);
            move.setRuinedEnPassantTarget(_enPassantTarget);
        }
    
        _hasEnPassantTarget = false;
        boolean pawnMove = false;
        
        if (movedPiece.pieceType() == Enums.PieceType.PAWN) {
            pawnMove = true;
            if (Math.abs(endPos.row - startPos.row) == 2) {
                _hasEnPassantTarget = true;
                _enPassantTarget = new Position(endPos.col, (startPos.row == 1 ? startPos.row + 1 : startPos.row - 1));
                move.setEnabledEnPassant(true);
                move.setEnabledEnPassantTarget(_enPassantTarget);
            }
        }
    
        _prevHalfMoveClock = _halfMoveClock;
    
        if (pawnMove || move.isCapture()) {
            _halfMoveClock = 0;
        }
    
        if (_whosTurnToGo == Enums.Color.BLACK) {
            ++_fullMoveNumber;
        }
        
        _whosTurnToGo = Enums.Color.oppositeColor(_whosTurnToGo);
    
        if (move.isPromotion()) {
            if (move.isCapture()) {
                _gameHistory.addCapturedPiece(_pieces.get(endIndex));
            }
            _pieces.set(endIndex, Piece.createPiece(move.promotionResult(), movedPiece.color(), endPos.col, endPos.row, this));
            _gameHistory.addPromotedPawn(_pieces.get(startIndex));
            _pieces.set(startIndex, Piece.GetBlankPiece());
        }
        else if (move.isCastle()) {
            Position rookStartPos = new Position(0, startPos.row);
            Position rookEndPos = new Position(0, startPos.row);
    
            if (endPos.col > startPos.col) {
                rookStartPos.col = 7;
                rookEndPos.col = 5;
            }
            else {
                rookStartPos.col = 0;
                rookEndPos.col = 3;
            }
    
            // King
            _pieces.set(Position.internalToIndex(endPos), _pieces.get(Position.internalToIndex(startPos)));
            _pieces.set(Position.internalToIndex(startPos), Piece.GetBlankPiece());
            _pieces.get(Position.internalToIndex(endPos)).setPosition(endPos.col, endPos.row);
    
            // Rook
            _pieces.set(Position.internalToIndex(rookEndPos), _pieces.get(Position.internalToIndex(rookStartPos)));
            _pieces.set(Position.internalToIndex(rookStartPos), Piece.GetBlankPiece());
            _pieces.get(Position.internalToIndex(rookEndPos)).setPosition(rookEndPos.col, rookEndPos.row);
        }
        else {
            if (move.isCapture()) {
                _gameHistory.addCapturedPiece(_pieces.get(endIndex));
            }
            else if (move.isEnPassant()) {
                Position enPassantPawnActualPos = new Position(_enPassantTarget.col, (move.color() == Enums.Color.WHITE ? _enPassantTarget.row - 1 : _enPassantTarget.row + 1));
                _gameHistory.addCapturedPiece(_pieces.get(Position.internalToIndex(enPassantPawnActualPos)));
                _pieces.set(Position.internalToIndex(enPassantPawnActualPos), Piece.GetBlankPiece());
            }
            _pieces.set(endIndex, _pieces.get(startIndex));
            _pieces.get(endIndex).setPosition(endPos.col, endPos.row);
            _pieces.set(startIndex, Piece.GetBlankPiece());
        }
    
        _isCheck = _scanForCheck();
    
        _calculateFen();
//        Logger.info("Handled move: " + move.algebraicFormat() + " FEN: " + _fen);
    
        _gameHistory.addMove(move, _fen);
    }

    protected void _undoMove(Move move) {
        if (_gameHistory.isEmpty()) {
            //MAINLOG("ERROR: Attempt to undo move when hisory is empty");
            throw new RuntimeException("ERROR: Attempt to undo move when hisory is empty");
        }
    
        Move lastMove = _gameHistory.lastMove();
    
        if (!lastMove.equals(move)) {
            //MAINLOG("ERROR: Last move " << lastmove.algebraicFormat() << " does not match undo move " << move.algebraicFormat());
            throw new RuntimeException("ERROR: Last move does not match undo move");
        }
    
        Piece lastCapturedPiece = Piece.GetBlankPiece(); // TODO: Not sure if this will work.
        if (lastMove.isCapture() || lastMove.isEnPassant()) {
            if (!_gameHistory.hasLastCapturedPiece()) {
                //MAINLOG("ERROR: No last captured piece but last move was a capture");
                throw new RuntimeException("ERROR: No last captured piece but last move was a capture");
            }
            lastCapturedPiece = _gameHistory.lastCapturedPiece();
        }
        else {
            lastCapturedPiece = Piece.GetBlankPiece();
        }
        Piece lastPromotedPawn = Piece.GetBlankPiece(); // TODO: Not sure if this will work.
        if (lastMove.isPromotion()) {
            if (!_gameHistory.hasLastPromotedPawn()) {
                //MAINLOG("ERROR: No last promoted pawn but last move was a promotion");
                throw new RuntimeException("ERROR: No last promoted pawn but last move was a promotion");
            }
            lastPromotedPawn = _gameHistory.lastPromotedPawn();
        }
        else {
            lastPromotedPawn = Piece.GetBlankPiece();
        }
    
        _gameHistory.undoLastMove();
    
        if (_gameHistory.isEmpty()) {
            //MAINLOG("ERROR: History is empty after undoing last move");
            throw new RuntimeException("ERROR: History is empty after undoing last move");
        }
    
        if (!_gameHistory.hasLastFen()) {
            //MAINLOG("ERROR: Attempt to get last FEN from history returned false after deleting previous move");
            throw new RuntimeException("ERROR: Attempt to get last FEN from history returned false after deleting previous move");
        }
    
        String lastFen = _gameHistory.lastFen();
    
        //MAINLOG("Undoing move " << lastmove.algebraicFormat())
    
        _calculateFen();
    
        Position startPos = lastMove.source();
        Position endPos = lastMove.target();
        int startIndex = Position.internalToIndex(startPos);
        int endIndex = Position.internalToIndex(endPos);
    
        if (lastMove.isPromotion()) {
            _pieces.set(startIndex, lastPromotedPawn);
            if (lastMove.isCapture()) {
                _pieces.set(endIndex, lastCapturedPiece);
            }
            else {
                _pieces.set(endIndex, Piece.GetBlankPiece());
            }
        }
        else if (lastMove.isCastle()) {
            _pieces.set(startIndex, _pieces.get(endIndex));
            _pieces.get(startIndex).setPosition(startPos.col, startPos.row);
            int rookStartCol = (lastMove.castleSide() == Enums.CastleSide.KINGSIDE ? 7 : 0);
            int rookEndCol = (lastMove.castleSide() == Enums.CastleSide.KINGSIDE ? 5 : 3);
            int kingStartCol = 4;
            int kingEndCol = (lastMove.castleSide() == Enums.CastleSide.KINGSIDE ? 6 : 2);
            int emptyCol1 = (lastMove.castleSide() == Enums.CastleSide.KINGSIDE ? 5 : 3);
            int emptyCol2 = (lastMove.castleSide() == Enums.CastleSide.KINGSIDE ? 6 : 2);
            int row = startPos.row;
    
            // Move the king back and update the piece's internal position
            _pieces.set(Position.internalToIndex(new Position(rookStartCol, row)), _pieces.get(Position.internalToIndex(new Position(rookEndCol, row))));
            _pieces.get(Position.internalToIndex(new Position(rookStartCol, row))).setPosition(rookStartCol, row);
    
            // Move the rook back and update the piece's internal position
            _pieces.set(Position.internalToIndex(new Position(kingStartCol, row)), _pieces.get(Position.internalToIndex(new Position(kingEndCol, row))));
            _pieces.get(Position.internalToIndex(new Position(kingStartCol, row))).setPosition(kingStartCol, row);
    
            // Set the squares they vacated to blank pieces
            _pieces.set(Position.internalToIndex(new Position(emptyCol1, row)), Piece.GetBlankPiece());
            _pieces.set(Position.internalToIndex(new Position(emptyCol2, row)), Piece.GetBlankPiece());
        }
        else if (lastMove.isCapture()) {
            _pieces.set(startIndex, _pieces.get(endIndex));
            _pieces.get(startIndex).setPosition(startPos.col, startPos.row);
            _pieces.set(endIndex, lastCapturedPiece);
        }
        else if (lastMove.isEnPassant()) {
            _pieces.set(startIndex, _pieces.get(endIndex));
            _pieces.get(startIndex).setPosition(startPos.col, startPos.row);
            Position locationOfCapturedPawn = new Position(endPos.col, (lastMove.color() == Enums.Color.WHITE ? endPos.row - 1 : endPos.row + 1));
            _pieces.set(Position.internalToIndex(locationOfCapturedPawn), lastCapturedPiece);
            _pieces.set(endIndex, Piece.GetBlankPiece());
        }
        else {
            _pieces.set(startIndex, _pieces.get(endIndex));
            _pieces.get(startIndex).setPosition(startPos.col, startPos.row);
            _pieces.set(endIndex, Piece.GetBlankPiece());
        }
    
        if (lastMove.enabledEnPassant()) {
            _hasEnPassantTarget = false;
            _enPassantTarget = new Position(0, 0);
        }
        if (lastMove.ruinedEnPassant()) {
            _hasEnPassantTarget = true;
            _enPassantTarget = lastMove.ruinedEnPassantTarget();
        }
    
        if (lastMove.color() == Enums.Color.BLACK) {
            --_fullMoveNumber;
        }
    
        _whosTurnToGo = lastMove.color();
        _halfMoveClock = _prevHalfMoveClock; // TODO: This won't work. Need to add halfMoveClock to Move object like the other items that are undone.
    
        for (int i = 0; i < 2; ++i) {
            if (lastMove.ruinedCastling(i)) {
                setCastlingRights(lastMove.color(), Enums.CastleSide.values()[i], true);
            }
        }
    
        _calculateFen();
        //MAINLOG("FEN: " << _fen)
    
        if (!_fen.equals(lastFen)) {
            Logger.error("Fen does not match after undo");
            Logger.error("Expected fen: " + lastFen);
            Logger.error("Fen after undo: " + _fen);
            Logger.error("Move was " + lastMove.algebraicFormat());

            throw new RuntimeException("ERROR: Fen does not match after undo");
        }
    
        _isCheck = _scanForCheck();    
    }

    protected boolean _scanForCheck() {
        return _scanForCheck(false);
    }

    protected boolean _scanForCheck(boolean otherPlayer) {
        // Find the king
        Piece king = Piece.GetBlankPiece(); // TODO: Not sure this works
        for (Piece piece : _pieces) {
            if (piece.pieceType() == Enums.PieceType.KING && piece.color() == (otherPlayer ? Enums.Color.oppositeColor(_whosTurnToGo) : _whosTurnToGo)) {
                king = piece;
                break;
            }
        }

        if (king.pieceType() != Enums.PieceType.KING) {
            throw new RuntimeException("ERROR: Failed to find king");
        }
        return isSquareAttacked(king.position(), Enums.Color.oppositeColor(king.color()));
    }

    protected void _calculateFen() {
        StringBuffer sstr = new StringBuffer();
        for (int row = 7; row >= 0; --row) {
            int col = 0;
            int blankCount = 0;
            while (col < 8) {
                int index = Position.internalToIndex(new Position(col, row));
                if (_pieces.get(index).pieceType() != Enums.PieceType.BLANK) {
                    if (blankCount > 0) {
                        sstr.append(blankCount);
                    }
                    blankCount = 0;
                }
                if (_pieces.get(index).pieceType() == Enums.PieceType.PAWN) {
                    sstr.append(_pieces.get(index).color() == Enums.Color.WHITE ? 'P' : 'p');
                }
                else if (_pieces.get(index).pieceType() == Enums.PieceType.KNIGHT) {
                    sstr.append(_pieces.get(index).color() == Enums.Color.WHITE ? 'N' : 'n');
                }
                else if (_pieces.get(index).pieceType() == Enums.PieceType.BISHOP) {
                    sstr.append(_pieces.get(index).color() == Enums.Color.WHITE ? 'B' : 'b');
                }
                else if (_pieces.get(index).pieceType() == Enums.PieceType.ROOK) {
                    sstr.append(_pieces.get(index).color() == Enums.Color.WHITE ? 'R' : 'r');
                }
                else if (_pieces.get(index).pieceType() == Enums.PieceType.QUEEN) {
                    sstr.append(_pieces.get(index).color() == Enums.Color.WHITE ? 'Q' : 'q');
                }
                else if (_pieces.get(index).pieceType() == Enums.PieceType.KING) {
                    sstr.append(_pieces.get(index).color() == Enums.Color.WHITE ? 'K' : 'k');
                }
                else {
                    ++blankCount;
                }
                ++col;
            }
            if (blankCount > 0) {
                sstr.append(blankCount);
            }
            if (row != 0) {
                sstr.append('/');
            }
        }
        sstr.append(' ');
        if (_whosTurnToGo == Enums.Color.WHITE) {
            sstr.append('w');
        }
        else {
            sstr.append('b');
        }
        sstr.append(' ');
        boolean someoneCanCastle = false;
        if (castlingRights(Enums.Color.WHITE, Enums.CastleSide.KINGSIDE)) {
            sstr.append('K');
            someoneCanCastle = true;
        }
        if (castlingRights(Enums.Color.WHITE, Enums.CastleSide.QUEENSIDE)) {
            sstr.append('Q');
            someoneCanCastle = true;
        }
        if (castlingRights(Enums.Color.BLACK, Enums.CastleSide.KINGSIDE)) {
            sstr.append('k');
            someoneCanCastle = true;
        }
        if (castlingRights(Enums.Color.BLACK, Enums.CastleSide.QUEENSIDE)) {
            sstr.append('q');
            someoneCanCastle = true;
        }
        if (!someoneCanCastle) {
            sstr.append('-');
        }
        sstr.append(' ');
        if (_hasEnPassantTarget) {
            String pos = Position.internalToUci(_enPassantTarget);
            
            sstr.append(pos);
        }
        else {
            sstr.append('-');
        }
        sstr.append(' ');
        sstr.append(_halfMoveClock);
        sstr.append(' ');
        sstr.append(_fullMoveNumber);
        _fen = sstr.toString();
    }
}

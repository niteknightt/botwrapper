package niteknightt.gameplay;

import java.util.List;

import niteknightt.common.Helpers;

public class NotationConverter {

    private Board _board;
    private Enums.Color _color;
    private int _sourcerow;
    private int _sourcecol;
    private int _targetrow;
    private int _targetcol;

    public final Enums.Color WHITE = Enums.Color.WHITE;
    public final Enums.Color BLACK = Enums.Color.BLACK;

    public NotationConverter(Board board) {
        _board = board.clone();
        _color = _board.whosTurnToGo();
    }

    private void _algebraicHelperPawnPush(String text, int length, int netLength) {
        _targetcol = text.charAt(0) - 'a';
        _validateCol(_targetcol);
        _targetrow = Character.getNumericValue(text.charAt(1)) - 1;
        _validateRow(_targetrow);
        if (_targetrow < 2 && _color == WHITE) {
            throw new RuntimeException("Target row of pawn push is " + (_targetrow + 1) + " and color is white");
        }
        if (_targetrow > 5 && _color == BLACK) {
            throw new RuntimeException("Target row of pawn push is " + (_targetrow + 1) + " and color is black");
        }
        _sourcecol = _targetcol;
        int appender = (_color == Enums.Color.WHITE ? -1 : 1);
        Piece pieceOneRankBeforeTarget = _board.pieceAt(new Position(_sourcecol, _targetrow + appender));
        if (pieceOneRankBeforeTarget.pieceType() == Enums.PieceType.BLANK) {
            Piece pieceTwoRanksBeforeTarget = _board.pieceAt(new Position(_sourcecol, _targetrow + appender * 2));
            if (pieceTwoRanksBeforeTarget.pieceType() != Enums.PieceType.PAWN ||
                pieceTwoRanksBeforeTarget.color() != _color) {
                    throw new RuntimeException("For pawn push, no piece in the rank before the target and a non-pawn or non-correct-color piece two ranks before the target");
            }
            _sourcerow = _targetrow + appender * 2;
        }
        else if (pieceOneRankBeforeTarget.pieceType() != Enums.PieceType.PAWN ||
                 pieceOneRankBeforeTarget.color() != _color) {
                     throw new RuntimeException("For pawn push, piece in the rank before the target is not a pawn or is the wrong color");
        }
        else {
            _sourcerow = _targetrow + appender;
        }
    }

    private void _algebraicHelperPawnCapture(String text, int length, int netLength) {
        if (netLength != 4 || text.charAt(1) != 'x' ||
            !(text.charAt(2) >= 'a' && text.charAt(2) <= 'h') ||
            !(text.charAt(3) >= '1' && text.charAt(3) <= '8')) {
                throw new RuntimeException("Move must be a pawn capture but it doesn't have that format");
        }
        _sourcecol = text.charAt(0) - 'a';
        _targetcol = text.charAt(2) - 'a';
        _targetrow = text.charAt(3) - '1';
        int appender = (_color == Enums.Color.WHITE ? -1 : 1);
        _sourcerow = _targetrow + appender;
        _validateRow(_sourcerow);
    }

    private void _algebraicHelperNonPawnMove(String text, int length, int netLength, boolean hasColSpecificity, boolean hasRowSpecificity, int specificCol, int specificRow) {
        if (text.charAt(0) == 'K') {
            Piece king = _board.getKing(_color);
            _sourcecol = king.position().col;
            _sourcerow = king.position().row;
            return;
        }
        Position targetPos = new Position(_targetcol, _targetrow);
        List<Piece> potentialPieces = _board.getAllAttackerOfTypeOnSquare(Helpers.letterToPieceType(text.charAt(0)), _color, targetPos);
        int pieceIndex = 0;
        if (hasColSpecificity && hasRowSpecificity) {
            if (potentialPieces.size() < 3) {
                throw new RuntimeException("Move has col and row specificity but there are less than 3 potential pieces for the move");
            }
            for (int i = 0; i < potentialPieces.size(); ++i) {
                if (potentialPieces.get(i).position().col == specificCol &&
                    potentialPieces.get(i).position().row == specificRow) {
                        pieceIndex = i;
                        break;
                }
            }
        }
        else if (hasColSpecificity) {
            if (potentialPieces.size() == 1) {
                throw new RuntimeException("Move has col specificity but there are less than 2 potential pieces for the move");
            }
            for (int i = 0; i < potentialPieces.size(); ++i) {
                if (potentialPieces.get(i).position().col == specificCol) {
                    pieceIndex = i;
                    break;
                }
            }
        }
        else if (hasRowSpecificity) {
            if (potentialPieces.size() == 1) {
                throw new RuntimeException("Move has row specificity but there is only one potential piece for the move");
            }
            for (int i = 0; i < potentialPieces.size(); ++i) {
                if (potentialPieces.get(i).position().row == specificRow) {
                    pieceIndex = i;
                    break;
                }
            }
        }
        else {
            if (potentialPieces.size() > 1) {
                throw new RuntimeException("Move does not have specificity but there is more than one potential piece for the move");
            }
        }
        _sourcecol = potentialPieces.get(pieceIndex).position().col;
        _sourcerow = potentialPieces.get(pieceIndex).position().row;
    }

    protected boolean _checkForCastling(String text) {
        int row = (_color == WHITE ? 0 : 7);
        if (text.equals("O-O")) {
            _sourcerow = row;
            _targetrow = row;
            _sourcecol = 4;
            _targetcol = 6;
            return true;
        }
        if (text.equals("O-O-O")) {
            _sourcerow = row;
            _targetrow = row;
            _sourcecol = 4;
            _targetcol = 2;
            return true;
        }
        return false;
    }

    public void handleAlgebraicNotation(String text) {
        if (_checkForCastling(text)) {
            return;
        }
        
        int length = text.length();
        int netLength = length; // Length of the text of the actual move, without extra stuff at the end

        if (text.charAt(length - 1) == '+' || text.charAt(length - 1) == '#') {
            // Check or checkmate
            --netLength;
        }
        if (text.charAt(netLength - 2) == '=') {
            // Promotion
            netLength -= 2;
        }

        if (netLength == 2) {
            // Pawn push one or two squares
            _algebraicHelperPawnPush(text, length, netLength);
        }
        else if (text.charAt(0) >= 'a' && text.charAt(0) <= 'h') {
            // Pawn capture
            _algebraicHelperPawnCapture(text, length, netLength);
        }
        else {
            int targetStartPos = netLength - 2;
            if (targetStartPos <= 0) {
                throw new RuntimeException("Got bad targetStartPos " + targetStartPos + " for non-pawn move");
            }
            _targetcol = text.charAt(targetStartPos) - 'a';
            _targetrow = text.charAt(targetStartPos + 1) - '1';
            boolean isCapture = (text.charAt(targetStartPos - 1) == 'x');
            boolean hasTwoSpecificity = false;
            boolean hasOneSpecificity = false;
            boolean hasColSpecificity = false;
            boolean hasRowSpecificity = false;
            int specificCol = -1;
            int specificRow = -1;
            if (isCapture) {
                if (targetStartPos == 4) {
                    hasTwoSpecificity = true;
                }
                else if (targetStartPos == 3) {
                    hasOneSpecificity = true;
                }
                else if (targetStartPos != 2) {
                    throw new RuntimeException("Non-pawn move with target at position " + targetStartPos + " and is a capture");
                }
            }
            else {
                if (targetStartPos == 3) {
                    hasTwoSpecificity = true;
                }
                if (targetStartPos == 2) {
                    hasOneSpecificity = true;
                }
                else if (targetStartPos != 1) {
                    throw new RuntimeException("Non-pawn move with target at position " + targetStartPos + " and is not a capture");
                }
            }
            if (hasTwoSpecificity) {
                hasColSpecificity = true;
                specificCol = text.charAt(1) - 'a';
                hasRowSpecificity = true;
                specificRow = text.charAt(2) - '1';
            }
            else if (hasOneSpecificity) {
                if (text.charAt(1) >= 'a' && text.charAt(1) <= 'h') {
                    hasColSpecificity = true;
                    specificCol = text.charAt(1) - 'a';
                }
                else if (text.charAt(1) >= '1' && text.charAt(1) <= '8') {
                    hasRowSpecificity = true;
                    specificRow = text.charAt(1) - '1';
                }
                else {
                    throw new RuntimeException("Has specificity but character at position 1 is " + text.charAt(1));
                }
            }
            if (text.charAt(0) != 'N' && text.charAt(0) != 'B' &&
                text.charAt(0) != 'R' && text.charAt(0) != 'Q' &&
                text.charAt(0) != 'K') {
                    throw new RuntimeException("Non-pawn move but first character is " + text.charAt(0));
            }
            _algebraicHelperNonPawnMove(text, length, netLength, hasColSpecificity, hasRowSpecificity, specificCol, specificRow);
        }
    }

    private void _validateCol(int col) {
        if (col < 0 || col > 7) {
            throw new RuntimeException("col out of bounds: " + col);
        }
    }

    private void _validateRow(int row) {
        if (row < 0 || row > 7) {
            throw new RuntimeException("row out of bounds: " + row);
        }
    }

    public int sourcecol() { return _sourcecol; }
    public int sourcerow() { return _sourcerow; }
    public int targetcol() { return _targetcol; }
    public int targetrow() { return _targetrow; }
}

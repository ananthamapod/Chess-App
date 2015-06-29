package cs213.android.chess.control;

import java.util.LinkedList;

import cs213.android.chess.util.PieceData;
import cs213.android.chess.util.PieceType;
import cs213.android.chess.util.Position;
import cs213.android.chess.model.*;

/**
 * Created by ananth on 5/6/15.
 */
/**
 * @author Ananth Rao
 * @author Allon Fineziber
 */

public class Board {
    public Piece[][] board;
    public LinkedList<PieceData[]> record;

    public King bKing, wKing;

    int turn;

    boolean draw;
    boolean isGameRunning;

    Position oppPrevStart;
    Position oppPrevEnd;
    Position prevStart;
    Position prevEnd;
    Position oppPrevPrevStart;
    Position oppPrevPrevEnd;
    Piece prevPiece = null;
    Piece oppPrevPiece = null;
    Piece tempPiece = null;

    public String message = "";

    boolean enpassant;

    boolean check;

    public Board() {
        turn = 1;
        draw = false;
        isGameRunning = true;
        check = false;

        oppPrevStart = null;
        oppPrevEnd = null;
        oppPrevPrevStart = null;
        oppPrevPrevEnd = null;
        prevStart = null;
        prevEnd = null;
        prevPiece = null;

        enpassant = false;

        record = new LinkedList<PieceData[]>();

        board = new Piece[8][8];
        for(int i=0; i< 8; i++) {
            boolean white = true;
            for(int j=0; j < 8; j++) {
                switch(i) {
                    case 7:
                        white = false;
                    case 0:
                        switch(j) {
                            case 0:
                            case 7:
                                board[i][j] = new Rook(white, new Position(j,i));
                                break;
                            case 1:
                            case 6:
                                board[i][j] = new Knight(white, new Position(j,i));
                                break;
                            case 2:
                            case 5:
                                board[i][j] = new Bishop(white, new Position(j,i));
                                break;
                            case 3:
                                board[i][j] = new Queen(white, new Position(j,i));
                                break;
                            case 4:
                                board[i][j] = new King(white, new Position(j,i));
                                if(white) {
                                    wKing = (King)board[i][j];
                                } else {
                                    bKing = (King)board[i][j];
                                }
                                break;
                        }
                        break;
                    case 6:
                        white = false;
                    case 1:
                        board[i][j] = new Pawn(white, new Position(j,i));
                        break;
                    default:
                        board[i][j] = null;
                }
            }
        }
        record.add(sendBoard());
    }

    public boolean gameInProgress(){
        return isGameRunning;
    }

    public void resign() {
        isGameRunning = false;
        message = ((turn%2 == 0)? "Black" : "White") + "resigned";
    }

    public void draw() {
        isGameRunning = false;
        message = "Game is a draw";
    }

    public boolean move(String input) {
        switch(input) {
            case "resign":
                resign();
                return true;
            case "draw":
                if(draw) {
                    draw();
                    return true;
                } else {
                    return false;
                }
            default:
                return false;
        }
    }

    public boolean move(Position startPos, Position endPos) {
        //Makes sure draw is not permanently set after being set once
        if(draw) {
            draw = false;
        }

        boolean isCastle = false;
        boolean castle = false;
        if(startPos == null) {
            return false;
        }
        if(endPos == null) {
            return false;
        }
        //Valid move in terms of piece movement rules
        if (isValid(startPos, endPos)) {
            //Checks if castle and commences castle if so
            if(board[startPos.y][startPos.x] instanceof King) {
                isCastle = isCastle(board[startPos.y][startPos.x], endPos);
                if(isCastle) {
                    castle = canCastle(startPos, endPos);
                }
            }
            //Handles invalid castle attempts
            if(isCastle && !castle) {
                return false;
            }
            //Updates position and handles invalid moves that result in check on the same side
            //If false, that means invalid move was encountered and position was reversed, return false
            if(!isCastle && !updatePosition(startPos, endPos)) {
                return false;
            }

            Piece currPiece = board[endPos.y][endPos.x];
            oppPrevPrevStart = prevStart;
            oppPrevPrevEnd = prevEnd;
            prevStart = oppPrevStart;
            prevEnd = oppPrevEnd;
            oppPrevStart = startPos;
            oppPrevEnd = endPos;

            //Asserts that the pawn has been moved so that the first move 2 space rule is no longer applicable
            if(currPiece instanceof Pawn) {
                Pawn pawn = (Pawn)currPiece;
                pawn.firstMove = false;
                if(isPromotion(pawn, endPos)) {
                    Piece newPiece = new Queen(pawn.isWhite(), endPos);
                    board[endPos.y][endPos.x] = newPiece;
                }
            } else if(currPiece instanceof King) {
                currPiece.firstMove = false;
            }

            //Turn counter incremented for black and white id
            turn++;
            enpassant = false;
            drawBoard();
            record.add(sendBoard());
            oppPrevPiece = prevPiece;
            prevPiece = tempPiece;
            if(isCheck(((turn%2 == 0)? bKing : wKing))) {
                if(isCheckmate(((turn%2 == 0)? bKing : wKing))) {
                    message = "Checkmate\n " + ((turn%2 == 1)? "Black" : "White") + " wins";
                    System.out.println();
                } else {
                    message = "Check by " + ((turn%2 == 1)? "Black" : "White");
                    System.out.println();
                    System.out.print(((turn%2 == 0)? "Black" : "White") + "'s move: ");
                }
            } else if(isStalemate(((turn%2 == 0)? bKing : wKing))) {
                message = "Stalemate";
                System.out.println();
            } else {
                message = "";
                System.out.print(((turn%2 == 0)? "Black" : "White") + "'s move: ");
            }
            return true;
        }
        return false;
    }

    public boolean move(Position startPos, Position endPos, String flag) {
        //Makes sure draw is not permanently set after being set once
        if(draw) {
            draw = false;
        }

        boolean isCastle = false;
        boolean castle = false;
        //If there is an attempted promotion (3rd argument), set to true
        //Then checks if promotion happens in isValid().
        //If promotion is real, checks if flag is valid piece type
        //If yes, update and replace piece type with new piece type
        //If not, return "Error"
        boolean tryPromote = false;
        switch(flag) {
            case "draw?":
                draw = true;
                break;
            default:
                tryPromote = true;
        }
        //Valid move
        if (isValid(startPos, endPos)) {
            //checks if the promotion attempted is a valid move
            if(tryPromote) {
                Piece pawn = board[startPos.y][startPos.x];
                if(!isPromotion(pawn, endPos)) {
                    return false;
                }
                switch(flag) {
                    case "R":
                    case "N":
                    case "B":
                    case "Q":
                        break;
                    default:
                        return false;
                }
            }

            //Checks if castle and commences castle if so
            if(board[startPos.y][startPos.x] instanceof King) {
                isCastle = isCastle(board[startPos.y][startPos.x], endPos);
                if(isCastle) {
                    castle = canCastle(startPos, endPos);
                }
            }
            //Handles invalid castle attempts
            if(isCastle && !castle) {
                return false;
            }
            //Updates position and handles invalid moves that result in check on the same side
            //If false, that means invalid move was encountered and position was reversed, return false
            if(!isCastle && !updatePosition(startPos, endPos)) {
                return false;
            }

            Piece currPiece = board[endPos.y][endPos.x];
            oppPrevPrevStart = prevStart;
            oppPrevPrevEnd = prevEnd;
            prevStart = oppPrevStart;
            prevEnd = oppPrevEnd;
            oppPrevStart = startPos;
            oppPrevEnd = endPos;

            if(currPiece instanceof Pawn) {
                Pawn pawn = (Pawn)currPiece;
                //Asserts that the pawn has been moved so that the first move 2 space rule is no longer applicable
                pawn.firstMove = false;

                //Implements promotion by replacing the pawn with the piece chosen to replace it
                if(isPromotion(pawn, endPos)) {
                    Piece newPiece = null;
                    switch(flag) {
                        case "R":
                            newPiece = new Rook(pawn.isWhite(), endPos);
                            break;
                        case "N":
                            newPiece = new Knight(pawn.isWhite(), endPos);
                            break;
                        case "B":
                            newPiece = new Bishop(pawn.isWhite(), endPos);
                            break;
                        case "Q":
                            //Since invalid promotion pieces have been handled before this point, the default case means there was a promotion and the player offered a draw
                        default :
                            newPiece = new Queen(pawn.isWhite(), endPos);
                    }
                    board[endPos.y][endPos.x] = newPiece;
                }
            } else if(currPiece instanceof King) {
                currPiece.firstMove = false;
            }

            //Turn counter incremented for black and white id
            turn++;
            enpassant = false;
            drawBoard();
            record.add(sendBoard());
            oppPrevPiece = prevPiece;
            prevPiece = tempPiece;
            if(isCheck(((turn%2 == 0)? bKing : wKing))) {
                if(isCheckmate(((turn%2 == 0)? bKing : wKing))) {
                    message = "Checkmate\n " + ((turn%2 == 1)? "Black" : "White") + " wins";
                    System.out.println();
                } else {
                    message = "Check by " + ((turn%2 == 0)? "Black" : "White");
                    System.out.println();
                    System.out.print(((turn%2 == 0)? "Black" : "White") + "'s move: ");
                }
            } else if(isStalemate(((turn%2 == 0)? bKing : wKing))) {
                message = "Stalemate";
                System.out.println();
            } else {
                message = "";
                System.out.print(((turn%2 == 0)? "Black" : "White") + "'s move: ");
            }
            return true;
        }
        return false;
    }


    public boolean isValid(Position startPos, Position endPos) {
        if(startPos == null || endPos == null) {
            return false;
        }
        if(startPos.equals(endPos)) {
            return false;
        }
        boolean retVal = false;
        Piece currPiece;
        retVal = isValidPosition(startPos);

        if(retVal) {
            currPiece = board[startPos.y][startPos.x];
            retVal = (currPiece != null);

            if(retVal) {
                retVal = (turn%2 == 0)? !currPiece.isWhite() : currPiece.isWhite();

                if(retVal) {
                    retVal = isValidPosition(endPos);

                    if(retVal) {
                        Piece cutPiece = board[endPos.y][endPos.x];
                        boolean isEmpty = (cutPiece == null);
                        //Checks for enpassant
                        if(currPiece instanceof Pawn) {
                            if(oppPrevEnd != null && oppPrevStart != null) {
                                if(oppPrevEnd.equals(new Position(endPos.x, startPos.y)) && oppPrevStart.equals(new Position(endPos.x, (currPiece.isWhite()? 6 : 1)))) {
                                    isEmpty = false;
                                    enpassant = true;
                                }
                            }
                        }
                        retVal = currPiece.canMove(endPos, isEmpty);

                        if(retVal) {
                            retVal = isValidPath(currPiece, startPos, endPos, isEmpty);
                        }
                    }
                }
            }
        }
        return retVal;
    }


    public boolean isValidPosition(Position pos) {
        int x = pos.x;
        int y = pos.y;
        if(x < 0 || x > 7) {
            return false;
        }
        if(y < 0 || y > 7) {
            return false;
        }
        return true;
    }


    public boolean isValidPath(Piece currPiece, Position startPos, Position endPos, boolean isEmpty) {
        if(!(currPiece instanceof Knight)) {
            LinkedList<Position> path = currPiece.pathToSquare(endPos, isEmpty);
            path.remove(startPos);
            for(Position square : path) {
                if(board[square.y][square.x] != null) {
                    return false;
                }
            }
        }
        return true;
    }


    public boolean isCheck(King king) {
        if(king == null) {
            return false;
        }

        Position testPos = king.getPosition();

        LinkedList<Piece> threats = axis(king);
        for(Piece p : threats) {
            if(p.canMove(testPos, false)) {
                //return true if any piece on the opposing side has a valid path to the king
                if(isValidPath(p, p.getPosition(), testPos, false)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isCheckmate(King king) {
        if(king == null) {
            return false;
        }
        if(!isCheck(king)) {
            return false;
        }

        //Starting position of king
        Position startingPoint = king.getPosition();

        //Compile list of threats
        LinkedList<Piece> threats = axis(king);

        //Finds all pieces that are putting the king in check right then
        LinkedList<Piece> immediateThreats = new LinkedList<Piece>();
        //Only opposing pieces can check obviously
        for(Piece threat : threats) {
            //If threat is able to travel to the king's square on the next turn, it is checking the king
            if(threat.canMove(startingPoint, false) && isValidPath(threat, threat.getPosition(), startingPoint, false)) {
                immediateThreats.add(threat);
            }
        }

        boolean canProtect = false;

        //Compiles list of ally pieces
        LinkedList<Piece> allies = allies(king);

        //There is no piece or move in chess which allows a piece to block or get rid of more than 1 checking piece at the same time
        //Therefore, if there is more than one piece checking the king, no ally piece can protect
        if(immediateThreats.size() == 1) {
            //This loop will only run once. Just seemed like a clean way of accessing the only checking piece
            for(Piece threat : immediateThreats) {
                //The path of the threat. If any ally piece can move to one of those squares, threat can be blocked
                //This path includes the position of the piece (ie. because an ally piece can also neutralize the threat by taking the checking piece
                LinkedList<Position> blockPoints = threat.pathToSquare(startingPoint, false);
                //Test each square in the path
                for(Position blockPoint : blockPoints) {
                    //Checks if the square is empty. This should apply to every square except the position of the attacking piece.
                    //(Obviously. If there were filled squares in the path of the threat, then the threat wouldn't be a threat)
                    boolean isEmpty = board[blockPoint.y][blockPoint.x] == null;
                    //Checks each ally piece to see if they can block
                    for(Piece ally : allies) {
                        if(ally.canMove(blockPoint, isEmpty) && isValidPath(ally, ally.getPosition(), blockPoint, isEmpty)) {
                            //If there is even one piece that can block, no need to continue loop
                            canProtect = true;
                            break;
                        }
                    }
                    //If a piece can protect, break out of this loop too
                    if(canProtect) {
                        break;
                    }
                }
            }
        }

        //Means a piece can block the check. Not checkmate.
        if(canProtect) {
            return false;
        }

        //Tests if the king can move out of check

        //Compiles list of surrounding positions
        LinkedList<Position> escapeMoves = surroundings(startingPoint);

        //Tests each surrounding position
        for(Position move : escapeMoves) {
            //Stores the piece at that position to test out what would happen if the king moved there
            Piece temp = board[move.y][move.x];
            //King can only move there if space is free or the piece there can be taken
            if(temp == null || temp.isWhite() != king.isWhite()) {
                //Move the king to that possible position
                //Check if king would still be in check
                //updatePosition reverses move and return false if move still results in check
                boolean isSafe = updatePosition(startingPoint, move);

                //Means king is able to move out of check. Not checkmate
                if(isSafe) {
                    //Reverses position of king and any cut piece
                    reverseMove(startingPoint, move, temp);
                    return false;
                }
            }
        }

        //At this point, it has to be checkmate. Therefore, the game is over.
        isGameRunning = false;

        return true;
    }

    public LinkedList<Position> surroundings(Position startingPoint) {

        if(startingPoint == null || !isValidPosition(startingPoint)) {
            return null;
        }

        LinkedList<Position> retList = new LinkedList<Position>();
        int x = startingPoint.x;
        int y = startingPoint.y;

        //If board is not at the bottom edge of the board (from white's perspective),
        //include the squares behind it in the surrounding positions
        if(x > 0) {
            //If board is not at left edge of the board ...
            if(y > 0) {
                retList.add(new Position(x-1, y-1));
            }
            retList.add(new Position(x-1, y));
            //If board is not at right edge of the board ...
            if(y < 7) {
                retList.add(new Position(x-1, y+1));
            }
        }
        //If board is not at left edge of the board ...
        if(y > 0) {
            retList.add(new Position(x, y-1));
        }
        //If board is not at right edge of the board ...
        if(y < 7) {
            retList.add(new Position(x, y+1));
        }
        //If board is not at top edge of the board ...
        if(x < 7) {
            //If board is not at left edge of the board ...
            if(y > 0) {
                retList.add(new Position(x+1, y-1));
            }
            retList.add(new Position(x+1, y));
            //If board is not at right edge of the board ...
            if(y < 7) {
                retList.add(new Position(x+1, y+1));
            }
        }
        return retList;
    }

    public boolean isPromotion(Piece piece, Position endPos) {

        if(piece instanceof Pawn) {
            boolean white = piece.isWhite();
            if(endPos != null) {
                //If pawn is white, other side of the board is 7,
                //else the pawn is black and other side is 0.
                if(endPos.y == (white? 7 : 0)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean updatePosition(Position startPos, Position endPos) {
        //board[new] = board[old], the piece pointer board[new] is now pointing to the piece that board[old] pointed to
        Piece currPiece = board[startPos.y][startPos.x];
        Piece cutPiece = board[endPos.y][endPos.x];
        board[endPos.y][endPos.x] = currPiece;
        //Clears old position
        board[startPos.y][startPos.x] = null;
        //Sets the x and y for piece object in board[new]
        currPiece.currPos = endPos;
        if(enpassant) {
            cutPiece = board[startPos.y][endPos.x];
            board[startPos.y][endPos.x] = null;
        }


        //Handles invalid move case of moving into check
        if(isCheck(((turn%2 == 0)? bKing : wKing))) {
            reverseMove(startPos, endPos, cutPiece);
            return false;
        }
        tempPiece = cutPiece;
        return true;
    }

    public void reverseMove(Position oldPos, Position currPos, Piece cutPiece) {
        if(currPos == null || oldPos == null) {
            return;
        }
        //Gets current piece
        Piece currPiece = board[currPos.y][currPos.x];
        //Moves current piece to previous position
        board[oldPos.y][oldPos.x] = currPiece;
        //Puts cut piece if there was one back into its old position
        if(enpassant) {
            board[oldPos.y][currPos.x] = cutPiece;
        } else {
            board[currPos.y][currPos.x] = cutPiece;
        }
        //Updates the current piece's position field
        currPiece.currPos = oldPos;

    }

    public LinkedList<Piece> allies(Piece me) {
        boolean isWhite = me.isWhite();
        Position myPosition = me.getPosition();
        //Compiles List of ally pieces and opposing pieces.
        LinkedList<Piece> allies = new LinkedList<Piece>();
        for(Piece[] row : board) {
            for(Piece p : row) {
                if(p != null) {
                    if(p.isWhite() == isWhite) {
                        //Doesn't include the piece as a list of its own allies
                        if(!p.getPosition().equals(myPosition))
                            allies.add(p);
                    }
                }
            }
        }
        return allies;

    }

    public LinkedList<Piece> axis(Piece me) {
        boolean isWhite = me.isWhite();
        //Compiles List of opposing pieces.
        LinkedList<Piece> threats = new LinkedList<Piece>();
        for(Piece[] row : board) {
            for(Piece p : row) {
                if(p != null) {
                    if(p.isWhite() != isWhite) {
                        threats.add(p);
                    }
                }
            }
        }
        return threats;

    }

    public boolean isCastle(Piece king, Position endPos) {
        if(king == null || endPos == null) {
            return false;
        }
        if(king instanceof King) {
            boolean white = king.isWhite();
            if(white) {
                if(king.getPosition().equals(new Position(4, 0))) {
                    if (endPos.equals(new Position(2,0)) || endPos.equals(new Position(6,0))) {
                        return true;
                    }
                }
            } else {
                if(king.getPosition().equals(new Position(4, 7))) {
                    if (endPos.equals(new Position(2,7)) || endPos.equals(new Position(6,7))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean canCastle(Position startPos, Position endPos) {

        int diffx = endPos.x - startPos.x;
        Piece king = board[startPos.y][startPos.x];
        if (king != null) {

            if(king instanceof King && !isCheck((King)king) && king.firstMove) {
                // Checks for Right-Side Castling
                Piece rook = null;
                if (Math. abs(diffx) == 2) {
                    Position rookStart = new Position((diffx > 0)? 7 : 0, startPos.y);
                    Position rookEnd = new Position((diffx > 0)? 5 : 3, startPos.y);
                    rook = board[rookStart.y][rookStart.x];
                    //Checks Rooks position
                    if (rook != null && rook instanceof Rook ) {
                        //Checks if rook has moved yet
                        if (rook.firstMove) {

//							if (isValidPath(king, endPos, endPos, true) && isValidPath(rook, rookStart, rookEnd, true)) {
                            if (isValidPath(king, startPos, endPos, true) && isValidPath(rook, rookStart, rookEnd, true)) {
                                Position midPos = new Position(startPos.x+(diffx/2), startPos.y);
                                if(updatePosition(startPos, midPos)) {
                                    if(updatePosition(midPos, endPos)) {
                                        board[rookStart.y][rookStart.x] = null;
                                        board[rookEnd.y][rookEnd.x] = rook;
                                        rook.currPos = rookEnd;
                                        return true;
                                    } else {
                                        updatePosition(midPos, startPos);
                                        return false;
                                    }
                                } else {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    public boolean isPathClear(int oldX, int oldY, int newX, int newY){

        String symbolName = board[oldY][oldX].toString();

        if (symbolName.equalsIgnoreCase("wk") || symbolName.equalsIgnoreCase("bk")) {
            return true;
        }
        else if (symbolName.equalsIgnoreCase("wn") || symbolName.equalsIgnoreCase("bn")) {
            return true;
        }

        int diffX = newX - oldX;
        int diffY = newY - oldY;

        int dx = 0;
        int dy = 0;
        int tempx = oldX;
        int tempy = oldY;

        if (diffX > 0) {
            dx = 1;
        }
        else if (diffX < 0) {
            dx = -1;
        }

        if (diffY > 0) {
            dy = 1;
        }
        else if (diffY < 0) {
            dy = -1;
        }

        boolean clearPath = true;

        if (diffY == 0) {
            tempx = tempx + dx;
            for (int i = 0; i < Math.abs(diffX) - 1; i++) {
                if (board[tempy][tempx] != null) {
                    clearPath = false;
                    break;
                }

                tempx = tempx + dx;
            }
            return clearPath;
        }

        if (diffX == 0) {
            tempy = tempy + dy;
            for (int i = 0; i < Math.abs(diffY) - 1; i++) {
                if (board[tempy][tempx] != null) {
                    clearPath = false;
                    break;
                }

                tempy = tempy + dy;
            }

            return clearPath;
        }

        if (diffX != 0 && diffY != 0) {
            tempx = tempx + dx;
            tempy = tempy + dy;
            for (int i = 0; i < Math.abs(diffY)-1; i++) {
                if (board[tempy][tempx] != null) {
                    clearPath = false;
                    break;
                }

                tempx = tempx + dx;
                tempy = tempy + dy;
            }
        }

        return clearPath;
    }

    public boolean isStalemate(Piece p) {
        if(p == null) {
            return false;
        }
        boolean white = p.isWhite();
        LinkedList<Piece> pieces = allies(p);
        pieces.add(p);
        for(Piece piece : pieces) {
            for(int y = 0; y < 8; y++) {
                for(int x = 0; x < 8; x++) {
                    Piece cutPiece = board[y][x];
                    if(cutPiece == null || cutPiece.isWhite() != white) {
                        Position testPos = new Position(x,y);
                        Position oldPos = piece.getPosition();
                        boolean isEmpty = (cutPiece == null);
                        if(piece.canMove(testPos, isEmpty) && isValidPath(piece, oldPos, testPos, isEmpty)) {
                            boolean canMove = updatePosition(oldPos, testPos);
                            if(canMove) {
                                reverseMove(oldPos, testPos, cutPiece);
                                return false;
                            }
                        }
                    }
                }
            }
        }
        isGameRunning = false;
        return true;
    }

    public void drawBoard() {
        System.out.println();
        for(int i = 7; i > -1; i--) {
            for(int j = 0; j < 8; j++) {
                if(board[i][j] == null) {
                    if(((i%2)^(j%2)) == 0) {
                        System.out.print("##");
                    } else {
                        System.out.print("  ");
                    }
                } else {
                    System.out.print(board[i][j]);
                }
                System.out.print(" ");
            }
            System.out.println(i+1);
        }
        System.out.println(" a  b  c  d  e  f  g  h");
        System.out.println();
    }

    public PieceData[] sendBoard() {
        PieceData[] ret = new PieceData[64];
        for(int i = 7; i > -1; i--) {
            for(int j = 0; j < 8; j++) {
                if(board[i][j] == null) {
                    ret[i*8 + j] = new PieceData(PieceType.BLANK, true);
                } else {
                    ret[i*8 +j] = new PieceData(board[i][j].type, board[i][j].isWhite());
                }
            }
        }
        return ret;
    }

    public boolean canUndo() {
        if(prevStart == null) {
            return false;
        } else {
            return true;
        }
    }

    public void undo() {
        updatePosition(oppPrevEnd, oppPrevStart);
        board[oppPrevEnd.y][oppPrevEnd.x] = prevPiece;
        updatePosition(prevEnd, prevStart);
        board[prevEnd.y][prevEnd.x] = oppPrevPiece;
        System.out.println("oppPrevPiece position: " + prevEnd.x + " " + prevEnd.y);

        prevStart = null;
        prevEnd = null;
        oppPrevEnd = oppPrevPrevEnd;
        oppPrevStart = oppPrevPrevStart;
        oppPrevPrevEnd = null;
        oppPrevPrevStart = null;

        record.removeLast();
        record.removeLast();
    }

    public LinkedList<PieceData[]> getRecord() {
        return record;
    }
}

package cs213.android.chess.model;

import java.util.LinkedList;

import cs213.android.chess.util.PieceType;
import cs213.android.chess.util.Position;

/**
 * Created by ananth on 5/6/15.
 */
/**
 * @author Ananth Rao
 * @author Allon Fineziber
 */

public abstract class Piece {
    public String symbol;
    private boolean white;
    public PieceType type = PieceType.BLANK;
    public boolean firstMove = true;

    public Position currPos;

    public boolean isWhite(){
        return this.white;
    }

    public void setWhite(boolean w){
        white = w;
    }

    public Position getPosition() {
        return currPos;
    }

    public void setPosition(Position newPos) {
        currPos = newPos;
    }

    public abstract boolean canMove(Position nextPos, boolean isEmpty);

    public abstract LinkedList<Position> pathToSquare(Position square, boolean isEmpty);

    public String toString() {
        return symbol;
    }
}
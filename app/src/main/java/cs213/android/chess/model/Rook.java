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

public class Rook extends Piece{

    public Rook(boolean w, Position start) {
        currPos = start;
        symbol = "R";
        type = PieceType.ROOK;
        this.setWhite(w);

        if (w == true) {
            this.symbol = "w" + this.symbol;
        }
        else {
            this.symbol = "b" + this.symbol;
        }
    }

    public boolean canMove(Position nextPos, boolean isEmpty) {

        int oldX = currPos.x;
        int oldY = currPos.y;
        int newX = nextPos.x;
        int newY = nextPos.y;

        int diffX;
        int diffY;

        diffX = Math.abs(newX - oldX);
        diffY = Math.abs(newY - oldY);

        if (diffX == 0 && diffY != 0){
            return true;
        }
        else if (diffX != 0 && diffY == 0) {
            return true;
        }

        return false;
    }

    @Override
    public LinkedList<Position> pathToSquare(Position square, boolean isEmpty) {
        // TODO Auto-generated method stub
        if(canMove(square, isEmpty)) {
            LinkedList<Position> retList = new LinkedList<Position>();
            int oldX = currPos.x;
            int oldY = currPos.y;
            int newX = square.x;
            int newY = square.y;

            int diffX = newX - oldX;
            int diffY = newY - oldY;

            if(diffX != 0) {
                while(diffX != 0) {
                    retList.add(new Position(oldX, oldY));
                    if(diffX > 0) {
                        oldX += 1;
                    } else {
                        oldX -= 1;
                    }
                    diffX = newX - oldX;
                }
            } else {
                while(diffY != 0) {
                    retList.add(new Position(oldX, oldY));
                    if(diffY > 0) {
                        oldY += 1;
                    } else {
                        oldY -= 1;
                    }
                    diffY = newY - oldY;
                }
            }
            return retList;
        }
        return null;
    }
}

package cs213.android.chess.util;

/**
 * Created by ananth on 5/6/15.
 */
/**
 * @author Ananth Rao
 * @author Allon Fineziber
 */
public class Position {
    public int x, y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Position) {
            Position other = (Position)o;
            return (x == other.x && y == other.y);
        }
        return false;
    }

    @Override
    public String toString() {
        return "x=" + x + ", y=" + y;
    }

    public static String convertToChessString(int position) {
        int x = position%8;
        int y = position/8;
        StringBuffer str = new StringBuffer();

        switch(x) {
            case 0:
                str.append('a');
                break;
            case 1:
                str.append('b');
                break;
            case 2:
                str.append('c');
                break;
            case 3:
                str.append('d');
                break;
            case 4:
                str.append('e');
                break;
            case 5:
                str.append('f');
                break;
            case 6:
                str.append('g');
                break;
            case 7:
                str.append('h');
                break;
            default:
                return null;
        }

        str.append(y+1);
        return str.toString();
    }
}
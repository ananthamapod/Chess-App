package cs213.android.chess.util;

import java.io.Serializable;

/**
 * Created by ananth on 5/6/15.
 */
public class PieceData implements Serializable {
    public PieceType type = PieceType.BLANK;
    public boolean isWhite = true;

    public PieceData(PieceType type, boolean isWhite) {
        this.isWhite = isWhite;
        this.type = type;
    }
}

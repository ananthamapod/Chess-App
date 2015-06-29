package cs213.android.chess.util;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by ananth on 5/7/15.
 */
public class SaveGame implements Serializable {

    private String fileName;
    private List<PieceData[]> moves;
    private Calendar cal;

    protected SaveGame(String fileName, List<PieceData[]> moves){
        this.fileName = fileName;
        this.moves = moves;
        cal = new GregorianCalendar();
        cal.set(Calendar.MILLISECOND,0);
        cal.setTime(cal.getTime());
    }

    protected List<PieceData[]> getMoves(){
        return moves;
    }


    protected String getFileName(){
        return fileName;
    }

    protected String getDate(){
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy-HH:mm:ss", Locale.ROOT);
        String date = df.format(cal.getTime());
        return date;
    }

    public byte[] getBytes(){
        return null;
    }
}

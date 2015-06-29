package cs213.android.chess.util;

/**
 * Created by ananth on 5/6/15.
 */
public class GameData implements Comparable<GameData>{
    String fileName;
    String date;

    public GameData(String fileName, String date){
        this.fileName = fileName;
        this.date = date;
    }

    public String getFileName(){
        return this.fileName;
    }

    public String getDate(){
        return this.date;
    }

    public int compareTo(GameData g){
        return this.date.compareTo(g.getDate());
    }

    public String toString(){
        return this.fileName;
    }
}

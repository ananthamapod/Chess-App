package cs213.android.chess.util;

/**
 * Created by ananth on 5/6/15.
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.util.Log;

public class RecordGame implements Serializable {

    private static String path;
    private SaveGame sg;

    private RecordGame(){}

    public static RecordGame getInstance(){
        RecordGame rg;
        rg = new RecordGame();
        return rg;
    }


    public boolean save(String fileName, List<PieceData[]> boardLayouts, Context parentActivity){

        sg = new SaveGame(fileName, boardLayouts);

        fileName = parentActivity.getFilesDir() + "/" + fileName + ".chess";

        File f = new File(fileName);
        if( f.exists() ){
            return false;
        }
        ObjectOutputStream oos;
        try{
            Log.i("A", fileName);
            FileOutputStream fos = new FileOutputStream(fileName);
            oos = new ObjectOutputStream(fos);
            Log.i("A", f.getAbsolutePath());
            Log.i("B", "GETS HERE");
            oos.writeObject(sg);
            Log.i("D", "GETS HERE");
            oos.close();
            fos.close();
            Log.i("G", "GETS HERE");
        }
        catch( IOException e ){
            Log.i("ERRORR!", e.toString());
            return false;
        }

        return true;
    }

    public List<PieceData[]> load(String fileName, Context parentActivity){

        fileName = parentActivity.getFilesDir() + "/" + fileName + ".chess";

        File f = new File( fileName );
        if( !f.exists() ){
            return null;
        }

        ObjectInputStream ois;

        try{
            FileInputStream fis = new FileInputStream(fileName);
            ois = new ObjectInputStream(fis);
            sg = (SaveGame)ois.readObject();
            ois.close();
            fis.close();
        }
        catch( IOException e ){
            return null;
        }
        catch( ClassNotFoundException e ){
            return null;
        }

        return sg.getMoves();
    }


    public List<GameData> listByName(Context parentActivity){
        File baseFile = parentActivity.getFilesDir();
        String[] files = baseFile.list(new FilenameFilter() {
            @Override
            public boolean accept(File file, String name) {
                if(name == null) {
                    return false;
                }
                if(name.endsWith(".chess")) {
                    return true;
                }
                return false;
            }
        });

        List<GameData> fileList = null;

        if( files == null ){
            return fileList;
        }

        for( int i = 0; i < files.length; i++ ){
            files[i] = files[i].substring(0, files[i].lastIndexOf(".chess"));
        }

        Arrays.sort(files, String.CASE_INSENSITIVE_ORDER);
        fileList = list(files, parentActivity);
        return fileList;
    }

    public List<GameData> listByDate(Context parentActivity){
        File baseFile = parentActivity.getFilesDir();
        String[] files = baseFile.list(new FilenameFilter() {
            @Override
            public boolean accept(File file, String name) {
                if(name == null) {
                    return false;
                }
                if(name.endsWith(".chess")) {
                    return true;
                }
                return false;
            }
        });

        List<GameData> fileList = null;

        if( files == null ){
            return fileList;
        }

        for( int i = 0; i < files.length; i++ ){
            files[i] = files[i].substring(0, files[i].lastIndexOf(".chess"));
        }

        fileList = list(files, parentActivity);

        Collections.sort(fileList, new Comparator<GameData>() {
            @Override
            public int compare(GameData gd1, GameData gd2) {
                try {
                    SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy-HH:mm:ss", Locale.ROOT);
                    Date date1 = df.parse(gd1.getDate());
                    Date date2 = df.parse(gd2.getDate());
                    return date1.compareTo(date2);
                } catch(Exception e) {
                    return 0;
                }
            }
        });

        return fileList;
    }

    private List<GameData> list(String [] files, Context parentActivity){

        File f;
        List<GameData> l = new LinkedList<GameData>();
        GameData gdn = null;
        for( int i = 0; i < files.length; i++ ){
            files[i] = parentActivity.getFilesDir() + "/" + files[i] + ".chess";
            Log.i("A", files[i]);
            f = new File( files[i] );
            try{
                FileInputStream fis =  new FileInputStream( f );
                ObjectInputStream ois = new ObjectInputStream(fis);
                sg = (SaveGame)ois.readObject();
                ois.close();
            }
            catch( IOException e ){
                Log.i("ERRORR!", e.toString());
                return null;
            }
            catch( ClassNotFoundException e ){
                Log.i("ERRORR!", e.toString());
                return null;
            }

            gdn = new GameData(sg.getFileName(), sg.getDate());
            l.add(gdn);
        }
        return l;
    }

}
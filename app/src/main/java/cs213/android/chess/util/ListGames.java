package cs213.android.chess.util;

import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import cs213.android.chess.util.GameData;

import cs213.android.chess.R;

/**
 * Created by ananth on 5/6/15.
 */
public class ListGames extends Activity {

    private ListView listView;
    private RecordGame rg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO
        //setContentView(R.layout.activity_list_games);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //TODO
        //getMenuInflater().inflate(R.menu.list_games, menu);
        return true;
    }

    protected void onStart(){
        super.onStart();

        int id = getResources().getIdentifier("list_games", "id", "cs213.android.chess");
        listView = (ListView)findViewById(id);
        rg = RecordGame.getInstance();

        List<GameData> rawr = rg.listByName(this);
        Toast.makeText(this, "No Replays Found", Toast.LENGTH_SHORT).show();


        listView.setOnItemClickListener(new OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GameData gd = (GameData)listView.getItemAtPosition(position);
                //TODO
                Intent intent = new Intent(getApplicationContext(), null);
                intent.putExtra("fileName", gd.getFileName());
                startActivity(intent);
            }

        });
    }

    public void sortByName(View view){
        int id = getResources().getIdentifier("list_games", "id", "cs213.android.chess");
        listView.setAdapter(new ArrayAdapter<GameData>( this, id, rg.listByName(this)));
    }

    public void sortByDate(View view){
        int id = getResources().getIdentifier("list_games", "id", "cs213.android.chess");
        //TODO
        //listView.setAdapter(new ArrayAdapter<GameData>(this, R.layout.game_list_element, rg.listByDate(this)));
    }
}
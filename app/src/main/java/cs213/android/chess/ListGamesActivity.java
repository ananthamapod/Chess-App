package cs213.android.chess;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import cs213.android.chess.util.GameData;
import cs213.android.chess.util.RecordGame;


public class ListGamesActivity extends AppCompatActivity {

    private ListView listView;
    private RecordGame rg;


    /**
     * Created by ananth on 5/6/15.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_games);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_games, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }
    protected void onStart(){
        super.onStart();

        int id = getResources().getIdentifier("list_games", "id", "cs213.android.chess");
        listView = (ListView)findViewById(id);
        rg = RecordGame.getInstance();

        List<GameData> rawr = rg.listByName(this);
        if(rawr == null || rawr.isEmpty())
            Toast.makeText(this, "No Replays Found", Toast.LENGTH_SHORT).show();

        listView.setAdapter(new ArrayAdapter<GameData>( this, android.R.layout.simple_list_item_1, rawr));


        listView.setOnItemClickListener(new OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GameData gd = (GameData)listView.getItemAtPosition(position);
                Log.i("NULL?", (gd.getFileName()) + "");
                //TODO
                Intent intent = new Intent(getApplicationContext(), ReplayActivity.class);
                intent.putExtra("filename", gd.getFileName());
                Log.i("S",intent.getStringExtra("filename"));
                        startActivity(intent);
            }

        });

        Button byName = (Button) findViewById(R.id.sort_by_name);
        Button byDate = (Button) findViewById(R.id.sort_by_date);
        byName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortByName();
            }
        });
        byDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortByDate();
            }
        });
    }

    public void sortByName(){
        int id = getResources().getIdentifier("list_games", "id", "cs213.android.chess");
        listView.setAdapter(new ArrayAdapter<GameData>( this, android.R.layout.simple_list_item_1, rg.listByName(this)));
    }

    public void sortByDate(){
        int id = getResources().getIdentifier("list_games", "id", "cs213.android.chess");
        listView.setAdapter(new ArrayAdapter<GameData>(this, android.R.layout.simple_list_item_1, rg.listByDate(this)));
    }
}

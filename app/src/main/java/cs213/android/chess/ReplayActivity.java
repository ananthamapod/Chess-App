package cs213.android.chess;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import java.util.List;

import cs213.android.chess.util.PieceData;
import cs213.android.chess.util.RecordGame;


public class ReplayActivity extends AppCompatActivity {
    String filename;
    List<PieceData[]> moves;
    int curr = 0;
    int length = 0;
    Button prevBtn, nextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replay);
        Intent startIntent = getIntent();
        Bundle extras = startIntent.getExtras();
        if(startIntent.hasExtra("filename")) {
            filename = startIntent.getStringExtra("filename");
            RecordGame rg = RecordGame.getInstance();
            moves = rg.load(filename, this);
            Log.i("F", "Gets here");
            Log.i("Moves null?", (moves == null) + "");
            if(moves == null) {
                Toast.makeText(this,"No Game Loaded", Toast.LENGTH_SHORT).show();
                finish();
            }
            length = moves.size();
            Log.i("List empty?", length + "");
            GridView boardView = (GridView) findViewById(R.id.board);
            //createSquares(boardView);
            final SquareAdapter adapter = new SquareAdapter(this);
            adapter.setParent(boardView);
            boardView.setAdapter(adapter);
            adapter.setData(moves.get(curr));

            prevBtn = (Button) findViewById(R.id.prev_btn);

            prevBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    curr--;
                    adapter.setData(moves.get(curr));
                    if(curr == 0) {
                        view.setEnabled(false);
                    }
                    nextBtn.setEnabled(true);
                }
            });
            nextBtn = (Button) findViewById(R.id.next_btn);

            nextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    curr++;
                    adapter.setData(moves.get(curr));
                    if(curr == length-1) {
                        view.setEnabled(false);
                    }
                    prevBtn.setEnabled(true);
                }
            });

            prevBtn.setEnabled(false);
            if(length == 1) {
                nextBtn.setEnabled(false);
            }
        } else {
            Toast.makeText(this,"No Game Loaded", Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_replay, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }
}

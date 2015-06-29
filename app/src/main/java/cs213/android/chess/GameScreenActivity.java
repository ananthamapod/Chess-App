package cs213.android.chess;

                                                                                                                                                                                                                                                                                                            import android.app.ActionBar;
                                                                                                                                                                                                                                                                                                            import android.content.Intent;
                                                                                                                                                                                                                                                                                                            import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
                                                                                                                                                                                                                                                                                                            import android.util.Log;
                                                                                                                                                                                                                                                                                                            import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
                                                                                                                                                                                                                                                                                                            import android.view.ViewGroup;
                                                                                                                                                                                                                                                                                                            import android.view.WindowManager;
                                                                                                                                                                                                                                                                                                            import android.widget.Button;
                                                                                                                                                                                                                                                                                                            import android.widget.GridLayout;
                                                                                                                                                                                                                                                                                                            import android.widget.GridView;
                                                                                                                                                                                                                                                                                                            import android.widget.ImageButton;
                                                                                                                                                                                                                                                                                                            import android.widget.ImageView;
                                                                                                                                                                                                                                                                                                            import android.widget.RelativeLayout;
                                                                                                                                                                                                                                                                                                            import android.widget.TableLayout;
                                                                                                                                                                                                                                                                                                            import android.widget.TableRow;
                                                                                                                                                                                                                                                                                                            import android.widget.Toast;

                                                                                                                                                                                                                                                                                                            import cs213.android.chess.control.Board;
                                                                                                                                                                                                                                                                                                            import cs213.android.chess.util.Position;
                                                                                                                                                                                                                                                                                                            import cs213.android.chess.util.RecordGame;


public class GameScreenActivity extends AppCompatActivity {

    public Board board;
    public SquareAdapter adapter;
    public Button moveBtn, resignBtn, drawBtn, undoBtn, aiBtn;

    public boolean draw = false;
    public boolean sameTurn = true;

    static final int SAVE_GAME = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        board = new Board();
        setContentView(R.layout.activity_game_screen);


        GridView boardView = (GridView) findViewById(R.id.board);
        //createSquares(boardView);
        adapter = new SquareAdapter(this);
        adapter.setParent(boardView);
        boardView.setAdapter(adapter);
        adapter.setData(board.sendBoard());

        moveBtn = (Button) findViewById(R.id.move_btn);
        resignBtn = (Button) findViewById(R.id.resign_btn);
        drawBtn = (Button) findViewById(R.id.draw_btn);
        undoBtn = (Button) findViewById(R.id.undo_btn);
        aiBtn = (Button) findViewById(R.id.ai_btn);


        moveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GameScreenActivity activity = GameScreenActivity.this;
                SquareAdapter adapter = activity.adapter;
                Board board = activity.board;
                int start = adapter.firstSelected;
                int end = adapter.secondSelected;
                if(start == -1 || end == -1) {
                    Toast.makeText(activity,"No move selected",Toast.LENGTH_LONG).show();
                } else {
                    int y1 = start/8;
                    int x1 = start%8;

                    int y2 = end/8;
                    int x2 = end%8;

                    Position startpos = new Position(x1,y1);
                    Position endpos = new Position(x2, y2);

                    boolean valid;
                    if(draw && sameTurn) {
                        valid = board.move(startpos, endpos, "draw?");
                    } else  {
                        valid = board.move(startpos, endpos);
                    }

                    if(valid) {
                        if(draw && sameTurn) {
                            sameTurn = false;
                            Toast.makeText(activity, "Your opponent has offered a draw. Press DRAW to accept.",Toast.LENGTH_LONG).show();
                        } else if(draw && !sameTurn) {
                            draw = false;
                        }
                    }
                    adapter.setData(board.sendBoard());
                    adapter.firstPiece.callOnClick();
                    if(board.canUndo()) {
                        undoBtn.setEnabled(true);
                    } else {
                        undoBtn.setEnabled(false);
                    }
                    if(!board.message.equals("")) {
                        Toast.makeText(activity, board.message,Toast.LENGTH_LONG).show();
                    }
                    if(!board.gameInProgress()) {
                        activity.moveBtn.setEnabled(false);
                        activity.resignBtn.setEnabled(false);
                        activity.drawBtn.setEnabled(false);
                        activity.undoBtn.setEnabled(false);
                        activity.aiBtn.setEnabled(false);
                        Intent saveIntent = new Intent(GameScreenActivity.this, SaveGameActivity.class);
                        startActivityForResult(saveIntent, SAVE_GAME);
                    }
                }
            }
        });

        resignBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GameScreenActivity activity = GameScreenActivity.this;
                SquareAdapter adapter = activity.adapter;
                Board board = activity.board;
                board.move("resign");
                Toast.makeText(activity, board.message,Toast.LENGTH_LONG).show();
                if(!board.gameInProgress()) {
                    activity.moveBtn.setEnabled(false);
                    activity.resignBtn.setEnabled(false);
                    activity.drawBtn.setEnabled(false);
                    activity.undoBtn.setEnabled(false);
                    activity.aiBtn.setEnabled(false);
                    Intent saveIntent = new Intent(GameScreenActivity.this, SaveGameActivity.class);
                    startActivityForResult(saveIntent, SAVE_GAME);
                }
            }
        });

        drawBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GameScreenActivity activity = GameScreenActivity.this;
                SquareAdapter adapter = activity.adapter;
                Board board = activity.board;

                if(draw && !sameTurn) {
                    board.move("draw");
                    Toast.makeText(activity, board.message,Toast.LENGTH_LONG).show();
                    if(!board.gameInProgress()) {
                        activity.moveBtn.setEnabled(false);
                        activity.resignBtn.setEnabled(false);
                        activity.drawBtn.setEnabled(false);
                        activity.undoBtn.setEnabled(false);
                        activity.aiBtn.setEnabled(false);
                        Intent saveIntent = new Intent(GameScreenActivity.this, SaveGameActivity.class);
                        startActivityForResult(saveIntent, SAVE_GAME);
                    }
                } else if(!draw) {
                    draw = true;
                    sameTurn = true;
                    Toast.makeText(activity, "You have offered your opponent a draw\nOpponent will be notified at the end of your turn",Toast.LENGTH_LONG).show();
                }
            }
        });

        undoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(board.canUndo()) {
                    board.undo();
                    adapter.setData(board.sendBoard());
                    if(adapter.firstPiece != null) {
                        adapter.firstPiece.callOnClick();
                    }
                }
                undoBtn.setEnabled(false);
            }
        });
        undoBtn.setEnabled(false);

        aiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == SAVE_GAME) {
            if(resultCode == RESULT_OK) {
                Log.i("A", "GETS HERE");
                String filename = data.getStringExtra("filename");
                Log.i("A", "Filename:" + filename);
                boolean record = RecordGame.getInstance().save(filename, board.getRecord(), this);
                if(record) {
                    Toast.makeText(this,"Game Saved",Toast.LENGTH_SHORT).show();
                }
            }
            finish();
        }
    }
}

package tracy_pc.myconnect4;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import java.util.Stack;

import static tracy_pc.myconnect4.R.id;
import static tracy_pc.myconnect4.R.layout;

public class GameActivity extends Activity implements View.OnClickListener{
    private final int firstPlayer = 1;
    private final int secondPlayer = 2;
    private final int redChess = R.drawable.red_t;
    private final int greenChess = R.drawable.green_t;
    private final int redWinColor = R.drawable.red_wint;
    private final int greenWinColor = R.drawable.green_wint;
    private final int redWinImg = R.drawable.img_win_red;
    private final int greenWinImg = R.drawable.img_win_green;
    private int currentPlayer;//first player = 1,second player = 2
    private int chessColor;//current color of the chess
    private int winColor;//color of the winner chess
    private int winImg;//picture of the winner
    private int countChess;//record the chess
    private int[][] chessState = new int[6][7];//the state of the chess board
    private TableLayout tableLayout;
    private ImageButton[][] btnChess = new ImageButton[6][7];//chess button
    private ImageView imageView;//top pictures
    private ImageButton newGame;//button to start a new game
    private ImageButton retractChess;//button to retract a chess
    private Stack rowStack = new Stack();//save the row
    private Stack columnStack = new Stack();//save the column
    private SoundPool soundPool;//music

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_game);
        WindowManager wm = (WindowManager)this.getSystemService(Context.WINDOW_SERVICE);
        int screenWidth = wm.getDefaultDisplay().getWidth();//get the width of the screen
        int columnWidth = screenWidth / 7;//get the with of the column

        tableLayout = (TableLayout)findViewById(id.tableLayout);
        imageView = (ImageView)findViewById(id.imageView);

        soundPool= new SoundPool(10,AudioManager.STREAM_SYSTEM,5);
        soundPool.load(this,R.raw.put_chess,1);//put chess
        soundPool.load(this,R.raw.new_game,1);//new game
        soundPool.load(this,R.raw.retract,1);//retract the chess

       //draw the chess board
        int countId = 0;
        for(int i = 0;i < btnChess.length;i++){
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
            for(int j = 0;j < btnChess[i].length;j++){
                btnChess[i][j] = new ImageButton(this);
                btnChess[i][j].setId(countId);
                btnChess[i][j].setLayoutParams(new TableRow.LayoutParams(columnWidth,columnWidth));//set the size
                btnChess[i][j].setPadding(0,0,0,0);//set paddings
                btnChess[i][j].setScaleType(ImageView.ScaleType.FIT_CENTER);
                btnChess[i][j].setImageResource(R.drawable.empty_t);
                btnChess[i][j].setBackgroundColor(Color.BLACK);

                btnChess[i][j].setOnClickListener(this);
                tableRow.addView(btnChess[i][j]);
                countId++;
            }
            tableLayout.addView(tableRow);
        }

        //button to start a new game
        newGame = (ImageButton)findViewById(R.id.btn_new_game);
        newGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(2,1,1,0,0,1);//play the music
                init();
                startGame();
            }
        });
        newGame.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    v.setBackgroundResource(R.drawable.btn_ng_pressed);
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    v.setBackgroundResource(R.drawable.btn_ng);
                }
                return false;
            }
        });

        //button to retract the chess
        retractChess = (ImageButton)findViewById(id.btn_retract);
        retractChess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(3,1,1,0,0,1);
                if(countChess == 0){
                    //no chess
                    Toast.makeText(getBaseContext(),"No chess to be retracted !",Toast.LENGTH_SHORT).show();
                }else{
                    int tmpRow = (int)rowStack.pop();
                    int tmpColumn = (int)columnStack.pop();
                    chessState[tmpRow][tmpColumn] = 0;
                    btnChess[btnChess.length - 1 - tmpRow][tmpColumn].setImageResource(R.drawable.empty_t);
                    countChess--;
                    togglePlayer();
                }
            }
        });
        retractChess.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    v.setBackgroundResource(R.drawable.btn_retract_pressed);
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    v.setBackgroundResource(R.drawable.btn_retract);
                }
                return false;
            }
        });


        init();//initial the chess board
        startGame();//start the game
    }

    private void init(){
        //initial the chess array，chessState=0
        for (int i = 0;i < chessState.length;i++){
            for (int j = 0;j < chessState[i].length;j++){
                chessState[i][j] = 0;
                btnChess[i][j].setImageResource(R.drawable.empty_t);
                btnChess[i][j].setClickable(true);
            }
        }
        countChess = 0;//restart the count
        //empty the stack
        while(!rowStack.empty()){
            rowStack.pop();
        }
        while(!columnStack.empty()){
            columnStack.pop();
        }
        retractChess.setEnabled(true);
    }

    private void startGame() {
        //all are set to the first player
        imageView.setImageResource(R.drawable.img_turn_red);
        currentPlayer = firstPlayer;
        chessColor = redChess;
        winColor = redWinColor;
        winImg = redWinImg;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override

    public void onClick(View v) {
        soundPool.play(1,1,1,0,0,1);//play the chess music

        int column = v.getId() % 7;//get the clicked column
        putChess(currentPlayer,column);
    }//end of onclick

    public void putChess(int currentPlayer, int column) {
        int row =0;
        boolean flagForWin = false;
        for (;row<chessState.length;row++) {
            //no chess
            if(chessState[row][column] == 0){
                //put chess and change the state of chessState
                chessState[row][column] = currentPlayer;
                //change the picture of chess
                btnChess[chessState.length - 1 - row][column].setImageResource(chessColor);
                //record the row and column
                rowStack.push(row);
                columnStack.push(column);

                break;
            }
        }

        //row=6,stack overflow
        if(row >= chessState.length){
            //this column is full
            flagForWin = false;
            Toast.makeText(this,"This column is full. Please choose another column !",Toast.LENGTH_SHORT).show();
            countChess--;//avoid to wrong countnum
            togglePlayer();//replay the chess
        }else {
            //vertical
            if(row >= 3){
                if(chessState[row - 1][column] == currentPlayer
                        && chessState[row - 2][column]==currentPlayer
                        && chessState[row - 3][column]==currentPlayer){
                    flagForWin = true;
                    //set the picture
                    for(int k = 0;k < 4;k++){
                        btnChess[chessState.length -1 - row + k][column].setImageResource(winColor);
                    }
                }
            }
            //horizontal
            for (int j = 0;j < 4;j++){
                if(chessState[row][j] == currentPlayer
                        && chessState[row][j + 1] == currentPlayer
                        && chessState[row][j + 2] == currentPlayer
                        && chessState[row][j + 3] == currentPlayer){
                    flagForWin = true;
                    //set the picture
                    for(int k = 0;k < 4;k++){
                        btnChess[chessState.length -1 - row][j + k].setImageResource(winColor);
                    }
                }
            }
            //left-up
            for (int i = 0;i < chessState.length;i++){
                for (int j = 0;j < chessState[i].length;j++){
                    if(((i + 3) < chessState.length) && ((j + 3) < chessState[i].length)
                            && chessState[i][j] == currentPlayer
                            && chessState[i + 1][j + 1] == currentPlayer
                            && chessState[i + 2][j + 2] == currentPlayer
                            && chessState[i + 3][j + 3] == currentPlayer){
                        flagForWin = true;
                        //set the picture
                        for(int k = 0;k < 4;k++){
                            btnChess[chessState.length -1 - i - k][j + k].setImageResource(winColor);
                        }
                    }
                }
            }
            //right-up
            for (int i = 0;i < chessState.length;i++){
                for (int j = 0;j < chessState[i].length;j++){
                    if(((i - 3) >= 0) && ((j + 3) < chessState[i].length)
                            && chessState[i][j] == currentPlayer
                            && chessState[i - 1][j + 1] == currentPlayer
                            && chessState[i - 2][j + 2] == currentPlayer
                            && chessState[i - 3][j + 3] == currentPlayer){
                        flagForWin = true;
                        //set the picture
                        for(int k = 0;k < 4;k++){
                            btnChess[chessState.length -1 - i + k][j + k].setImageResource(winColor);
                        }
                    }
                }
            }
        }
        if(flagForWin){
            imageView.setImageResource(winImg);
            //chess cannot be clicked
            for (int i = 0;i < btnChess.length;i++){
                for (int j = 0;j < btnChess[i].length;j++){
                    btnChess[i][j].setClickable(false);
                }
            }
            retractChess.setEnabled(false);
            Toast.makeText(this,"Player "+currentPlayer+" wins !",Toast.LENGTH_SHORT).show();
        }else{
            togglePlayer();//change the player
        }
        countChess++;

        //the chess board is full
        if(countChess == 42){
            imageView.setImageResource(R.drawable.img_draw);
            //chess cannot be clicked
            for (int i = 0;i < btnChess.length;i++){
                for (int j = 0;j < btnChess[i].length;j++){
                    btnChess[i][j].setClickable(false);
                }
            }
            retractChess.setEnabled(false);
            Toast.makeText(this,"Draw game !",Toast.LENGTH_SHORT).show();
        }
    }

    public void togglePlayer(){
        if(currentPlayer == firstPlayer){
            //change player 1 to player 2
            imageView.setImageResource(R.drawable.img_turn_green);
            currentPlayer = secondPlayer;
            chessColor = greenChess;
            winColor = greenWinColor;
            winImg = greenWinImg;
        }else{
            //change player 2 to player 1
            imageView.setImageResource(R.drawable.img_turn_red);
            currentPlayer = firstPlayer;
            chessColor = redChess;
            winColor = redWinColor;
            winImg = redWinImg;
        }
    }

}
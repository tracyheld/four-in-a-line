package tracy_pc.myconnect4;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

public class StartActivity extends Activity {

    private ImageButton btnStart;
    private SoundPool soundPool;//music

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        soundPool= new SoundPool(10, AudioManager.STREAM_SYSTEM,5);
        soundPool.load(this,R.raw.start_game,1);//start game

        btnStart = (ImageButton)findViewById(R.id.btn_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(1,1,1,0,0,1);//play the music
                Intent intent = new Intent(getBaseContext(),GameActivity.class);
                startActivity(intent);
            }
        });
        btnStart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    //pic when pressed
                    v.setBackgroundResource(R.drawable.btn_start_pressed);
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    //normal pic
                    v.setBackgroundResource(R.drawable.btn_start);
                }
                return false;
            }
        });
    }
}

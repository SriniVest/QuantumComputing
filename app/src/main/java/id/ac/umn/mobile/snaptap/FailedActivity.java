package id.ac.umn.mobile.snaptap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class FailedActivity extends AppCompatActivity {

    private int id;
    private String username;
    private String fullname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_failed);

        id = getIntent().getExtras().getInt("ID");
        username = getIntent().getExtras().getString("USERNAME");
        fullname = getIntent().getExtras().getString("FULLNAME");

        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(2500);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    finish();
                }
            }
        };
        timerThread.start();
    }
}

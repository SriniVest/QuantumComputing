package id.ac.umn.mobile.snaptap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SuccessEnrollActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_enroll);

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

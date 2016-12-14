package id.ac.umn.mobile.snaptap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class SuccessActivity extends AppCompatActivity {

    private int id;
    private String username;
    private String fullname;
    private String tipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        id = getIntent().getExtras().getInt("ID");
        username = getIntent().getExtras().getString("USERNAME");
        fullname = getIntent().getExtras().getString("FULLNAME");
        tipe = getIntent().getExtras().getString("TYPE");

        Thread timerThread = new Thread(){
            public void run(){
                try{
                    new DoAbsent().execute(String.valueOf(id), tipe);
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

    class DoAbsent extends AsyncTask<String, Void, Boolean> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(SuccessActivity.this);
            progressDialog.setMessage("Retrieving List");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                String user_id = params[0];
                String tipe = params[1];
                HttpURLConnection connection =
                        (HttpURLConnection) new URL("http://umnfestival.com/snaptap/addDataAbsensi.php").openConnection();
                connection.setRequestMethod("POST");
                connection.connect();
                connection.getOutputStream().write(
                        String.format("user_id=%s&tipe=%s", user_id, tipe).getBytes()
                );
                InputStream input = new BufferedInputStream(connection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                connection.disconnect();

                return false;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            progressDialog.hide();
            progressDialog.dismiss();
        }
    }
}

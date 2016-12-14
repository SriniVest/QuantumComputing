package id.ac.umn.mobile.snaptap;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class UpdateProfileActivity extends AppCompatActivity {

    private int id;
    private String username;
    private String fullname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        id = getIntent().getExtras().getInt("ID");
        username = getIntent().getExtras().getString("USERNAME");
        fullname = getIntent().getExtras().getString("FULLNAME");

        final EditText passwordEdit = (EditText) findViewById(R.id.password_edit);
        final EditText fullnameEdit = (EditText) findViewById(R.id.fullname_edit);

        fullnameEdit.setText(fullname);

        Button updateButton = (Button) findViewById(R.id.button_update);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new DoUpdate().execute(String.valueOf(id), passwordEdit.getText().toString(), fullnameEdit.getText().toString()).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                Toast.makeText(getApplicationContext(), "Update Berhasil!!!", Toast.LENGTH_SHORT).show();

                finish();
            }
        });
    }

    class DoUpdate extends AsyncTask<String, Void, Boolean> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(UpdateProfileActivity.this);
            progressDialog.setMessage("Updating Account");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                String user_id = params[0];
                String password = params[1];
                String fullname = params[2];
                HttpURLConnection connection =
                        (HttpURLConnection) new URL("http://umnfestival.com/snaptap/updateDataAccount.php").openConnection();
                connection.setRequestMethod("POST");
                connection.connect();
                connection.getOutputStream().write(
                        String.format("user_id=%s&password=%s&fullname=%s", user_id, password, fullname).getBytes()
                );
                InputStream input = new BufferedInputStream(connection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                connection.disconnect();

                return Boolean.valueOf(stringBuilder.toString());
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

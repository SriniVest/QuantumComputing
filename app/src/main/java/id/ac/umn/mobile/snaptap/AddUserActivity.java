package id.ac.umn.mobile.snaptap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class AddUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        final EditText usernameAdd = (EditText) findViewById(R.id.username_add);
        final EditText passwordAdd = (EditText) findViewById(R.id.password_add);
        final EditText fullnameAdd = (EditText) findViewById(R.id.fullname_add);
        final EditText latitudeAdd = (EditText) findViewById(R.id.latitude_add);
        final EditText longitudeAdd = (EditText) findViewById(R.id.longitude_add);
        final EditText macAddressAdd = (EditText) findViewById(R.id.macaddress_add);

        Button addButton = (Button) findViewById(R.id.button_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameAdd.getText().toString();
                String password = passwordAdd.getText().toString();
                String fullname = fullnameAdd.getText().toString();
                String latitude = latitudeAdd.getText().toString();
                String longitude = longitudeAdd.getText().toString();
                String macaddress = macAddressAdd.getText().toString();

                if(!username.equals("") && !password.equals("") && !fullname.equals("")
                        && !latitude.equals("") && !longitude.equals("") && !macaddress.equals(""))
                {
                    try {
                        new DoAdd().execute(username, password, fullname, latitude, longitude, macaddress).get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getApplicationContext(), "Add Berhasil!!!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(AddUserActivity.this, CameraAdminActivity.class);
                    intent.putExtra("USERNAME", username);
                    intent.putExtra("FULLNAME", fullname);
                    startActivity(intent);

                    finish();
                } else {
                    Toast.makeText(AddUserActivity.this, "Semua field harus diisi!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    class DoAdd extends AsyncTask<String, Void, Boolean> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(AddUserActivity.this);
            progressDialog.setMessage("Adding Account");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                String username = params[0];
                String password = params[1];
                String fullname = params[2];
                String latitude = params[3];
                String longitude = params[4];
                String macaddress = params[5];
                HttpURLConnection connection =
                        (HttpURLConnection) new URL("http://umnfestival.com/snaptap/addDataAccount.php").openConnection();
                connection.setRequestMethod("POST");
                connection.connect();
                connection.getOutputStream().write(
                        String.format("username=%s&password=%s&fullname=%s&latitude=%s&longitude=%s&macaddress=%s", username, password, fullname, latitude, longitude, macaddress).getBytes()
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

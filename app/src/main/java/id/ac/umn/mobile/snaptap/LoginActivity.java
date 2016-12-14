package id.ac.umn.mobile.snaptap;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.test.mock.MockPackageManager;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.List;
import java.util.concurrent.ExecutionException;

public class LoginActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSION = 2;
    String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;

    GPSTracker gps;

    int idDb;
    String usernameDb;
    String passwordDb;
    String fullnameDb;
    String roleDb;
    Double longitudeDb;
    Double latitudeDb;
    String macIdDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        try {
            if (ActivityCompat.checkSelfPermission(this, mPermission)
                    != MockPackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{mPermission},
                        REQUEST_CODE_PERMISSION);

                // If any permission above not allowed by user, this condition will
                //execute every time, else your else part will work
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        final EditText passwordEdit = (EditText) findViewById(R.id.password_edit);
        final EditText usernameEdit = (EditText) findViewById(R.id.username_edit);

        Button b = (Button) findViewById(R.id.button_login);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!usernameEdit.getText().toString().equals("") && !passwordEdit.getText().toString().equals("")) {

                    String username = usernameEdit.getText().toString();
                    String password = passwordEdit.getText().toString();

                    try {
                        Boolean result = new LoginTask().execute(new String[]{username, password}).get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }

                    if(username.equals(usernameDb) && password.equals(passwordDb)) {
                        //Toast.makeText(LoginActivity.this, String.valueOf(idDb) + " " + String.valueOf(longitudeDb) + " " + fullnameDb, Toast.LENGTH_SHORT);

                        if(roleDb.equals("admin")) {
                            startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                        }
                        else {
                            int id = idDb;
                            String fullname = fullnameDb;
                            double dblatitude = latitudeDb;
                            double dblongitude = longitudeDb;
                            String dbmacId = macIdDb;
                            gps = new GPSTracker(LoginActivity.this);
                            if (gps.canGetLocation()) {

                                double latitude = gps.getLatitude();
                                double longitude = gps.getLongitude();

                                if (((latitude >= (dblatitude - 0.006)) && (latitude <= (dblatitude + 0.006)) &&
                                        (longitude >= (dblongitude - 0.006)) && (longitude <= (dblongitude + 0.006)))) {

                                    //if(latitude != dblatitude && longitude != dblongitude) {
                                    // \n is for new line
                                    //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: "
                                    //        + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();

                                    if (checkWifi()) {
                                        String macId = "" + getMacId();

                                        //Toast.makeText(getApplicationContext(), "WIFI MAC ADDRESS : " + macId + " " + dbmacId, Toast.LENGTH_SHORT).show();

                                        if (macId.length() == 0) {
                                            Intent intent = new Intent(Intent.ACTION_MAIN);
                                            intent.setClassName("com.android.phone", "com.android.phone.NetworkSetting");
                                            startActivity(intent);
                                            Toast.makeText(getApplicationContext(), "Anda harus menghidupkan wifi!!!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            if (macId.equals(dbmacId)) {
                                                Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                                                intent.putExtra("ID", id);
                                                intent.putExtra("USERNAME", username);
                                                intent.putExtra("FULLNAME", fullname);
                                                startActivity(intent);
                                                Toast.makeText(getApplicationContext(), "Anda berhasil login sebagai " + fullname, Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Anda harus connect ke wifi di tempat kerja!!!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    } else {
                                        //Intent intent = new Intent(Intent.ACTION_MAIN);
                                        //intent.setClassName("com.android.phone", "com.android.phone.NetworkSetting");
                                        //startActivity(intent);
                                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                        Toast.makeText(getApplicationContext(), "Anda harus menghidupkan wifi!!!", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(LoginActivity.this, "Lokasi tidak berada di tempat kerja!!!", Toast.LENGTH_SHORT).show();
                                    Log.d("Lokasi", "Lat : " + latitude + " Long : " + longitude);
                                }

                                //Intent i = new Intent(getBaseContext(),MenuActivity.class);
                                //startActivity(i);

                            } else {
                                //Nyalain GPS
                                gps.showSettingsAlert();
                            }
                        }
                    }
                    else {
                        Toast.makeText(LoginActivity.this, "Username atau Password salah!!!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Username dan Password harus diisi!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public String getMacId() {

        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo.getBSSID();
    }

    public Boolean checkWifi() {
        WifiManager wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        return wifi.isWifiEnabled();
    }

    class LoginTask extends AsyncTask<String, Void, Boolean> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage("Checking Account");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                String username = params[0];
                String password = params[1];
                HttpURLConnection connection =
                        (HttpURLConnection) new URL("http://umnfestival.com/snaptap/getData.php").openConnection();
                connection.setRequestMethod("POST");
                connection.connect();
                connection.getOutputStream().write(
                        String.format("username=%s&password=%s", username, password).getBytes()
                );
                InputStream input = new BufferedInputStream(connection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                connection.disconnect();

                Log.d("Response", stringBuilder.toString());

                JSONObject object = new JSONObject(stringBuilder.toString());
                JSONArray jsonArray = object.getJSONArray("result");
                for(int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    idDb = jsonObject.getInt("user_id");
                    usernameDb = jsonObject.getString("username");
                    passwordDb = jsonObject.getString("password");
                    fullnameDb = jsonObject.getString("fullname");
                    roleDb = jsonObject.getString("role");
                    latitudeDb = jsonObject.getDouble("latitude");
                    longitudeDb = jsonObject.getDouble("longitude");
                    macIdDb = jsonObject.getString("macaddress");
                }

                return Boolean.valueOf(stringBuilder.toString());
            } catch (JSONException | IOException e){
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

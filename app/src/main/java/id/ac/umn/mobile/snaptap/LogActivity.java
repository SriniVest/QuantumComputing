package id.ac.umn.mobile.snaptap;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

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

public class LogActivity extends AppCompatActivity {

    private int id;
    private String username;
    private String fullname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        id = getIntent().getExtras().getInt("ID");
        username = getIntent().getExtras().getString("USERNAME");
        fullname = getIntent().getExtras().getString("FULLNAME");

        try {
            new GetInTask().execute(new String[]{String.valueOf(id)}).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        try {
            new GetOutTask().execute(new String[]{String.valueOf(id)}).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    class GetInTask extends AsyncTask<String, Void, ArrayList<HashMap<String, String>>> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(LogActivity.this);
            progressDialog.setMessage("Retrieving List");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected ArrayList<HashMap<String, String>> doInBackground(String... params) {
            ArrayList<HashMap<String, String>> listIn = new ArrayList<>();
            try {
                String user_id = params[0];

                HttpURLConnection connection =
                        (HttpURLConnection) new URL("http://umnfestival.com/snaptap/getDataAbsensi.php").openConnection();
                connection.setRequestMethod("POST");
                connection.connect();
                connection.getOutputStream().write(
                        String.format("user_id=%s", user_id).getBytes()
                );
                InputStream input = new BufferedInputStream(connection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                connection.disconnect();

                JSONObject object = new JSONObject(stringBuilder.toString());
                JSONArray jsonArray = object.getJSONArray("result");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String tipe = jsonObject.getString("tipe");
                    String waktu = jsonObject.getString("waktu");
                    HashMap<String, String> tapIn = new HashMap<>();
                    tapIn.put("tipe", tipe);
                    tapIn.put("waktu", waktu);
                    listIn.add(tapIn);
                }
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }

            return listIn;
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String, String>> hashMaps) {
            super.onPostExecute(hashMaps);
            progressDialog.hide();
            progressDialog.dismiss();
            ListView listIn = (ListView) findViewById(R.id.list_absen_in);

            listIn.setAdapter(new SimpleAdapter(
                    LogActivity.this,
                    hashMaps,
                    android.R.layout.simple_list_item_2,
                    new String[] {
                            "tipe",
                            "waktu"
                    },
                    new int[] {
                            android.R.id.text1,
                            android.R.id.text2
                    }
            ));
        }
    }

    class GetOutTask extends AsyncTask<String, Void, ArrayList<HashMap<String, String>>> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(LogActivity.this);
            progressDialog.setMessage("Retrieving List");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected ArrayList<HashMap<String, String>> doInBackground(String... params) {
            ArrayList<HashMap<String, String>> listOut = new ArrayList<>();
            try {
                String user_id = params[0];

                HttpURLConnection connection =
                        (HttpURLConnection) new URL("http://umnfestival.com/snaptap/getDataAbsensiOut.php").openConnection();
                connection.setRequestMethod("POST");
                connection.connect();
                connection.getOutputStream().write(
                        String.format("user_id=%s", user_id).getBytes()
                );
                InputStream input = new BufferedInputStream(connection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                connection.disconnect();

                JSONObject object = new JSONObject(stringBuilder.toString());
                JSONArray jsonArray = object.getJSONArray("result");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String tipe = jsonObject.getString("tipe");
                    String waktu = jsonObject.getString("waktu");
                    HashMap<String, String> tapOut = new HashMap<>();
                    tapOut.put("tipe", tipe);
                    tapOut.put("waktu", waktu);
                    listOut.add(tapOut);
                }
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }

            return listOut;
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String, String>> hashMaps) {
            super.onPostExecute(hashMaps);
            progressDialog.hide();
            progressDialog.dismiss();
            ListView listOut = (ListView) findViewById(R.id.list_absen_out);

            listOut.setAdapter(new SimpleAdapter(
                    LogActivity.this,
                    hashMaps,
                    android.R.layout.simple_list_item_2,
                    new String[] {
                            "tipe",
                            "waktu"
                    },
                    new int[] {
                            android.R.id.text1,
                            android.R.id.text2
                    }
            ));
        }
    }
}

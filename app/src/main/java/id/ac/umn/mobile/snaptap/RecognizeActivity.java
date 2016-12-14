package id.ac.umn.mobile.snaptap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.kairos.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class RecognizeActivity extends AppCompatActivity {

    private int id;
    private String username;
    private String fullname;
    private String tipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognize);

        id = getIntent().getExtras().getInt("ID");
        username = getIntent().getExtras().getString("USERNAME");
        fullname = getIntent().getExtras().getString("FULLNAME");
        tipe = getIntent().getExtras().getString("TYPE");

        File out = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");

        out = new File(out.getPath() + File.separator +
                "IMG_Ricognize.jpg");

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        Bitmap bitmap = BitmapFactory.decodeFile(out.getPath(), options);

        //mFaceOverlayView.setBitmap(bitmap);

        // listener
        KairosListener listener = new KairosListener() {

            @Override
            public void onSuccess(String response) {
                Log.d("SUCCESS", response);
                //responseBody = response;
                try {
                    JSONObject parentObject = new JSONObject(response);
                    JSONArray jsonArray = parentObject.getJSONArray("images");
                    Log.d("RESPONSE", jsonArray.getString(0));
                    JSONObject childObject = new JSONObject(jsonArray.getString(0));
                    JSONObject realObject = new JSONObject(childObject.getString("transaction"));
                    Log.d("RESPONSE", realObject.getString("status"));
                    String result = realObject.getString("status");

                    if(result.equals("success"))
                    {

                        //new DoAbsent().execute(new String[]{String.valueOf(id), tipe}).get();

                        Toast.makeText(RecognizeActivity.this, "Anda berhasil melakukan absensi!!!", Toast.LENGTH_SHORT);
                        Intent intent = new Intent(RecognizeActivity.this, SuccessActivity.class);
                        intent.putExtra("ID", id);
                        intent.putExtra("USERNAME", username);
                        intent.putExtra("FULLNAME", fullname);
                        intent.putExtra("TYPE", tipe);
                        startActivity(intent);

                        finish();
                    } else {
                        Toast.makeText(RecognizeActivity.this, "Anda tidak dikenal!!!", Toast.LENGTH_SHORT);
                        Intent intent = new Intent(RecognizeActivity.this, FailedActivity.class);
                        intent.putExtra("ID", id);
                        intent.putExtra("USERNAME", username);
                        intent.putExtra("FULLNAME", fullname);
                        startActivity(intent);

                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } //catch (InterruptedException e) {

                //  e.printStackTrace();
               // } catch (ExecutionException e) {
               //     e.printStackTrace();
               // }

            }

            @Override
            public void onFail(String response) {
                Log.d("FAILED", response);
                //responseBody = response;
                Toast.makeText(RecognizeActivity.this, "Anda gagal melakukan absensi!!!", Toast.LENGTH_SHORT);
                Intent intent = new Intent(RecognizeActivity.this, FailedActivity.class);
                intent.putExtra("ID", id);
                intent.putExtra("USERNAME", username);
                intent.putExtra("FULLNAME", fullname);
                startActivity(intent);

                finish();
            }
        };

        /* * * instantiate a new kairos instance * * */
        Kairos myKairos = new Kairos();

        /* * * set authentication * * */
        String app_id = "8e6a2375";
        String api_key = "9edc542860e7e80e48537898c8f86348";
        myKairos.setAuthentication(this, app_id, api_key);



        try {
            //Log.d("Size", String.valueOf(bitmap.getByteCount()));

            //myKairos.listGalleries(listener);

            //String selector = "FULL";
            //String minHeadScale = "0.25";
            //myKairos.detect(bitmap, selector, minHeadScale, listener);

            // Log.d("Status", "Detect Complete");

            //String subjectId = "Rico";
            //String galleryId = "employee";
            //String selector = "FULL";
            //String multipleFaces = "false";
            //String minHeadScale = "0.25";
            //myKairos.enroll(bitmap,
            //        subjectId,
            //        galleryId,
            //        selector,
            //        multipleFaces,
            //        minHeadScale,
            //       listener);

            String galleryId = username;
            String selector = "FULL";
            String threshold = "0.75";
            String minHeadScale = "0.25";
            String maxNumResults = "25";
            myKairos.recognize(bitmap,
                    galleryId,
                    selector,
                    threshold,
                    minHeadScale,
                    maxNumResults,
                    listener);

            //  List galleries
            //myKairos.listGalleries(listener);

            //  List subjects in gallery
            //myKairos.listSubjectsForGallery("employee", listener);

            //Log.d("Balesan", responseBody);
            //JSONObject jsonObject = new JSONObject(responseBody);
            //JSONArray jsonArray = jsonObject.getJSONArray("image");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    class DoAbsent extends AsyncTask<String, Void, Boolean> {

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
    }
}
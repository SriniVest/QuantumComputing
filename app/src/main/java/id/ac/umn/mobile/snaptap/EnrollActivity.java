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

import com.kairos.Kairos;
import com.kairos.KairosListener;

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

public class EnrollActivity extends AppCompatActivity {

    private String username;
    private String fullname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognize);

        username = getIntent().getExtras().getString("USERNAME");
        fullname = getIntent().getExtras().getString("FULLNAME");

        File out = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");

        out = new File(out.getPath() + File.separator +
                "IMG_RicognizeAdmin.jpg");

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
                        startActivity(new Intent(EnrollActivity.this, SuccessEnrollActivity.class));

                        finish();
                    } else {
                        startActivity(new Intent(EnrollActivity.this, FailedEnrollActivity.class));

                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFail(String response) {
                Log.d("FAILED", response);

                startActivity(new Intent(EnrollActivity.this, FailedEnrollActivity.class));

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

            String subjectId = fullname;
            String galleryId = username;
            String selector = "FULL";
            String multipleFaces = "false";
            String minHeadScale = "0.25";
            myKairos.enroll(bitmap,
                    subjectId,
                    galleryId,
                    selector,
                    multipleFaces,
                    minHeadScale,
                    listener);

            //String galleryId = "employee";
            //String selector = "FULL";
            //String threshold = "0.75";
            //String minHeadScale = "0.25";
            //String maxNumResults = "25";
            //myKairos.recognize(bitmap,
            //        galleryId,
            //        selector,
            //        threshold,
            //        minHeadScale,
            //        maxNumResults,
            //        listener);

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
}

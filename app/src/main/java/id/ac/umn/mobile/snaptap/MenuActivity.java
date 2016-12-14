package id.ac.umn.mobile.snaptap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MenuActivity extends AppCompatActivity {

    private int id;
    private String username;
    private String fullname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        id = getIntent().getExtras().getInt("ID");
        username = getIntent().getExtras().getString("USERNAME");
        fullname = getIntent().getExtras().getString("FULLNAME");

        ImageView tapIn = (ImageView) findViewById(R.id.tap_in);
        tapIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent i = new Intent(getBaseContext(), CameraActivity.class);
                i.putExtra("ID", id);
                i.putExtra("USERNAME", username);
                i.putExtra("FULLNAME", fullname);
                i.putExtra("TYPE", "IN");
                startActivity(i);
            }
        });

        ImageView tapOut = (ImageView) findViewById(R.id.tap_out);
        tapOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), CameraActivity.class);
                i.putExtra("ID", id);
                i.putExtra("USERNAME", username);
                i.putExtra("FULLNAME", fullname);
                i.putExtra("TYPE", "OUT");
                startActivity(i);
            }
        });

        ImageView log = (ImageView) findViewById(R.id.log);
        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), LogActivity.class);
                i.putExtra("ID", id);
                i.putExtra("USERNAME", username);
                i.putExtra("FULLNAME", fullname);
                startActivity(i);
            }
        });

        ImageView userProfile = (ImageView) findViewById(R.id.profile_user);
        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), UpdateProfileActivity.class);
                i.putExtra("ID", id);
                i.putExtra("USERNAME", username);
                i.putExtra("FULLNAME", fullname);
                startActivity(i);
            }
        });
    }
}

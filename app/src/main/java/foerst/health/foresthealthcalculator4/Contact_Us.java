package foerst.health.foresthealthcalculator4;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class Contact_Us extends AppCompatActivity implements View.OnClickListener{
    private Toolbar toolbar;
    private EditText feed,email;
    private ImageView facebook,linkedin,twitter,youtube,instagram;
    private String url = "http://203.135.62.111:8080/tree-health/";
    // local ip 10.99.0.66
    // public ip 203.135.62.111

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact__us);
        toolbar=findViewById(R.id.feedback_toolbar);
        setTitle("Contact US");
        setSupportActionBar(toolbar);

        feed=findViewById(R.id.feedback);
        email = findViewById(R.id.email_address);

        /*facebook = findViewById(R.id.facebook);
        facebook.setOnClickListener(this);
        linkedin = findViewById(R.id.linkedin_view);
        linkedin.setOnClickListener(this);
        twitter = findViewById(R.id.twitter);
        twitter.setOnClickListener(this);
        youtube = findViewById(R.id.yout);
        youtube.setOnClickListener(this);
        instagram = findViewById(R.id.instagram);
        instagram.setOnClickListener(this);*/
    }
    @Override
    public void onClick(View v) {
        /*switch (v.getId()){
            case R.id.facebook:
                Intent faceIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/lahoreuniversityofmanagementsciences"));
                startActivity(faceIntent);
                break;
            case R.id.linkedin_view:
                Intent linkedinIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/school/lahore-university-of-management-sciences/"));
                startActivity(linkedinIntent);
                break;
            case R.id.twitter:
                Intent twitterIntent = new Intent(Intent.ACTION_VIEW,Uri.parse("https://twitter.com/lifeatlums"));
                startActivity(twitterIntent);
                break;
            case R.id.yout:
                startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.youtube.com/channel/UCNyztcNIfmZquNp45gf54dg")));
                break;
            case R.id.instagram:
                startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.instagram.com/lifeatlums/")));
                break;
            default:
                break;
        }*/
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.send_feedback,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sendfeedbackapp:
                sendFeedBack();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendFeedBack() {
        final String feedback = feed.getText().toString();
        final String email_address = email.getText().toString();
        final String curr_id = String.valueOf(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));

        if (feedback.isEmpty()) {
            feed.setError("You can't send empty message");
            feed.requestFocus();
            return;
        }
        if (email_address.isEmpty()) {
            email.setError("You can't send empty email");
            email.requestFocus();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url + "feedback/",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), "Feedback Sent", Toast.LENGTH_SHORT).show();
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error: while sending feedback \n" + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", feedback);
                params.put("field_of_interest", email_address);
                params.put("organization", curr_id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}

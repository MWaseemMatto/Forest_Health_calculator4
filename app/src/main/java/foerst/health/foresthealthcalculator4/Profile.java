package foerst.health.foresthealthcalculator4;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Profile extends AppCompatActivity {
    private TextView proname,profoi,proorg;
    private Toolbar toolbar_profile;
    private FloatingActionButton editProfile;
    private String currentId;
    private String url = "http://203.135.62.111:8080/tree-health/";
    // local ip 10.99.0.66
    // public ip 203.135.62.111

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        toolbar_profile = findViewById(R.id.pro_toolbar);
        setTitle("My Profile");
        setSupportActionBar(toolbar_profile);

        proname = findViewById(R.id.profile_name);
        profoi = findViewById(R.id.profile_foi);
        proorg = findViewById(R.id.profile_orga);

        currentId=String.valueOf(Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID));
        //load user information
        userInfo();
        editProfile = findViewById(R.id.editprofile);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Profile.this,Profile_Edit.class));
            }
        });
    }

    //load user information
    private void userInfo(){
        StringRequest stringRequest=new StringRequest(Request.Method.GET,
                url+"users/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array=new JSONArray(response);
                            for(int i=0;i<array.length();i++){
                                JSONObject object=array.getJSONObject(i);
                                String id=object.getString("android_id");
                                if (currentId.equals(id)){
                                    String name=object.getString("name");
                                    proname.setText(name);
                                    String foi=object.getString("field_of_interest");
                                    profoi.setText(foi);
                                    String org=object.getString("organization");
                                    proorg.setText(org);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error! while fetching profile\n"+error,Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(Profile.this);
        requestQueue.add(stringRequest);
    }
}

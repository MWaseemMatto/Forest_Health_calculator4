package foerst.health.foresthealthcalculator4;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Mobile_Info_settings extends AppCompatActivity implements View.OnClickListener{
    private EditText sensorsize;
    private TextView focal_length;
    private Button mobileInfoSave;
    private Toolbar toolbar;
    private ProgressDialog progressDialog = null;
    private String currentId;

    private String url = "http://203.135.62.111:8080/tree-health/";
    // local ip 10.99.0.66
    // public ip 203.135.62.111

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile__info_settings);
        toolbar=findViewById(R.id.mobile_info_toolbar);
        setSupportActionBar(toolbar);
        setTitle("Mobile Info Setting");
        progressDialog=new ProgressDialog(this);
        sensorsize = findViewById(R.id.sensorheight);
        sensorsize.setSelection(sensorsize.getText().length());
        focal_length = findViewById(R.id.focal_len);
        mobileInfoSave = findViewById(R.id.mobile_info_save);
        mobileInfoSave.setOnClickListener(this);
        currentId=String.valueOf(Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID));
        getMobileInfo();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mobile_info_save:
                updateMobileInfo();
                break;
            default:
                break;
        }
    }
    private void getMobileInfo(){
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
                                    String sensor_h=object.getString("sensor_height");
                                    sensorsize.setText(sensor_h);
                                    String focal_len=object.getString("focal_length");
                                    focal_length.setText(focal_len);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error! \n"+error,Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(Mobile_Info_settings.this);
        requestQueue.add(stringRequest);
    }

    private void updateMobileInfo(){
        final String android_id= String.valueOf(Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID));
        final String sensor_height = sensorsize.getText().toString();

        progressDialog.setTitle("Processing");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        StringRequest stringRequest=new StringRequest(Request.Method.PUT,
                url+android_id+"/update-user/",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        startActivity(new Intent(Mobile_Info_settings.this, Image_processing.class));
                        Toast.makeText(getApplicationContext(),"User Info inserted!",Toast.LENGTH_SHORT).show();
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Error: while updating user \n"+error,Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<String, String>();
                params.put("sensor_height",sensor_height);
                return  params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(Mobile_Info_settings.this);
        requestQueue.add(stringRequest);
    }
}

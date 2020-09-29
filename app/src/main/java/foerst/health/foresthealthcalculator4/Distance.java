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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static foerst.health.foresthealthcalculator4.Image_processing.imgID;

public class Distance extends AppCompatActivity {
    private Button cali_next=null;
    private Toolbar toolbarC=null;
    EditText diameterofTree=null, step=null;
    private ProgressDialog progressDialog=null;
    public static String id_img;
    private String url = "http://203.135.62.111:8080/tree-health/";
    // local ip 10.99.0.66
    // public ip 203.135.62.111

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distance);
        progressDialog=new ProgressDialog(this);
        diameterofTree = findViewById(R.id.diameter_of_tree);
        step = findViewById(R.id.steps);
        cali_next = findViewById(R.id.cali_button);
        toolbarC = findViewById(R.id.toolbar);
        setSupportActionBar(toolbarC);
        cali_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateMobileInfo();
            }
        });
    }

    private void updateMobileInfo() {
        final String distance=step.getText().toString();
        final String diameter= diameterofTree.getText().toString();
        if(distance.isEmpty()){
            step.setError("Please Enter diameter!");
            step.requestFocus();
            return;
        }
        StringRequest stringRequest=new StringRequest(Request.Method.PUT,
                url+imgID+"/update-image/",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        calculate_Height();
                        //startActivity(new Intent(Distance.this, Image_Processing.class));
                        Toast.makeText(getApplicationContext(),"diameter inserted!",Toast.LENGTH_SHORT).show();
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Error: while sending diameter \n"+error,Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<String, String>();
                params.put("distance",distance);
                params.put("diameter",diameter);
                return  params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(Distance.this);
        requestQueue.add(stringRequest);
    }

    public void calculate_Height() {
        progressDialog.setTitle("Processing");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        final String id_android_waseem = String.valueOf(Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID));
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url+"calc-health/",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            progressDialog.dismiss();
                            JSONObject json = new JSONObject(response);
                            id_img = json.getString("mobile_image_id");
                            startActivity(new Intent(Distance.this, Results.class));
                            Toast.makeText(getApplicationContext(), "height calculation done!!", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Exception \n"+e,Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Error: while height calculation \n"+"\n"+error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("android_id",id_android_waseem);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

}

package foerst.health.foresthealthcalculator4;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
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

import java.util.HashMap;
import java.util.Map;

public class Signin extends AppCompatActivity {
    private Button proceeding_button = null;

    private EditText epname = null, eorganization = null, eFOT = null, ss= null;
    private ProgressDialog progressDialog = null;
    private Toolbar toolbarM = null;
    public int h =0, w=0;
    public String androidid = "";

    private String base_url="http://203.135.62.111:8080/tree-health/";
    // local ip 10.99.0.66
    // public ip 203.135.62.111


    Camera mcamera;
    Float focul_length;
    Float verticalViewAngle ;
    Float horizontalViewAngle ;
    Camera.Parameters params ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        toolbarM= findViewById(R.id.toolbar);
        setTitle("User Info");
        setSupportActionBar(toolbarM);
        //navigationView = (NavigationView) findViewById(R.id.nvi);

        ss = findViewById(R.id.ssheight);
        progressDialog=new ProgressDialog(this);
        proceeding_button=(Button) findViewById(R.id.nextbutton);
        epname=(EditText) findViewById(R.id.id_name);
        eorganization=(EditText) findViewById(R.id.orgid);
        eFOT=(EditText) findViewById(R.id.interstid);

        proceeding_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send_user_data(); //this
                //startActivity(new Intent(Main2Activity.this, Calibration.class));
            }
        });
        screenheight();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    private void screenheight() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        h = displaymetrics.heightPixels+=300;
        w = displaymetrics.widthPixels+=50;
    }

    private void getFocalLength(){
        params = mcamera.getParameters();
        focul_length = params.getFocalLength();
        verticalViewAngle = params.getVerticalViewAngle();
        horizontalViewAngle = params.getHorizontalViewAngle();
        //Toast.makeText(getApplicationContext(),""+focul_length,Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(mcamera == null) {
            mcamera = Camera.open();
            getFocalLength();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mcamera != null){
            mcamera.release();
            mcamera = null;
        }
    }


    public void send_user_data(){
        final String name=epname.getText().toString();
        final String interest=eFOT.getText().toString();
        final String orga=eorganization.getText().toString();
        final String id = String.valueOf(Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID));
        final String FL = focul_length.toString();
        final String ss_height = ss.getText().toString();

        if(name.isEmpty()){
            epname.setError("Please Enter Name!");
            epname.requestFocus();
            return ;
        }
        if(orga.isEmpty()){
            eorganization.setError("Please Enter organization!");
            eorganization.requestFocus();
            return ;
        }

        if(ss_height.isEmpty()){
            ss.setError("Please Enter Sensor Height!");
            ss.requestFocus();
            return ;
        }

        progressDialog.setTitle("Processing");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        StringRequest stringRequest=new StringRequest(Request.Method.POST,
                base_url+"create-user/",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        startActivity(new Intent(Signin.this, Image_processing.class));
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Error: while creating user \n"+error,Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<String, String>();
                params.put("name",name);
                params.put("field_of_interest",interest);
                params.put("organization",orga);
                params.put("focal_length",FL);
                params.put("sensor_height",ss_height);
                params.put("android_id",id);
                return  params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(Signin.this);
        requestQueue.add(stringRequest);
    }
}

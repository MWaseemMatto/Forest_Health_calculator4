package foerst.health.foresthealthcalculator4;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

public class Photo_Details extends AppCompatActivity {
    private TextView histH,histB,histC,histTC;
    private String H,B,C,TC;
    String str;
    private Toolbar toolbar;
    private ImageView image_result;
    private ProgressDialog progressDialog;
    private String url="http://203.135.62.111:8080/tree-health/";
    // local ip 10.99.0.66
    // public ip 203.135.62.11

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo__details);
        progressDialog=new ProgressDialog(Photo_Details.this);
        toolbar= findViewById(R.id.resulted2_toolbar);
        setSupportActionBar(toolbar);
        setTitle("View Details");

        histH = findViewById(R.id.hist_height);
        histB = findViewById(R.id.hist_biomass);
        histC = findViewById(R.id.hist_carbon_content);
        histTC = findViewById(R.id.hist_total_carbon);
        image_result=(ImageView) findViewById(R.id.hist_image);
        str = getIntent().getStringExtra("id");
        //Toast.makeText(getApplicationContext(),""+str,Toast.LENGTH_SHORT).show();
        imageRequest();
        imageValues();
    }

    private void imageRequest() {
        //progressDialog.setTitle("");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        StringRequest stringRequest =new StringRequest(Request.Method.GET,
                url+str+"/image-info/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {

                            JSONObject object=new JSONObject(response);
                            String image=object.getString("original_image");
                            Glide.with(getApplicationContext()).load(image).into(image_result);

                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Error: while showing result\n"+error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void imageValues(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url+str+"/health-result",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object= new JSONObject(response);
                            H = String.valueOf(object.getDouble("height"));
                            histH.setText(H);
                            //Toast.makeText(getActivity().getApplicationContext(),height+" Height",Toast.LENGTH_SHORT).show();
                            B = String.valueOf(object.getDouble("biomass"));
                            //Toast.makeText(getActivity().getApplicationContext(),biomass+" biomass",Toast.LENGTH_SHORT).show();
                            histB.setText(B);
                            C = String.valueOf(object.getDouble("carbon_content"));
                            histC.setText(C);
                            TC = String.valueOf(object.getDouble("total_carbon_absorbed"));
                            histTC.setText(TC);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error: while getting values \n" +error,Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
}

package foerst.health.foresthealthcalculator4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DashBoard extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar=null;
    public static String curr_id;
    String matto;
    int ALL_PERMISSIONS = 101;

    private Button start;
    private String m3_url = "http://203.135.62.111:8080/tree-health/";
    // local ip 10.99.0.66
    // public ip 203.135.62.111
    private int STORAGE_PERMISSION_CODE = 1;


   private CardView profile_card, explore_card, contact_card, about_card;

    String km;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        toolbar=findViewById(R.id.toolbar_dashboard);
        setTitle("Forest Health Calculator");
        setSupportActionBar(toolbar);
        permissions();
        curr_id = String.valueOf(Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID));
        start = findViewById(R.id.strbttn);
        start.setOnClickListener(this);
        profile_card = findViewById(R.id.profile_card_view);
        profile_card.setOnClickListener(this);
        explore_card = findViewById(R.id.Explore_card_view);
        explore_card.setOnClickListener(this);
        contact_card = findViewById(R.id.contact_card_view);
        contact_card.setOnClickListener(this);
        about_card = findViewById(R.id.about_card_view);
        about_card.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.strbttn:
                Starter();
                break;
            case R.id.profile_card_view:
                startActivity(new Intent(DashBoard.this,Profile.class));
                break;
            case R.id.Explore_card_view:
                startActivity(new Intent(DashBoard.this,MapActivity.class));
                break;
            case R.id.contact_card_view:
                startActivity(new Intent(DashBoard.this,Contact_Us.class));
                break;
            case R.id.about_card_view:
                startActivity(new Intent(DashBoard.this,About_Us.class));
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.setting:
                startActivity(new Intent(DashBoard.this, settings_app.class));
                break;
            case R.id.learn_more:
                startActivity(new Intent(getApplicationContext(),Walkout_activity.class));
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void Starter(){
        StringRequest stringRequest=new StringRequest(Request.Method.GET, m3_url+"users/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray=new JSONArray(response);
                            for(int i = 0;i < jsonArray.length(); i++){
                                JSONObject object= jsonArray.getJSONObject(i);
                                String ids=object.getString("android_id");
                                km= object.getString("organization");
                                if(curr_id.equals(ids)) {
                                    matto = object.getString("android_id");
                                }
                            }
                            if(curr_id.equals(matto)){
                                startActivity(new Intent(DashBoard.this,Image_processing.class));
                            }
                            else {
                                startActivity(new Intent(DashBoard.this,Signin.class));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error: checking connection\n"+error,Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(DashBoard.this);
        requestQueue.add(stringRequest);
    }

    private void permissions(){
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

        } else {
            requestStoragePermission();
        }
    }

    private void requestStoragePermission() {
        final String[] permissions = new String[]{Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION};
        ActivityCompat.requestPermissions(this, permissions, ALL_PERMISSIONS);
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(DashBoard.this,
                                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ALL_PERMISSIONS)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == STORAGE_PERMISSION_CODE)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

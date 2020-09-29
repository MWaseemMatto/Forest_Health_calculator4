package foerst.health.foresthealthcalculator4;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Walkout_activity extends AppCompatActivity {
    private ViewPager viewPager;
    private Walkout_adapter pager_adapter;
    private List<Walkout_model> mlist;
    private Button next_button,btn_getstarted;
    private TextView skip;
    private int position;
    private Animation started_button;
    public static String curr_id;
    String matto;
    private String m3_url = "http://203.135.62.111:8080/tree-health/";
    // local ip 10.99.0.66
    // public ip 203.135.62.111

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walkout_activity);
        mlist=new ArrayList<>();
        mlist.add(new Walkout_model("Men Styles","we make you more handsome",R.drawable.ic_directions_walk_black_24dp));
        mlist.add(new Walkout_model("Woman Styles","we make you more gorgeous and beautiful",R.drawable.ic_photo_black_24dp));
        mlist.add(new Walkout_model("Home service","we can provide you services at your doorstep",R.drawable.ic_photo_black_24dp));
        curr_id = String.valueOf(Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID));
        viewPager=findViewById(R.id.view_paper);
        pager_adapter = new Walkout_adapter(this,mlist);
        viewPager.setAdapter(pager_adapter);

        skip=findViewById(R.id.skip_text);
        next_button=findViewById(R.id.nextbutton);
        btn_getstarted = findViewById(R.id.btn_getstarted);
        btn_getstarted.setVisibility(View.INVISIBLE);
        started_button = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.getstarted);


        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Starter();
            }
        });
        btn_getstarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Starter();
            }
        });
        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position=viewPager.getCurrentItem();
                if(position<mlist.size()){
                    position++;
                    viewPager.setCurrentItem(position);
                }
                if(position == mlist.size()-1){
                    skip.setVisibility(View.INVISIBLE);
                    next_button.setVisibility(View.INVISIBLE);
                    btn_getstarted.setVisibility(View.VISIBLE);
                    btn_getstarted.setAnimation(started_button);
                }
            }
        });
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
                                if(curr_id.equals(ids)) {
                                    matto = object.getString("android_id");
                                }
                            }
                            if(curr_id.equals(matto)){
                                startActivity(new Intent(Walkout_activity.this,Image_processing.class));
                            }
                            else {
                                startActivity(new Intent(Walkout_activity.this,Signin.class));
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

        RequestQueue requestQueue = Volley.newRequestQueue(Walkout_activity.this);
        requestQueue.add(stringRequest);
    }
}

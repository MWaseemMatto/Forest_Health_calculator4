package foerst.health.foresthealthcalculator4;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.Objects;

import static foerst.health.foresthealthcalculator4.Distance.id_img;

public class Resulted_image extends Fragment {
    private View view;
    public ImageView result_image;
    private TextView treeH,treeB,treeC,treeTC;
    public static String height,biomass,carbon,total_carbon ;
    private ProgressDialog progressDialog;
    private String url = "http://203.135.62.111:8080/tree-health/";
    // local ip 10.99.0.66
    // public ip 203.135.62.111

    public Resulted_image() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_resulted_image, container, false);

        progressDialog=new ProgressDialog(getActivity());
        treeH = view.findViewById(R.id.tree_height);
        treeB = view.findViewById(R.id.biomass);
        treeC = view.findViewById(R.id.carbon_content);
        treeTC =view.findViewById(R.id.total_carbon);
        result_image = view.findViewById(R.id.resulted_image_img);
        currentimageRequest();
        currentImageValues();

        return view;
    }
    private void currentimageRequest() {
        progressDialog.setTitle("Processing");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        StringRequest stringRequest =new StringRequest(Request.Method.GET,
                url+id_img+"/image-info/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {

                            JSONObject object=new JSONObject(response);
                            String image=object.getString("original_image");
                            Glide.with(getActivity()).load(image).into(result_image);

                        } catch (JSONException e) {
                            //progressDialog.dismiss();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(),"Error: while getting result\n"+error,Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()));
        requestQueue.add(stringRequest);
    }

    private void currentImageValues(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url+id_img+"/health-result/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object= new JSONObject(response);
                            height = String.valueOf(object.getDouble("height"));
                            treeH.setText(height);
                            biomass = String.valueOf(object.getDouble("biomass"));
                            treeB.setText(biomass);
                            carbon = String.valueOf(object.getDouble("carbon_content"));
                            treeC.setText(carbon);
                            total_carbon = String.valueOf(object.getDouble("total_carbon_absorbed"));
                            treeTC.setText(total_carbon);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),"Error: while fetching results values\n" +error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()));
        requestQueue.add(stringRequest);
    }

}

package foerst.health.foresthealthcalculator4;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Objects;


public class History extends Fragment {

    private View view;
    private RecyclerView recyclerView =null;
    private LinearLayoutManager linearLayoutManager=null;
    private List<Model_history> model_histories;
    private Adapter_Hist adapter=null;
    private String userId;
    private JSONArray array;
    private String id_image;
    private TextView total_results,userName;
    private TextView clear_data;
    private String url5 = "http://203.135.62.111:8080/tree-health/";
    // local ip 10.99.0.66
    // public ip 203.135.62.111
    public History() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_history, container, false);
        total_results = view.findViewById(R.id.result_count);

        userName = view.findViewById(R.id.username);
        model_histories=new ArrayList<>();
        userId = Settings.Secure.getString(getActivity().getContentResolver(),Settings.Secure.ANDROID_ID);
        clear_data = view.findViewById(R.id.clear);
        clear_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
                ab.setMessage("Clear history?").setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //code here for delete dat
                        clearData();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = ab.create();
                alertDialog.show();
            }
        });
        setRecyclerView();
        getUserName();
        allRequests();
        getAllResultCount();
        return view;
    }
    private void setRecyclerView(){
        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager=new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void getAllResultCount(){
        StringRequest stringRequest=new StringRequest(Request.Method.GET,
                url5+userId+"/images-count/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        total_results.setText(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),"Error! \n"+error,Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()));
        requestQueue.add(stringRequest);
    }


    private void getUserName(){
        StringRequest stringRequest=new StringRequest(Request.Method.GET,
                url5+"users/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array=new JSONArray(response);
                            for(int i=0;i<array.length();i++){
                                JSONObject object=array.getJSONObject(i);
                                String id=object.getString("android_id");
                                if (userId.equals(id)){
                                    String name=object.getString("name");
                                    userName.setText(name);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),"Error! \n"+error,Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()));
        requestQueue.add(stringRequest);
    }
    private void allRequests() {
        StringRequest stringRequest =new StringRequest(Request.Method.GET,
                url5+userId+"/history/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            array= new JSONArray(response);
                            for(int i=0;i<array.length();i++){
                                JSONObject object=array.getJSONObject(i);
                                id_image= String.valueOf(object.getInt("mobile_image_id"));
                                String image=object.getString("original_image");
                                String time = object.getString("date_time");
                                Model_history history = new Model_history(image,time,id_image);
                                model_histories.add(history);
                            }
                            adapter=new Adapter_Hist(getContext(), model_histories);
                            recyclerView.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),"Error: while fetching history\n"+error,
                        Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()));
        requestQueue.add(stringRequest);
    }

    private void clearData() {
        final String id_android_waseem = String.valueOf(Settings.Secure.getString(getActivity().getContentResolver(),Settings.Secure.ANDROID_ID));
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE,
                url5+id_android_waseem+"/delete-history/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        model_histories.clear();
                        adapter.notifyDataSetChanged();
                        recyclerView.removeAllViewsInLayout();
                        Toast.makeText(getActivity(),"Data Cleared!", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),"Error: While deleting history!! \n"+error, Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }
}

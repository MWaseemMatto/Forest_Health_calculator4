package foerst.health.foresthealthcalculator4;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static foerst.health.foresthealthcalculator4.R.drawable.forest;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnMarkerClickListener{

    private GoogleMap mMap =null;
    private JSONArray result = null;
    private String url = "http://203.135.62.111:8080/tree-health/";
    // local ip 10.99.0.66
    // public ip 203.135.62.111
/*    private ArrayList<LatLng> latilongi = new ArrayList<LatLng>();*/
    FusedLocationProviderClient client;
    Location mLocation=null;

  /*  LatLng fsd = new LatLng(31.2155, 72.9992);
    LatLng lhr = new LatLng(31.5204, 74.3587);
    LatLng smd = new LatLng(31.4504, 73.1350);
    LatLng okra = new LatLng(30.8138, 73.4534);
    LatLng jhg = new LatLng(31.2781, 72.3317);
    LatLng sgd = new LatLng(32.0740, 72.6861);*/

    String image_latitude,image_longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        client = LocationServices.getFusedLocationProviderClient(this);
        getLocation();
        getlatlong();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);

        LatLng l = new LatLng(mLocation.getLatitude(),mLocation.getLongitude());
        MarkerOptions markerOptions= new MarkerOptions().position(l).title("You are here!");
        mMap.animateCamera(CameraUpdateFactory.newLatLng(l));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(l,10000));
        mMap.addMarker(markerOptions);

        StringRequest stringRequest=new StringRequest(Request.Method.GET, url+"location/", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    result =new JSONArray(response);
                    for(int i=0;i<result.length();i++){
                        JSONObject object=result.getJSONObject(i);
                        //id_image= String.valueOf(object.getInt("mobile_image_id"));
                        String lat=object.getString("latitude");
                        String longi = object.getString("longitude");
                        Toast.makeText(getApplicationContext(),"latitude"+lat+"longitude"+longi,Toast.LENGTH_SHORT).show();



                        mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(lat),
                                Double.parseDouble(longi))).title(Double.parseDouble(lat)+","+Double.parseDouble(longi))).setIcon(bitmapDescriptorFromVector(getApplicationContext(),R.drawable.ic_local_florist_black_24dp));

                        mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));
                       // mMap.moveCamera(CameraUpdateFactory.newLatLng(latilongi.get(i)));
                    }
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        int socketTimeout = 10000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);


        /*latilongi.add(fsd);
        latilongi.add(lhr);
        latilongi.add(jhg);
        latilongi.add(sgd);
        latilongi.add(smd);
        latilongi.add(okra);

        for (int i= 0; i<latilongi.size(); i++){
            mMap.addMarker(new MarkerOptions().position(latilongi.get(i))
                    .title((Double.parseDouble(String.valueOf(latilongi.get(i).latitude)))+", "+Double.parseDouble(String.valueOf(latilongi.get(i).longitude))))
                    .setIcon(bitmapDescriptorFromVector(getApplicationContext(),R.drawable.ic_local_florist_black_24dp));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latilongi.get(i)));
        }*/


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

        //        Toast.makeText(getApplicationContext(),marker.getPosition().latitude+", "+marker.getPosition().longitude,Toast.LENGTH_SHORT).show();
                image_latitude =String.valueOf(marker.getPosition().latitude);
                image_longitude =String.valueOf(marker.getPosition().longitude);
                Intent intent = new Intent(MapActivity.this,Markar_Details.class);
                intent.putExtra("lati",image_latitude);
                intent.putExtra("longi",image_longitude);
                startActivity(intent);
                return false;
            }
        });
        /*
        StringRequest stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    result =new JSONArray(response);
                    for(int i=0;i<result.length();i++){
                        JSONObject object=result.getJSONObject(i);
                        //id_image= String.valueOf(object.getInt("mobile_image_id"));
                        String lat=object.getString("latitude");
                        String longi = object.getString("longitude");
                        Toast.makeText(getApplicationContext(),"latitude"+lat+"longitude"+longi,Toast.LENGTH_SHORT).show();

                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(Double.parseDouble(lat) , Double.parseDouble(longi)))
                                .title(Double.valueOf(lat).toString() + "," + Double.valueOf(longi).toString())
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
                        );
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(23.6850,90.3563), 6.0f));
                    }
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        int socketTimeout = 10000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);*/
    }

    private void getlatlong(){
        StringRequest stringRequest=new StringRequest(Request.Method.GET, url+"/location/", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    result =new JSONArray(response);
                    for(int i=0;i<result.length();i++){
                        JSONObject object=result.getJSONObject(i);
                        String id_image= String.valueOf(object.getInt("mobile_image_id"));
                        String lat=object.getString("latitude");
                        String longi = object.getString("longitude");
                        //Toast.makeText(getApplicationContext(),"latitude"+lat+"longitude"+longi+"wih image id"+id_image,Toast.LENGTH_SHORT).show();
                    }
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        int socketTimeout = 10000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResid){
        Drawable vectorDrawable = ContextCompat.getDrawable(context,vectorResid);
        vectorDrawable.setBounds(0,0,vectorDrawable.getIntrinsicWidth(),vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),vectorDrawable.getIntrinsicHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
       /* LatLng latLng = new LatLng(mLocation.getLatitude(),mLocation.getLongitude());
        final String latitude = String.valueOf(latLng.latitude);
        Toast.makeText(getApplicationContext(),""+latitude,Toast.LENGTH_SHORT).show();*/
        return false;
    }

    private void getLocation(){
       Task<Location> task = client.getLastLocation();
       task.addOnSuccessListener(new OnSuccessListener<Location>() {
           @Override
           public void onSuccess(Location location) {
               mLocation=location;
               SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                       .findFragmentById(R.id.map);
               mapFragment.getMapAsync(MapActivity.this);
           }
       });
    }
}

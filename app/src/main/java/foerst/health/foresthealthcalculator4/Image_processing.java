package foerst.health.foresthealthcalculator4;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class Image_processing extends AppCompatActivity implements View.OnClickListener {
    private final int PICK_IMAGE_CAMERA = 1, PICK_IMAGE_GALLERY = 2;
    ImageView imageView;
    private ProgressDialog progressDialog;
    private ImageButton galleryImage, takeImage;
    private Button send;
    private Toolbar toolbar;
    public String originalPath = "";
    private Uri selectedImageUri;
    private Uri cropimage;
    public static String imgID;
    private File fileimage;
    private Uri uri;
    boolean flags = false;
    private String url3 = "http://203.135.62.111:8080/tree-health/";
    // local ip 10.99.0.66
    // public ip 203.135.62.111
    private String currentId;
    private Context context;

    TextView addresses1;
    double latitude = 0.0, longitude = 0.0;
    private ResultReceiver resultReceiver;


    LocationManager locationManager =null;
    boolean gps_enabled = false;
    boolean network_enabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_processing);
        toolbar = findViewById(R.id.selecttoolbar);
        setTitle("Select Image");
        setSupportActionBar(toolbar);
        currentId=String.valueOf(Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID));
        progressDialog = new ProgressDialog(Image_processing.this);
        resultReceiver = new AddressResultReceiver(new Handler());
        takeImage =  findViewById(R.id.browse);
        takeImage.setOnClickListener(this);
        galleryImage = findViewById(R.id.gallery);
        galleryImage.setOnClickListener(this);
        send = findViewById(R.id.nextbutton);
        send.setOnClickListener(this);
        send.setVisibility(View.INVISIBLE);
        imageView=findViewById(R.id.capture_image);
        getlocation();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.browse:
                //locationEnabled();
                captureImage();
                break;
            case R.id.gallery:
                //locationEnabled();
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, PICK_IMAGE_GALLERY);
                break;
            case R.id.nextbutton:
                if(flags==false){
                    uploadImageInfo();
                    flags=true;
                }
                else {
                    UpdatedData();
                    flags=false;
                }
                break;
            default:
                break;
        }
    }

   private void locationEnabled(){

       locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

       try {
           gps_enabled = Objects.requireNonNull(locationManager).isProviderEnabled(LocationManager.GPS_PROVIDER);
       } catch(Exception ex) {}

       try {
           network_enabled = Objects.requireNonNull(locationManager).isProviderEnabled(LocationManager.NETWORK_PROVIDER);
       } catch(Exception ex) {}

       if(!gps_enabled && !network_enabled) {
           // notify user
           AlertDialog.Builder builder = new AlertDialog.Builder(Image_processing.this);
           builder.setTitle("");
           builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
                   startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
               }
           }).setNegativeButton("Cancel",null).show();
       }
    }

    private void getlocation() {
        LocationRequest request= new LocationRequest();
        request.setInterval(10000);
        request.setFastestInterval(30000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.getFusedLocationProviderClient(Image_processing.this)
                .requestLocationUpdates(request, new LocationCallback(){
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);

                        LocationServices.getFusedLocationProviderClient(Image_processing.this)
                                .removeLocationUpdates(this);
                        if(locationResult != null && locationResult.getLocations().size() > 0){
                            int latestlocation =  locationResult.getLocations().size()-1;
                            latitude = locationResult.getLocations().get(latestlocation).getLatitude();
                            longitude = locationResult.getLocations().get(latestlocation).getLongitude();
                           // Toast.makeText(getApplicationContext(),latitude+"\n"+longitude,Toast.LENGTH_SHORT).show();
                            Location location =new Location("providerNA");
                            location.setLatitude(latitude);
                            location.setLongitude(longitude);
                            getAddress(location);
                        }
                    }
                }, Looper.getMainLooper());
    }

    private void getAddress(Location location){
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Contents.RECEIVER, resultReceiver);
        intent.putExtra(Contents.LOCATION_DATA_EXTRA,location);
        startService(intent);
    }

    private class AddressResultReceiver extends ResultReceiver {
        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if(resultCode ==Contents.RESULT_SUCCESS){
                //addresses1.setText(resultData.getString(Contents.RESULT_DATA_KEY));
                //Toast.makeText(Image_processing.this, "address \n"+resultData.getString(Contents.RESULT_DATA_KEY), Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(Image_processing.this,
                        resultData.getString(Contents.RESULT_DATA_KEY),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    File getPictureFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String pictureName = "IMAGE_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(pictureName,  ".jpg", storageDir);
        originalPath = image.getAbsolutePath();
        //Toast.makeText(getApplicationContext(),""+originalPath,Toast.LENGTH_LONG).show();
        return image;
    }


    private void captureImage() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File pictureFile = null;
            try {
                pictureFile = getPictureFile();
            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(),
                        "Photo file can't be created, please try again",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if (pictureFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getApplicationContext(),
                        "foerst.health.foresthealthcalculator4",
                        pictureFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, PICK_IMAGE_CAMERA);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri,proj,null,null,null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_GALLERY && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            originalPath = getRealPathFromURI(getApplicationContext(), selectedImageUri);
            //Toast.makeText(getApplicationContext(), "original image path \n" + originalPath, Toast.LENGTH_SHORT).show();
            CropImage.activity(selectedImageUri).start(Image_processing.this);
        }
        if (requestCode == PICK_IMAGE_CAMERA && resultCode == RESULT_OK) {
            File imgFile = new File(originalPath);
            if (imgFile.exists()) {
                imageView.setImageURI(Uri.fromFile(imgFile));
            }
            uri = Uri.fromFile(imgFile);
            CropImage.activity(uri).start(Image_processing.this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            send.setVisibility(View.VISIBLE);
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                cropimage = result.getUri();
                fileimage = new File(cropimage.getPath());
                // Toast.makeText(getApplicationContext(), "cropped image \n" + fileimage, Toast.LENGTH_LONG).show();
                imageView.setImageURI(cropimage);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(getApplicationContext(), "Error \n" + error, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void uploadImageInfo(){
        String lat=String.valueOf(latitude);
        String logi=String.valueOf(longitude);
        final String user_id = String.valueOf(Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID));
        //Intent intent = getIntent();
        //final String str = intent.getStringExtra("wth");
        //get the cropped image path from gallery or camera
        final String path = fileimage.toString();
        File imageFile = new File(path);
        // retrofit call
        Retrofit retrofit = new Retrofit.Builder().baseUrl(url3)
                .addConverterFactory(GsonConverterFactory.create()).build();
        // model class API
        Image1API postApi= retrofit.create(Image1API.class);
        // cropped image
        RequestBody requestBody_crop = RequestBody.create(MediaType.parse("multipart/data"), imageFile);
        // body request for crop image
        MultipartBody.Part image_crop = MultipartBody.Part.createFormData("far_image", imageFile.getName(), requestBody_crop);
        // refernce object
        //RequestBody width = RequestBody.create(MediaType.parse("multipart/form-data"), str);
        // current time and date
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String datetime = df.format(c.getTime());
        RequestBody time = RequestBody.create(MediaType.parse("multipart/form-data"), datetime);
        // latitude  and longitude of the image where processed
        RequestBody lati = RequestBody.create(MediaType.parse("multipart/form-data"), lat);

        //RequestBody dist = RequestBody.create(MediaType.parse("multipart/form-data"), str);
        RequestBody  log= RequestBody.create(MediaType.parse("multipart/form-data"), logi);
        RequestBody  ids= RequestBody.create(MediaType.parse("multipart/form-data"), user_id);

        Call<Model_id> call1 = postApi.uploadata(image_crop,log,lati,ids,time);
        progressDialog.setTitle("Processing");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        call1.enqueue(new Callback<Model_id>() {
            @Override
            public void onResponse(Call<Model_id> call, Response<Model_id> response) {
                progressDialog.dismiss();
                if(response.isSuccessful()){
                    imgID = String.valueOf(response.body().getId());
                    updateOriginalImage();
                }
            }
            @Override
            public void onFailure(Call<Model_id> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Error: image info not send \n"+"\n"+t, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // upload the original image
    private void updateOriginalImage(){
        //get the image path from gallery or camera
        final String path = originalPath;
        File imageFile = new File(path);
        // retrofit call
        Retrofit retrofit = new Retrofit.Builder().baseUrl(url3+imgID+"/")
                .addConverterFactory(GsonConverterFactory.create()).build();
        // model class API
        OriginalImageAPI postApi = retrofit.create(OriginalImageAPI.class);
        // image
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/data"), imageFile);
        // body request
        MultipartBody.Part multiPartBody = MultipartBody.Part.createFormData("original_image", imageFile.getName(), requestBody);
        Call<ResponseBody> call = postApi.original(multiPartBody);
        progressDialog.setTitle("Processing");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //PickImage();
                progressDialog.dismiss();
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Error: image not sent \n"+"\n"+t, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void UpdatedData(){
        //get the image path from gallery or camera
        final String path = fileimage.toString();
        File imageFile = new File(path);
        // retrofit call
        Retrofit retrofit = new Retrofit.Builder().baseUrl(url3+imgID+"/")
                .addConverterFactory(GsonConverterFactory.create()).build();
        // model class API
        image2API postApi = retrofit.create(image2API.class);
        // image
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/data"), imageFile);
        // body request
        MultipartBody.Part multiPartBody = MultipartBody.Part.createFormData("close_image", imageFile.getName(), requestBody);
        Call<ResponseBody> call = postApi.updatedata(multiPartBody);
        progressDialog.setTitle("Processing");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();
                startActivity(new Intent(Image_processing.this, Distance.class));
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Error: image not sent \n"+"\n"+t, Toast.LENGTH_SHORT).show();
            }
        });
    }

}

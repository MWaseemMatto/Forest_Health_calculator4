package foerst.health.foresthealthcalculator4;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Profile_Edit extends AppCompatActivity {
    private static final int PICK_IMAGE_GALLERY = 1;
    private static final int PICK_IMAGE_CAMERA =2 ;
    private ImageView profileimage=null;
    Uri selectedImageUri=null;
    public String imgPath = "";
    private Toolbar toolbar=null;
    private ProgressDialog progressDialog = null;
    private EditText pn,pf,pinst;
    ImageView imageView=null;
    private Button profile_save=null;
    private String currentId="";
    public String originalPath = "";
    private File file_image = null;
    Uri cropimage=null;

    private String url = "http://203.135.62.111:8080/tree-health/";
    // local ip 10.99.0.66
    // public ip 203.135.62.111

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile__edit);
        toolbar=findViewById(R.id.settingprofile_toolbar);
        setTitle("Edit Profile");
        setSupportActionBar(toolbar);
        progressDialog=new ProgressDialog(this);
        currentId=String.valueOf(Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID));
        pn = findViewById(R.id.profile_name);
        pf = findViewById(R.id.profile_field);
        pinst = findViewById(R.id.profile_inst);

        // profileimage=findViewById(R.id.profile_photo);
        /*profileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickImage();
            }
        });*/
        profile_save =findViewById(R.id.profile_savebtn);
        getUserInfo();
        profile_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });
    }

    private void getUserInfo(){
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
                                    String name=object.getString("name");
                                    pn.setText(name);
                                    String foi=object.getString("field_of_interest");
                                    pf.setText(foi);
                                    String org=object.getString("organization");
                                    pinst.setText(org);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error! fetching user info\n"+error,Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void updateProfile(){
        final String name = pn.getText().toString();
        final String filed = pf.getText().toString();
        final String orga = pinst.getText().toString();
        // final String photo = file_image.toString();
        final String android_id= String.valueOf(Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID));

        progressDialog.setTitle("Processing");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        StringRequest stringRequest=new StringRequest(Request.Method.PUT,
                url+android_id+"/update-user/",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
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
                params.put("name", name);
                params.put("field_of_interest", filed);
                params.put("organization", orga);
                // params.put("profile", photo);
                return  params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }

   /* private void PickImage(){
        final CharSequence[] options = {"Take image", "From Gallery","Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileSetting.this);
        builder.setCancelable(true);
        builder.setTitle("Select Option");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take image")) {
                    captureImage();
                    dialog.dismiss();
                } else if (options[item].equals("From Gallery")) {
                    //Toast.makeText(getApplicationContext(),"from galley",Toast.LENGTH_SHORT).show();
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, PICK_IMAGE_GALLERY);
                    dialog.dismiss();
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_GALLERY && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            originalPath = getRealPathFromURI(getApplicationContext(), selectedImageUri);
            //Toast.makeText(getApplicationContext(), "original image path \n" + originalPath, Toast.LENGTH_SHORT).show();
            CropImage.activity(selectedImageUri).start(ProfileSetting.this);
        }
        if (requestCode == PICK_IMAGE_CAMERA && resultCode == RESULT_OK) {
            File imgFile = new File(originalPath);
            *//*if (imgFile.exists()) {
                imageView.setImageURI(Uri.fromFile(imgFile));
            }*//*
            Uri uri = Uri.fromFile(imgFile);
            CropImage.activity(uri).start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                cropimage = result.getUri();
                file_image = new File(cropimage.getPath());
                // Toast.makeText(getApplicationContext(), "cropped image \n" + fileimage, Toast.LENGTH_LONG).show();
                imageView.setImageURI(cropimage);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(getApplicationContext(), "Error \n" + error, Toast.LENGTH_LONG).show();
            }
        }
    }

      public String getRealPathFromURI(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri,proj,null,null,null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

     File getPictureFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String pictureName = "IMAGE_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(pictureName,  ".jpg", storageDir);
        imgPath = image.getAbsolutePath();
        //Toast.makeText(getApplicationContext(),""+imgPath,Toast.LENGTH_SHORT).show();
        return image;
    }

       private void captureImage() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File pictureFile = null;
            try {
                pictureFile = getPictureFile();
            } catch (Exception ex) {
                Toast.makeText(this,
                        "Photo file can't be created, please try again",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if (pictureFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                       "com.example.muhammadwaseem.forestryhealthassessment",
                       pictureFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, PICK_IMAGE_CAMERA);
            }
        }
    }*/
}

package foerst.health.foresthealthcalculator4;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface Image1API {
    @Multipart
    @POST("create-new-image/")
    Call<Model_id> uploadata(@Part MultipartBody.Part file,
                             @Part("longitude") RequestBody log,
                             @Part("latitude") RequestBody lati,
                             @Part("android_id") RequestBody iduser,
                             @Part("date_time") RequestBody time);
}

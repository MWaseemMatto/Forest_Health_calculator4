package foerst.health.foresthealthcalculator4;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.PUT;
import retrofit2.http.Part;

public interface OriginalImageAPI {
    @Multipart
    @PUT("update-image/")
    Call<ResponseBody> original(@Part MultipartBody.Part file);
}

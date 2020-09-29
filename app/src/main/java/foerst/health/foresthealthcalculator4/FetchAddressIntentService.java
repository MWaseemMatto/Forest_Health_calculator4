package foerst.health.foresthealthcalculator4;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.Nullable;

public class FetchAddressIntentService extends IntentService {
    private ResultReceiver resultReceiver;
    public FetchAddressIntentService() {
        super("FetchAddressIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent != null){
            String errorMessage = "";
            resultReceiver = intent.getParcelableExtra(Contents.RECEIVER);
            Location location = intent.getParcelableExtra(Contents.LOCATION_DATA_EXTRA);
            if(location == null)
            {
                return;
            }
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            } catch (IOException e) {
                e.printStackTrace();
                errorMessage = e.getMessage();
            }
            if(addresses == null || addresses.isEmpty()){
                deliverResultToReceiver(Contents.RESULT_FAILURE,errorMessage);
            }else {
                Address address = addresses.get(0);
                ArrayList<String> addressfragment = new ArrayList<>();
                for (int i=0;i<=address.getMaxAddressLineIndex();i++){
                    addressfragment.add(address.getAddressLine(i));
                }
                deliverResultToReceiver(Contents.RESULT_SUCCESS,
                        TextUtils.join(Objects.requireNonNull(System.getProperty("line.separator")),addressfragment));
            }
        }
    }

    private void deliverResultToReceiver(int resultCode, String addressMessage){
        Bundle bundle =new Bundle();
        bundle.putString(Contents.RESULT_DATA_KEY,addressMessage);
        resultReceiver.send(resultCode,bundle);

    }
}

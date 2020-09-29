package foerst.health.foresthealthcalculator4;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

public class Markar_Details extends AppCompatActivity {
    private TextView latitude, longitude;
    private String lati, longi;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_markar__details);
        toolbar=findViewById(R.id.markar_toolbar);
        setTitle("Marker details");
        setSupportActionBar(toolbar);
        latitude = findViewById(R.id.lati);
        longitude = findViewById(R.id.longi);
        lati = getIntent().getStringExtra("lati");
        longi = getIntent().getStringExtra("longi");
        setvalues();
    }
    @SuppressLint("SetTextI18n")
    private void setvalues(){
        latitude.setText(lati+getString(R.string.N_degree)+" N");
        longitude.setText(longi+getString(R.string.E_degree)+" E");
    }
}

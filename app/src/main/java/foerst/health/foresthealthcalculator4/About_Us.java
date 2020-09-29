package foerst.health.foresthealthcalculator4;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

public class About_Us extends AppCompatActivity {
    private Toolbar toolbar=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about__us);

        toolbar=(Toolbar) findViewById(R.id.about_us_toolbar);
        setTitle("About Us");
        setSupportActionBar(toolbar);
    }
}

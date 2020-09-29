package foerst.health.foresthealthcalculator4;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class settings_app extends AppCompatActivity implements View.OnClickListener{
    private LinearLayout profile_linearLayout,feedback_linearLayout,mobileinfo_linearLayout;
    Toolbar toolbar1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_app);
        toolbar1=findViewById(R.id.settingtoolbar);
        setTitle("Settings");
        setSupportActionBar(toolbar1);

        profile_linearLayout = findViewById(R.id.profile_layout);
        profile_linearLayout.setOnClickListener(this);
        feedback_linearLayout = findViewById(R.id.feedback_layout);
        feedback_linearLayout.setOnClickListener(this);
        mobileinfo_linearLayout = findViewById(R.id.mobile_info_layout);
        mobileinfo_linearLayout.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.feedback_layout:
                startActivity(new Intent(settings_app.this, Contact_Us.class));
                break;
            case R.id.profile_layout:
                startActivity(new Intent(settings_app.this, Profile_Edit.class));
                break;
            case R.id.mobile_info_layout:
                startActivity(new Intent(settings_app.this, Mobile_Info_settings.class));
                break;
            default:
                break;
        }
    }
}

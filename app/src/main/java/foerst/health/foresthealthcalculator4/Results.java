package foerst.health.foresthealthcalculator4;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

public class Results extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPaperAdapter viewPaperAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        tabLayout= findViewById(R.id.tabview);
        viewPager = findViewById(R.id.viewpaper);
        viewPaperAdapter=new ViewPaperAdapter(getSupportFragmentManager());
        //add fragment here
        viewPaperAdapter.addfragment(new Resulted_image(),"Results");
        viewPaperAdapter.addfragment(new History(),"View History");
        viewPager.setAdapter(viewPaperAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}

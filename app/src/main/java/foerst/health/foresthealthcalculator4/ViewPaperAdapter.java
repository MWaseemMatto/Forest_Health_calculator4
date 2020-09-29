package foerst.health.foresthealthcalculator4;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPaperAdapter extends FragmentPagerAdapter {
    private final List<Fragment> frglist=new ArrayList<>();
    private  final List<String> listofeventstitles =new ArrayList<>();

    public ViewPaperAdapter(FragmentManager fm) {
        super(fm);
    }


    @Override
    public Fragment getItem(int position) {
        return frglist.get(position);
    }

    @Override
    public int getCount() {
        return listofeventstitles.size();
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return listofeventstitles.get(position);
    }



    public  void addfragment(Fragment fragment,String title){
        frglist.add(fragment);
        listofeventstitles.add(title);

    }
}

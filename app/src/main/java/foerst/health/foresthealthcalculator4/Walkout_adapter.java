package foerst.health.foresthealthcalculator4;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class Walkout_adapter extends PagerAdapter {

    Context context;
    List<Walkout_model> list;

    public Walkout_adapter(Context context,List<Walkout_model> list){
        this.context=context;
        this.list=list;

    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.walkout_screen_layout,null);

        ImageView icon = view.findViewById(R.id.walkout_icon);
        TextView service = view.findViewById(R.id.walkout_service);
        TextView description= view.findViewById(R.id.walkout_details);

        service.setText (list.get(position).getService());
        description.setText(list.get(position).getDetails());
        icon.setImageResource(list.get(position).getIcon());
        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

}

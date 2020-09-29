package foerst.health.foresthealthcalculator4;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

public class Adapter_Hist extends RecyclerView.Adapter <Adapter_Hist.ViewHolder> {
    private Context context;
    private List<Model_history> list;
    Intent intent;
    private String id;
    private String url4 = "http://203.135.62.111:8080/tree-health/";
    // local ip 10.99.0.66
    // public ip 203.135.62.111
    //private static ItemClickListener itemClickListener;

    public Adapter_Hist(Context context, List<Model_history> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater= LayoutInflater.from(context);
        View itemview= layoutInflater.inflate(R.layout.historycard,null);
        ViewHolder holder =new ViewHolder(itemview);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        final Model_history lst= list.get(position);
        Glide.with(context).load(lst.getImg()).into(viewHolder.imageresult);
        viewHolder.tim.setText(lst.getTime());
        //viewHolder.idimage.setText(lst.getImageid());
        viewHolder.imageresult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Model_history hist= list.get(position);
                id=hist.getImageid();
                intent=new Intent(context,Photo_Details.class);
                intent.putExtra("id",id);
                context.startActivity(intent);
            }
        });


        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Model_history hist= list.get(position);
                id=hist.getImageid();
                final AlertDialog.Builder ab = new AlertDialog.Builder(context);
                ab.setMessage("Confirm to delete?").setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //code here for delete data
                        StringRequest stringRequest = new StringRequest(Request.Method.DELETE,
                                url4+id+"/delete-image/",
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        list.remove(position);
                                        notifyDataSetChanged();
                                        // progressDialog.dismiss();
                                        Toast.makeText(context,"Image deleted!", Toast.LENGTH_SHORT).show();
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(context,"Error: While deleting image!! \n"+error, Toast.LENGTH_SHORT).show();
                            }
                        });
                        RequestQueue requestQueue = Volley.newRequestQueue(context);
                        requestQueue.add(stringRequest);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = ab.create();
                alertDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class  ViewHolder extends RecyclerView.ViewHolder  {
        ImageView imageresult;
        TextView tim;
        ImageButton delete,image_location;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageresult = (ImageView) itemView.findViewById(R.id.imageview);
            tim = itemView.findViewById(R.id.timeview);

            delete=itemView.findViewById(R.id.image_delete);
            image_location=itemView.findViewById(R.id.location_image);
        }
    }
}

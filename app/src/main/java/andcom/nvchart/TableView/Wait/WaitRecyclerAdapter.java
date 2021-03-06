package andcom.nvchart.TableView.Wait;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Pattern;

import andcom.nvchart.MainActivity;
import andcom.nvchart.R;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by csy on 2018-03-23.
 */

public class WaitRecyclerAdapter extends RecyclerView.Adapter<WaitRecyclerAdapter.ItemViewHolder> {

    JSONArray jarrWaitList;

    public WaitRecyclerAdapter(String jsonArray){
        try{
            JSONObject json = new JSONObject(jsonArray);
            jarrWaitList = json.getJSONArray("List");
        }catch (JSONException je){
            je.printStackTrace();
        }

    }
    @Override
    public WaitRecyclerAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wait_item_new,parent,false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WaitRecyclerAdapter.ItemViewHolder holder, int position) {
        try{
            JSONObject jsonItem =  jarrWaitList.getJSONObject(position);
            holder.txtSeq.setText(jsonItem.getString("CSSEQ"));
            holder.txtName.setText(jsonItem.getString("CSNAME"));
            holder.txtAge.setText(jsonItem.getString("CSAGE"));
            holder.txtCharNo.setText(jsonItem.getString("CSCHARTNO"));
            holder.txtState.setText(jsonItem.getString("CSSATE"));
            switch(holder.txtState.getText().toString()){
                case "접수" :
                    holder.txtState.setTextColor(MainActivity.context.getColor(R.color.cs_state_0));
                    holder.stateBar.setBackgroundColor(MainActivity.context.getColor(R.color.cs_state_0));
                    break;
                case "진료중" :
                    holder.txtState.setTextColor(MainActivity.context.getColor(R.color.cs_state_1));
                    holder.stateBar.setBackgroundColor(MainActivity.context.getColor(R.color.cs_state_1));

                    break;
                case  "진료완료" :
                    holder.txtState.setTextColor(MainActivity.context.getColor(R.color.cs_state_2));
                    holder.stateBar.setBackgroundColor(MainActivity.context.getColor(R.color.cs_state_2));

                    break;
            }
            holder.txtTime.setText(jsonItem.getString("CSTIME"));
            String desc = jsonItem.getString("CSDESC");
            if(desc.equals("")){
                desc = "-";
            }
            String catime = jsonItem.getString("CATIME");
            if(catime.equals("")){
                catime = "-";
            }
            holder.txtDesc.setText(desc);
            holder.txtDoctor.setText(jsonItem.getString("CSDOCTOR"));
            holder.txtJsonBag.setText(jsonItem.toString());

            int imageID = R.drawable.avatar;
            String[] cAge = jsonItem.getString("CSAGE").split("/");
            int age = 0;
            boolean isMan = true;
            if(cAge.length>0){
                age = Integer.parseInt(cAge[0]);
                isMan = cAge[1].equals("남") ;
            }

            if(age<14){
                if(isMan){
                    imageID = R.drawable.avatar_under_10_male;
                }else{
                    imageID = R.drawable.avatar_under_10_female;
                }
            }else if(age<20){
                if(isMan){
                    imageID = R.drawable.avatar_over_10_male;
                }else{
                    imageID = R.drawable.avatar_over_10_female;
                }
            }else if(age<30){
                if(isMan){
                    imageID = R.drawable.avatar_over_30_male;
                }else{
                    imageID = R.drawable.avatar_over_30_female;
                }
            }else if(age<45){
                if(isMan){
                    imageID = R.drawable.avatar_over_40_male;
                }else{
                    imageID = R.drawable.avatar_over_40_female;
                }
            }else if(age<65){
                if(isMan){
                    imageID = R.drawable.avatar_over_50_male;
                }else{
                    imageID = R.drawable.avatar_over_50_female;
                }

            }else if(age>=65){
                if(isMan){
                    imageID = R.drawable.avatar_over_60_male;
                }else{
                    imageID = R.drawable.avatar_over_60_female;
                }

            }
            if(age==0){
                imageID = R.drawable.avatar;
            }

            RequestOptions options = new RequestOptions();
            options.diskCacheStrategy(DiskCacheStrategy.NONE);
            options.skipMemoryCache(true);
            options.fitCenter();

            Glide.with(MainActivity.context)
                    .load(imageID)
                    .apply(options)
                    .into(holder.ivAvatar);

        }catch (JSONException je){
            je.printStackTrace();
        }

    }


    @Override
    public int getItemViewType(int position) {
        //return super.getItemViewType(position);
        return position;
    }
    @Override
    public int getItemCount() {
        return jarrWaitList.length();
    }

    public String getChartNo(int position){
        String chartno ;
        try{
            chartno = jarrWaitList.getJSONObject(position).getString("CSCHARTNO");
        }catch (JSONException je){
            je.printStackTrace();
            chartno = null;
        }

        return chartno;
    }
    class ItemViewHolder extends RecyclerView.ViewHolder{
        private CardView cardView;
        private TextView txtSeq;
        private TextView txtCharNo;
        private TextView txtName;
        private TextView txtState;
        private TextView txtTime;
        private TextView txtDesc;
        private TextView txtDoctor;
        private TextView txtJsonBag;
        private TextView txtAge;
        private TextView txtBirth;
        private ImageView ivAvatar;
        private View stateBar;
        public ItemViewHolder(View itemView){
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.cardView);
            txtSeq = (TextView)itemView.findViewById(R.id.seq);
            txtCharNo = (TextView)itemView.findViewById(R.id.chartno);
            txtName = (TextView)itemView.findViewById(R.id.name);
            txtAge = (TextView)itemView.findViewById(R.id.age);
            txtState = (TextView)itemView.findViewById(R.id.state);
            txtTime = (TextView)itemView.findViewById(R.id.time);
            txtDesc = (TextView)itemView.findViewById(R.id.desc);
            txtDoctor = (TextView)itemView.findViewById(R.id.doctor);
            txtJsonBag = (TextView)itemView.findViewById(R.id.jsonBag);
            stateBar = itemView.findViewById(R.id.stateBar);
            //txtAge = (TextView)itemView.findViewById(R.id.txtAgeSex);
            //txtBirth = (TextView)itemView.findViewById(R.id.txtBirth);
            ivAvatar = (ImageView)itemView.findViewById(R.id.icon);
        }
    }
}

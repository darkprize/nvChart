package andcom.nvchart.TableView.Order;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import andcom.nvchart.MainActivity;
import andcom.nvchart.R;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by csy on 2018-03-23.
 */

public class OrderRecyclerAdapter extends RecyclerView.Adapter<OrderRecyclerAdapter.ItemViewHolder> {


    JSONArray jarrOrderList;

    public OrderRecyclerAdapter(JSONArray jsonArray){
        jarrOrderList = jsonArray;

    }
    @Override
    public OrderRecyclerAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item_call_new,parent,false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderRecyclerAdapter.ItemViewHolder holder, int position) {
        try{
            JSONObject jsonItem =  jarrOrderList.getJSONObject(position);
            try{ //예약
                holder.txtName.setText(jsonItem.getString("CANAME"));
                holder.txtCharNo.setText(jsonItem.getString("CACHARTNO"));
                holder.txtTime.setText(jsonItem.getString("CATIME"));
                holder.txtDesc.setText(jsonItem.getString("CASIGN"));

                if(!jsonItem.getString("CAVIP").equals("0")){
                    String backgroundColor = jsonItem.getString("CAVIP_FONTFORECOLOR");
                    backgroundColor = "#FF00FF";
                    int backColor = Integer.parseInt(jsonItem.getString("CAVIP_FONTFORECOLOR"));

                    holder.cardView.setCardBackgroundColor(Color.parseColor(toHex(backColor)));
                }
                holder.txtJsonBag.setText(jsonItem.toString());
            }catch (JSONException je){ //접수
                je.printStackTrace();


            }

        }catch (JSONException je){
            je.printStackTrace();
        }

    }

    public static String toHex(int decimal){
        int rem;
        String hex="";
        char hexchars[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        while(decimal>0)
        {
            rem=decimal%16;
            hex=hexchars[rem]+hex;
            decimal=decimal/16;
        }
        while(hex.length()<6){
            hex="0"+hex;
        }
        Log.w("toHex","#"+hex);
        return "#"+hex;
    }
    @Override
    public int getItemViewType(int position) {
        //return super.getItemViewType(position);
        return position;
    }

    public String getChartNo(int position){
        String chartno ;
        try{
            chartno = jarrOrderList.getJSONObject(position).getString("CACHARTNO");
        }catch (JSONException je){
            je.printStackTrace();
            chartno = null;
        }

        return chartno;
    }
    @Override
    public int getItemCount() {
        return jarrOrderList.length();
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
        public ItemViewHolder(View itemView){
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.cardView);
            txtCharNo = (TextView)itemView.findViewById(R.id.chartno);
            txtName = (TextView)itemView.findViewById(R.id.name);
            txtTime = (TextView)itemView.findViewById(R.id.time);
            txtDesc = (TextView)itemView.findViewById(R.id.desc);
            txtJsonBag = (TextView)itemView.findViewById(R.id.jsonBag);
            //txtAge = (TextView)itemView.findViewById(R.id.txtAgeSex);
            //txtBirth = (TextView)itemView.findViewById(R.id.txtBirth);
        }
    }
}

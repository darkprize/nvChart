package andcom.nvchart.TableView.Order;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import andcom.nvchart.R;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;

/**
 * Created by csy on 2018-03-23.
 */

public class OrderCashRecyclerAdapter extends RecyclerView.Adapter<OrderCashRecyclerAdapter.ItemViewHolder> {


    JSONArray jarrCashList;

    public OrderCashRecyclerAdapter(JSONArray jsonArray,int gubun){
        jarrCashList = new JSONArray();
            switch(gubun){
                //CSCASHCALLCASH 0:접수, 1:예약 and 접수
                //CSSTATE 1:진료중,2:진료완료,3:수납완료
                //CSCASH 0 ?
                //-보류
                case 0 : //접수
                    for(int i=0;i<jsonArray.length();i++){
                        try {
                            if(jsonArray.getJSONObject(i).getInt("CSSTATE")==0){
                                jarrCashList.put(jsonArray.getJSONObject(i));

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case 1 : //진료중
                    for(int i=0;i<jsonArray.length();i++){
                        try {
                            if(jsonArray.getJSONObject(i).getInt("CSSTATE")==1){
                                jarrCashList.put(jsonArray.getJSONObject(i));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case 2 : //진료완료
                    for(int i=0;i<jsonArray.length();i++){
                        try {
                            if(jsonArray.getJSONObject(i).getInt("CSSTATE")==2){
                                jarrCashList.put(jsonArray.getJSONObject(i));

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case 3 : //수납완료
                    for(int i=0;i<jsonArray.length();i++){
                        try {
                            if(jsonArray.getJSONObject(i).getInt("CSSTATE")==3){
                                jarrCashList.put(jsonArray.getJSONObject(i));

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }


    }
    @Override
    public OrderCashRecyclerAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item_call,parent,false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderCashRecyclerAdapter.ItemViewHolder holder, int position) {
        try{
            JSONObject jsonItem =  jarrCashList.getJSONObject(position);
            try{ //예약

                holder.txtDesc.setVisibility(View.GONE);

                holder.txtName.setText(jsonItem.getString("CSNAME"));
                holder.txtCharNo.setText(jsonItem.getString("CSCHARTNO"));
                holder.txtTime.setText(jsonItem.getString("CSTIME"));
                holder.txtJsonBag.setText(jsonItem.toString());

                if(!jsonItem.getString("CSCASH").equals("0")){
                    holder.finishCash.setVisibility(View.VISIBLE);
                    DecimalFormat df = new DecimalFormat("#,###");

                    holder.txtCash.setText(df.format(jsonItem.getInt("CSCASH"))+ " 원");

                }
            }catch (JSONException je){ //접수
                je.printStackTrace();


            }

        }catch (JSONException je){
            je.printStackTrace();
        }

    }

    public String getChartNo(int position){
        String chartno ;
        try{
            chartno = jarrCashList.getJSONObject(position).getString("CSCHARTNO");
        }catch (JSONException je){
            je.printStackTrace();
            chartno = null;
        }

        return chartno;
    }

    @Override
    public int getItemViewType(int position) {
        //return super.getItemViewType(position);
        return position;
    }
    @Override
    public int getItemCount() {
        return jarrCashList.length();
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
        private LinearLayout finishCash;
        private TextView txtCash;
        public ItemViewHolder(View itemView){
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.cardView);
            txtCharNo = (TextView)itemView.findViewById(R.id.chartno);
            txtName = (TextView)itemView.findViewById(R.id.name);
            txtTime = (TextView)itemView.findViewById(R.id.time);
            txtDesc = (TextView)itemView.findViewById(R.id.desc);
            txtJsonBag = (TextView)itemView.findViewById(R.id.jsonBag);

            finishCash = itemView.findViewById(R.id.finishCash);
            txtCash = itemView.findViewById(R.id.cash);
            //txtAge = (TextView)itemView.findViewById(R.id.txtAgeSex);
            //txtBirth = (TextView)itemView.findViewById(R.id.txtBirth);
        }
    }
}

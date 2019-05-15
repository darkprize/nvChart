package andcom.nvchart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by csy on 2018-03-23.
 */

public class CustListRecyclerAdapter extends RecyclerView.Adapter<CustListRecyclerAdapter.ItemViewHolder> {

    JSONArray jarrNvList;

    public CustListRecyclerAdapter(String jsonArray){
        try{
            JSONObject json = new JSONObject(jsonArray);
            jarrNvList = json.getJSONArray("List");
        }catch (JSONException je){
            je.printStackTrace();
        }

    }
    @Override
    public CustListRecyclerAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cust_item,parent,false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustListRecyclerAdapter.ItemViewHolder holder, int position) {
        try{
            JSONObject jsonItem =  jarrNvList.getJSONObject(position);
            holder.txtName.setText(jsonItem.getString("CTNAME"));
            holder.txtChartNo.setText(jsonItem.getString("CTCHARTNO"));
            holder.txtJumin.setText(jsonItem.getString("CTJUMINNO"));
            holder.txtPhone.setText(jsonItem.getString("CTTEL"));
            holder.txtJsonBag.setText(jsonItem.toString());
        }catch (JSONException je){
            je.printStackTrace();
        }

    }
    public String getChartNo(int position){
        try{
            String chartno = jarrNvList.getJSONObject(position).getString("CTCHARTNO");
            return chartno;
        }catch(JSONException je){
            je.printStackTrace();
            return null;
        }
    }

    @Override
    public int getItemViewType(int position) {
        //return super.getItemViewType(position);
        return position;
    }
    @Override
    public int getItemCount() {
        return jarrNvList.length();
    }
    class ItemViewHolder extends RecyclerView.ViewHolder{
        private CardView cardView;
        private TextView txtName;
        private TextView txtChartNo;
        private TextView txtJumin;
        private TextView txtPhone;
        private TextView txtJsonBag;
        public ItemViewHolder(View itemView){
            super(itemView);
            //cardView = (CardView)itemView.findViewById(R.id.cardView);
            txtName = (TextView)itemView.findViewById(R.id.name);
            txtChartNo = (TextView)itemView.findViewById(R.id.chartno);
            txtJumin = (TextView)itemView.findViewById(R.id.jumin);
            txtPhone = (TextView)itemView.findViewById(R.id.phone);
            txtJsonBag = (TextView)itemView.findViewById(R.id.jsonBag);
        }
    }
}

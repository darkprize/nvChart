package andcom.nvchart;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by csy on 2018-03-23.
 */

public class NvListRecyclerAdapter extends RecyclerView.Adapter<NvListRecyclerAdapter.ItemViewHolder> {

    JSONArray jarrNvList;

    public NvListRecyclerAdapter(String jsonArray){
        try{
            JSONObject json = new JSONObject(jsonArray);
            jarrNvList = json.getJSONArray("List");
        }catch (JSONException je){
            je.printStackTrace();
        }

    }
    @Override
    public NvListRecyclerAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nv_item,parent,false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NvListRecyclerAdapter.ItemViewHolder holder, int position) {
        try{
            JSONObject jsonItem =  jarrNvList.getJSONObject(position);
            holder.txtTitle.setText(jsonItem.getString("NS_TITLE"));
            holder.txtNodeKey.setText(jsonItem.getString("NS_NODEKEY"));
            holder.txtCount.setText(jsonItem.getString("ND_PAGE_CNT"));
            holder.txtJsonBag.setText(jsonItem.toString());
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
        return jarrNvList.length();
    }
    class ItemViewHolder extends RecyclerView.ViewHolder{
        private CardView cardView;
        private TextView txtTitle;
        private TextView txtNodeKey;
        private TextView txtCount;
        private TextView txtJsonBag;
        public ItemViewHolder(View itemView){
            super(itemView);
            //cardView = (CardView)itemView.findViewById(R.id.cardView);
            txtTitle = (TextView)itemView.findViewById(R.id.title);
            txtNodeKey = (TextView)itemView.findViewById(R.id.nodekey);
            txtCount = (TextView)itemView.findViewById(R.id.count);
            txtJsonBag = (TextView)itemView.findViewById(R.id.jsonBag);
        }
    }
}

package andcom.nvchart;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by csy on 2018-03-23.
 */

public class NvListRecyclerAdapter extends RecyclerView.Adapter<NvListRecyclerAdapter.ItemViewHolder> {
    static NvListRecyclerAdapter nvListRecyclerAdapter;

    JSONArray jarrNvList;
    static HashMap<String,Integer> pageCnt;
    static HashMap<String,Integer> pageIndex;
    static int seletedPosition;
    private NvListRecyclerAdapter(String jsonArray){

        try{
            pageCnt = new HashMap<>();
            pageCnt.clear();
            pageIndex = new HashMap<>();
            pageIndex.clear();

            JSONObject json = new JSONObject(jsonArray);
            jarrNvList = json.getJSONArray("List");

            for(int i=0;i<jarrNvList.length();i++){
                JSONObject jsonItem = jarrNvList.getJSONObject(i);
                pageCnt.put(jsonItem.getString("NS_NODEKEY"),Integer.parseInt(jsonItem.getString("ND_PAGE_CNT")));
                Log.e("pageCnt",Integer.parseInt(jsonItem.getString("ND_PAGE_CNT"))+"c");
                pageIndex.put(jsonItem.getString("NS_NODEKEY"),i);
            }
            Log.e("NvListAdapter",jarrNvList+" Initializing");

        }catch (JSONException je){
            je.printStackTrace();
        }

    }

    public static NvListRecyclerAdapter getInstance(String jsonArray){
        nvListRecyclerAdapter = new NvListRecyclerAdapter(jsonArray);

        return nvListRecyclerAdapter;
    }
    public static NvListRecyclerAdapter getInstance(){

        return nvListRecyclerAdapter;
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

        if(seletedPosition == position){
            holder.cardView.setSelected(true);
        }else{
            holder.cardView.setSelected(false);
        }

    }

    public String getNodeKey(int position){
        String nodekey ;
        try{
            nodekey = jarrNvList.getJSONObject(position).getString("NS_NODEKEY");
        }catch (Exception je){
            je.printStackTrace();
            nodekey = null;
        }

        return nodekey;
    }
    public String getTitle(int position){
        String title ;
        try{
            title = jarrNvList.getJSONObject(position).getString("NS_TITLE");
        }catch (Exception je){
            je.printStackTrace();
            title = null;
        }

        return title;
    }
    public String getPageCnt(int position){
        String pagecnt ;
        try{
            pagecnt = jarrNvList.getJSONObject(position).getString("ND_PAGE_CNT");
        }catch (Exception je){
            je.printStackTrace();
            pagecnt = null;
        }

        return pagecnt;
    }

    public int getPageCnt(String nodeKey){
        return pageCnt.get(nodeKey);
    }

    public int getPageIndex(String nodeKey){
        return pageIndex.get(nodeKey);
    }

    public void setSeletedPosition(int n){
        seletedPosition = n;
        notifyDataSetChanged();
    }
    public int getSeletedPosition(){
        return seletedPosition;
    }

    @Override
    public int getItemViewType(int position) {
        //return super.getItemViewType(position);
        return position;
    }
    @Override
    public int getItemCount() {
        return jarrNvList==null? 0: jarrNvList.length();
    }
    static class ItemViewHolder extends RecyclerView.ViewHolder{
        private LinearLayout cardView;
        private TextView txtTitle;
        private TextView txtNodeKey;
        private TextView txtCount;
        private TextView txtJsonBag;
        public ItemViewHolder(View itemView){
            super(itemView);
            cardView = (LinearLayout)itemView.findViewById(R.id.cardView);
            txtTitle = (TextView)itemView.findViewById(R.id.title);
            txtNodeKey = (TextView)itemView.findViewById(R.id.nodekey);
            txtCount = (TextView)itemView.findViewById(R.id.count);
            txtJsonBag = (TextView)itemView.findViewById(R.id.jsonBag);
        }
    }
}

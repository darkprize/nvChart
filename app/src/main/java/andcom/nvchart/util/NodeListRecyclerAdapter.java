package andcom.nvchart.util;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import andcom.nvchart.R;

/**
 * Created by csy on 2018-03-23.
 */

public class NodeListRecyclerAdapter extends RecyclerView.Adapter<NodeListRecyclerAdapter.ItemViewHolder> {
    static NodeListRecyclerAdapter nvListRecyclerAdapter;

    JSONArray jarrNvList;
    ArrayList<JSONObject> arrJson;
    public NodeListRecyclerAdapter(JSONObject jsonData){

        try{

            jarrNvList = jsonData.getJSONArray("List");


        }catch (JSONException je){
            je.printStackTrace();
        }

    }
    public NodeListRecyclerAdapter(ArrayList<JSONObject> arrJson){
        jarrNvList = new JSONArray();
        this.arrJson = arrJson;
        int index = 0;
        for(JSONObject item : arrJson){
            try{
                JSONArray arrTemp = item.getJSONArray("List");
                for(int i =0;i<arrTemp.length();i++){
                    arrTemp.getJSONObject(i).put("DB",item.getString("DB"));
                    jarrNvList.put(index,arrTemp.getJSONObject(i));
                    index++;
                }

            }catch (JSONException je){
                je.printStackTrace();
            }
        }


    }

    @Override
    public NodeListRecyclerAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.node_refresh_dialog_item,parent,false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NodeListRecyclerAdapter.ItemViewHolder holder, int position) {
        try{
            JSONObject jsonItem =  jarrNvList.getJSONObject(position);
            holder.txtDB.setText("DB"+jsonItem.getString("DB"));
            holder.txtTitle.setText(jsonItem.getString("NS_TITLE"));
            holder.txtNodeKey.setText(jsonItem.getString("NS_NODEKEY"));
            holder.txtJsonBag.setText(jsonItem.toString());

            holder.nodeFile = jsonItem.getString("DB")+":"+jsonItem.getString("NS_NODEKEY");

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
        return jarrNvList==null? 0: jarrNvList.length();
    }

    public void isChecked(int position){

    }
    static class ItemViewHolder extends RecyclerView.ViewHolder{
        private CheckBox checkBox;
        private TextView txtTitle;
        private TextView txtDB;
        private TextView txtNodeKey;
        private TextView txtJsonBag;
        private String nodeFile;
        public ItemViewHolder(View itemView){
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox);
            txtDB = itemView.findViewById(R.id.db);
            txtTitle = (TextView)itemView.findViewById(R.id.title);
            txtNodeKey = (TextView)itemView.findViewById(R.id.nodekey);
            txtJsonBag = (TextView)itemView.findViewById(R.id.jsonBag);
        }

        public void check(){
            checkBox.setChecked(!checkBox.isChecked());
        }

        public boolean isChecked(){
            return checkBox.isChecked();
        }

        public String getNode(){
            return nodeFile;
        }
    }
}

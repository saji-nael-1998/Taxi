package com.example.taxi.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taxi.R;
import com.example.taxi.database.VolleyCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;

public class DriverAdapter extends RecyclerView.Adapter<DriverAdapter.ViewHolder> {
    private LinkedList<Integer> listdata;
    private Context context;


    // RecyclerView recyclerView;
    public DriverAdapter(LinkedList<Integer> listdata, Context context) {
        this.listdata = listdata;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.driver_card, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DriverAdapter.ViewHolder holder, int position) {
        final int myListData = listdata.get(position);
        HashMap<String, String> data = new HashMap<String, String>();

        data.put("userID", String.valueOf(myListData));
        MainActivity.dbConnection.getData(new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) throws Exception {

                try {

                        JSONObject driverJSON = result.getJSONObject("driver");

                        holder.nameTV.setText(driverJSON.getString("FName") + " " + driverJSON.getString("LName"));
                        String imagePath=driverJSON.getString("imagePath");
                        //set taxi
                        JSONObject taxiJSON = driverJSON.getJSONObject("taxi");
                        holder.taxiTV.setText(taxiJSON.getString("plate_no"));

                    uploadImage( imagePath,holder);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onSuccessImage(Bitmap bitmap) throws Exception {

            }

            @Override
            public void onError(String result) {

                Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
            }
        }, "driver", data);

    }

    public  void  uploadImage(String imagePath, ViewHolder holder){
        MainActivity.dbConnection.uploadImage(new VolleyCallback() {


            @Override
            public void onSuccess(JSONObject result) throws Exception {

            }

            @Override
            public void onSuccessImage(Bitmap bitmap) throws Exception {
               holder.imageView.setImageBitmap(bitmap);
            }

            @Override
            public void onError(String result) {

            }


        },"upload", imagePath);
    }
    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView nameTV;
        public TextView taxiTV;
        public LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.driver_list_photo);
            this.taxiTV = (TextView) itemView.findViewById(R.id.driver_list_taxi_plate_no);
            this.nameTV = (TextView) itemView.findViewById(R.id.driver_list_name);

            linearLayout = (LinearLayout) itemView.findViewById(R.id.driver_card);
        }
    }
}

package com.example.taxi.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taxi.R;
import com.example.taxi.modal.Trip;

import java.util.LinkedList;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.ViewHolder> {
    private LinkedList<Trip> listdata;
    private Context context;


    // RecyclerView recyclerView;
    public TripAdapter(LinkedList<Trip> listdata, Context context) {
        this.listdata = listdata;
        this.context = context;
    }

    @Override
    public TripAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.trip_item, parent, false);
        TripAdapter.ViewHolder viewHolder = new TripAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TripAdapter.ViewHolder holder, int position) {
        final Trip myListData = listdata.get(position);
        holder.totalRiderTV.setText(myListData.getTotalRider());
        holder.destTV.setText(myListData.getDest());
        holder.srcTV.setText(myListData.getSrc());
        holder.startTV.setText(myListData.getStart());
        holder.endTV.setText(myListData.getEnd());
        holder.dateTV.setText(myListData.getStart()+","+myListData.getDate());
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView dateTV;

        public TextView startTV;
        public TextView endTV;
        public TextView destTV;
        public TextView srcTV;
        public TextView totalRiderTV;

        public LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            this.dateTV = (TextView) itemView.findViewById(R.id.date);

            this.startTV = (TextView) itemView.findViewById(R.id.startTrip);
            this.endTV = (TextView) itemView.findViewById(R.id.endTrip);
            this.srcTV = (TextView) itemView.findViewById(R.id.src);
            this.destTV = (TextView) itemView.findViewById(R.id.dest);
            this.totalRiderTV = (TextView) itemView.findViewById(R.id.total_rider);



        }
    }
}

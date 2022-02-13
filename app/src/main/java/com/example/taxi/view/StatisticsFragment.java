package com.example.taxi.view;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taxi.database.VolleyCallback;
import com.example.taxi.databinding.FragmentStatisticsBinding;
import com.example.taxi.modal.Driver;
import com.example.taxi.modal.Trip;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;


public class StatisticsFragment extends Fragment {
    private FragmentStatisticsBinding binding;
    final Calendar myCalendar = Calendar.getInstance();


    private Driver currentDriver = MainActivity.driver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStatisticsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.selectDate.setOnClickListener(this::selectDateBTN);

        return root;
    }

    public void selectDateBTN(View view) {
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                selectDate();
            }
        };
        new DatePickerDialog(getContext(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();

    }

    private void selectDate() {

        String myFormat2 = "yyyy-MM-dd";
        SimpleDateFormat dateFormat2 = new SimpleDateFormat(myFormat2, Locale.US);
        getData(dateFormat2.format(myCalendar.getTime()));
    }

    private void getData(String date) {
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("date", date);
        data.put("userID", currentDriver.getUserID());
        ProgressDialog progressDoalog = new ProgressDialog(getContext());
        progressDoalog.setMessage("Checking ....");
        progressDoalog.show();

        MainActivity.dbConnection.getData(new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) throws Exception {
                //hide progress dialop
                progressDoalog.dismiss();
                try {

                    if (result.getJSONObject("data").getString("process").equals("failed") != true) {
                        setData(result.getJSONObject("data").getJSONObject("workday"));
                        binding.noData.setVisibility(View.GONE);
                        binding.resultBox.setVisibility(View.VISIBLE);
                        binding.noData.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(getContext(), "No data", Toast.LENGTH_SHORT).show();
                        binding.noData.setVisibility(View.VISIBLE);
                        binding.resultBox.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onSuccessImage(Bitmap bitmap) throws Exception {

            }

            @Override
            public void onError(String result) {
                //hide progress dialop
                progressDoalog.dismiss();
                Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
            }
        }, "workday", data);


    }

    private void setData(JSONObject jsonObject) {
        try {
            String parkID = jsonObject.getString("park_id");
            String userID = jsonObject.getString("user_id");
            String date = jsonObject.getString("workday_date");
            getDate(parkID, date, userID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getDate(String parkID, String date, String userID) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("days/" + date + "/" + parkID + "/");
        databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> result) {
                DataSnapshot driver = result.getResult().child("drivers").child(userID);

                setWorkdayDetails(date, driver);
                getTrip(date,result.getResult().child("recentTrips"), driver.child("routeID").getValue().toString(), userID);
            }
        });
    }


    private void setWorkdayDetails(String date, DataSnapshot driver) {
        binding.date.getEditText().setText(date);
        binding.startTime.getEditText().setText(driver.child("startTime").getValue().toString());
        if (driver.child("endTime").getValue() != null) {
            binding.endTime.getEditText().setText(driver.child("endTime").getValue().toString());

        } else {
            binding.endTime.getEditText().setText("still working");
        }
        binding.plateNo.getEditText().setText(driver.child("taxi").getValue().toString());

    }

    private void getTrip(String date, DataSnapshot recentTrips, String route, String driver) {
        if (recentTrips.getValue() != null) {
            DataSnapshot trips = recentTrips.child(route).child(driver);

            if (trips.getValue() != null) {
                LinkedList<Trip> listdata = new LinkedList<>();

                for (DataSnapshot trip : trips.getChildren()) {

                    Trip t = new Trip();
                    t.setDate(date);
                    t.setDest(trip.child("dest").getValue().toString());
                    t.setSrc(trip.child("src").getValue().toString());
                    String startTime = trip.child("startTime").getValue().toString();
                    String split[] = startTime.split(":");
                    if (split[0].length() == 1)
                        split[0] = "0" + split[0];
                    t.setStart(split[0] + ":" + split[1]);
                    String endTime = trip.child("endTime").getValue().toString();
                     split = endTime.split(":");
                    if (split[0].length() == 1)
                        split[0] = "0" + split[0];

                    t.setEnd(split[0] + ":" + split[1]);
                    t.setTotalRider(trip.child("totalRider").getValue().toString());
                    listdata.add(t);
                }
                if(listdata.size()!=0){
                    binding.totalTrip.setText("Total trip: "+listdata.size());
                }
                RecyclerView recyclerView = (RecyclerView) binding.history;
                TripAdapter adapter = new TripAdapter(listdata, getContext());
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(adapter);
            }
        }
    }
}
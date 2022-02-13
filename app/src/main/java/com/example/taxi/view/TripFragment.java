package com.example.taxi.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.example.taxi.R;
import com.example.taxi.databinding.FragmentTripBinding;
import com.example.taxi.modal.Driver;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class TripFragment extends Fragment {
    private FragmentTripBinding binding;
    private String startTime;
    private int totalRider;
    private int currentRider;
    private String currentDest;
    private boolean inPark;
    private Driver currentDriver;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            totalRider = Integer.parseInt(getArguments().getString("totalRider"));
            currentRider = Integer.parseInt(getArguments().getString("currentRider"));
            currentDest = getArguments().getString("currentDest");
            startTime = getArguments().getString("startTime");

            if (getArguments().getString("inPark").equals("yes")) {
                inPark = true;
            }
            currentDriver = MainActivity.driver;
        }
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTripBinding.inflate(inflater, container, false);
        View root = binding.getRoot();        // Inflate the layout for this fragment
        binding.inParkTB.setChecked(inPark);
        binding.inParkTB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(getContext(), "2", Toast.LENGTH_SHORT).show();
                    DatabaseReference updateData = FirebaseDatabase.getInstance()
                            .getReference("days/" + getCurrentDate() + "/" + currentDriver.getTaxi().getRoute().getParkID() + "/trips/" + currentDriver.getTaxi().getRoute().getRouteID() + "/" + currentDriver.getUserID() + "/");
                    updateData.child("inPark").setValue("yes");
                } else {
                    DatabaseReference updateData = FirebaseDatabase.getInstance()
                            .getReference("days/" + getCurrentDate() + "/" + currentDriver.getTaxi().getRoute().getParkID() + "/trips/" + currentDriver.getTaxi().getRoute().getRouteID() + "/" + currentDriver.getUserID() + "/");
                    updateData.child("inPark").setValue("no");

                }
            }
        });


        binding.currentDestTB.setTextOff(currentDriver.getTaxi().getRoute().getSrc());
        binding.currentDestTB.setTextOn(currentDriver.getTaxi().getRoute().getDest());
        if (currentDest.equals(currentDriver.getTaxi().getRoute().getDest())) {
            binding.currentDestTB.setChecked(true);
        } else {
            binding.currentDestTB.setChecked(true);
        }
        binding.currentDestTB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    DatabaseReference updateData = FirebaseDatabase.getInstance()
                            .getReference("days/" + getCurrentDate() + "/" + currentDriver.getTaxi().getRoute().getParkID() + "/trips/" + currentDriver.getTaxi().getRoute().getRouteID() + "/" + currentDriver.getUserID() + "/");
                    updateData.child("currentDest").setValue(currentDriver.getTaxi().getRoute().getDest());
                } else {
                    DatabaseReference updateData = FirebaseDatabase.getInstance()
                            .getReference("days/" + getCurrentDate() + "/" + currentDriver.getTaxi().getRoute().getParkID() + "/trips/" + currentDriver.getTaxi().getRoute().getRouteID() + "/" + currentDriver.getUserID() + "/");
                    updateData.child("currentDest").setValue(currentDriver.getTaxi().getRoute().getSrc());

                }
            }
        });
        binding.route.getEditText().setText(currentDriver.getTaxi().getRoute().getSrc());
        binding.dest.getEditText().setText(currentDriver.getTaxi().getRoute().getDest());
        binding.rider.setText(String.valueOf(currentRider));
        binding.totalRider.setText(String.valueOf(totalRider));
        binding.increaseRider.setOnClickListener(this::increaseRiderCount);
        binding.decreaseRider.setOnClickListener(this::decreaseRiderCount);
        binding.endTrip.setOnClickListener(this::endTrip);
        return root;
    }

    public void increaseRiderCount(View view) {
        if (currentRider == 7) {
            Toast.makeText(getContext(), "max of rider", Toast.LENGTH_SHORT).show();
        } else if (currentRider < 7) {
            currentRider++;
            totalRider++;
            DatabaseReference updateData = FirebaseDatabase.getInstance()
                    .getReference("days/" + getCurrentDate() + "/" + currentDriver.getTaxi().getRoute().getParkID() + "/trips/" + currentDriver.getTaxi().getRoute().getRouteID() + "/" + currentDriver.getUserID() + "/");
            updateData.child("currentRider").setValue(currentRider);
            updateData.child("totalRider").setValue(totalRider);
            //change on screent
            binding.rider.setText(String.valueOf(currentRider));
            binding.totalRider.setText(String.valueOf(totalRider));
        }

    }

    public void decreaseRiderCount(View view) {
        if (currentRider == 0) {
            Toast.makeText(getContext(), "min of rider", Toast.LENGTH_SHORT).show();
        } else if (currentRider <= 7) {
            currentRider--;

            DatabaseReference updateData = FirebaseDatabase.getInstance()
                    .getReference("days/" + getCurrentDate() + "/" + currentDriver.getTaxi().getRoute().getParkID() + "/trips/" + currentDriver.getTaxi().getRoute().getRouteID() + "/" + currentDriver.getUserID() + "/");
            updateData.child("currentRider").setValue(currentRider);
            //change on screent
            binding.rider.setText(String.valueOf(currentRider));

        }

    }

    public void endTrip(View view) {
        if (currentRider == 0) {
            //write trip details
            Map<String, Object> data = new HashMap<String, Object>();
            Map<String, Object> trip = new HashMap<String, Object>();

            trip.put("startTime", startTime);
            DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss aa");
            String dateString = dateFormat.format(new Date()).toString();
            trip.put("endTime", dateString);
            trip.put("totalRider", String.valueOf(totalRider));
            trip.put("src", currentDriver.getTaxi().getRoute().getSrc());
            trip.put("dest", currentDriver.getTaxi().getRoute().getDest());
            DateFormat key = new SimpleDateFormat("hh:mm");
            String keyString = key.format(new Date()).toString();
            data.put(keyString,trip);
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference("days/" + getCurrentDate() + "/" + currentDriver.getTaxi().getRoute().getParkID() + "/recentTrips/"+currentDriver.getTaxi().getRoute().getRouteID()+"/"+currentDriver.getUserID());
            databaseReference.updateChildren(data);
            //delete current trip
            DatabaseReference updateData = FirebaseDatabase.getInstance()
                    .getReference("days/" + getCurrentDate() + "/" + currentDriver.getTaxi().getRoute().getParkID() + "/trips/" + currentDriver.getTaxi().getRoute().getRouteID() + "/");

            updateData.child(currentDriver.getUserID()).removeValue();
            //go to dashboard
            NavController navController = Navigation.findNavController(view);
            NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.action_tripFragment_to_shiftFragment, true).build();
            navController.navigate(R.id.action_tripFragment_to_shiftFragment);
        } else {
            Toast.makeText(getContext(), "you must drop off all rider", Toast.LENGTH_SHORT).show();
        }
    }

    public String getCurrentDate() {
        Calendar cal = Calendar.getInstance();
        // displaying date
        Format f = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = f.format(new Date());
        return strDate;
    }
}
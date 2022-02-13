package com.example.taxi.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.example.taxi.R;
import com.example.taxi.databinding.FragmentHomeBinding;
import com.example.taxi.modal.Driver;
import com.example.taxi.view.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class HomeFragment extends Fragment {


    private FragmentHomeBinding binding;
    private Driver currentDriver;
    private DataSnapshot park;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        currentDriver = MainActivity.driver;
        binding.startWorkday.setOnClickListener(this::startShiftBTN);
        //set data
        setData(binding);
        return root;
    }

    public void setData(FragmentHomeBinding binding) {
        ((TextInputLayout) binding.parkName).getEditText().setText(MainActivity.driver.getTaxi().getRoute().getParkName());
        ((TextInputLayout) binding.parkLocation).getEditText().setText(MainActivity.driver.getTaxi().getRoute().getLocation());
        ((TextInputLayout) binding.plateNo).getEditText().setText(MainActivity.driver.getTaxi().getTaxiPlateNO());
        ((TextInputLayout) binding.registrationDate).getEditText().setText(convertDateToString(MainActivity.driver.getTaxi().getReqistrationDate()));
        ((TextInputLayout) binding.route).getEditText().setText(MainActivity.driver.getTaxi().getRoute().getDest());

    }

    public String convertDateToString(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
        String strDate = dateFormat.format(date);
        return strDate;
    }

    public void startShiftBTN(View view) {
      /*  NavController navController = Navigation.findNavController(view);
        NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.action_nav_home_to_shiftFragment, true).build();
        navController.navigate(R.id.action_nav_home_to_shiftFragment);*/
        // The value will be default as empty string because for
        // the very first time when the app is opened, there is nothing to show
        String currentDate = getCurrentDate();
        try {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference("days/" + currentDate);
            databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    } else {
                        DataSnapshot result = task.getResult();
                        //check the day
                        if (result.getValue() != null) {
                             park = result.child(currentDriver.getTaxi().getRoute().getParkID());
                            //check park
                            if (park.getValue() != null) {
                                //check driver
                                if (checkDriverList(park.child("drivers"))) {

                                    //open workday dashboard
                                    NavController navController = Navigation.findNavController(view);
                                    NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.action_nav_home_to_shiftFragment, true).build();
                                    navController.navigate(R.id.action_nav_home_to_shiftFragment);
                                } else {
                                    if (checkRequest(park.child("requests"))) {
                                        Toast.makeText(getActivity(), "waiting response!!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        //sent request
                                        sendStartWorkday();
                                    }
                                }
                            } else {
                                Toast.makeText(getActivity(), "park is not active today", Toast.LENGTH_SHORT).show();

                            }
                        } else {
                            Toast.makeText(getActivity(), "park is not active today", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                private void sendStartWorkday() {
                    Map<String, Object> data = new HashMap<String, Object>();
                    Map<String, Object> request = new HashMap<String, Object>();

                    request.put("requestTitle", "start shift");
                    DateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
                    String dateString = dateFormat.format(new Date()).toString();
                    request.put("requestTime", dateString);
                    request.put("requestStatus", "pending");
                    data.put(currentDriver.getUserID(), request);
                    DatabaseReference databaseReference = firebaseDatabase.getReference("days/" + getCurrentDate() + "/" + currentDriver.getTaxi().getRoute().getParkID() + "/requests/");
                    databaseReference.updateChildren(data);
                    Toast.makeText(getActivity(), "sent!!", Toast.LENGTH_SHORT).show();

                }

                private boolean checkRequest(DataSnapshot requests) {
                    if (requests.getValue() != null) {
                        if (requests.child(currentDriver.getUserID()).getValue() != null) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                    return false;
                }

                private boolean checkDriverList(DataSnapshot drivers) {

                    if (drivers.getValue() != null) {
                        if (drivers.child(currentDriver.getUserID()).getValue() != null) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                    return false;
                }


            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String getCurrentDate() {
        Calendar cal = Calendar.getInstance();
        // displaying date
        Format f = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = f.format(new Date());
        return strDate;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
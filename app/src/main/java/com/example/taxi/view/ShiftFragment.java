package com.example.taxi.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taxi.R;
import com.example.taxi.databinding.FragmentShiftBinding;
import com.example.taxi.modal.Driver;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


public class ShiftFragment extends Fragment {
    private FragmentShiftBinding binding;
    private Driver currentDriver;
    private DataSnapshot park;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentShiftBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        currentDriver = MainActivity.driver;
        readDataFirebase();
        setDriverList();

        ((TextInputLayout) binding.route).getEditText().setText(currentDriver.getTaxi().getRoute().getDest());
        binding.listRequestBtn.setOnClickListener(this::sendListRequest);
        binding.endWorkdayBtn.setOnClickListener(this::sendEndWorkdayRequest);
        return root;
    }


    private void readDataFirebase() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

                DatabaseReference databaseReference = firebaseDatabase.getReference("days/" + getCurrentDate() + "/" + currentDriver.getTaxi().getRoute().getParkID() + "/trips/" + currentDriver.getTaxi().getRoute().getRouteID() + "/");

                ValueEventListener postListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot trips) {

                        if (trips.child(currentDriver.getUserID()).getValue() != null) {


                            DataSnapshot trip=trips.child(currentDriver.getUserID());
                            Bundle bundle = new Bundle();
                            bundle.putString("currentDest", trip.child("currentDest").getValue().toString());
                            bundle.putString("currentRider", trip.child("currentRider").getValue().toString());
                            bundle.putString("inPark", trip.child("inPark").getValue().toString());
                            bundle.putString("totalRider", trip.child("totalRider").getValue().toString());
                            bundle.putString("startTime", trip.child("start").getValue().toString());
                            binding.tripDatetime.setText(trip.child("start").getValue().toString()+","+getCurrentDate());
                            binding.src.setText(currentDriver.getTaxi().getRoute().getSrc());
                            binding.dest.setText(currentDriver.getTaxi().getRoute().getDest());



                            ((LinearLayout) binding.tripBox).setVisibility(View.VISIBLE);
                            binding.goToTrip.setOnClickListener(view -> {
                                NavController navController = Navigation.findNavController(view);
                                NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.action_shiftFragment_to_tripFragment, true).build();

                                navController.navigate(R.id.action_shiftFragment_to_tripFragment,bundle);
                            });
                        } else {
                            ((LinearLayout) binding.tripBox).setVisibility(View.GONE);

                        }


                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Toast.makeText(getContext(), databaseError.toException().toString(), Toast.LENGTH_SHORT).show();

                    }
                };

                databaseReference.addValueEventListener(postListener);
            }
        });
        //start thread
        thread.start();
    }

    private void setDriverList() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

                DatabaseReference databaseReference = firebaseDatabase.getReference("days/" + getCurrentDate() + "/" + currentDriver.getTaxi().getRoute().getParkID() + "/routes/" + currentDriver.getTaxi().getRoute().getRouteID() + "/drivers/");

                ValueEventListener postListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot list) {
                        LinkedList<Integer> listdata = new LinkedList<>();

                        if (list.getValue() != null) {

                            for (DataSnapshot driver : list.getChildren()) {
                                listdata.add(Integer.parseInt(driver.getKey()));

                            }

                        }
                        RecyclerView recyclerView = (RecyclerView) binding.driverQueue;
                        DriverAdapter adapter = new DriverAdapter(listdata, getContext());
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        recyclerView.setAdapter(adapter);
                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Toast.makeText(getContext(), databaseError.toException().toString(), Toast.LENGTH_SHORT).show();

                    }
                };

                databaseReference.addValueEventListener(postListener);
            }
        });
        //start thread
        thread.start();
    }


    public void sendListRequest(View view) {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("days/" + getCurrentDate() + "/" + currentDriver.getTaxi().getRoute().getParkID() + "/");
        databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    DataSnapshot result = task.getResult();
                    if (checkDriverList(result.child("routes").child(currentDriver.getTaxi().getRoute().getRouteID()).child("drivers")) != true) {
                        if (checkRequest(result.child("requests")) != true) {

                            if(checkTripList(result.child("trips").child(currentDriver.getTaxi().getRoute().getRouteID()))!=true){
                                sendlistRequest();
                            }else{
                                Toast.makeText(getContext(), "you are already have a trip", Toast.LENGTH_SHORT).show();

                            }

                        } else {
                            Toast.makeText(getContext(), "you are already sent a request", Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        Toast.makeText(getContext(), "you are already in list !", Toast.LENGTH_SHORT).show();
                    }

                }
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
            private boolean checkTripList(DataSnapshot trips) {

                if (trips.getValue() != null) {
                    if (trips.child(currentDriver.getUserID()).getValue() != null) {
                        return true;
                    } else {
                        return false;
                    }
                }
                return false;
            }


        });

    }

    public void sendEndWorkdayRequest(View view) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("days/" + getCurrentDate() + "/" + currentDriver.getTaxi().getRoute().getParkID() + "/");
        databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    DataSnapshot result = task.getResult();
                    if (checkDriverList(result.child("routes").child(currentDriver.getTaxi().getRoute().getRouteID()).child("drivers")) != true) {
                        if (checkRequest(result.child("requests")) != true) {
                            sendEndWorkday();
                        } else {
                            Toast.makeText(getContext(), "you are already sent a request", Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        Toast.makeText(getContext(), "you are already in list !", Toast.LENGTH_SHORT).show();
                    }

                }
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
    }

    private void sendEndWorkday() {
        Map<String, Object> data = new HashMap<String, Object>();
        Map<String, Object> request = new HashMap<String, Object>();

        request.put("requestTitle", "end shift");
        DateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
        String dateString = dateFormat.format(new Date()).toString();
        request.put("requestTime", dateString);
        request.put("requestStatus", "pending");
        data.put(currentDriver.getUserID(), request);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        DatabaseReference databaseReference = firebaseDatabase.getReference("days/" + getCurrentDate() + "/" + currentDriver.getTaxi().getRoute().getParkID() + "/requests/");
        databaseReference.updateChildren(data);
        Toast.makeText(getActivity(), "sent!!", Toast.LENGTH_SHORT).show();

    }

    private void sendlistRequest() {
        Map<String, Object> data = new HashMap<String, Object>();
        Map<String, Object> request = new HashMap<String, Object>();

        request.put("requestTitle", "add to list");
        DateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
        String dateString = dateFormat.format(new Date()).toString();
        request.put("requestTime", dateString);
        request.put("requestStatus", "pending");
        data.put(currentDriver.getUserID(), request);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        DatabaseReference databaseReference = firebaseDatabase.getReference("days/" + getCurrentDate() + "/" + currentDriver.getTaxi().getRoute().getParkID() + "/requests/");
        databaseReference.updateChildren(data);
        Toast.makeText(getActivity(), "sent!!", Toast.LENGTH_SHORT).show();

    }

    public String getCurrentDate() {
        Calendar cal = Calendar.getInstance();
        // displaying date
        Format f = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = f.format(new Date());
        return strDate;
    }
}
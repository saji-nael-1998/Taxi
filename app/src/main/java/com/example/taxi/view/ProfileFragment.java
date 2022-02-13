package com.example.taxi.view;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.taxi.database.VolleyCallback;
import com.example.taxi.databinding.FragmentProfileBinding;
import com.example.taxi.modal.Driver;
import com.google.android.material.imageview.ShapeableImageView;

import org.json.JSONObject;


public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private Driver currentDriver;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
currentDriver=MainActivity.driver;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.driverName.setText(currentDriver.getDriverName());
        binding.driverLocation.setText(currentDriver.getStreet() + "," + currentDriver.getCity());
        binding.email.getEditText().setText(currentDriver.getEmail());
        binding.phone.getEditText().setText(currentDriver.getPhoneNO());
        binding.driverId.getEditText().setText(currentDriver.getID());
        binding.birthdate.getEditText().setText(currentDriver.getBirthdate());

        uploadImage(currentDriver.getImagePath(),  binding.profilePhoto);
        //upload image
        return root;
    }
    public  void  uploadImage(String imagePath, ShapeableImageView profilePhoto){
        MainActivity.dbConnection.uploadImage(new VolleyCallback() {


            @Override
            public void onSuccess(JSONObject result) throws Exception {

            }

            @Override
            public void onSuccessImage(Bitmap bitmap) throws Exception {
                profilePhoto.setImageBitmap(bitmap);
            }

            @Override
            public void onError(String result) {

            }


        },"upload", imagePath);
    }
}
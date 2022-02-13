package com.example.taxi.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.taxi.constant.Tools;
import com.example.taxi.database.VolleyCallback;
import com.example.taxi.databinding.FragmentLoginBinding;
import com.example.taxi.modal.Driver;
import com.example.taxi.modal.Route;
import com.example.taxi.modal.Taxi;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;
    private TextInputLayout emailTIL;
    private TextInputLayout passTIL;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        binding.login.setOnClickListener(this::loginBTN);
        return root;
    }

    public void loginBTN(View view) {
        ProgressDialog progressDoalog = new ProgressDialog(getContext());
        progressDoalog.setMessage("Checking ....");
        progressDoalog.show();

        String email = binding.email.getEditText().getText().toString().trim();
        String pass = binding.pass.getEditText().getText().toString();
        System.out.println(email + " " + pass);
        Driver driver = new Driver(email, pass);
        if (driver.isValidEmail() == 0) {
            displayEmailError("please fill email input!");

        } else if (driver.isValidEmail() == -1) {
            displayEmailError("invalid email!");
        } else {
            binding.email.setError("");
        }
        if (driver.isValidPassword() == 0) {
            displayPassError("please fill password input!");

        } else if (driver.isValidPassword() == -1) {
            displayPassError("password must be more than 6 digits!");
        } else {
            binding.pass.setError("");
        }

        if (driver.isValidEmail() == 1 && driver.isValidPassword() == 1) {
            HashMap<String, String> data = new HashMap<String, String>();
            try {
                data.put("email", email);
                data.put("pass", pass);

                MainActivity.dbConnection.getData(new VolleyCallback() {
                    @Override
                    public void onSuccess(JSONObject result) throws Exception {
                        //hide progress dialop
                        progressDoalog.dismiss();
                        try {

                            if (result.getJSONObject("data").getString("proccess").equals("failed") != true) {
                                JSONObject driverJSON = result.getJSONObject("data").getJSONObject("driver");
                                driver.setDriverName(driverJSON.getString("FName") + " " + driverJSON.getString("LName"));
                                driver.setUserID(driverJSON.getString("user_id"));
                                driver.setID(driverJSON.getString("ID"));
                                driver.setBirthdate(driverJSON.getString("birthdate"));
                                driver.setPhoneNO(driverJSON.getString("phoneNO"));
                                driver.setCity(driverJSON.getString("city"));
                                driver.setStreet(driverJSON.getString("street"));
                                driver.setImagePath(driverJSON.getString("imagePath"));
                                Toast.makeText(getContext(),driverJSON.getString("imagePath"),Toast.LENGTH_LONG).show();
                                //set taxi
                                JSONObject taxiJSON = driverJSON.getJSONObject("taxi");
                                Taxi taxi = new Taxi();
                                taxi.setTaxiID(taxiJSON.getString("taxi_id"));
                                taxi.setTaxiBrand(taxiJSON.getString("brand"));
                                taxi.setTaxiYear(taxiJSON.getString("car_year"));
                                taxi.setTaxiPlateNO(taxiJSON.getString("plate_no"));
                                taxi.setReqistrationDate(taxiJSON.getString("reqistration_date"));
                                driver.setTaxi(taxi);
                                //set route
                                JSONObject routeJSON = taxiJSON.getJSONObject("route");

                                Route route=new Route();
                                route.setRouteID(routeJSON.getString("route_id"));
                                route.setParkID(routeJSON.getString("park_id"));
                                route.setParkName(routeJSON.getString("park_name"));
                                route.setLocation(routeJSON.getString("street")+","+routeJSON.getString("city"));
                                route.setDest(routeJSON.getString("dest"));
                                route.setSrc(routeJSON.getString("src"));
                                driver.getTaxi().setRoute(route);
                                saveDriverData(driver);
                                //close activity
                                getActivity().finish();
                            } else {
                                Toast.makeText(getContext(), "email or password is worng", Toast.LENGTH_SHORT).show();

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
                }, "login", data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void saveDriverData(Driver driver) throws Exception {
        SharedPreferences sharedpreferences = getContext().getSharedPreferences(Tools.DRIVER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        //set login
        editor.putBoolean("isLogged", true);
        //create driver object
        Gson gson = new Gson();
        String json = gson.toJson(driver);
        editor.putString("driver", json);
        editor.commit();
    }

    public void displayEmailError(String text) {
        binding.email.setError(text);
    }

    public void displayPassError(String text) {
        binding.pass.setError(text);
    }

    public void displayLoginSucceed() {
        Toast.makeText(getContext(), "Welcome", Toast.LENGTH_LONG).show();
    }

}
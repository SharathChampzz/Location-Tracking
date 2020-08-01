package com.example.covidwarriors;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements LocationListener , AdapterView.OnItemSelectedListener {
    private static final String FILE_NAME = "login.txt";
    private static final String UPLOAD_MISSING = "data.txt";
    Button button_location , logout;
    TextView textView_location;
    Spinner spinner;
    LocationManager locationManager;
    DatabaseReference reff;
    String longitude = "77.78787", latitude = "77.78787";
    String final_location = "Invalid";
    String time = "10:30:08" , day = "Tuesday";
    String name = "Name NA";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        load_offline(FILE_NAME);
        load_offline("user.txt");
        textView_location = findViewById(R.id.text_location);
        button_location = findViewById(R.id.button_location);
        logout = findViewById(R.id.lo_gout);
        spinner = findViewById(R.id.websites);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.websites, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File dir = getFilesDir();
                File file = new File(dir , FILE_NAME);
                boolean deleted = file.delete();
                if(deleted){
                    Toast.makeText(MainActivity.this, "Successfully Logged out!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this , EnteryActivity.class));
                    finish();
                }
            }
        });
        Toast.makeText(this, "Credential = " + final_location, Toast.LENGTH_SHORT).show();

        reff = FirebaseDatabase.getInstance().getReference().child(final_location);



        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            },100);
        }
        getLocation();
        button_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create method
                getLocation();
                dummy();
            }
        });

    }

    private void dummy() {
        setdatetime();
        updateFirebase();
    }

    private void setdatetime() {
        Date currentTime = Calendar.getInstance().getTime();
        String formatteddate = DateFormat.getDateInstance(DateFormat.FULL).format(currentTime);
        time =currentTime.toString().split(" ")[3];
        day = formatteddate.split(" ")[0];
    }

    private void updateFirebase() {
        Helper help = new Helper();
        help.setLatitude(latitude);
        help.setLongitude(longitude);
        help.setTime(time);
        help.setDay(day);
        help.setName(name);

        reff = FirebaseDatabase.getInstance().getReference().child(final_location);
        reff.push().setValue(help)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.this, "Sucess!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, e.toString() , Toast.LENGTH_SHORT).show();
                        Toast.makeText(MainActivity.this, final_location, Toast.LENGTH_SHORT).show();

                    }
                });

    }

    public void load_offline(String filename){
        FileInputStream fis = null;
        try {
            fis = openFileInput(filename);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;
            while ((text = br.readLine()) != null) {
                if(filename.equals(UPLOAD_MISSING)){
                    String[] x = text.split(":");
                    longitude = x[0];
                    latitude = x[1];
                    time  = x[2];
                    day = x[3];
                    name = x[4];
                    setdatetime();
                    updateFirebase();
                }
                else {
                    sb.append(text).append("\n");
                }
                //
            }
            if(filename.equals(FILE_NAME)){ // adress
                final_location = sb.toString().trim();
            }
            else if(filename.equals(UPLOAD_MISSING)){
                File dir = getFilesDir();
                File file = new File(dir , filename);
                boolean deleted = file.delete();
                if(deleted){
                    Toast.makeText(this, "Old Values Erased!", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                name = sb.toString().trim(); //name of a user
            }

            //mEditText.setText(sb.toString());
            /*
            File dir = getFilesDir();
            File file = new File(dir , FILE_NAME);
            boolean deleted = file.delete();
            if(deleted){
                Toast.makeText(this, "Old Values Erased!", Toast.LENGTH_SHORT).show();
            }  */
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @SuppressLint("MissingPermission")
    private void getLocation() {
        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,5,MainActivity.this);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(this, ""+location.getLatitude()+","+location.getLongitude(), Toast.LENGTH_SHORT).show();
        latitude = String.valueOf(location.getLatitude());
        longitude = String.valueOf(location.getLongitude());

        setdatetime();
        if(CheckConnection()){
            // Check whether file exists is exists read from there
            if(fileExist()){
                Toast.makeText(this, "Old file found! Uploading from there!", Toast.LENGTH_SHORT).show();
                load_offline(UPLOAD_MISSING);
            }
            updateFirebase();
        }
        else{
            Toast.makeText(MainActivity.this, "Storing Offline!", Toast.LENGTH_SHORT).show();
            save_offline();
        }

        //Toast.makeText(MainActivity.this, "Location Updated in Firebase! " + final_location, Toast.LENGTH_SHORT).show();

        try {
            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            String address = addresses.get(0).getAddressLine(0);

            textView_location.setText(address);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean fileExist() {
        File file = getBaseContext().getFileStreamPath(MainActivity.UPLOAD_MISSING);
        return file.exists();
    }

    private void save_offline() {
        String text = longitude + ":" + latitude + ":" + time + ":" + day + ":" + name;
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(FILE_NAME, Context.MODE_APPEND);
            fos.write(text.getBytes());
            //mEditText.getText().clear();
            //Toast.makeText(this, "Saved to " + getFilesDir() + "/" + FILE_NAME,
                    //Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Offline Added : " + text, Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean CheckConnection() {
        ConnectivityManager manager = (ConnectivityManager)  getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        assert manager != null;
        NetworkInfo activenetwork = manager.getActiveNetworkInfo();
        if(null != activenetwork){
            return true;
        }
        else{
            return false;
            //Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
        String sSelected=parent.getItemAtPosition(position).toString();
        if( sSelected.equals("WHO")){
            Toast.makeText(this, "WHO site will be opened..!!", Toast.LENGTH_SHORT).show();
            Intent browserintent = new Intent(Intent.ACTION_VIEW ,
                    Uri.parse("https://www.who.int/"));
            startActivity(browserintent);
        }

        else if(sSelected.equals("Corona Positives Nearby")){
            Toast.makeText(this, "Opening site!", Toast.LENGTH_SHORT).show();
            Intent browserintent = new Intent(Intent.ACTION_VIEW ,
                    Uri.parse("https://covidnearyou.org/"));
            startActivity(browserintent);
        }
        else if(sSelected.equals("Other Popular Webiste")){
            Toast.makeText(this, "Work Under Progress!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}

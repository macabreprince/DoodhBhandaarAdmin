package com.doodhbhandaar.dbadmin;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class AddCustomerActivity extends AppCompatActivity {


    ArrayList<String> areaItems;
    ArrayList<DeliveryBoyReference> deliveryBoyItems;
    ArrayList<String> deliveryBoysNamesItems;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference areasReference;
    DatabaseReference deliveryBoyReference;
    DatabaseReference customersReference;
    Spinner dropdown,deliveryBoysSpinner;
    String area = "0";
    String deliveryBoyContactNumber="0";
    String delveryBoyName = "0";
    ArrayAdapter<String> adapter,adapterDeliveryBoy;
    EditText editTextName, editTextContact, editTextaddress, editTextQuantity;
    CheckBox isMorningClicked;
    CheckBox isEveningClicked;
    Button register;
    EditText textView, textView2;
    Button btn;
    String string,string2;

    FusedLocationProviderClient client;
    LocationCallback callback;

    @Override
    protected void onStart() {
        super.onStart();
        LocationRequest request = new LocationRequest();
        request.setInterval(5000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setFastestInterval(3000);

        callback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if(locationResult != null){
                    List<Location> locations = locationResult.getLocations();
                    for(Location location: locations){
                         string=location.getLatitude()+"";
                         string2=location.getLongitude()+"";
                    }
                }
                super.onLocationResult(locationResult);
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(this,"st",Toast.LENGTH_SHORT).show();
            return;
        }
        client.requestLocationUpdates(request,callback, null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        client.removeLocationUpdates(callback);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);
        /*mychange........................*/
        client = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(this,"st2",Toast.LENGTH_SHORT).show();

            return;
        }
        client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                  string=location.getLatitude()+"";
                  string2=location.getLongitude()+"";
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        textView=findViewById(R.id.Latitude);
        textView2=findViewById(R.id.Longitude);

        btn=findViewById(R.id.btn1);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //textView.setVal;
                textView2.setText(string2);
                textView.setText(string);


            }
        });

        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabaseReference.getDatabaseInstance();
        fetchAreas();
        fetchDeliveryBoys();
        customersReference = firebaseDatabase.getReference("CUSTOMERS");
        dropdown = findViewById(R.id.spinnerType);
        deliveryBoysSpinner = findViewById(R.id.spinnerDeliveryBoy);
        areaItems = new ArrayList<>();
        deliveryBoyItems = new ArrayList<>();
        deliveryBoysNamesItems = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, areaItems);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                area = areaItems.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        adapterDeliveryBoy = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,deliveryBoysNamesItems);
        deliveryBoysSpinner.setAdapter(adapterDeliveryBoy);
        deliveryBoysSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                delveryBoyName = deliveryBoysNamesItems.get(position);
                deliveryBoyContactNumber = deliveryBoyItems.get(position).contactNo;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        findViews();

    }

    private void fetchDeliveryBoys() {
        deliveryBoyReference = firebaseDatabase.getReference("DELIVERYBOY");

        deliveryBoyReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                DeliveryBoyReference deliveryBoyReference = dataSnapshot.getValue(DeliveryBoyReference.class);
                deliveryBoyItems.add(deliveryBoyReference);
                int x = deliveryBoyItems.size();
                deliveryBoysNamesItems.add(deliveryBoyItems.get(x-1).name);
                adapterDeliveryBoy.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {


            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.i("fail","fail1");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("fail1","fail");
            }
        });

    }

    private void findViews() {
        editTextName = findViewById(R.id.signup_input_name);
        editTextaddress = findViewById(R.id.addressEditText);
        editTextContact = findViewById(R.id.contactNumberEditText);
        editTextQuantity = findViewById(R.id.QuantityEditText);
        isMorningClicked = findViewById(R.id.checkboxMorning);
        isEveningClicked = findViewById(R.id.checkboxEvening);
        register = findViewById(R.id.btn_signup);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerCustomer();
            }
        });
    }

    private void registerCustomer() {
        String todaysDate = "0";
        CustomerData customerData = new CustomerData();
        customerData.customerName = editTextName.getText().toString();
        customerData.customerPhonenumber = editTextContact.getText().toString();
        customerData.customerAddress = editTextaddress.getText().toString();
        customerData.defaultQuantity = editTextQuantity.getText().toString();
        customerData.lastDeliveryDate="0";
        if(area.equals("0"))
            area = areaItems.get(0);
        customerData.area = area;
        if (isMorningClicked.isChecked())
            customerData.isMorning = true;
        if(isEveningClicked.isChecked())
            customerData.isEvening = true;
        customerData.deliverBoyName = delveryBoyName;
        customerData.deliveryBoyContactNumber = deliveryBoyContactNumber;

        customerData.latitude=string;
        customerData.longitude = string2;
        CustomerDeliveriesReference customerDeliveriesReference = new CustomerDeliveriesReference();
//        for(int i=0;i<4;i++){
        customerDeliveriesReference.date = todaysDate;
        customerDeliveriesReference.quantity ="0";
//        }
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Registering...");
        progressDialog.show();
        String key = customersReference.push().getKey();
        customerData.pk = key;
        customersReference.child(key).setValue(customerData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(AddCustomerActivity.this,"Succesfully Registered",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddCustomerActivity.this,"Failed to Registered",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        finish();
                    }
                });


    }

    private void fetchAreas() {
        areasReference = firebaseDatabase.getReference("AREAS");
        areasReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String ss = dataSnapshot.getValue(String.class);
                areaItems.add(ss);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
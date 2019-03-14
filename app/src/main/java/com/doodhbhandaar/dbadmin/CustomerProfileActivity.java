package com.doodhbhandaar.dbadmin;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CustomerProfileActivity extends AppCompatActivity  {

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
    EditText latitude,longitude;
    Button register,currentLocation,btn;
    Bundle bundle;
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
        client = LocationServices.getFusedLocationProviderClient(this);
        callback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if(locationResult != null){
                    List<Location> locations = locationResult.getLocations();
                    for(Location location: locations){
                        string=location.getLatitude()+"";
                        string2=location.getLongitude()+"";
                        Log.d("zz",string+"?"+string2);
                    }
                }
                super.onLocationResult(locationResult);
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

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
        setContentView(R.layout.activity_customer_profile);
        bundle=getIntent().getExtras();
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabaseReference.getDatabaseInstance();
        btn=findViewById(R.id.btn1);
        latitude =findViewById(R.id.Latitude);
        longitude = findViewById(R.id.Longitude);
        latitude.setText(bundle.getString("latitude"));
        longitude.setText(bundle.getString("longitude"));
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //textView.setVal;
                latitude.setText(string);
                longitude.setText(string2);


            }
        });
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


//
//    private void changeDB(final int item) {
//        String contactNo = deliveryBoyItems.get(item).contactNo;
////        Query deleteDB = deliveryBoyReference.orderByChild("contactNo").equalTo(contactNo);
////        deleteDB.addListenerForSingleValueEvent(new ValueEventListener() {
////            @Override
////            public void onDataChange(DataSnapshot dataSnapshot) {
////                for(DataSnapshot d:dataSnapshot.getChildren()){
////                    DeliveryBoyReference deliveryBoyReference = d.getValue(DeliveryBoyReference.class);
////                        int a = Integer.parseInt(pno);
////                    if(deliveryBoyReference.cidList==null)
////                        deliveryBoyReference.cidList = new ArrayList<>();
////                    deliveryBoyReference.cidList.add(a);
////                    d.getRef().setValue(deliveryBoyReference);
////                }
////            }
////
////            @Override
////            public void onCancelled(DatabaseError databaseError) {
////
////            }
////        });
//        Query deleteDBa = customersReference.orderByChild("pk").equalTo(pk);
//        deleteDBa.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for(DataSnapshot d:dataSnapshot.getChildren()){
//                    CustomerData customerData = d.getValue(CustomerData.class);
//                    customerData.deliverBoyName = deliveryBoyItems.get(item).name;
//                    customerData.deliveryBoyContactNumber=deliveryBoyItems.get(item).contactNo;
//                    d.getRef().setValue(customerData);
//                    deliveryBoyNameTextView.setText("Delivery Boy: "+deliveryBoyItems.get(item).name);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//    }
//
//    private void fetchDeliveryBoy() {
//        final ProgressDialog pd = new ProgressDialog(this);
//        pd.setMessage("loading");
//        pd.setCanceledOnTouchOutside(false);
//        pd.show();
//        deliveryBoyItems = new ArrayList<>();
//        firebaseDatabase = FirebaseDatabaseReference.getDatabaseInstance();
//        deliveryBoyReference = firebaseDatabase.getReference("DELIVERYBOY");
//        customersReference = firebaseDatabase.getReference("CUSTOMERS");
//        deliveryBoyReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for(DataSnapshot d:dataSnapshot.getChildren()){
//                    DeliveryBoyReference deliveryBoyReference = d.getValue(DeliveryBoyReference.class);
//                    deliveryBoyItems.add(deliveryBoyReference);
//                }
//                pd.dismiss();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }
private void fetchDeliveryBoys() {
    deliveryBoyReference = firebaseDatabase.getReference("DELIVERYBOY");

    deliveryBoyReference.addChildEventListener(new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            DeliveryBoyReference deliveryBoyReference = dataSnapshot.getValue(DeliveryBoyReference.class);
            deliveryBoyItems.add(deliveryBoyReference);
            int x = deliveryBoyItems.size();
            deliveryBoysNamesItems.add(deliveryBoyItems.get(x-1).name);

            if(deliveryBoyItems.get(x-1).name.equals(bundle.getString("DBN")))
                deliveryBoysSpinner.setSelection(deliveryBoyItems.size()-1);
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
        editTextName.setText(bundle.getString("name"));
        editTextaddress = findViewById(R.id.addressEditText);
        editTextaddress.setText(bundle.getString("Address"));
        editTextContact = findViewById(R.id.contactNumberEditText);
        editTextContact.setText(bundle.getString("PhoneNo"));
        editTextQuantity = findViewById(R.id.QuantityEditText);
        editTextQuantity.setText(bundle.getString("dq"));
        isMorningClicked = findViewById(R.id.checkboxMorning);
        isMorningClicked.setChecked(bundle.getBoolean("isM"));
        isEveningClicked = findViewById(R.id.checkboxEvening);
        isEveningClicked.setChecked(bundle.getBoolean("isD"));

        register = findViewById(R.id.btn_signup);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(CustomerProfileActivity.this);
                builder.setTitle("confirm update");
                builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        registerCustomer();
                    }
                });
                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                         return;
                    }
                });
                builder.create().show();
            }
        });
    }

    private void registerCustomer() {
        CustomerData customerData = new CustomerData();
        customerData.customerName = editTextName.getText().toString();
        customerData.customerPhonenumber = editTextContact.getText().toString();
        customerData.customerAddress = editTextaddress.getText().toString();
        customerData.defaultQuantity = editTextQuantity.getText().toString();
        customerData.latitude = string;
        customerData.longitude = string2;
        customerData.area = area;
        if (isMorningClicked.isChecked())
            customerData.isMorning = true;
        if(isEveningClicked.isChecked())
            customerData.isEvening = true;
        customerData.deliverBoyName = delveryBoyName;
        customerData.deliveryBoyContactNumber = deliveryBoyContactNumber;
        customerData.latitude=string;
        customerData.longitude = string2;
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("updating...");
        progressDialog.show();

        customersReference.child(bundle.getString("pk")).child("customerName").setValue(customerData.customerName);
        customersReference.child(bundle.getString("pk")).child("customerPhonenumber").setValue(customerData.customerPhonenumber);
        customersReference.child(bundle.getString("pk")).child("customerAddress").setValue(customerData.customerAddress);
        customersReference.child(bundle.getString("pk")).child("defaultQuantity").setValue(customerData.defaultQuantity);
        customersReference.child(bundle.getString("pk")).child("area").setValue(customerData.area);
        customersReference.child(bundle.getString("pk")).child("isMorning").setValue(customerData.isMorning);
        customersReference.child(bundle.getString("pk")).child("isEvening").setValue(customerData.isEvening);
        customersReference.child(bundle.getString("pk")).child("deliverBoyName").setValue(customerData.deliverBoyName);
        customersReference.child(bundle.getString("pk")).child("deliveryBoyContactNumber").setValue(customerData.deliveryBoyContactNumber);
        customersReference.child(bundle.getString("pk")).child("latitude").setValue(customerData.latitude);
        customersReference.child(bundle.getString("pk")).child("longitude").setValue(customerData.longitude)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        Toast.makeText(CustomerProfileActivity.this,"Succesfully updated",Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CustomerProfileActivity.this,"error error",Toast.LENGTH_SHORT).show();
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
                if(ss.equals(bundle.getString("area")))
                    dropdown.setSelection(areaItems.size()-1);
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

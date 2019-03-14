package com.doodhbhandaar.dbadmin;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class FiltersActivity extends AppCompatActivity {

    ArrayList<String> areaItems;
    ArrayList<DeliveryBoyReference> deliveryBoyItems;
    ArrayList<String> deliveryBoysNamesItems;
    ArrayList<CustomerData> customerDataArrayList;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference areasReference;
    DatabaseReference deliveryBoyReference;
    DatabaseReference customersReference;
    Spinner dropdown,deliveryBoysSpinner;
    String area = "0";
    String deliveryBoyContactNumber="0";
    String delveryBoyName = "0";
    ArrayAdapter<String> adapter,adapterDeliveryBoy;
    CheckBox isMorningClicked;
    CheckBox isEveningClicked;
    CheckBox isFilterOnDeliveryBoy, isFilterOnArea;
    Button register;
    int radioFlag = 0;
    ProgressDialog pd;
    RecyclerView customerRecyclerView;
    CustomerAdapter customerAdapter;
    LinearLayout rootFiltersLayout;
    private RadioGroup radioGroup;
    private RadioButton highToLow,lowToHigh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);
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
        customerDataArrayList = new ArrayList<>();
        pd = new ProgressDialog(this);
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
        setRecyclerView();

    }

    private void setRecyclerView() {
        CustomersInterface customersInterface = new CustomersInterface() {
            @Override
            public void onViewClick(View view,int position) {
                //code to edit
                Intent intent = new Intent(FiltersActivity.this,CustomerProfileActivity.class);
                intent.putExtra("pk",customerDataArrayList.get(position).pk);
                intent.putExtra("name",customerDataArrayList.get(position).customerName);
                intent.putExtra("Address",customerDataArrayList.get(position).customerAddress);
                intent.putExtra("PhoneNo",customerDataArrayList.get(position).customerPhonenumber);
                intent.putExtra("DBN",customerDataArrayList.get(position).deliverBoyName);
                intent.putExtra("LATITIUDE",customerDataArrayList.get(position).latitude);
                intent.putExtra("LONGITUDE",customerDataArrayList.get(position).longitude);
                intent.putExtra("isM",customerDataArrayList.get(position).isMorning);
                intent.putExtra("isD",customerDataArrayList.get(position).isEvening);
                intent.putExtra("dq",customerDataArrayList.get(position).defaultQuantity);
                intent.putExtra("area",customerDataArrayList.get(position).area);
                startActivityForResult(intent,1);
            }

            @Override
            public void onLongClick(View view, final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FiltersActivity.this);
                final String name=customerDataArrayList.get(position).customerName;
                builder.setTitle("Delete");
                builder.setCancelable(true);
                builder.setMessage("Are you sure to Delete "+name);
                builder.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        customersReference.child(customerDataArrayList.get(position).pk).setValue(null);
//                        Toast.makeText(this,name+"is deleted",Toast.LENGTH_LONG).show();
                        customerAdapter.notifyDataSetChanged();
//                        updatingdata();
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
        };
        customerAdapter=new CustomerAdapter(this,customerDataArrayList,customersInterface);
        customerRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        customerRecyclerView.setAdapter(customerAdapter);
        customerAdapter.notifyDataSetChanged();
    }

    private void findViews() {
        rootFiltersLayout = findViewById(R.id.rootFiltersLayout);
        isMorningClicked = findViewById(R.id.checkboxMorning);
        isEveningClicked = findViewById(R.id.checkboxEvening);
        isFilterOnArea = findViewById(R.id.checkboxArea);
        isFilterOnDeliveryBoy = findViewById(R.id.checkboxDeliveryBoy);
        customerRecyclerView = findViewById(R.id.recyclerView);
        radioGroup = findViewById(R.id.radioGroup);
        highToLow = findViewById(R.id.highToLow);
        lowToHigh = findViewById(R.id.lowToHigh);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.highToLow){
                    radioFlag = 2;
                }else{
                    radioFlag = 3;
                }
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

    public void apply(View view) {
        fetchCustomers();
        rootFiltersLayout.setVisibility(View.GONE);
        pd.setMessage("Loading...");
        pd.setCanceledOnTouchOutside(false);
        pd.show();
    }

    private void fetchCustomers() {
        customersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    CustomerData data = ds.getValue(CustomerData.class);
                    refineData(data);
                }

                customerAdapter.notifyDataSetChanged();
                if(radioFlag!=0)
                    sortFilter();
                pd.dismiss();
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sortFilter() {
        if(radioFlag==2)
            Collections.sort(customerDataArrayList, new Comparator<CustomerData>() {
                @Override
                public int compare(CustomerData o1, CustomerData o2) {
                    return o2.defaultQuantity.compareTo(o1.defaultQuantity);
                }
            });
        else
            Collections.sort(customerDataArrayList, new Comparator<CustomerData>() {
                @Override
                public int compare(CustomerData o1, CustomerData o2) {
                    return o1.defaultQuantity.compareTo(o2.defaultQuantity);
                }
            });
    }

    private void refineData(CustomerData data) {
        if(isMorningClicked.isChecked()) {
            if (data.isMorning)
                refineCheckDeliveryBoy(data);
        }
        else if(isEveningClicked.isChecked()){
            if(data.isEvening)
                refineCheckDeliveryBoy(data);
        }
    }

    private void refineCheckDeliveryBoy(CustomerData data) {
        if(isFilterOnDeliveryBoy.isChecked()){
            if(data.deliveryBoyContactNumber.equals(deliveryBoyContactNumber))
                refineCheckArea(data);
        }else
            refineCheckArea(data);
    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.customer_search_menu,menu);
//
//        final SearchView search = (SearchView)menu.findItem(R.id.search).getActionView();
//        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                newText = newText.toLowerCase();
//                final ArrayList<CustomerData> newlist = new ArrayList<CustomerData>();
//                final String finalNewText = newText;
//                customersReference.addListenerForSingleValueEvent
//                        (new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot snapshot) {
//
//                                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
//
//                                    CustomerData fo = postSnapshot.getValue(CustomerData.class);
//
//                                    String name = fo.customerName.toLowerCase();
//
//                                    if (name.startsWith(finalNewText)) {
//                                        newlist.add(fo);
//                                    }
//                                }
//                                if(!newlist.isEmpty()) {
//                                    setFilter(newlist);
//                                }
//
//
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//                        });
//
//
//                return true;
//            }
//        });
//        return true;
//    }

    private void refineCheckArea(CustomerData data) {
        if(isFilterOnArea.isChecked()){
            if(data.area==null)
                return;
            if(data.area.equals(area))
                customerDataArrayList.add(data);
        }else
            customerDataArrayList.add(data);
    }


//    public void setFilter (ArrayList<CustomerData> newlist)
//    {
//        customerDataArrayList.clear();
//        customerDataArrayList.addAll(newlist);
//        customerAdapter.notifyDataSetChanged();
//
//    }
}
package com.doodhbhandaar.dbadmin;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class CustomersFragment extends Fragment {

    RecyclerView customerRecyclerView;
    ArrayList<CustomerData> customerDataArrayList;
    CustomerAdapter customerAdapter;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference customersReference;


    public CustomersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View output=inflater.inflate(R.layout.fragment_customers, container, false);
        setHasOptionsMenu(true);

        FloatingActionButton fab = output.findViewById(R.id.fabCustomerAdd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


//                for(int i=0;i<customerDataArrayList.size();i++){
//                    String key=customerDataArrayList.get(i).pk;
//                    customersReference.child(customerDataArrayList.get(i).pk).setValue(customerDataArrayList.get(i));
//
//                }


                addCustmoer();
            }
        });

        customerRecyclerView=output.findViewById(R.id.customer_recyclerview);
        customerRecyclerView.setVerticalScrollBarEnabled(true);
        updatingdata();
    return output;
    }

    private void updatingdata() {
        customerDataArrayList=new ArrayList<>();

        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("loading");
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        firebaseDatabase = FirebaseDatabaseReference.getDatabaseInstance();
        customersReference = firebaseDatabase.getReference("CUSTOMERS");
        customersReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                CustomerData customerData = dataSnapshot.getValue(CustomerData.class);
                customerDataArrayList.add(customerData);
                customerAdapter.notifyDataSetChanged();
                pd.dismiss();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                CustomerData customerData = dataSnapshot.getValue(CustomerData.class);
                String contactNo = customerData.customerPhonenumber;
                for(int i=0;i<customerDataArrayList.size();i++){
                    if(customerDataArrayList.get(i).customerPhonenumber==contactNo){
                        customerDataArrayList.get(i).deliverBoyName = customerData.deliverBoyName;
                        customerAdapter.notifyDataSetChanged();
                        break;
                    }
                }
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

        CustomersInterface customersInterface = new CustomersInterface() {
            @Override
            public void onViewClick(View view,int position) {
                //code to edit
                Intent intent = new Intent(getContext(),CustomerProfileActivity.class);
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
                intent.putExtra("latitude",customerDataArrayList.get(position).latitude);
                intent.putExtra("longitude",customerDataArrayList.get(position).longitude);
                startActivityForResult(intent,1);
            }

            @Override
            public void onLongClick(View view, final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                final String name=customerDataArrayList.get(position).customerName;
                builder.setTitle("Delete");
                builder.setCancelable(true);
                builder.setMessage("Are you sure to Delete "+name);
                builder.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        customersReference.child(customerDataArrayList.get(position).pk).setValue(null);
                        Toast.makeText(getContext(),name+"is deleted",Toast.LENGTH_LONG).show();
                        customerAdapter.notifyDataSetChanged();
                        updatingdata();
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

        customerAdapter=new CustomerAdapter(getContext(),customerDataArrayList,customersInterface);
        customerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        customerRecyclerView.setAdapter(customerAdapter);
        customerAdapter.notifyDataSetChanged();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode==1&&resultCode==RESULT_OK){
          updatingdata();
        }
    }

    private void addCustmoer() {
        Intent intent = new Intent(getContext(),AddCustomerActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(R.id.filter==item.getItemId()){
            Intent intent = new Intent(getContext(),FiltersActivity.class);
            startActivity(intent);
            return true;
        }
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.customer_search_menu,menu);

        final SearchView search = (SearchView)menu.findItem(R.id.search).getActionView();
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                newText = newText.toLowerCase();
                final ArrayList<CustomerData> newlist = new ArrayList<CustomerData>();
                final String finalNewText = newText;
                customersReference.addListenerForSingleValueEvent
                        (new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {

                                for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                                    CustomerData fo = postSnapshot.getValue(CustomerData.class);

                                    String name = fo.customerName.toLowerCase();

                                    if (name.startsWith(finalNewText)) {
                                        newlist.add(fo);
                                    }
                                }
                                if(!newlist.isEmpty()) {
                                    setFilter(newlist);
                                }


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                return true;
            }
        });
//        return true;
    }
    public void setFilter (ArrayList<CustomerData> newlist)
    {
        customerDataArrayList.clear();
        customerDataArrayList.addAll(newlist);
        customerAdapter.notifyDataSetChanged();

    }


}

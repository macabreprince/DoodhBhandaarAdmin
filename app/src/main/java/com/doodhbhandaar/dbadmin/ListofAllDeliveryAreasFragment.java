package com.doodhbhandaar.dbadmin;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import static com.google.firebase.auth.FirebaseAuth.getInstance;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListofAllDeliveryAreasFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<DeliveryBoyReference> deliveryBoyItems;
    ListOfAllDeliveryAreasAdapter adapter;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference deliveryBoyReference;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    String username;
    String password;

    public ListofAllDeliveryAreasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View output= inflater.inflate(R.layout.fragment_listof_all_delivery_areas, container, false);

        recyclerView = output.findViewById(R.id.list_of_all_delivery_area_recyclerview);
        deliveryBoyItems = new ArrayList<>();

        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("loading");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        firebaseStorage=FirebaseDatabaseReference.getStorageINSTANCE();
        storageReference=firebaseStorage.getReference("DeliveryBoyImage/");


        firebaseDatabase = FirebaseDatabaseReference.getDatabaseInstance();
        deliveryBoyReference = firebaseDatabase.getReference("DELIVERYBOY");

        deliveryBoyReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                DeliveryBoyReference deliveryBoyReference = dataSnapshot.getValue(DeliveryBoyReference.class);
                deliveryBoyItems.add(deliveryBoyReference);
                adapter.notifyDataSetChanged();
                if(pd.isShowing())
                    pd.dismiss();
                Log.i("failuree","fail0");
//                Toast.makeText(getContext(),"fail 0",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                Log.i("fail","fail1");

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                Log.i("fail","fail2");

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

//        for(int i=0;i<10;i++){
//            DeliveryBoyReference deliveryBoyReference = new DeliveryBoyReference();
//            deliveryBoyReference.address="address";
//            deliveryBoyReference.name="name";
//            deliveryBoyReference.contactNo="phonenumber11";
//            deliveryBoyItems.add(deliveryBoyReference);
//        }
        ListOfAllDeliveryBoyInterface deliveryBoyInterface = new ListOfAllDeliveryBoyInterface() {
            @Override
            public void onViewSingleClick(View view, int position) {
                DatabaseReference deliveryBoyReferenceForUnP = firebaseDatabase.getReference("DELIVERYBOYUnP");
                Query query = deliveryBoyReferenceForUnP.orderByChild("contactNo").equalTo(deliveryBoyItems.get(position).contactNo);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot d:dataSnapshot.getChildren()){
                            DeliveryBoyPasswordReference deliveryBoyPasswordReference = d.getValue(DeliveryBoyPasswordReference.class);
                            username = deliveryBoyPasswordReference.username;
                            password = deliveryBoyPasswordReference.password;
                        }
                        showDialogForUnP();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onViewLongClick(View view, final int position) {
                DeliveryBoyReference deliveryBoyReference= deliveryBoyItems.get(position);
                final String contactNo =deliveryBoyReference.contactNo;
                final String imgname=deliveryBoyReference.imageNameInDb;
                String name = deliveryBoyReference.name;
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setTitle("Delete");
                builder.setCancelable(true);
                builder.setMessage("Are you sure to Delete "+name);
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deletePicture(imgname);
                        deleteDeliveryBoy(contactNo,position);
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        };
        adapter = new ListOfAllDeliveryAreasAdapter(getContext(),deliveryBoyItems,deliveryBoyInterface);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        FloatingActionButton fab = output.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addDeliveryboy();
            }
        });
       /*

        */
        return output;
    }

    private void showDialogForUnP() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Username & Password");
        builder.setCancelable(true);
        builder.setMessage("Username: "+username+"\nPassword: "+password);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        username="";
        password="";
    }

    private void deletePicture(String imgname) {
        Log.i("image",imgname+" ");
        if(imgname==null)
            return;
        if(imgname.length()==0)
            return;
        StorageReference fileReference = storageReference.child(imgname);

// Delete the file
        fileReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getContext(),"picture is deleted",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getContext(),"picture is not deleted",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void deleteDeliveryBoy(String contactNo, final int position) {
        Query deleteDB = deliveryBoyReference.orderByChild("contactNo").equalTo(contactNo);
        deleteDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot d:dataSnapshot.getChildren()){
                    d.getRef().removeValue();
                }
                deliveryBoyItems.remove(position);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addDeliveryboy() {
//
//        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//        View dialogView = getLayoutInflater().inflate(R.layout.activity_register_delivery_man,null);
//        builder.setView(dialogView);
//
//        final EditText nameEditText = dialogView.findViewById(R.id.register_deliveryboy_name);
//        final EditText phoneEditText = dialogView.findViewById(R.id.register_deliveryboy_phonenumber);
//        final EditText addressEditText=dialogView.findViewById(R.id.register_deliveryboy_address);
//
//        builder.setTitle("Add Delivery Boy");
//        builder.setPositiveButton("register", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                String name = nameEditText.getText().toString();
//                String phone = phoneEditText.getText().toString();
//                String address = addressEditText.getText().toString();
//                DeliveryBoyReference deliveryBoyReferenceObject = new DeliveryBoyReference();
//                deliveryBoyReferenceObject.address = address;
//                deliveryBoyReferenceObject.name = name;
//                deliveryBoyReferenceObject.contactNo = phone;
//                deliveryBoyReferenceObject.latitude="77.2.4";
//                deliveryBoyReferenceObject.longitude = "564.35";
//                deliveryBoyReferenceObject.cidList = new ArrayList<>();
//                deliveryBoyReferenceObject.cidList.add(1);
//                deliveryBoyReferenceObject.cidList.add(2);
//                deliveryBoyReference.push().setValue(deliveryBoyReferenceObject);
//                adapter.notifyDataSetChanged();
//            }
//        });
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//
//            }
//        });
//        builder.create().show();

        Intent intent=new Intent(getContext(),RegisterDeliveryManActivity.class);
        startActivity(intent);


    }


}

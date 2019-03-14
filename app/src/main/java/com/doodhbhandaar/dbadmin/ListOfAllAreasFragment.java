package com.doodhbhandaar.dbadmin;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListOfAllAreasFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<String> areaItems;
    AreasAdapter adapter;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference areasReference;

    public ListOfAllAreasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_of_all_areas, container, false);

        FloatingActionButton fab = view.findViewById(R.id.fabAreasAdd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                addArea();
            }
        });
        recyclerView = view.findViewById(R.id.areas_recycler_view);
        areaItems = new ArrayList<>();
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("loading");
        pd.show();

        FirebaseApp.initializeApp(getContext());
        firebaseDatabase = FirebaseDatabaseReference.getDatabaseInstance();
        areasReference = firebaseDatabase.getReference("AREAS");
        areasReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String ss = dataSnapshot.getValue(String.class);
                areaItems.add(ss);
//                Toast.makeText(getContext(),s+ " +",Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();
                pd.dismiss();
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


        AreasInterface areasInterface = new AreasInterface() {
            @Override
            public void onViewClick(View view, final int position) {
                final String area = areaItems.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setTitle("Delete");
                builder.setCancelable(true);
                builder.setMessage("Are you sure to delete "+area);
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteArea(area,position);
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //code for negative button
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        };
        adapter = new AreasAdapter(getContext(),areaItems,areasInterface);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        return view;
    }

    private void deleteArea(String area, final int position) {
        Query deleteDB = areasReference.orderByValue().equalTo(area);
        deleteDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot d:dataSnapshot.getChildren()){
                    d.getRef().removeValue();
                }
                areaItems.remove(position);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addArea() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.add_area_dialog_layout,null);
        builder.setView(dialogView);
        final EditText areaEditText = dialogView.findViewById(R.id.area_add_edit_text);

        builder.setTitle("Add Area");
        builder.setCancelable(true);
        builder.setMessage("Enter Area");
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String newArea = "Panchawati ";
                newArea = areaEditText.getText().toString();
                areasReference.push().setValue(newArea);
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

}

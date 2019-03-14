package com.doodhbhandaar.dbadmin;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class AreasAdapter extends RecyclerView.Adapter<ViewHolderListOfAllAreas> {

    ArrayList<String> areasList;
    LayoutInflater inflater;
    AreasInterface areasInterface;

    public AreasAdapter(Context context, ArrayList<String> areas,AreasInterface areasInterface) {
        inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        areasList = areas;
        this.areasInterface = areasInterface;
    }

    @NonNull
    @Override
    public ViewHolderListOfAllAreas onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View output = inflater.inflate(R.layout.list_of_all_areas_view_holder,parent,false);
        return new ViewHolderListOfAllAreas(output);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderListOfAllAreas holder, final int position) {
        String s = areasList.get(position);
        holder.textView.setText(s);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                areasInterface.onViewClick(v,position);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return areasList.size();
    }
}

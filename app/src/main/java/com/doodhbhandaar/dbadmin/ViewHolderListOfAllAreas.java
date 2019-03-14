package com.doodhbhandaar.dbadmin;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class ViewHolderListOfAllAreas extends RecyclerView.ViewHolder {

    View itemView;
    TextView textView;
    public ViewHolderListOfAllAreas(View itemView) {
        super(itemView);
        this.itemView = itemView;
        textView = itemView.findViewById(R.id.areas_text_view);

    }
}

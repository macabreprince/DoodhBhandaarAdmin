package com.doodhbhandaar.dbadmin;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewHolderListOfAllDeliveryAreas extends RecyclerView.ViewHolder {
        View item;
        ImageView deliveryboyImage;
        TextView deliveryboyName;
        TextView deliveryboyAddress;
        TextView deliveryboyPhonenumber;

        public ViewHolderListOfAllDeliveryAreas(View itemView) {
            super(itemView);
            item=itemView;
            deliveryboyAddress=item.findViewById(R.id.deliveryboy_address);
            deliveryboyImage=item.findViewById(R.id.deliveryboy_image);
            deliveryboyName=item.findViewById(R.id.deliveryboy_name);
            deliveryboyPhonenumber=item.findViewById(R.id.deliveryboy_phonenumber);
        }
    }
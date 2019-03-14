package com.doodhbhandaar.dbadmin;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomerViewholder extends RecyclerView.ViewHolder {

    View item;
    ImageView customerImage;
    TextView customerName;
    TextView customerAddress;
    TextView customerPhonenumber;


    public CustomerViewholder(View itemView) {
        super(itemView);
        item=itemView;
        customerAddress=item.findViewById(R.id.customer_address);
        customerImage=item.findViewById(R.id.customer_image);
        customerName=item.findViewById(R.id.customer_name);
        customerPhonenumber=item.findViewById(R.id.customer_phonenumber);
    }
}
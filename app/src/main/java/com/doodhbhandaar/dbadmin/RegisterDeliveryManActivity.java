package com.doodhbhandaar.dbadmin;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Random;

public class RegisterDeliveryManActivity extends AppCompatActivity {
     private static int IMAGE_REQUEST_CODE=5;

    DatabaseReference deliveryBoyReference;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    ImageView imageView;
    Uri mimageUri;
    Button register;
     ProgressDialog pd;
     EditText nameEditText;
     EditText phoneEditText;
     EditText addressEditText;
     String username;
     String password;
    String name;
    String phone;
    String address;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_delivery_man);
        imageView=findViewById(R.id.register_deliveryboy_photo);
         nameEditText = findViewById(R.id.register_deliveryboy_name);
         phoneEditText = findViewById(R.id.register_deliveryboy_phonenumber);
         addressEditText = findViewById(R.id.register_deliveryboy_address);



        register=findViewById(R.id.register_deliveryboy);
        firebaseDatabase = FirebaseDatabaseReference.getDatabaseInstance();
        firebaseStorage=FirebaseDatabaseReference.getStorageINSTANCE();
        deliveryBoyReference = firebaseDatabase.getReference("DELIVERYBOY");
         storageReference=firebaseStorage.getReference("DeliveryBoyImage/");

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = nameEditText.getText().toString();
                phone = phoneEditText.getText().toString();
                address = addressEditText.getText().toString();
                makeUsernamePassword();
                if(name.length()==0||phone.length()==0||address.length()==0){
                    Toast.makeText(RegisterDeliveryManActivity.this,"Field are empty\n fill proper data",Toast.LENGTH_LONG).show();
                    return;

                }
                pd = new ProgressDialog(RegisterDeliveryManActivity.this);
                pd.setMessage("loading");
                pd.setCanceledOnTouchOutside(false);
                pd.show();
                uploadimage();
            }
        });


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileOpen();
            }
        });
    }

    private void makeUsernamePassword() {
        DatabaseReference deliveryBoyReferenceUnP;
        deliveryBoyReferenceUnP = firebaseDatabase.getReference("DELIVERYBOYUnP");
        String n=name;
        username="";
        password = "";
        for(int i=0;i<n.length()&&i<6;i++){
            int x = ((n.charAt(i)+1)%26)+65;
            char c = (char) x;
            username = username + c;
        }
        Random random = new Random(System.currentTimeMillis());
        long x = random.nextLong();
        if(x<0)
            x=-1*x;
        x%=10000000;
        String s = ""+x;
        username = username + s;
        random = new Random(System.currentTimeMillis());
        x = random.nextInt();
        if(x<0)
            x=-1*x;
        x%=100000000000L;
        s = ""+x;
        password = password + s;
        DeliveryBoyPasswordReference deliveryBoyPasswordReference = new DeliveryBoyPasswordReference();
        deliveryBoyPasswordReference.username = username;
        deliveryBoyPasswordReference.password = password;
        deliveryBoyPasswordReference.contactNo = phone;
        deliveryBoyReferenceUnP.push().setValue(deliveryBoyPasswordReference);
        Log.d("zzz",username+" "+password);
    }

    private void uploadimage() {
        if(mimageUri!=null){
            final String imageNameinDb=System.currentTimeMillis()+"."+getFIleExtension(mimageUri);
            StorageReference fileReference=storageReference.child(imageNameinDb);
            fileReference.putFile(mimageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            addDeliveryboy(taskSnapshot.getDownloadUrl().toString(),imageNameinDb);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegisterDeliveryManActivity.this,"image Upload fail\n Check InternetConnection",Toast.LENGTH_LONG).show();

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        }
                    });
        }

        else{
            Toast.makeText(this,"Image is not chossen",Toast.LENGTH_LONG).show();
            addDeliveryboy();
        }

    }

    private void fileOpen() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       Log.i("result",""+resultCode);
        if(requestCode==IMAGE_REQUEST_CODE&&resultCode==RESULT_OK){
            mimageUri=data.getData();
            Picasso.get().load(mimageUri).into(imageView);
        }
    }

    private String getFIleExtension(Uri uri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void addDeliveryboy(String imageurl,String imagename) {

        DeliveryBoyReference deliveryBoyReferenceObject = new DeliveryBoyReference();
        deliveryBoyReferenceObject.address = address;
        deliveryBoyReferenceObject.imageNameInDb=imagename;
        deliveryBoyReferenceObject.name = name;
        deliveryBoyReferenceObject.contactNo = phone;
        deliveryBoyReferenceObject.latitude = "77.2.4";
        deliveryBoyReferenceObject.longitude = "564.35";
        deliveryBoyReferenceObject.cidList = new ArrayList<>();
        deliveryBoyReferenceObject.cidList.add(1);
        deliveryBoyReferenceObject.cidList.add(2);
        deliveryBoyReferenceObject.photoUrl=imageurl;
        deliveryBoyReference.push().setValue(deliveryBoyReferenceObject);
        pd.dismiss();
        finish();
    }
    private void addDeliveryboy() {

        DeliveryBoyReference deliveryBoyReferenceObject = new DeliveryBoyReference();
        deliveryBoyReferenceObject.address = address;
        deliveryBoyReferenceObject.name = name;
        deliveryBoyReferenceObject.contactNo = phone;
        deliveryBoyReferenceObject.latitude = "77.2.4";
        deliveryBoyReferenceObject.longitude = "564.35";
        deliveryBoyReferenceObject.cidList = new ArrayList<>();
        deliveryBoyReferenceObject.cidList.add(1);
        deliveryBoyReferenceObject.cidList.add(2);
        deliveryBoyReference.push().setValue(deliveryBoyReferenceObject);
        pd.dismiss();
        finish();
    }
}
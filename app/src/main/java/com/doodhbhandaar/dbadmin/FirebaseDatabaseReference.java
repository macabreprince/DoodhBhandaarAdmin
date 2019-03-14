package com.doodhbhandaar.dbadmin;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public class FirebaseDatabaseReference {
    private static FirebaseDatabase INSTANCE;
    private static FirebaseStorage storageINSTANCE;

    public static FirebaseDatabase getDatabaseInstance() {

        if (INSTANCE == null) {
            INSTANCE = FirebaseDatabase.getInstance();
            INSTANCE.setPersistenceEnabled(true);
        }
        return INSTANCE;
    }


    public static FirebaseStorage getStorageINSTANCE(){
        if(storageINSTANCE==null){
            storageINSTANCE=FirebaseStorage.getInstance();

        }
        return storageINSTANCE;
    }
}

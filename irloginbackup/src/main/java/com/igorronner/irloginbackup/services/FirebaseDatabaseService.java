package com.igorronner.irloginbackup.services;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.igorronner.irloginbackup.models.FirebaseBackup;
import com.igorronner.irloginbackup.models.FirebaseUser;
import com.igorronner.irloginbackup.preferences.FirebasePreference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by IgorR on 30/07/2017.
 */

public class FirebaseDatabaseService {

    private Activity context;
    private static FirebaseDatabaseService instance;
    public FirebaseDatabase database;
    private static final String TAG = "FirebaseAuthService";
    private ServiceListener serviceListener;

    public Activity getContext() {
        return context;
    }

    public void setContext(Activity context) {
        this.context = context;
    }


    public interface ServiceListener<T> {
        void onComplete(T result);
    }

    public FirebaseDatabaseService.ServiceListener getServiceListener() {
        return serviceListener;
    }

    public FirebaseDatabaseService setServiceListener(FirebaseDatabaseService.ServiceListener serviceListener) {
        this.serviceListener = serviceListener;
        return this;
    }

    public FirebaseDatabaseService() {

    }

    public static FirebaseDatabaseService getInstance(Activity activity) {
        if (instance == null)
            instance = new FirebaseDatabaseService();

        instance.setContext(activity);
        instance.database = FirebaseDatabase.getInstance();
        return instance;
    }

    public void getBackups(final FirebaseDatabaseService.ServiceListener<List<FirebaseBackup>> serviceListener){
        database.getReference()
                .child("users-backup")
                .child(FirebasePreference.getUserId(context))
                .orderByChild("created_at")
                .limitToLast(10)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount()==0){
                            serviceListener.onComplete(null);
                            return;
                        }

                        List<FirebaseBackup> list = new ArrayList<>();
                        FirebaseBackup firebaseBackup = null;
                        for (DataSnapshot backupData : dataSnapshot.getChildren()){
                            firebaseBackup = backupData.getValue(FirebaseBackup.class);
                            firebaseBackup.setUuid(backupData.getKey());
                            list.add(firebaseBackup);
                        }
                        serviceListener.onComplete(list);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        serviceListener.onComplete(null);
                    }
                });
    }
    public void getBackup(final FirebaseDatabaseService.ServiceListener<FirebaseBackup> serviceListener){
        database.getReference()
                .child("users-backup")
                .child(FirebasePreference.getUserId(context))
                .orderByChild("created_at")
                .limitToLast(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount()==0){
                            serviceListener.onComplete(null);
                            return;
                        }

                        FirebaseBackup firebaseBackup = null;
                        for (DataSnapshot backupData : dataSnapshot.getChildren()){
                            firebaseBackup = backupData.getValue(FirebaseBackup.class);
                            firebaseBackup.setUuid(backupData.getKey());
                        }
                        serviceListener.onComplete(firebaseBackup);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        serviceListener.onComplete(null);
                    }
                });
    }

    public void getBackup(String backupId, final FirebaseDatabaseService.ServiceListener<FirebaseBackup> serviceListener){
        database.getReference()
                .child("users-backup")
                .child(FirebasePreference.getUserId(context))
                .child(backupId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount()==0){
                            serviceListener.onComplete(null);
                            return;
                        }

                        FirebaseBackup firebaseBackup = null;
                        for (DataSnapshot backupData : dataSnapshot.getChildren()){
                            firebaseBackup = backupData.getValue(FirebaseBackup.class);
                            firebaseBackup.setUuid(backupData.getKey());
                        }
                        serviceListener.onComplete(firebaseBackup);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        serviceListener.onComplete(null);
                    }
                });
    }

    public void saveUser(final FirebaseUser user, final FirebaseDatabaseService.ServiceListener<FirebaseUser> serviceListener){

        database.getReference()
                .child("users")
                .child(user.getUuid())
                .setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                           serviceListener.onComplete(user);
                        }
                    }
                });
    }

    public void insertBackup(String uuid, String url){

        FirebaseBackup firebaseBackup = new FirebaseBackup();
        firebaseBackup.setCreated_at(Calendar.getInstance().getTimeInMillis());
        firebaseBackup.setFile_path(url);
        FirebaseDatabase
                .getInstance()
                .getReference()
                .child("users-backup")
                .child(FirebasePreference.getUserId(context))
                .child(uuid)
                .setValue(firebaseBackup)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (serviceListener !=null)
                            serviceListener.onComplete(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (serviceListener !=null)
                            serviceListener.onComplete(false);
                    }
                });
    }
}

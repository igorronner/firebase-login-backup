package com.igorronner.irloginbackup.services;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.igorronner.irloginbackup.models.FirebaseUser;
import com.igorronner.irloginbackup.preferences.MainPreference;
import com.igorronner.irloginbackup.views.SignInActivity;

public class FirebaseAuthService {

    private Activity context;
    private static FirebaseAuthService instance;
    public FirebaseAuth mAuth;
    private static final String TAG = "FirebaseAuthService";

    public Activity getContext() {
        return context;
    }

    public void setContext(Activity context) {
        this.context = context;
    }


    public interface ServiceListener {
        void onError(String error);
        void onAuthComplete();
    }


    public FirebaseAuthService() {

    }

    public static FirebaseAuthService getInstance(Activity activity) {
        if (instance == null)
            instance = new FirebaseAuthService();

        instance.setContext(activity);
        instance.mAuth = FirebaseAuth.getInstance();
        return instance;
    }

    public void register(final FirebaseUser user, final ServiceListener serviceListener){

        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        serviceListener.onError(e.getLocalizedMessage());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        user.setUuid(authResult.getUser().getUid());
                        FirebaseDatabaseService.getInstance(context).saveUser(user,
                                new FirebaseDatabaseService.ServiceListener<FirebaseUser>() {
                                    @Override
                                    public void onComplete(FirebaseUser result){
                                        signWithEmailAndPassword(user.getEmail(), user.getPassword(), serviceListener);
                                    }
                                });
                    }
                });
    }

    public void signWithEmailAndPassword(String email, String password, final ServiceListener serviceListener){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            serviceListener.onError(task.getException().getMessage());
                            return;
                        }
                        MainPreference.setUuid(context, task.getResult().getUser().getUid());
                        serviceListener.onAuthComplete();

                    }
                });
    }

    public void logout(boolean keepCurrentActivity){
        mAuth.signOut();
        if (!keepCurrentActivity)
            context.startActivity(new Intent(context, SignInActivity.class));
    }

}
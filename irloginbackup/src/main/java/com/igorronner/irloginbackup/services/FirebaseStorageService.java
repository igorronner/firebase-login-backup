package com.igorronner.irloginbackup.services;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.igorronner.irloginbackup.R;
import com.igorronner.irloginbackup.init.ConfigUtil;
import com.igorronner.irloginbackup.preferences.FirebasePreference;
import com.igorronner.irloginbackup.utils.BackupAndRestore;

import java.io.File;
import java.util.UUID;

public class FirebaseStorageService extends NotificationCompat {

    private Activity context;
    private NotificationManager mNotificationManager;
    public static int uploadId = 1;
    private static FirebaseStorageService instance;
    private Builder builder;

    private UploadServiceListener uploadServiceListener;

    public interface UploadServiceListener {
        void onUploadComplete();
        void onError();
    }

    private DownloadServiceListener downloadServiceListener;

    public FirebaseStorageService setUploadServiceListener(UploadServiceListener uploadServiceListener) {
        this.uploadServiceListener = uploadServiceListener;
        return this;
    }

    public interface DownloadServiceListener<T> {
        void onDownloadComplete(T object);
    }


    public FirebaseStorageService() {

    }

    public static FirebaseStorageService getInstance(Activity activity) {
        if (instance == null)
            instance = new FirebaseStorageService();


        instance.context = activity;

        instance.mNotificationManager =
                (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        instance.builder = new Builder(activity);
        return instance;
    }
    
    public void downloadBackupFile(String dir, final DownloadServiceListener<File> downloadServiceListener){

        try {
            File sd = Environment.getExternalStorageDirectory();
            String backupDBPath = String.format("%s.bak", ConfigUtil.DATABASE_NAME);
            final File backupDB = new File(sd, backupDBPath);
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            storageRef.child(dir).getFile(backupDB).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    downloadServiceListener.onDownloadComplete(backupDB);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    downloadServiceListener.onDownloadComplete(null);
                }
            });
        } catch (Exception exception){

        }
    }


    public void uploadBackupWithNotification(){

        if (FirebasePreference.getInstance().getUserId(context) == null)
            return;

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                File file = null;
                try {
                    file = BackupAndRestore.exportDB(context);
                }catch (Exception e){
                    if (uploadServiceListener!=null)
                        uploadServiceListener.onError();


                    return null;
                }
                if (file != null) {
                    try {

                        builder.setContentTitle(context.getString(R.string.progress_backup))
                                .setContentText(file.getName())
                                .setSmallIcon(R.drawable.cloud_upload);
                        final String uuid = UUID.randomUUID().toString();
                        final String dir = FirebasePreference.getInstance().getUserId(context) + File.separator + "backups" + File.separator + uuid + File.separator + file.getName();
                        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

                        StorageReference storageReference = storageRef.child(dir);
                        try {
                            final UploadTask uploadTask = storageReference.putFile(Uri.fromFile(file));
                            // Register observers to listen for when the download is done or if it fails
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    mNotificationManager.cancel(uploadId);
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    try {
                                        builder.setProgress(0, 0, false)
                                                .setContentTitle(context.getString(R.string.backup_success))
                                                .setSmallIcon(R.drawable.cloud_check);
                                        mNotificationManager.notify(uploadId, builder.build());
                                    } catch (Exception exception) {
                                        builder.setProgress(0, 0, false)
                                                .setContentTitle(context.getString(R.string.backup_success))
                                                .setSmallIcon(R.drawable.cloud_check);
                                        mNotificationManager.notify(uploadId, builder.build());

                                    }

                                    FirebaseDatabaseService
                                            .getInstance(context)
                                            .setServiceListener(new FirebaseDatabaseService.ServiceListener() {
                                                @Override
                                                public void onComplete(Object result) {
                                                    if (uploadServiceListener!=null)
                                                        uploadServiceListener.onUploadComplete();
                                                }
                                            })
                                            .insertBackup(uuid, dir);

                                }
                            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                    builder.setProgress(100, (int) progress, false);
                                    mNotificationManager.notify(uploadId, builder.build());

                                }
                            }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                                    taskSnapshot.getUploadSessionUri();
                                }
                            });
                        } catch (Exception exception) {
                            mNotificationManager.cancel(uploadId);
                            if (uploadServiceListener!=null)
                                uploadServiceListener.onError();

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        if (uploadServiceListener!=null)
                            uploadServiceListener.onError();
                    }
                } else {
                    if (uploadServiceListener!=null)
                        uploadServiceListener.onError();
                }

                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    public void uploadBackup(){

        File file = null;
        try {
            file = BackupAndRestore.exportDB(context);
        }catch (Exception e){
            e.printStackTrace();
        }
        if (file == null)
            return;

        try {

            final String uuid = UUID.randomUUID().toString();
            final String dir = FirebasePreference.getInstance().getUserId(context) + File.separator + "backups" + File.separator + uuid + File.separator + file.getName();
            StorageReference storageRef =FirebaseStorage.getInstance().getReference();
            StorageReference storageReference = storageRef.child(dir);

            try {
                UploadTask uploadTask = storageReference.putFile(Uri.fromFile(file));
                // Register observers to listen for when the download is done or if it fails
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        FirebaseDatabaseService
                                .getInstance(context)
                                .setServiceListener(new FirebaseDatabaseService.ServiceListener() {
                                    @Override
                                    public void onComplete(Object result) {
                                        if (uploadServiceListener!=null)
                                            uploadServiceListener.onUploadComplete();
                                    }
                                })
                                .insertBackup(uuid, dir);

                    }
                });
            } catch (Exception exception) {
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
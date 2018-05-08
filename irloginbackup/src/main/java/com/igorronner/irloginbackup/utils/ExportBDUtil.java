package com.igorronner.irloginbackup.utils;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.igorronner.irloginbackup.init.ConfigUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class ExportBDUtil {

    public static void importDB(Context context) {

        try {
            File sd = Environment.getExternalStorageDirectory();
            File data  = Environment.getDataDirectory();

            if (sd.canWrite()) {

                String  currentDBPath= "//data//" + context.getPackageName()
                        + "//databases//" +  ConfigUtil.DATABASE_NAME;
                String backupDBPath  = "/BackupFolder/" + ConfigUtil.DATABASE_NAME;
                File  backupDB= new File(data, currentDBPath);
                File currentDB  = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(context, backupDB.toString(),
                        Toast.LENGTH_LONG).show();

            }
        } catch (Exception e) {

            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG)
                    .show();

        }
    }
    //exporting database
    public static File exportDB(Context context) {

        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String  currentDBPath= "//data//" + context.getPackageName()
                        + "//databases//" +  ConfigUtil.DATABASE_NAME;
                String backupDBPath  = "/BackupFolder/" + ConfigUtil.DATABASE_NAME;
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(context, backupDB.toString(),
                        Toast.LENGTH_LONG).show();
                return backupDB;
            }
        } catch (Exception e) {

            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG)
                    .show();

        }

        return null;
    }

}
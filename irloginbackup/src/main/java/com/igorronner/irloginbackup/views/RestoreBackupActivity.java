package com.igorronner.irloginbackup.views;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.igorronner.irloginbackup.R;
import com.igorronner.irloginbackup.adapters.BackupRecycleAdapter;
import com.igorronner.irloginbackup.init.ConfigUtil;
import com.igorronner.irloginbackup.models.FirebaseBackup;
import com.igorronner.irloginbackup.services.FirebaseDatabaseService;
import com.igorronner.irloginbackup.services.FirebaseStorageService;
import com.igorronner.irloginbackup.utils.BackupAndRestore;
import com.igorronner.irloginbackup.utils.ConnectionUtil;

import java.io.File;
import java.util.List;

public class RestoreBackupActivity extends BaseActivity implements BackupRecycleAdapter.OnItemClickListener{

    private RecyclerView recyclerView;
    private BackupRecycleAdapter recycleAdapter;
    private List<FirebaseBackup> list;
    private ProgressBar progressBar;
    private LinearLayout noDataLayout;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restore_backup);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(ContextCompat.getColor(this, ConfigUtil.COLOR_PRIMARY));
        setActionBar((Toolbar) findViewById(R.id.toolbar));
        setStatusBarColor(ConfigUtil.COLOR_PRIMARY_DARK);

        recyclerView = (RecyclerView) findViewById(R.id.recycleView);
        progressBar = findViewById(R.id.progressBar);
        noDataLayout = findViewById(R.id.noData);

        if (!ConnectionUtil.isConnected(this)){
            Toast.makeText(this, R.string.need_internet, Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseDatabaseService
                .getInstance(this)
                .getBackups(new FirebaseDatabaseService.ServiceListener<List<FirebaseBackup>>() {
                    @Override
                    public void onComplete(List<FirebaseBackup> result) {

                        progressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        if (result==null) {
                            noDataLayout.setVisibility(View.VISIBLE);
                            return;
                        } else
                            noDataLayout.setVisibility(View.GONE);
                        list = result;
                        recycleAdapter = new BackupRecycleAdapter(list);
                        final LinearLayoutManager layoutManager
                                = new LinearLayoutManager(RestoreBackupActivity.this, LinearLayoutManager.VERTICAL, false);
                        layoutManager.scrollToPosition(0);
                        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                                layoutManager.getOrientation());
                        recyclerView.addItemDecoration(dividerItemDecoration);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setAdapter(recycleAdapter);
                        recycleAdapter.setOnItemClickListener(RestoreBackupActivity.this);
                    }
                });

    }

    @Override
    public void onItemClick(FirebaseBackup item) {
        if (!ConnectionUtil.isConnected(this)){
            Toast.makeText(this, R.string.need_internet, Toast.LENGTH_SHORT).show();
            return;
        }

        if(!requestWriteExternalStoragePermission()) {
            showProgressDialog(R.string.loading);
            FirebaseStorageService
                    .getInstance(this)
                    .downloadBackupFile(item.getFile_path(), new FirebaseStorageService.DownloadServiceListener<File>() {
                        @Override
                        public void onDownloadComplete(File file) {
                            BackupAndRestore.importDB(RestoreBackupActivity.this);
                            hideProgressDialog();
                            Toast.makeText(RestoreBackupActivity.this, R.string.restore_success, Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}

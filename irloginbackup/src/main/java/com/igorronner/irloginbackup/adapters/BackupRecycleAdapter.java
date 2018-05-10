package com.igorronner.irloginbackup.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.igorronner.irloginbackup.R;
import com.igorronner.irloginbackup.models.FirebaseBackup;
import com.igorronner.irloginbackup.utils.DateUtils;

import java.util.List;

public class BackupRecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private BackupRecycleAdapter.OnItemClickListener onItemClickListener;
    private List<FirebaseBackup> list;

    public interface OnItemClickListener{
        void onItemClick(FirebaseBackup item);
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public LinearLayout layout;

        public ViewHolder(View convertView) {
            super(convertView);

            title = (TextView) convertView.findViewById(R.id.name);
            layout = (LinearLayout) convertView.findViewById(R.id.layout);

        }
    }

    public BackupRecycleAdapter(List<FirebaseBackup> list) {
        this.list = list;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.backup_item, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {

        BackupRecycleAdapter.ViewHolder itemViewHolder = (BackupRecycleAdapter.ViewHolder) viewHolder;
        final FirebaseBackup firebaseBackup = list.get(position);

        itemViewHolder.title.setText(DateUtils.GetDateDDMMAAAAHHmm(firebaseBackup.getCreated_at(), "/"));

        itemViewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null)
                    onItemClickListener.onItemClick(firebaseBackup);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
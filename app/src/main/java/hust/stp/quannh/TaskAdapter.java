package hust.stp.quannh;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by sev_user on 1/20/2017.
 */
public class TaskAdapter extends ArrayAdapter<Task> {
    private Context mContext;
    private int mLayoutResourceId;
    private ArrayList<Task> mData;
    private TaskDbHelper mDbHelper;

    public TaskAdapter(Context context, int layoutResourceId, ArrayList<Task> data) {
        super(context, layoutResourceId, data);
        this.mLayoutResourceId = layoutResourceId;
        this.mContext = context;
        this.mData = data;
        this.mDbHelper = new TaskDbHelper(context);
    }

    @Override
    public View getView(final int position, View row, ViewGroup parent) {
        TaskHolder holder = null;

        if(row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(mLayoutResourceId, parent, false);

            row.setFocusable(false);
            row.setClickable(false);

            CheckBox tmp = (CheckBox) row.findViewById(R.id.cbTaskStatus);
            tmp.setFocusable(false);

            holder = new TaskHolder();
            holder.cbStatus = (CheckBox) row.findViewById(R.id.cbTaskStatus);
            holder.tvName = (TextView) row.findViewById(R.id.tvName);
            holder.tvNotes = (TextView) row.findViewById(R.id.tvNotes);
            holder.tvDate = (TextView) row.findViewById(R.id.tvDate);

            row.setTag(holder);
        } else {
            holder = (TaskHolder) row.getTag();
        }

        Task task = mData.get(position);
        holder.tvName.setText(task.getName());
        holder.tvNotes.setText(task.getNotes());

        holder.cbStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mData.get(position).setIsChecked(isChecked);
                //mDbHelper.updateAllTaskStatus(mData);
                mDbHelper.updateTaskStatus(mData.get(position));
            }
        });

        holder.cbStatus.setChecked(task.getIsChecked());

        holder.tvDate.setText(task.getDate()+" "+task.getTime());

        return row;
    }

    public Task getItem(int position) {
        return mData.get(position);
    }

    static class TaskHolder {
        CheckBox cbStatus;
        TextView tvName;
        TextView tvNotes;
        TextView tvDate;
    }

    @Override
    public int getCount() {
        return mData.size();
    }
}

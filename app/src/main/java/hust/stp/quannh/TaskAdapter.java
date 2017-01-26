package hust.stp.quannh;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
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
        final TaskHolder holder;

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
                if(isChecked){
                    holder.tvName.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.tvNotes.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.tvDate.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    holder.tvName.setPaintFlags(holder.tvName.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                    holder.tvNotes.setPaintFlags(holder.tvNotes.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                    holder.tvDate.setPaintFlags(holder.tvDate.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                }
            }
        });

        holder.cbStatus.setChecked(task.getIsChecked());

        holder.tvDate.setText(task.getDate()+" "+task.getTime());

        if(task.getIsChecked()){
            holder.tvName.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvNotes.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvDate.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        }

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

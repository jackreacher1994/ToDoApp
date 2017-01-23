package hust.stp.quannh;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by sev_user on 1/20/2017.
 */
public class NewTaskActivity extends AppCompatActivity {
    private EditText mEtName;
    private EditText mEtNotes;
    private TextView mTvDateDisplay;
    private TextView mTvTimeDisplay;

    private String mTaskName;
    private String mTaskNotes;
    private String mTaskDate;
    private String mTaskTime;
    private TextView mTvErrorMsg;

    private Button mBtnDate;
    private Button mBtnTime;

    private Calendar mCal;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_task_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.task_edit_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mEtName = (EditText) findViewById(R.id.etName);
        mEtNotes = (EditText) findViewById(R.id.etNotes);
        mTvDateDisplay = (TextView) findViewById(R.id.tvDateDisplay);
        mTvTimeDisplay = (TextView) findViewById(R.id.tvTimeDisplay);
        mTvErrorMsg = (TextView) findViewById(R.id.tvErrorMsg);
        mBtnDate = (Button) findViewById(R.id.btnDate);
        mBtnTime = (Button) findViewById(R.id.btnTime);

        getDefaultInfor();

        registerListeners();
        loadData();

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void loadData() {
        if(getIntent() != null) {
            Bundle extras = getIntent().getExtras();
            if(extras != null) {
                mTaskName = extras.getString("existingName");
                mTaskNotes = extras.getString("existingNotes");
                mTaskDate = extras.getString("existingDate");
                mTaskTime = extras.getString("existingTime");

                mEtName.setText(mTaskName);
                mEtNotes.setText(mTaskNotes);
                mTvDateDisplay.setText(mTaskDate);
                mTvTimeDisplay.setText(mTaskTime);
            }
        }
    }

    private void registerListeners() {
        mBtnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });
        mBtnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog();
            }
        });
    }

    public void onDone() {
        mTaskName = mEtName.getText().toString();
        mTaskName = mTaskName.trim();
        mTaskNotes = mEtNotes.getText().toString();
        mTaskNotes = mTaskNotes.trim();

        if(mTaskName.isEmpty() || mTaskNotes.isEmpty()) {
            mTvErrorMsg.setText(R.string.taskNameNotOk);
            mTvErrorMsg.setTextColor(Color.RED);
        } else {
            mTaskDate = mTvDateDisplay.getText().toString();
            mTaskTime = mTvTimeDisplay.getText().toString();

            Intent resultIntent = new Intent();
            resultIntent.putExtra("taskName", mTaskName);
            resultIntent.putExtra("taskNotes", mTaskNotes);
            resultIntent.putExtra("taskDate", mTaskDate);
            resultIntent.putExtra("taskTime", mTaskTime);

            String mTaskDateTime = mTaskDate + " " + mTaskTime;
            startAlert(mTaskName, mTaskDateTime);

            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }
    }

    public void onCancel() {
        finish();
    }

    public void getDefaultInfor() {
        mCal = Calendar.getInstance();
        SimpleDateFormat dft = null;
        dft = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String strDate = dft.format(mCal.getTime());
        mTvDateDisplay.setText(strDate);
        dft = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String strTime = dft.format(mCal.getTime());
        mTvTimeDisplay.setText(strTime);
    }

    public void showDatePickerDialog() {
        DatePickerDialog.OnDateSetListener callback = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year,
                                  int monthOfYear,
                                  int dayOfMonth) {
                mTvDateDisplay.setText((dayOfMonth) +"/"+(monthOfYear+1)+"/"+year);
                mCal.set(year, monthOfYear, dayOfMonth);
            }
        };

        String s = mTvDateDisplay.getText() + "";
        String strArrtmp[] = s.split("/");
        int day = Integer.parseInt(strArrtmp[0]);
        int month = Integer.parseInt(strArrtmp[1]) - 1;
        int year = Integer.parseInt(strArrtmp[2]);
        DatePickerDialog pic = new DatePickerDialog(
                this,
                callback, year, month, day);
        pic.setTitle(R.string.btnDate_add);
        pic.show();
    }

    public void showTimePickerDialog() {
        TimePickerDialog.OnTimeSetListener callback = new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                //String s = hourOfDay + ":" + minute;
                mTvTimeDisplay.setText(String.format("%02d:%02d", hourOfDay, minute));
                mCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                mCal.set(Calendar.MINUTE, minute);
            }
        };

        String s = mTvTimeDisplay.getText() + "";
        String strArr[] = s.split(":");
        int hour = Integer.parseInt(strArr[0]);
        int minute = Integer.parseInt(strArr[1]);
        TimePickerDialog time = new TimePickerDialog(this, callback, hour, minute, true);
        time.setTitle(R.string.btnTime_add);
        time.show();
    }

    public void startAlert(String taskName, String taskDateTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        Date taskDate = new Date();
        try {
            taskDate = dateFormat.parse(taskDateTime);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(taskDate);
        //Toast.makeText(this, "time: " + calendar.getTimeInMillis(), Toast.LENGTH_LONG).show();

        Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
        intent.putExtra("taskName", taskName);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), 234324243, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
        {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            //Toast.makeText(getBaseContext(), "call alarmManager.set()", Toast.LENGTH_LONG).show();
        }else{
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            //Toast.makeText(getBaseContext(), "call alarmManager.setExact()", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.edit_task_activity_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_done:
                onDone();
                break;
            case R.id.menu_cancel:
                onCancel();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}

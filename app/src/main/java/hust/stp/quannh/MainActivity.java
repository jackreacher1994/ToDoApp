package hust.stp.quannh;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by sev_user on 1/20/2017.
 */
public class MainActivity extends AppCompatActivity {
    static final int NEW_TASK_CODE = 1;
    static final int EDIT_TASK_CODE = 2;

    final static String DATE_TIME_FORMAT = "yyyy-MM-dd kk:mm:ss";

    private ArrayList<Task> mTaskData;
    private TaskAdapter mAdapter;
    private ListView mListView;
    private TaskDbHelper mDbHelper;
    private Task mSelectedTask;

    private MenuItem mSearchMenuItem;
    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mDbHelper = new TaskDbHelper(this);
        mTaskData = mDbHelper.readAllTasks();

        mListView = (ListView) findViewById(R.id.lvTasks);

        mAdapter = new TaskAdapter(this, R.layout.task_row_layout, mTaskData);
        mAdapter.setNotifyOnChange(true);
        mListView.setAdapter(mAdapter);
        mListView.setTextFilterEnabled(true);

        setupListListeners();
    }

    private void setupListListeners() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapter, View view, int pos, long id) {
                mSelectedTask = (Task) adapter.getItemAtPosition(pos);

                PopupMenu popup = new PopupMenu(MainActivity.this, view);
                popup.getMenuInflater().inflate(R.menu.selected_task_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch(item.getItemId()) {
                            case R.id.popup_menu_edit:
                                Intent intent = new Intent(MainActivity.this, NewTaskActivity.class);
                                intent.putExtra("existingName", mSelectedTask.getName());
                                intent.putExtra("existingNotes", mSelectedTask.getNotes());
                                intent.putExtra("existingDate", mSelectedTask.getDate());
                                intent.putExtra("existingTime", mSelectedTask.getTime());

                                MainActivity.this.startActivityForResult(intent, EDIT_TASK_CODE);
                                return true;

                            case R.id.popup_menu_delete:
                                new DeleteExistingTask(mSelectedTask).execute();
                                return true;
                        }

                        return true;
                    }
                });

                popup.show();

                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);

        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchMenuItem = menu.findItem(R.id.menu_main_search);
        mSearchView = (SearchView) mSearchMenuItem.getActionView();
        mSearchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        //mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String searchQuery) {
                mTaskData.clear();
                mTaskData.addAll(mDbHelper.searchTask(searchQuery.trim()));
                mAdapter.notifyDataSetChanged();
                return true;
            }
        });
        MenuItemCompat.setOnActionExpandListener(mSearchMenuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                setItemsVisibility(menu, mSearchMenuItem, false);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                setItemsVisibility(menu, mSearchMenuItem, true);
                return true;
            }
        });

        return true;
    }

    private void setItemsVisibility(Menu menu, MenuItem exception, boolean visible) {
        for (int i=0; i<menu.size(); ++i) {
            MenuItem item = menu.getItem(i);
            if (item != exception)
                item.setVisible(visible);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_main_add:
                Intent intent = new Intent(this, NewTaskActivity.class);
                this.startActivityForResult(intent, NEW_TASK_CODE);
                break;
            case R.id.all_filter:
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTaskData.clear();
                        mTaskData.addAll(mDbHelper.readAllTasks());
                        mAdapter.notifyDataSetChanged();
                    }
                });
                break;
            case R.id.check_filter:
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTaskData.clear();
                        mTaskData.addAll(mDbHelper.readTasksByStatus(true));
                        mAdapter.notifyDataSetChanged();
                    }
                });
                break;
            case R.id.uncheck_filter:
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTaskData.clear();
                        mTaskData.addAll(mDbHelper.readTasksByStatus(false));
                        mAdapter.notifyDataSetChanged();
                    }
                });
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case(EDIT_TASK_CODE) : {
                if(resultCode == Activity.RESULT_OK) {
                    if(getIntent() != null) {
                        Bundle extras = data.getExtras();
                        String taskName = extras != null ? extras.getString("taskName") : "";
                        String taskNotes = extras != null ? extras.getString("taskNotes") : "";
                        String taskDate = extras != null ? extras.getString("taskDate") : "";
                        String taskTime = extras != null ? extras.getString("taskTime") : "";
                        boolean taskStatus = extras != null && extras.getBoolean("taskStatus");

                        mSelectedTask.setName(taskName);
                        mSelectedTask.setNotes(taskNotes);
                        mSelectedTask.setDate(taskDate);
                        mSelectedTask.setTime(taskTime);
                        mSelectedTask.setIsChecked(taskStatus);

                        mAdapter.notifyDataSetChanged();
                        new UpdateExistingTask(mSelectedTask).execute();
                    }
                }
                break;
            }
            case(NEW_TASK_CODE) : {
                if(resultCode == Activity.RESULT_OK) {
                    if(getIntent() != null) {
                        Bundle extras = data.getExtras();
                        String taskName = extras != null ? extras.getString("taskName") : "";
                        String taskNotes = extras != null ? extras.getString("taskNotes") : "";
                        String taskDate = extras != null ? extras.getString("taskDate") : "";
                        String taskTime = extras != null ? extras.getString("taskTime") : "";

                        Task newTask = new Task(taskName, taskNotes, taskDate, taskTime);
                        new SaveNewTask(newTask).execute();
                    }
                }
                break;
            }
        }
    }

    private class SaveNewTask extends AsyncTask {
        private Task newTask;
        private long newRowId;

        public SaveNewTask(Task newTask) {
            this.newTask = newTask;
        }

        @Override
        protected Object doInBackground(final Object... objects) {
            try {
                Thread.sleep(0);
                newRowId = mDbHelper.createTask(newTask);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Object object) {
            super.onPostExecute(object);

            newTask.setDbRowId(newRowId);
            mTaskData.add(0, newTask);
            mAdapter.notifyDataSetChanged();
        }
    }


    private class UpdateExistingTask extends AsyncTask {
        private Task updatedTask;
        private int status;

        public UpdateExistingTask(Task updatedTask) {
            this.updatedTask = updatedTask;
            this.status = 0;
        }

        @Override
        protected Object doInBackground(final Object... objects) {
            try {
                Thread.sleep(0);
                status = mDbHelper.updateTask(updatedTask);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Object object) {
            super.onPostExecute(object);
        }
    }


    private class DeleteExistingTask extends AsyncTask {
        private Task currentTask;

        public DeleteExistingTask(Task currentTask) {
            this.currentTask = currentTask;
        }

        @Override
        protected Object doInBackground(final Object... objects) {
            try {
                Thread.sleep(0);
                mDbHelper.deleteTask(currentTask);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Object object) {
            super.onPostExecute(object);

            MainActivity.this.mAdapter.remove(currentTask);
            MainActivity.this.mAdapter.notifyDataSetChanged();
        }
    }
}

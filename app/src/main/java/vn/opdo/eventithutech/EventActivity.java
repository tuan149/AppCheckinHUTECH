package vn.opdo.eventithutech;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import vn.opdo.adapter.EventAdapter;
import vn.opdo.model.Event;
import vn.opdo.webapi.API;

public class EventActivity extends AppCompatActivity {
    ListView lstEvent;
    ProgressBar eventProcessbar;
    EventAdapter adapter;
    API api;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getSupportActionBar().hide();
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_event);

        addControls();
        addEvents();
        addData();
    }

    private void addControls() {
        api = new API();
        adapter = new EventAdapter(this, R.layout.list_item_event);
        lstEvent = findViewById(R.id.lstEvent);
        eventProcessbar = findViewById(R.id.eventProcessbar);
        lstEvent.setAdapter(adapter);
    }

    private void addData() {
        api.getEventAPI().getEvent(new Callback<List<Event>>() {
            @Override
            public void success(List<Event> events, Response response) {
                eventProcessbar.setVisibility(View.GONE);
                adapter.clear();
                for (Event e: events) {
                    adapter.add(e);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EventActivity.this);
                builder.setPositiveButton("Thử lại", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addData();
                    }
                });
                builder.setMessage("Lỗi lấy dữ liệu " + error.getMessage());
                builder.create().show();
            }
        });
    }

    private void addEvents() {
        lstEvent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event e = adapter.getItem(position);
                Intent i = new Intent(EventActivity.this, CheckinActivity.class);
                i.putExtra("IdEvent", e.IdEvent);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        addData();
    }
}

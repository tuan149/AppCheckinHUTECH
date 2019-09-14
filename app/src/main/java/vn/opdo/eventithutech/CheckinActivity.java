package vn.opdo.eventithutech;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.camera.CameraSettings;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import vn.opdo.adapter.StudentAdapter;
import vn.opdo.model.StaticData;
import vn.opdo.model.Student;
import vn.opdo.webapi.API;

public class CheckinActivity extends AppCompatActivity {
    DecoratedBarcodeView barcodeView;
    ListView lstStudent;
    StudentAdapter adapter;
    EditText edtMSSVCheckin;
    ProgressBar checkinProcessbar;
    API api;
    int IdEvent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getSupportActionBar().hide();
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_checkin);

        addControls();
        addEvents();
        addData();
    }

    private void addData() {
        api.getEventAPI().getListStudentEvent(IdEvent, new Callback<List<Student>>() {
            @Override
            public void success(List<Student> students, Response response) {
                checkinProcessbar.setVisibility(View.GONE);
                adapter.clear();
                for (Student e: students) {
                    e.Status = 1;
                    adapter.add(e);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CheckinActivity.this);
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
        barcodeView.setStatusText("Quét MSSV để tự động checkin");
        barcodeView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                addStudent(result.getText());

            }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {

            }
        });

        lstStudent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Student s = adapter.getItem(position);
                if (s.Status != -1) return;
                String mssv = s.StudentCode;
                adapter.remove(s);
                addStudent(mssv);
            }
        });

        lstStudent.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Student st = adapter.getItem(position);
                removeStudent(st);
                return false;
            }
        });
    }

    private void removeStudent(final Student st) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CheckinActivity.this);
        builder.setMessage("Bạn có muốn xóa MSSV " + st.StudentCode + " hay không?");
        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                api.getEventAPI().removeStudentEvent(IdEvent, st.IdStudent, new Callback<Boolean>() {
                    @Override
                    public void success(Boolean aBoolean, Response response) {
                        if (aBoolean) {
                            adapter.remove(st);
                            Toast.makeText(CheckinActivity.this, "Đã xóa MSSV " + st.StudentCode, Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            Toast.makeText(CheckinActivity.this, "Xóa MSSV " + st.StudentCode + " thất bại", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(CheckinActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
                dialog.dismiss();
            }
        });
        builder.create().show();
    }


    private void addStudent(String mssv)
    {
        edtMSSVCheckin.setText("");
        ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
        tg.startTone(ToneGenerator.TONE_PROP_BEEP);

        for (int i = 0; i < adapter.getCount(); i++)
        {
            Student st = adapter.getItem(i);
            if (st.StudentCode.equals(mssv))
            {
                Toast.makeText(CheckinActivity.this, "MSSV này đã điểm danh rồi", Toast.LENGTH_LONG).show();
                return;
            }
        }
        final Student s = new Student();
        s.FullNameCheckin = StaticData.IdUser + "";
        s.StudentCode = mssv;
        s.StudentName = "<Đang nhận dạng>";
        s.Checkin = "hôm nay";
        s.Status = 0;
        adapter.insert(s, 0);
        Toast.makeText(CheckinActivity.this, "Điểm danh MSSV " + mssv, Toast.LENGTH_LONG).show();

        api.getEventAPI().addStudentEvent(IdEvent, s, new Callback<Student>() {
            @Override
            public void success(Student student, Response response) {
                if (student.Status == 1)
                {
                    s.Status = 1;
                    s.StudentName = student.StudentName;
                    s.StudentCode = student.StudentCode;
                    s.StudentClass = student.StudentClass;
                    s.FullNameCheckin = student.FullNameCheckin;
                    s.Checkin = student.Checkin;
                    s.IdStudent = student.IdStudent;
                    adapter.notifyDataSetChanged();
                }
                else
                {
                    Toast.makeText(CheckinActivity.this, "Không thể checkin MSSV " + s.StudentCode, Toast.LENGTH_LONG).show();
                    adapter.remove(s);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(CheckinActivity.this, "Lỗi checkin MSSV " + s.StudentCode + "\nẤn vào để thử lại", Toast.LENGTH_LONG).show();
                s.Status = -1;
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void addControls() {
        Intent i = getIntent();
        IdEvent = i.getIntExtra("IdEvent", 0);
        if (IdEvent == 0) finish();

        api = new API();
        lstStudent = findViewById(R.id.lstStudent);
        edtMSSVCheckin = findViewById(R.id.edtMSSVCheckin);
        barcodeView = findViewById(R.id.barcodeView);
        checkinProcessbar = findViewById(R.id.checkinProcessbar);
        adapter = new StudentAdapter(this, R.layout.list_item_student);
        lstStudent.setAdapter(adapter);

        CameraSettings mCameraSettings = new CameraSettings();
        mCameraSettings.setFocusMode(CameraSettings.FocusMode.MACRO);
        mCameraSettings.setMeteringEnabled(true);
        mCameraSettings.setBarcodeSceneModeEnabled(true);
        mCameraSettings.setAutoFocusEnabled(true);
        mCameraSettings.setContinuousFocusEnabled(false);
        mCameraSettings.setExposureEnabled(false);

        barcodeView.getBarcodeView().setCameraSettings(mCameraSettings);
    }

    @Override
    protected void onStart() {
        barcodeView.resume();
        super.onStart();
    }

    @Override
    protected void onResume() {
        addData();
        super.onResume();
    }

    @Override
    protected void onPause() {
        barcodeView.pause();
        super.onPause();
    }

    public void actionAdd(View view) {
        String mssv = edtMSSVCheckin.getText().toString();
        if (mssv.isEmpty())
        {
            Toast.makeText(CheckinActivity.this, "Vui lòng nhập MSSV", Toast.LENGTH_LONG).show();
            return;
        }
        if (mssv.length() < 10)
        {
            Toast.makeText(CheckinActivity.this, "MSSV không hợp lệ", Toast.LENGTH_LONG).show();
            return;
        }

        addStudent(mssv);
    }
}

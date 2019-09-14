package vn.opdo.eventithutech;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import vn.opdo.model.Login;
import vn.opdo.model.StaticData;
import vn.opdo.webapi.API;

public class MainActivity extends AppCompatActivity {
    EditText edtAccount, edtPassword;
    API api;
    ProgressDialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getSupportActionBar().hide();
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_main);
        initPermission();
        addControls();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setPositiveButton("Thoát", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        if (requestCode == 1) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                builder.setMessage("Bạn chưa cấp quyền cho ứng dụng truy cập camera");
                builder.create().show();

            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void initPermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setPositiveButton("Thoát", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               finish();
            }
        });

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            //Permisson don't granted
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                //builder.setMessage("Bạn chưa cấp quyền cho ứng dụng truy cập camera");
                //builder.create().show();
            }
            // Permisson don't granted and dont show dialog again.
            else {
                builder.setMessage("Bạn chưa cấp quyền cho ứng dụng truy cập camera");
                builder.create().show();
            }
            //Register permission
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);

        }
    }

    private void addControls() {
        edtAccount = findViewById(R.id.edtAccount);
        edtPassword = findViewById(R.id.edtPassword);
        api = new API();
    }

    public void actionLogin(View view) {
        String account = edtAccount.getText().toString();
        String password = edtPassword.getText().toString();


        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        if (account.isEmpty() || password.isEmpty()) {

            builder.setMessage("Vui lòng nhập đầy đủ thông tin bên trên");
            builder.create().show();
            return;
        }

        Login login = new Login();
        login.Username =account;
        login.Password = password;

        progress = ProgressDialog.show(this, "Đang đăng nhập",
                "Vui lòng đợi", true);
        api.getLoginAPI().checkLogin(login, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                progress.dismiss();
                try {
                    StaticData.IdUser = Integer.parseInt(s);
                    Intent i = new Intent(MainActivity.this, EventActivity.class);
                    startActivity(i);
                    finish();
                }
                catch (Exception ex) {
                    builder.setMessage(s);
                    builder.create().show();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                progress.dismiss();
                builder.setMessage(error.getMessage());
                builder.create().show();
            }
        });


    }
}

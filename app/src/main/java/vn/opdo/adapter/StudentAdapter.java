package vn.opdo.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import vn.opdo.eventithutech.R;
import vn.opdo.model.Student;

public class StudentAdapter extends ArrayAdapter<Student> {
    Activity context;
    int resource;
    public StudentAdapter(Activity context, int resource) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View custom = context.getLayoutInflater().inflate(resource, null);
        TextView txtNameStudent, txtMSSV, txtCheckTime;
        ImageView imgStatus;

        imgStatus = custom.findViewById(R.id.imgStatus);
        txtNameStudent = custom.findViewById(R.id.txtNameStudent);
        txtMSSV = custom.findViewById(R.id.txtMSSV);
        txtCheckTime = custom.findViewById(R.id.txtCheckTime);

        Student s = getItem(position);

        if (s.Status == 0) imgStatus.setImageResource(R.drawable.ic_sync_black_24dp);
        else if (s.Status == 1) imgStatus.setImageResource(R.drawable.ic_done_black_24dp);
        else imgStatus.setImageResource(R.drawable.ic_error_black_24dp);

        txtNameStudent.setText(s.StudentName);

        txtMSSV.setText(s.StudentCode);
        txtCheckTime.setText(s.FullNameCheckin + " vào " + s.Checkin);

        return custom;
    }
}

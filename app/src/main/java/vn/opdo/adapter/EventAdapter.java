package vn.opdo.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import vn.opdo.eventithutech.R;
import vn.opdo.model.Event;

public class EventAdapter extends ArrayAdapter<Event> {
    Activity context;
    int resource;

    public EventAdapter(Activity context, int resource) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View custom = context.getLayoutInflater().inflate(resource, null);

        TextView txtDate, txtName, txtRoom;
        txtDate = custom.findViewById(R.id.txtDate);
        txtName = custom.findViewById(R.id.txtName);
        txtRoom = custom.findViewById(R.id.txtRoom);

        Event e = getItem(position);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat output = new SimpleDateFormat("dd/MM");
        Date d = null;
        try {
            d = sdf.parse(e.EventDate);
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        String formattedTime = output.format(d);


        txtDate.setText(formattedTime);
        txtRoom.setText(e.EventRoom);
        txtName.setText(e.EventName);

        return custom;
    }
}

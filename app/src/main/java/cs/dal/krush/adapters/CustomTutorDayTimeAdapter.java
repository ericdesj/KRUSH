package cs.dal.krush.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import cs.dal.krush.R;
import cs.dal.krush.models.DBHelper;

/**
 * Custom array adapter used to contain the textview of a specific time,
 * and a delete icon for the specific time.
 */

public class CustomTutorDayTimeAdapter extends ArrayAdapter<TutorDayTimeRowitem>{

    /**
     * Declare variables
     */
    Context context;
    List<TutorDayTimeRowitem> items;
    String date;
    int USER_ID;

    /**
     * Overload constructor
     * @param context of the application
     * @param resourceId
     * @param items times to display
     * @param date current date from previous bundle
     * @param USER_ID of current logged in user
     */
    public CustomTutorDayTimeAdapter(Context context, int resourceId,
                                     List<TutorDayTimeRowitem> items,
                                     String date,
                                     int USER_ID){
        super(context,resourceId,items);
        this.items = items;
        this.context = context;
        this.date = date;
        this.USER_ID = USER_ID;
    }

    /**
     * For much faster processing time, holder for item views.
     */
    private class ViewHolder{
        ImageView imageView;
        TextView txtText;
    }

    /**
     * Allows attachment of a specified view for interactions,
     * also inflates this view for display purposes.
     * @param position of item being manipulated
     * @param convertView handle of current view
     * @param parent viewgroup for layout of parent
     * @return inflated, modified view
     */
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        TutorDayTimeRowitem rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.tutor_single_day_availability_row_layout, null);
            holder = new ViewHolder();
            holder.txtText = (TextView) convertView.findViewById(R.id.tvAvailabilityTimeOfDay);
            holder.imageView = (ImageView) convertView.findViewById(R.id.ivDeleteTimeOfDay);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.txtText.setText(rowItem.getText());

        /**
         * Delete handler
         */
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TutorDayTimeRowitem item = items.get(position);

                //get the full date for sqlite
                String formattedDate = formatDate(date,item.getText());

                DBHelper db = new DBHelper(context);
                db.availableTime.deleteRecord(formattedDate,USER_ID);

                items.remove(position);
                notifyDataSetChanged();
                Toast.makeText(context, "Deleted time:" + item.getText(), Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }

    /**
     * Formatter to match Java date with sqlite, this makes
     * it much easier to query dates in sqlite.
     * @param date selected date
     * @param time specific time of day
     * @return
     */
    public String formatDate(String date, String time){
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        // split and parse
        String[] splitDate = date.split("[-]");
        int year = Integer.parseInt(splitDate[0]);
        int month = Integer.parseInt(splitDate[1]);
        int day = Integer.parseInt(splitDate[2]);

        // get the hour and minute of day
        String[] splitTime = time.split("\\s");
        String[] splitStartTime = splitTime[0].split("[:]");
        int hourOfDay = Integer.parseInt(splitStartTime[0]);
        int minute = Integer.parseInt(splitStartTime[1]);

        // convert to calendar, then format
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MONTH, month-1);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        return formatter.format(calendar.getTime());
    }
}

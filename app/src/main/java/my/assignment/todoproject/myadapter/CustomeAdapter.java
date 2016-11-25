package my.assignment.todoproject.myadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import my.assignment.todoproject.Model.Item;
import my.assignment.todoproject.Model.Note;
import my.assignment.todoproject.R;

/**
 * Created by root on 9/1/16.
 */

public class CustomeAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    ArrayList<Note> noteList;
    ArrayList<Note>arraylist;
    private final DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
    private final Calendar cal = Calendar.getInstance();

    public CustomeAdapter(Context c,ArrayList<Note> items){
        context=c;
        noteList=items;
        arraylist=new ArrayList<>();
        arraylist.addAll(items);
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {

        return noteList.size();
    }

    @Override
    public Object getItem(int i) {
        return noteList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void add(Note data){
        noteList.add(data);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder=null;
       final Note A=(Note)noteList.get(i);

        if(view==null) {

            view = (View) inflater.inflate(R.layout.item, null);
            holder=new ViewHolder();
            holder.tv = (TextView) view.findViewById(R.id.item);
            holder.tv1 = (TextView) view.findViewById(R.id.createdTime);
            view.setTag(holder);


        }else{
            holder=(ViewHolder)view.getTag();
        }

        holder.tv.setText(A.getTitle());
        cal.setTimeInMillis(A.getCreatedTime());
        holder.tv1.setText(df.format(cal.getTime()));


        return view;
    }

    public class ViewHolder{
        TextView tv;
        TextView tv1;
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        noteList.clear();
        if (charText.length() == 0) {
            noteList.addAll(arraylist);
        } else {
            for (Note wp : arraylist) {
                if (wp.getTitle().toLowerCase(Locale.getDefault()).contains(charText)) {
                    noteList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}

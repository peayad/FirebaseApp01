package edu.iti.firebaseapp01;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MyListAdapter extends ArrayAdapter<Message>{

    Context context;
    ArrayList<Message> messages;

    public MyListAdapter(Context context, ArrayList<Message> messages){
        super(context, 0, messages);
        this.context = context;
        this.messages = messages;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        Message message = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_layout, parent, false);
        }

        TextView tvTitle = (TextView) convertView.findViewById(R.id.msgTitleTV);
        TextView tvContent = (TextView) convertView.findViewById(R.id.msgContentTV);

        return convertView;

    }

}

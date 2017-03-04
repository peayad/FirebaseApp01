package edu.iti.firebaseapp01;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ArrayList<Message> messages = new ArrayList<>();

        Message msg1 = new Message();
        msg1.setMsgTitle("ptr");
        msg1.setMsgContent("Hello World!");

        Message msg2 = new Message();
        msg2.setMsgTitle("peter");
        msg2.setMsgContent("Hello Again!");

        messages.add(msg1);
        messages.add(msg2);

        MyListAdapter adapter = new MyListAdapter(this, messages);

        ListView listView = (ListView) findViewById(R.id.messagesLV);
        listView.setAdapter(adapter);
    }
}

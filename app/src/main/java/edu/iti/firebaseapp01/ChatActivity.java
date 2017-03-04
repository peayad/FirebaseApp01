package edu.iti.firebaseapp01;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    final static private String TAG = "ptr";

    FirebaseUser user;
    FirebaseDatabase fireDB;
    DatabaseReference fireMsgs;

    ArrayList<Message> messages;
    MyListAdapter adapter;

    TextView msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messages = new ArrayList<>();
        adapter = new MyListAdapter(this, messages);

        ListView listView = (ListView) findViewById(R.id.messagesLV);
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(adapter);

        user = FirebaseAuth.getInstance().getCurrentUser();
        fireDB = FirebaseDatabase.getInstance();
//        fireDB.setPersistenceEnabled(true);
        fireMsgs = fireDB.getReference("messages");

        fireMsgs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i(TAG, "Firebase database has changed!");


                messages.clear();
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    String msgTitle = (String) messageSnapshot.child("msgTitle").getValue();
                    String msgContent = (String) messageSnapshot.child("msgContent").getValue();

                    messages.add(new Message(msgTitle, msgContent));
                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG, "Failed to read from firebase!");
            }
        });

        Button logoutBtn = (Button) findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionLogout();
            }
        });

        msg = (TextView) findViewById(R.id.messageET);

        Button sendBtn = (Button) findViewById(R.id.chatSendBtn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionSendMsg();
            }
        });
    }

    private void actionSendMsg() {
        String userID = fireMsgs.push().getKey();
        Message newMsg = new Message(user.getEmail(), msg.getText().toString());
        fireMsgs.child(userID).setValue(newMsg);

        msg.setText("");
    }

    private void actionLogout(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
}

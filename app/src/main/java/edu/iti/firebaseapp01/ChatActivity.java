package edu.iti.firebaseapp01;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ptr";
    public ProgressDialog mProgressDialog;

    FirebaseUser user;
    FirebaseDatabase fireDB;
    DatabaseReference fireMsgs;
    StorageReference storageRef;

    ListView listView;
    ArrayList<Message> messages;
    MyListAdapter adapter;

    TextView msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        storageRef = FirebaseStorage.getInstance().getReference();

        messages = new ArrayList<>();
        adapter = new MyListAdapter(this, messages);

        listView = (ListView) findViewById(R.id.messagesLV);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Message tempMsg = messages.get(position);
                if (tempMsg.msgLink == null || tempMsg.msgLink.equals("")) {

                    Log.i(TAG, "there is no link attached!");
                    return;
                }

                actionDownload(tempMsg.getMsgLink());
            }
        });

        user = FirebaseAuth.getInstance().getCurrentUser();
        fireDB = FirebaseDatabase.getInstance();
        fireMsgs = fireDB.getReference("messages");

        fireMsgs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i(TAG, "Firebase database has changed!");


                messages.clear();
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    String msgTitle = (String) messageSnapshot.child("msgTitle").getValue();
                    String msgContent = (String) messageSnapshot.child("msgContent").getValue();
                    String msgLink = (String) messageSnapshot.child("msgLink").getValue();

                    messages.add(new Message(msgTitle, msgContent, msgLink));
                }

                adapter.notifyDataSetChanged();
                listView.setSelection(adapter.getCount() - 1);

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

        Button uploadBtn = (Button) findViewById(R.id.uploadBtn);
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionUpload();
            }
        });
    }

    private void actionSendMsg() {
        String userID = fireMsgs.push().getKey();
        Message newMsg = new Message(user.getEmail(), msg.getText().toString(), "");
        fireMsgs.child(userID).setValue(newMsg);

        msg.setText("");
    }

    private void actionLogout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void actionUpload() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReferenceFromUrl("gs://iti-firebase-01.appspot.com").child("test.jpg");

        InputStream stream = getResources().openRawResource(R.raw.test);
        if (stream == null) {
            Log.i(TAG, "file not found!");
            Toast.makeText(getApplicationContext(), "file is not found!", Toast.LENGTH_SHORT).show();
            return;
        }

        showProgressDialog();
        UploadTask uploadTask = storageReference.putStream(stream);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                hideProgressDialog();
                Log.i(TAG, "a5eran et3amla upload");
                Toast.makeText(getApplicationContext(), "upload completed!", Toast.LENGTH_SHORT).show();

                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Message tempMsg = new Message(user.getEmail(), "Image File", downloadUrl.toString());

                String userID = fireMsgs.push().getKey();
                fireMsgs.child(userID).setValue(tempMsg);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                hideProgressDialog();
                Log.i(TAG, "mafesh faida");
                Toast.makeText(getApplicationContext(), "failed to upload!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void actionDownload(String filepath) {
        try {
            StorageReference fileRef = FirebaseStorage.getInstance().getReferenceFromUrl(filepath);
            showProgressDialog();
            final File localFile = File.createTempFile("images", "jpg");
            fileRef.getFile(localFile).addOnSuccessListener(
                    new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            hideProgressDialog();
                            Toast.makeText(getApplicationContext(), "file has been downloaded!", Toast.LENGTH_SHORT).show();
                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            ImageView imageView = (ImageView) findViewById(R.id.imageView);
                            imageView.setImageBitmap(bitmap);
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading ...");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}

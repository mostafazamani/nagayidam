package com.op.crush;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.op.crush.adapter.ListAdapter;
import com.op.crush.models.followmodel;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;


import retrofit2.Call;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private HomeActivity activity = this;
    public ListView mainListView;
    public ListAdapter adapter;
    DbFollow dbHelper;
    public long nextCursor = -1L;

    public TwitterSession session;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Twitter.initialize(this);
        setContentView(R.layout.activity_home);

        session = TwitterCore.getInstance().getSessionManager().getActiveSession();
        Toast.makeText(activity, "" + session.getUserName() + "    \n" + session.getUserId(), Toast.LENGTH_SHORT).show();
        mainListView = (ListView) findViewById(R.id.mainListView);

        adapter = new ListAdapter(getApplicationContext());
        mainListView.setAdapter(adapter);



//        loginMethod(session, nextCursor);




    }





}
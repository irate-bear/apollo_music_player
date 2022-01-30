package com.iratebear.apollo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.iratebear.apollo.adapters.ParentItemAdapter;
import com.iratebear.apollo.playerutils.IPlayQueueItem;
import com.spotify.android.appremote.api.ContentApi;
import com.spotify.protocol.types.ListItem;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    public static ListItem[] playItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_Apollo);
        setContentView(R.layout.activity_search);

        RecyclerView recyclerView = findViewById(R.id.searchActivityRecycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        ParentItemAdapter parentItemAdapter = new ParentItemAdapter(this, playItems, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(parentItemAdapter);

    }

    public void onBackClicked(View view) {
        finish();
    }
}
package com.iratebear.apollo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.iratebear.apollo.adapters.ListViewAdapter;
import com.iratebear.apollo.adapters.ListViewHeaderAdapter;
import com.iratebear.apollo.adapters.ViewPagerAdapter;
import com.iratebear.apollo.playerutils.IPlayQueueItem;
import com.iratebear.apollo.playerutils.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayItemActivity extends AppCompatActivity {

    public static List<IPlayQueueItem> tracks;
    public static Player player;
    public static IPlayQueueItem playlist;
    private RecyclerView playItemRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_Apollo);
        setContentView(R.layout.activity_play_item);

        playItemRecycler = findViewById(R.id.playItemRecycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        playItemRecycler.setLayoutManager(layoutManager);

        ListViewAdapter listViewAdapter = new ListViewAdapter(this, tracks, false);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        List<IPlayQueueItem> playItemHeader = new ArrayList<>();
        playItemHeader.add(playlist);
        viewPagerAdapter.setItems(playItemHeader);
        ListViewHeaderAdapter listViewHeaderAdapter = new ListViewHeaderAdapter(this, new RecyclerViewTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                MainActivity.isTrack = true;
                player.playUri(playlist.getUri());
            }

            @Override
            public void onLongPress(View view, int position) {

            }
        });
        listViewHeaderAdapter.setItems(playItemHeader);
        ConcatAdapter adapter = new ConcatAdapter(listViewHeaderAdapter, listViewAdapter);
        playItemRecycler.setAdapter(adapter);
        ImageButton btnBack = findViewById(R.id.btnSearchActivityBack);
        btnBack.setOnClickListener(view -> {
            finish();
        });
    }
}
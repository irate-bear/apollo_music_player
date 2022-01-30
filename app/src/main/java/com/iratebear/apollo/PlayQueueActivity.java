package com.iratebear.apollo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iratebear.apollo.adapters.ListViewAdapter;
import com.iratebear.apollo.playerutils.IPlayQueueItem;
import com.iratebear.apollo.playerutils.Player;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerContext;

import java.util.List;

public class PlayQueueActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ListViewAdapter adapter;
    public static Player player;
    public static Button btnUpdate;

    private final Subscription.EventCallback<PlayerContext> mPlayerStateEventCallback = new Subscription.EventCallback<PlayerContext>() {
        @Override
        public void onEvent(PlayerContext data) {
            adapter.notifyItemChanged(player.GetCurrent());
            adapter.notifyItemChanged(player.GetCurrent() + 1);
            updateQueue();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_Apollo);
        setContentView(R.layout.activity_play_queue);

        recyclerView = findViewById(R.id.playQueue);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ListViewAdapter(this, player.GetPlayQueue(), true);
        recyclerView.setAdapter(adapter);
        updateQueue();
        btnUpdate = findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateQueue();
            }
        });
    }

    private void updateQueue() {
        adapter.setPlayItems(player.GetPlayQueue());
        adapter.notifyDataSetChanged();;

        LinearLayout layout = findViewById(R.id.playQueueContainer);
        TextView title = findViewById(R.id.currentlyPLayingTitle);
        TextView artists = findViewById(R.id.currentlyPlayingArtists);
        ImageView coverArt = findViewById(R.id.currentlyPlayingCoverArt);
        title.setText(player.GetCurrentItem().getTitle());
        artists.setText(player.GetCurrentItem().getArtists());
        if (player.GetCurrentItem().getImage() != null)
            coverArt.setImageBitmap(player.GetCurrentItem().getImage().getBitmap());
        layout.setBackgroundColor(MainActivity.darkenColor(player.GetCurrentItem().getImage().getColor(), 0.3f));
        getWindow().setStatusBarColor(MainActivity.darkenColor(player.GetCurrentItem().getImage().getColor(), 0.3f));
    }

    public void onExitClicked(View view) {
        finish();
    }
}
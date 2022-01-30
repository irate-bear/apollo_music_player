package com.iratebear.apollo.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iratebear.apollo.MainActivity;
import com.iratebear.apollo.PlayItemActivity;
import com.iratebear.apollo.R;
import com.iratebear.apollo.RecyclerViewTouchListener;
import com.iratebear.apollo.adapters.ListViewAdapter;
import com.iratebear.apollo.playerutils.IPlayQueueItem;
import com.iratebear.apollo.playerutils.PlayQueueItem;
import com.iratebear.apollo.playerutils.PlaylistQueueItem;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LibraryFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<IPlayQueueItem> playlistQueueItems;
    private List<IPlayQueueItem> playQueueItemsTracks;
    private List<PlaylistSimple> playlists;
    private int current;

    public LibraryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_library, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = getView().findViewById(R.id.searchRecyclerView);
        if (MainActivity.spotify != null)
            MainActivity.spotify.getMyPlaylists(new Callback<Pager<PlaylistSimple>>() {
                @Override
                public void success(Pager<PlaylistSimple> playlistSimplePager, Response response) {
                    List<IPlayQueueItem> playlistQueuelist = new ArrayList<>();
                    playlists = new ArrayList<>();
                    playlists = playlistSimplePager.items;
                    for (PlaylistSimple playlist : playlistSimplePager.items) {
                        playlistQueuelist.add(new PlaylistQueueItem(playlist));
                    }
                    playlistQueueItems = playlistQueuelist;

                    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(layoutManager);

                    ListViewAdapter adapterPlay = new ListViewAdapter(getContext(), playlistQueueItems, false);
                    recyclerView.setAdapter(adapterPlay);
                    recyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(getContext(), recyclerView, new RecyclerViewTouchListener.ClickListener() {
                        @Override
                        public void onClick(View view, int position) {
                            current = position;
                            MainActivity.spotify.getPlaylistTracks(MainActivity.user.uri.split(":")[2], adapterPlay.getPlayItems().get(position).getUri().split(":")[2], new Callback<Pager<PlaylistTrack>>() {
                                @Override
                                public void success(Pager<PlaylistTrack> playlistTrackPager, Response response) {
                                    playQueueItemsTracks = new ArrayList<>();
                                    for (PlaylistTrack track : playlistTrackPager.items) {
                                        playQueueItemsTracks.add(new PlayQueueItem(track.track,
                                                (track.track.album.images.size() == 0) ? playlistQueuelist.get(position).getImageUri() : track.track.album.images.get(0).url));
                                    }
                                    openIntent();
                                }

                                @Override
                                public void failure(RetrofitError error) {

                                }
                            });
                        }

                        @Override
                        public void onLongPress(View view, int position) {

                        }
                    }));
                }

                @Override
                public void failure(RetrofitError error) {
                }
            });
    }

    private void openIntent() {
        PlayItemActivity.tracks = playQueueItemsTracks;
        PlayItemActivity.player = MainActivity.player;
        PlayItemActivity.playlist = new PlaylistQueueItem(playlists.get(current));
        Intent intent = new Intent(getActivity(), PlayItemActivity.class);
        startActivity(intent);
    }
}
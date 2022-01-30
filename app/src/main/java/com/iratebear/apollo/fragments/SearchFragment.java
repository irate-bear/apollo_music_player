package com.iratebear.apollo.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.iratebear.apollo.MainActivity;
import com.iratebear.apollo.PlayItemActivity;
import com.iratebear.apollo.R;
import com.iratebear.apollo.RecyclerViewTouchListener;
import com.iratebear.apollo.SearchActivity;
import com.iratebear.apollo.adapters.GridViewAdapter;
import com.iratebear.apollo.adapters.ListViewSimpleAdapter;
import com.iratebear.apollo.adapters.ParentItemAdapter;
import com.iratebear.apollo.adapters.SearchResultsAdapter;
import com.iratebear.apollo.playerutils.IPlayQueueItem;
import com.iratebear.apollo.playerutils.PlayAlbumItem;
import com.iratebear.apollo.playerutils.PlayListItem;
import com.iratebear.apollo.playerutils.PlayQueueItem;
import com.iratebear.apollo.playerutils.PlaylistQueueItem;
import com.spotify.android.appremote.api.ContentApi;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.types.ListItem;
import com.spotify.protocol.types.ListItems;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kaaes.spotify.webapi.android.models.AlbumSimple;
import kaaes.spotify.webapi.android.models.AlbumsPager;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TracksPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SearchFragment extends Fragment {
    private RecyclerView collectionView;
    private EditText textSearch;
    private ImageButton btnBack;
    private RecyclerViewTouchListener.ClickListener clickListener;
    private RecyclerViewTouchListener touchListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        List<String> categories = new ArrayList<String>(Arrays.asList(
                "automotive",
                "fitness",
                "navigation",
                "sleep",
                "wake"
        ));
        clickListener = new RecyclerViewTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                MainActivity.mSpotifyAppRemote.getContentApi().getRecommendedContentItems(categories.get(position))
                        .setResultCallback(new CallResult.ResultCallback<ListItems>() {
                            @Override
                            public void onResult(ListItems data) {
                                openIntent(data);
                            }
                        });
            }

            @Override
            public void onLongPress(View view, int position) {

            }
        };

        touchListener = new RecyclerViewTouchListener(getContext(), collectionView, clickListener);
        collectionView = view.findViewById(R.id.searchRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        ListViewSimpleAdapter adapter = new ListViewSimpleAdapter(getContext(), categories);
        collectionView.setLayoutManager(layoutManager);
        collectionView.setAdapter(adapter);
        collectionView.addOnItemTouchListener(touchListener);


        btnBack = view.findViewById(R.id.btnSearchBack);
        btnBack.setVisibility(View.GONE);
        btnBack.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                btnBack.setVisibility(View.GONE);
                textSearch.clearFocus();
                textSearch.setText("");
                InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getRootView().getWindowToken(), 0);
                collectionView.setAdapter(adapter);
                collectionView.addOnItemTouchListener(touchListener);
                return false;
            }
        });

        textSearch = view.findViewById(R.id.searchTextInputEditText);
        textSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                btnBack.setVisibility(View.VISIBLE);
                return false;
            }
        });
        textSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    im.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                    getTracks();
                    return true;
                }
                return false;
            }
        });
    }

    private void openIntent(ListItems data) {
        SearchActivity.playItems = data.items;
        Intent intent = new Intent(getActivity(), SearchActivity.class);
        startActivity(intent);
    }

    private void getTracks() {
        MainActivity.spotify.searchTracks(textSearch.getText().toString(), new Callback<TracksPager>() {
            @Override
            public void success(TracksPager tracksPager, Response response) {
                getAlbums(tracksPager);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private void getAlbums(TracksPager tracksPager) {
        MainActivity.spotify.searchAlbums(textSearch.getText().toString(), new Callback<AlbumsPager>() {
            @Override
            public void success(AlbumsPager albumsPager, Response response) {
                getPlaylists(tracksPager, albumsPager);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private void getPlaylists(TracksPager tracksPager, AlbumsPager albumsPager) {
        MainActivity.spotify.getPlaylists(textSearch.getText().toString(), new Callback<Pager<PlaylistSimple>>() {
            @Override
            public void success(Pager<PlaylistSimple> playlistSimplePager, Response response) {
                List<Pair<String, List<IPlayQueueItem>>> items = new ArrayList();
                List<IPlayQueueItem> albums = new ArrayList();
                List<IPlayQueueItem> tracks = new ArrayList();
                List<IPlayQueueItem> playlists = new ArrayList();
                for (Track track : tracksPager.tracks.items) {
                    tracks.add(new PlayQueueItem(track, track.album.images.get(0).url));
                }
                for (AlbumSimple album : albumsPager.albums.items) {
                    albums.add(new PlayAlbumItem(album));
                }
                for (PlaylistSimple playlist : playlistSimplePager.items) {
                    playlists.add(new PlaylistQueueItem(playlist));
                }
                if (tracks.size() > 0)
                    items.add(new Pair<>("Tracks", tracks));
                if (albums.size() > 0)
                    items.add(new Pair<>("Albums", albums));
                if (playlists.size() > 0)
                    items.add(new Pair<>("Playlists", playlists));
                SearchResultsAdapter adapter = new SearchResultsAdapter(getContext(), items);
                collectionView.setAdapter(adapter);
                collectionView.removeOnItemTouchListener(touchListener);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}
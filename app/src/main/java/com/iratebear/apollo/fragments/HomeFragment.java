package com.iratebear.apollo.fragments;

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
import com.iratebear.apollo.R;
import com.iratebear.apollo.adapters.ParentItemAdapter;
import com.spotify.android.appremote.api.ContentApi;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;

    public HomeFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = getView().findViewById(R.id.parentRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        if (MainActivity.mSpotifyAppRemote != null) {
            MainActivity.mSpotifyAppRemote.getContentApi().getRecommendedContentItems(ContentApi.ContentType.DEFAULT)
                    .setResultCallback(data -> {
                        ParentItemAdapter parentItemAdapter = new ParentItemAdapter(getContext(), data.items, true);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(parentItemAdapter);
                    });
        }

    }
}
package com.iratebear.apollo.fragments;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.iratebear.apollo.MainActivity;
import com.iratebear.apollo.R;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.types.PlayerState;

public class SlidingFragment extends Fragment {

    public static ImageButton btnUpdate;

    public SlidingFragment() {
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
        View view =  inflater.inflate(R.layout.fragment_sliding, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if ((MainActivity.player != null) && (MainActivity.player.GetCurrentItem().getImage() != null)) {
            int color = MainActivity.player.GetCurrentItem().getImage().getColor();
            view.findViewById(R.id.slidePanel).setBackgroundColor(MainActivity.darkenColor(color, 0.3f));
            getActivity().getWindow().setStatusBarColor(MainActivity.darkenColor(color, 0.3f));
            view.findViewById(R.id.btnPlayLarge).setBackgroundTintList(ColorStateList.valueOf(MainActivity.darkenColor(color, 0.9f)));
            TextView title = view.findViewById(R.id.txtTitle);
            TextView artists = view.findViewById(R.id.txtArtists);
            TextView titleSmall = view.findViewById(R.id.txtSlideTitle);
            TextView artistsSmall = view.findViewById(R.id.txtSlideArtists);
            artistsSmall.setText(MainActivity.player.GetCurrentItem().getArtists());
            titleSmall.setText(MainActivity.player.GetCurrentItem().getTitle());
            artists.setText(MainActivity.player.GetCurrentItem().getArtists());
            title.setText(MainActivity.player.GetCurrentItem().getTitle());
            ImageView cover = view.findViewById(R.id.imgSlide);
            MainActivity.mSpotifyAppRemote.getPlayerApi().getPlayerState().setResultCallback(new CallResult.ResultCallback<PlayerState>() {
                @Override
                public void onResult(PlayerState data) {
                    MainActivity.mSpotifyAppRemote.getImagesApi().getImage(data.track.imageUri).setResultCallback(new CallResult.ResultCallback<Bitmap>() {
                        @Override
                        public void onResult(Bitmap bitmap) {
                            cover.setImageBitmap(bitmap);
                        }
                    });
                }
            });
            titleSmall.setVisibility(View.INVISIBLE);
            artistsSmall.setVisibility(View.INVISIBLE);
            cover.setVisibility(View.INVISIBLE);
        }

        btnUpdate = view.findViewById(R.id.btnMore);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSlideFragment();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void updateSlideFragment() {
        int color = MainActivity.player.GetCurrentItem().getImage().getColor();
        if (MainActivity.musicControlsPanel.getPanelState() != SlidingUpPanelLayout.PanelState.COLLAPSED) {
            getView().findViewById(R.id.slidePanel).setBackgroundColor(MainActivity.darkenColor(color, 0.3f));
            getActivity().getWindow().setStatusBarColor(MainActivity.darkenColor(color, 0.3f));
        }
        ImageButton btnPlayLarge = getView().findViewById(R.id.btnPlayLarge);
        ImageButton btnPlaySmall = getView().findViewById(R.id.btnPlaySmall);
        if (MainActivity.State != null) {
            if (MainActivity.State.isPaused) {
                btnPlayLarge.setImageResource(R.drawable.ic_baseline_play_arrow_48);
                btnPlaySmall.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            } else {
                btnPlayLarge.setImageResource(R.drawable.ic_baseline_pause_48);
                btnPlaySmall.setImageResource(R.drawable.ic_baseline_pause_24);
            }
        }
        getView().findViewById(R.id.btnPlayLarge).setBackgroundTintList(ColorStateList.valueOf(MainActivity.darkenColor(color, 0.9f)));
        TextView title = getView().findViewById(R.id.txtTitle);
        TextView artists = getView().findViewById(R.id.txtArtists);
        TextView titleSmall = getView().findViewById(R.id.txtSlideTitle);
        TextView artistsSmall = getView().findViewById(R.id.txtSlideArtists);
        artistsSmall.setText(MainActivity.player.GetCurrentItem().getArtists());
        titleSmall.setText(MainActivity.player.GetCurrentItem().getTitle());
        artists.setText(MainActivity.player.GetCurrentItem().getArtists());
        title.setText(MainActivity.player.GetCurrentItem().getTitle());
        ImageView cover = getView().findViewById(R.id.imgSlide);
        MainActivity.mSpotifyAppRemote.getPlayerApi().getPlayerState().setResultCallback(new CallResult.ResultCallback<PlayerState>() {
            @Override
            public void onResult(PlayerState data) {
                MainActivity.mSpotifyAppRemote.getImagesApi().getImage(data.track.imageUri).setResultCallback(new CallResult.ResultCallback<Bitmap>() {
                    @Override
                    public void onResult(Bitmap bitmap) {
                        cover.setImageBitmap(bitmap);
                    }
                });
            }
        });
    }
}
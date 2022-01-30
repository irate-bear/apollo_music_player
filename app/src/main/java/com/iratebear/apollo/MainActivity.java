package com.iratebear.apollo;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.ColorUtils;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iratebear.apollo.adapters.ViewPagerAdapter;
import com.iratebear.apollo.fragments.SlidingFragment;
import com.iratebear.apollo.playerutils.IPlayQueueItem;
import com.iratebear.apollo.playerutils.PlayQueueItem;
import com.iratebear.apollo.playerutils.Player;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.Capabilities;
import com.spotify.protocol.types.Image;
import com.spotify.protocol.types.PlayerContext;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Repeat;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.Playlist;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TrackSimple;
import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends AppCompatActivity {
    private String ACCESS_TOKEN;
    private ConstraintLayout constraintLayout;
    public static boolean isTrack = false;

    private PlayerContext Context;
    public static PlayerState State;
    public static SpotifyService spotify;
    public static Player player;
    public static UserPrivate user;

    private BottomNavigationView bottomNavigation;
    public static SlidingUpPanelLayout musicControlsPanel;

    private ImageButton btnPlayLarge;
    private ImageButton btnPlaySmall;
    private ImageButton btnNextSmall;
    private ImageButton btnNextLarge;
    private ImageButton btnPrev;
    private ImageButton btnLike;
    private ImageButton btnRepeat;
    private ImageButton btnMore;
    private ImageButton btnShuffle;
    private ImageButton btnCollapse;

    public static ViewPager2 viewPager;
    public static ViewPagerAdapter viewPagerAdapter;

    private TextView textTitle;
    private TextView textArtist;
    private TextView textTitleSmall;
    private TextView textArtistSmall;
    private ImageView imageView;

    private ProgressBar progressBar;
    private SeekBar seekBar;
    private TrackProgressBar trackProgressBar;

    private RecyclerView playQueue;

    private List<View> expandedButtons;
    private List<View> collapsedButtons;

    private static final int REQUEST_CODE = 1337;
    private static final String CLIENT_ID = "00e25386df6242579b7dd0d203efef4f";
    private static final String REDIRECT_URI = "http://apollo.com/callback/";
    public static  SpotifyAppRemote mSpotifyAppRemote;

    AuthorizationRequest request =
        new AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI).setScopes(new String[]{"streaming"}).build();


    Subscription<PlayerState> mPlayerStateSubscription;
    Subscription<PlayerContext> mPlayerContextSubscription;

    private final Subscription.EventCallback<PlayerContext> mPlayerContextEventCallback =
            playerContext -> {
                PlayerContext oldContext = Context;
                if (Context == null) {
                    Context = playerContext;
                }
                if (isTrack && !oldContext.uri.equals(playerContext.uri)) {
                    isTrack = false;
                    Context = playerContext;
                    UpdatePlayQueue();
                }

            };

    private final Subscription.EventCallback<PlayerState> mPlayerStateEventCallback =
            playerState -> {

                State = playerState;

                if (playerState.playbackPosition >= seekBar.getMax()) {
                    player.PlayNext();
                }

                viewPager.setCurrentItem(viewPagerAdapter.getItemIndex(playerState.track.uri));

                if (PlayQueueActivity.btnUpdate != null) {
                    PlayQueueActivity.btnUpdate.callOnClick();
                } else {
                    SlidingFragment.btnUpdate.callOnClick();
                }

                int i;
                boolean isFound = false;
                for (i = 0; i < player.GetPlayQueue().size() && !isFound; i++) {
                    if (playerState.track.uri.equals(player.GetPlayQueue().get(i).getUri())) {
                        isFound = true;
                        break;
                    }
                }
                if (isFound) {
                    viewPager.setCurrentItem(i);
                    if (musicControlsPanel.getPanelState() != SlidingUpPanelLayout.PanelState.COLLAPSED) {
                        constraintLayout.setBackgroundColor(darkenColor(viewPagerAdapter.getColor(i), 0.3f));
                        getWindow().setStatusBarColor(darkenColor(viewPagerAdapter.getColor(i), 0.3f));
                    }
                    btnPlayLarge.setBackgroundTintList(ColorStateList.valueOf(darkenColor(viewPagerAdapter.getColor(i), 0.9f)));
                }

                if (playerState.playbackSpeed > 0) {
                    trackProgressBar.unpause();
                } else {
                    trackProgressBar.pause();
                }

                if (playerState.track != null) {
                    player.SetCurrent(player.GetIndexOf(playerState.track.uri));
                    textTitle.setText(playerState.track.name);
                    textArtist.setText(playerState.track.artist.name);
                    textTitleSmall.setText(playerState.track.name);
                    textArtistSmall.setText(playerState.track.artist.name);
                    mSpotifyAppRemote.getImagesApi()
                            .getImage(playerState.track.imageUri, Image.Dimension.LARGE)
                            .setResultCallback(
                                    bitmap -> imageView.setImageBitmap(bitmap)
                            );

                    seekBar.setMax((int) playerState.track.duration);
                    progressBar.setMax((int) playerState.track.duration);
                    //seekBar.setProgress((int)playerState.playbackPosition);
                    progressBar.setProgress((int)playerState.playbackPosition);

                    trackProgressBar.setDuration(playerState.track.duration);
                    trackProgressBar.update(playerState.playbackPosition);
                }
                seekBar.setEnabled(true);

                if (playerState.isPaused) {
                    btnPlayLarge.setImageDrawable(getDrawable(R.drawable.ic_baseline_play_arrow_48));
                    btnPlaySmall.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                } else {
                    btnPlayLarge.setImageDrawable(getDrawable(R.drawable.ic_baseline_pause_48));
                    btnPlaySmall.setImageResource(R.drawable.ic_baseline_pause_24);
                }

                if (playerState.playbackOptions.isShuffling) {
                    btnShuffle.setImageResource(R.drawable.ic_baseline_shuffle_24);
                } else {
                    btnShuffle.setImageResource(R.drawable.ic_baseline_shuffle_off_24);
                }

                if (playerState.playbackOptions.repeatMode == Repeat.ALL) {
                    btnRepeat.setImageResource(R.drawable.ic_baseline_repeat_24);
                } else if (playerState.playbackOptions.repeatMode == Repeat.ONE) {
                    btnRepeat.setImageResource(R.drawable.ic_baseline_repeat_one_24);
                } else {
                    btnRepeat.setImageResource(R.drawable.ic_baseline_repeat_off_24);
                }

            };

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_Apollo);
        setContentView(R.layout.activity_main);

        constraintLayout = findViewById(R.id.slidePanel);
        constraintLayout.setBackgroundColor(darkenColor(Color.DKGRAY, 0.6f));

        bottomNavigation = findViewById(R.id.botNavView);
        NavController navController = Navigation.findNavController(this, R.id.fragContView );
        NavigationUI.setupWithNavController(bottomNavigation, navController);

        musicControlsPanel = findViewById(R.id.sliding_layout);
        progressBar = findViewById(R.id.progressBar);
        seekBar = findViewById(R.id.seekBar);
        trackProgressBar = new TrackProgressBar(seekBar);

        textTitle = findViewById(R.id.txtTitle);
        textTitle.setSelected(true);
        textArtist = findViewById(R.id.txtArtists);

        textTitleSmall = findViewById(R.id.txtSlideTitle);
        textArtistSmall = findViewById(R.id.txtSlideArtists);
        imageView = findViewById(R.id.imgSlide);

        btnPlayLarge = findViewById(R.id.btnPlayLarge);
        btnPlaySmall = findViewById(R.id.btnPlaySmall);
        btnNextSmall = findViewById(R.id.btnNextSmall);
        btnNextLarge = findViewById(R.id.btnNextLarge);
        btnPrev = findViewById(R.id.btnPrevLarge);
        btnLike = findViewById(R.id.btnLike);
        btnRepeat = findViewById(R.id.btnRepeat);
        btnMore = findViewById(R.id.btnMore);
        btnShuffle = findViewById(R.id.btnShuffle);
        btnCollapse = findViewById(R.id.btnCollapse);

        viewPager = findViewById(R.id.viewPager2);

        View[] fabsCol = {
                btnPlaySmall,
                btnNextSmall,
                imageView,
                textTitleSmall,
                textArtistSmall,
        };

        View[] fabsExp = {
                btnNextLarge,
                btnPrev,
                btnLike,
                btnRepeat,
                btnMore,
                btnShuffle,
                btnCollapse,
                btnPlayLarge,
        };
        expandedButtons = new ArrayList<>();
        collapsedButtons = new ArrayList<>();
        for (View fab : fabsExp) {
            expandedButtons.add(fab);
        }
        for (View fab : fabsCol) {
            collapsedButtons.add(fab);
        }

        musicControlsPanel.addPanelSlideListener(onPanelSlide(expandedButtons, collapsedButtons));

        playQueue = findViewById(R.id.playQueue);
        if (viewPagerAdapter == null)
            viewPagerAdapter = new ViewPagerAdapter(this);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if ((position < player.GetPlayQueue().size()) && (!player.GetPlayQueue().get(position).getUri().equals(State.track.uri))) {
                    player.SetCurrent(position, State);
                }
            }
        });

        connect(true);
    }


    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == REQUEST_CODE) {
            AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    ACCESS_TOKEN = response.getAccessToken();
                    initSpotifyApi(response.getAccessToken());
                    break;

                // Auth flow returned an error
                case ERROR:
                    // Handle error response
                    break;

                // Most likely auth flow was cancelled
                default:
                    // Handle other cases
            }
        }
    }

    private void initSpotifyApi(String accessToken) {
        SpotifyApi api = new SpotifyApi();

        api.setAccessToken(accessToken);

        spotify = api.getService();

        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setPageTransformer(new MarginPageTransformer(150));

        spotify.getMe(new Callback<UserPrivate>() {
            @Override
            public void success(UserPrivate userPrivate, Response response) {
                user = userPrivate;
                UpdatePlayQueue();
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private void UpdatePlayQueue() {
        if (Context.uri.indexOf(":") != -1) {
            String type = Context.uri.split(":")[1];
            String uri = Context.uri.split(":")[2];


            switch (type.charAt(0)) {
                case 'p':
                    getPlayList(uri);
                    break;
                case 'a':
                    getAlbum(uri);
                    break;
                case 't':
                    getTrack(uri);
                    break;
                default:
                    getTrack(State.track.uri.split(":")[2]);
                    break;
            }
            musicControlsPanel.setVisibility(View.VISIBLE);
            musicControlsPanel.setPanelHeight(217);
            if (musicControlsPanel.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED)
                progressBar.setVisibility(View.VISIBLE);
        } else {

            player.playUri(Context.uri);

            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void getPlayList(String uri) {
        player.SetPlayQueue(new ArrayList<>());
        spotify.getPlaylist(user.uri, uri, new Callback<Playlist>() {
            @Override
            public void success(Playlist playlist, Response response) {
                List<IPlayQueueItem> playQueueItems = new ArrayList<>();
                for (PlaylistTrack playlistTrack : playlist.tracks.items) {
                    if (playlistTrack.track.id != null) {
                        playQueueItems.add(new PlayQueueItem(playlistTrack.track, playlistTrack.track.album.images.get(0).url));
                    }
                }

                player.SetPlayQueue(playQueueItems);
                viewPagerAdapter.setItems(playQueueItems);
                viewPagerAdapter.notifyDataSetChanged();
                player.SetCurrent(viewPagerAdapter.getItemIndex(State.track.uri));
                viewPager.setCurrentItem(viewPagerAdapter.getItemIndex(State.track.uri));
            }

            @Override
            public void failure(RetrofitError error) {
                System.out.println(error.toString());
            }
        });
    }

    private void getTrack(String uri) {
        List<IPlayQueueItem> playQueueItems = new ArrayList<>();
        player.SetPlayQueue(new ArrayList<>());
        spotify.getTrack(uri, new Callback<Track>() {
            @Override
            public void success(Track track, Response response) {
                playQueueItems.add(new PlayQueueItem(track, track.album.images.get(0).url));
                player.SetPlayQueue(playQueueItems);
                viewPagerAdapter.setItems(player.GetPlayQueue());
                viewPagerAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private void getAlbum(String uri) {
        List<IPlayQueueItem> playQueueItems = new ArrayList<>();
        player.SetPlayQueue(new ArrayList<>());
        spotify.getAlbum(uri, new Callback<Album>() {
            @Override
            public void success(Album album, Response response) {
                for (TrackSimple track : album.tracks.items) {
                    playQueueItems.add(new PlayQueueItem(track, album.images.get(0).url));
                }
                player.SetPlayQueue(playQueueItems);
                viewPagerAdapter.setItems(player.GetPlayQueue());
                viewPagerAdapter.notifyDataSetChanged();
                if (State != null) {
                    viewPager.setCurrentItem(viewPagerAdapter.getItemIndex(State.track.uri));
                    player.SetCurrent(viewPagerAdapter.getItemIndex(State.track.uri));
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnect();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void connect (boolean showAuthView) {
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
        onConnecting();
        SpotifyAppRemote.connect(
            getApplicationContext(),
            new ConnectionParams.Builder(CLIENT_ID)
                .setRedirectUri(REDIRECT_URI)
                .showAuthView(true)
                .build(),
            new Connector.ConnectionListener() {
            @Override
            public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                mSpotifyAppRemote = spotifyAppRemote;
                player = new Player(mSpotifyAppRemote);
                MainActivity.this.onConnected();
            }

            @Override
            public void onFailure(Throwable error) {
                MainActivity.this.onDisconnected();
            }
        });
    }

    private void onConnected() {
        bottomNavigation.setSelectedItemId(R.id.searchFragment);
        player.SetPlayerContextCallback(mPlayerContextEventCallback, mPlayerContextSubscription);
        player.SetPlayerStateCallback(mPlayerStateEventCallback, mPlayerStateSubscription);

        if (State == null) {
            AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request);
        } else {
            initSpotifyApi(ACCESS_TOKEN);
        }
    }

    private void onConnecting() {
        imageView.setAlpha(0f);
        textTitleSmall.setAlpha(0f);
        textArtistSmall.setAlpha(0f);
        if (musicControlsPanel.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
            setFade(expandedButtons, 0f);
        } else {
            setFade(collapsedButtons, 0f);
        }
    }

    private void disconnect() {
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
        onDisconnected();
    }

    private void onDisconnected() {

    }

    public void onButtonCollapseClicked(View view) {
        musicControlsPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }

    public void onPlayPauseButtonClicked(View view) {
        player.PlayPauseQueue();
    }

    public void onSkipNextButtonClicked(View view) {
        player.PlayNext();
    }

    public void onSkipPrevButtonClicked(View view) {
        player.PlayPrevious();
    }

    public void onToggleShuffleClicked(View view) {
        player.ToggleShuffle();
    }

    public void onToggleRepeatClicked(View view) {
        player.ToggleRepeat();
    }

    public void onPlayQueueClicked(View view) {
        PlayQueueActivity.player = MainActivity.player;
        Intent intent = new Intent(this, PlayQueueActivity.class);
        startActivity(intent);
    }

    public void onSeekBack(View view) {
        mSpotifyAppRemote
                .getPlayerApi()
                .seekToRelativePosition(-15000);
    }

    public void onSeekForward(View view) {
        mSpotifyAppRemote
                .getPlayerApi()
                .seekToRelativePosition(15000);
    }

    private SlidingUpPanelLayout.PanelSlideListener onPanelSlide(List<View> butExp, List<View> butCol) {
        return new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                bottomNavigation.setTranslationY((bottomNavigation.getHeight()) * (slideOffset));
                if (State.track != null && (viewPagerAdapter.getItemCount() > 0)) {
                    int color = viewPagerAdapter.getColor(viewPagerAdapter.getItemIndex(State.track.uri));

                    if (slideOffset == 0.0) {
                        progressBar.setVisibility(View.VISIBLE);
                        panel.setClickable(true);
                    } else {
                        SlidingFragment.btnUpdate.callOnClick();
                        progressBar.setVisibility(View.INVISIBLE);
                        constraintLayout.setBackgroundColor(darkenColor(color, 0.3f));
                        btnPlayLarge.setBackgroundTintList(ColorStateList.valueOf(darkenColor(color, 0.9f)));
                        panel.setClickable(false);
                        float bottomFade = 1.0f - Math.min(1.0f / 3.0f, slideOffset) * 3.0f;
                        float topFade = (Math.max(2.0f / 3.0f, slideOffset) - (2.0f / 3.0f)) * 3.0f;
                        setFade(butExp, topFade);
                        setFade(butCol, bottomFade);
                        getWindow().setStatusBarColor(ColorUtils.setAlphaComponent(darkenColor(color, 0.3f), (int) (slideOffset * 255)));
                    }
                }
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        };
    }

    public static int darkenColor(int color, float factor) {
        return Color.argb(
            Color.alpha(color),
            Math.max((int)(Color.red(color) * factor), 0),
            Math.max((int)(Color.green(color) * factor), 0),
            Math.max((int)(Color.blue(color) * factor), 0)
        );
    }

    private void setFade(List<View> views, float alpha) {
        for (View view : views) {
            view.setAlpha(alpha);
        }
    }

    private class TrackProgressBar {

        private static final int LOOP_DURATION = 500;
        private final SeekBar mSeekBar;
        private final Handler mHandler;

        private final SeekBar.OnSeekBarChangeListener mSeekBarChangeListener =
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        progressBar.setProgress(progress);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        mSpotifyAppRemote
                                .getPlayerApi()
                                .seekTo(seekBar.getProgress());
                    }
                };

        private final Runnable mSeekRunnable =
                new Runnable() {
                    @Override
                    public void run() {
                        int progress = mSeekBar.getProgress();
                        mSeekBar.setProgress(progress + LOOP_DURATION);
                        mHandler.postDelayed(mSeekRunnable, LOOP_DURATION);
                    }
                };

        private TrackProgressBar(SeekBar seekBar) {
            mSeekBar = seekBar;
            mSeekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);
            mHandler = new Handler();
        }

        private void setDuration(long duration) {
            mSeekBar.setMax((int) duration);
        }

        private void update(long progress) {
            mSeekBar.setProgress((int) progress);
        }

        private void pause() {
            mHandler.removeCallbacks(mSeekRunnable);
        }

        private void unpause() {
            mHandler.removeCallbacks(mSeekRunnable);
            mHandler.postDelayed(mSeekRunnable, LOOP_DURATION);
        }
    }
}
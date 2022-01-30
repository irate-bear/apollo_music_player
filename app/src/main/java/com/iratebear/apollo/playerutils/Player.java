package com.iratebear.apollo.playerutils;

import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.Empty;
import com.spotify.protocol.types.PlayerContext;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Repeat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.security.auth.callback.Callback;

public class Player {
    private List<IPlayQueueItem> playQueue;
    private SpotifyAppRemote spotifyAppRemote;
    private String uri;
    private int current;

    public Player(SpotifyAppRemote spotifyAppRemote) {
        this.playQueue = new ArrayList<>();
        this.spotifyAppRemote = spotifyAppRemote;
    }

    public void playUri(String uri) {
        spotifyAppRemote
                .getPlayerApi()
                .play(uri);
    }

    public void AddItem(IPlayQueueItem playQueueItem) {
        playQueue.add(playQueueItem);
    }

    public void SetPlayQueue(List<IPlayQueueItem> playQueue) {
        this.playQueue = playQueue;
    }

    public SpotifyAppRemote GetApi() {
        return spotifyAppRemote;
    }

    public List<IPlayQueueItem> GetPlayQueue() {
        return  playQueue;
    }

    public int GetCurrent() {
        return current;
    }

    public IPlayQueueItem GetCurrentItem() {
        return playQueue.get(current);
    }

    public int GetIndexOf(String uri) {
        for (int i = 0; i < playQueue.size(); i++) {
            if (playQueue.get(i).getUri().equals(uri)) {
                return i;
            }
        }
        return -1;
    }

    public void SetCurrent(int position) {
        current = position;
    }

    public void SetCurrent(int position, PlayerState state) {
        current = position;
        playUri(
                playQueue.get(position).getUri()
        );
    }

    public void PlayTrack(String uri) {
        if (uri != playQueue.get(current).getUri()) {
            for (int i = 0; i < playQueue.size(); i++) {
                if (playQueue.get(i).getUri() == uri) {
                    current = i;
                    playUri(uri);
                }
            }
        }
    }

    public void PlayNext() {
        spotifyAppRemote.getPlayerApi().getPlayerState().setResultCallback(playerState -> {
            if (playerState.playbackPosition < 3_000) {
                if (playerState.playbackOptions.isShuffling) {
                    current = new Random().nextInt(playQueue.size());
                } else if (current < playQueue.size()) {
                    current++;
                } else if (playerState.playbackOptions.repeatMode == Repeat.ALL) {
                    current = 0;
                }
            }
            playUri(playQueue.get(current).getUri());
        });
    }

    public void PlayPrevious() {
        spotifyAppRemote.getPlayerApi().getPlayerState().setResultCallback(playerState -> {
            if (playerState.playbackPosition < 3_000) {
                if (playerState.playbackOptions.isShuffling) {
                    current = new Random().nextInt(playQueue.size());
                } else if (current > 0) {
                    current--;
                } else if (playerState.playbackOptions.repeatMode == Repeat.ALL) {
                    current = playQueue.size() - 1;
                }
            }
            playUri(playQueue.get(current).getUri());
        });
    }

    public void PlayPauseQueue() {
        spotifyAppRemote.getPlayerApi()
            .getPlayerState()
            .setResultCallback(
                    playerState -> {
                        if (playerState.isPaused) {
                            spotifyAppRemote.getPlayerApi()
                                    .resume();
                        }
                        else {
                            spotifyAppRemote.getPlayerApi()
                                    .pause();
                        }
                    }
            );
    }

    public  void ToggleShuffle() {
        spotifyAppRemote.getPlayerApi()
                .toggleShuffle();
    }

    public void ToggleRepeat() {
        spotifyAppRemote.getPlayerApi()
                .toggleRepeat();
    }

    public void SetPlayerStateCallback(Subscription.EventCallback<PlayerState> playerStateEventCallback, Subscription<PlayerState> playerStateSubscription) {
        if (playerStateSubscription != null && !playerStateSubscription.isCanceled()) {
            playerStateSubscription.cancel();
            playerStateSubscription = null;
        }

        playerStateSubscription = spotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(playerStateEventCallback);
    }

    public void SetPlayerContextCallback(Subscription.EventCallback<PlayerContext> playerContextEventCallback, Subscription<PlayerContext> playerContextSubscription) {
        if (playerContextSubscription != null && !playerContextSubscription.isCanceled()) {
            playerContextSubscription.cancel();
        }

        spotifyAppRemote.getPlayerApi()
                .subscribeToPlayerContext()
                .setEventCallback(playerContextEventCallback);
    }
}

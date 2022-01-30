package com.iratebear.apollo.playerutils;

import com.iratebear.apollo.imageutils.ImageItem;
import com.spotify.protocol.types.ImageUri;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Playlist;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.TrackSimple;

public class PlaylistQueueItem implements IPlayQueueItem{

    private PlaylistSimple playlist;
    private ImageItem image;

    public PlaylistQueueItem(PlaylistSimple playlist) {
        this.playlist = playlist;
    }

    public PlaylistSimple getPlaylist() {
        return playlist;
    }

    public String getUri() {
        return playlist.uri;
    }

    public String getImageUri() {
        if (playlist.images.size() == 0) {
            return "";
        } else {
            return playlist.images.get(0).url;
        }
    }

    @Override
    public ImageUri getImageURI() {
        return null;
    }

    public ImageItem getImage() {
        if (image != null) {
            return image;
        } else {
            return null;
        }
    }

    public void setImage(ImageItem image) {
        this.image = image;
    }

    public String getTitle() {
        return playlist.name;
    }

    public String getArtists() {
        return playlist.owner.display_name;
    }

    @Override
    public String ownerUri() {
        return playlist.owner.uri;
    }

}

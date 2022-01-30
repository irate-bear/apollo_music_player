package com.iratebear.apollo.playerutils;

import com.iratebear.apollo.imageutils.ImageItem;
import com.spotify.protocol.types.ImageUri;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.TrackSimple;

public class PlayQueueItem implements IPlayQueueItem {

    private String uri;
    private TrackSimple track;
    private String imageURI;
    private ImageItem image;

    public PlayQueueItem(TrackSimple track, String imageURI) {
        this.uri = track.uri;
        this.track = track;
        this.imageURI = imageURI;
    }

    public String getUri() {
        return uri;
    }

    public String getImageUri() {
        return imageURI;
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
        return track.name;
    }

    public String getArtists() {
        String names = "";
        List<ArtistSimple> artists = track.artists;
        for (ArtistSimple artist : artists) {
            names += (artist.name + ", ");
        }
        return names.substring(0, names.length() - 2);
    }

    @Override
    public String ownerUri() {
        return null;
    }
}

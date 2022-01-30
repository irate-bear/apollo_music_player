package com.iratebear.apollo.playerutils;

import android.os.Parcelable;

import com.iratebear.apollo.imageutils.ImageItem;
import com.spotify.protocol.types.ImageUri;

import java.io.Serializable;
import java.util.List;

import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.TrackSimple;

public interface IPlayQueueItem {

    String getUri();
    String getImageUri();
    ImageUri getImageURI();
    ImageItem getImage();
    void setImage(ImageItem image);
    String getTitle();
    String getArtists();
    String ownerUri();
}

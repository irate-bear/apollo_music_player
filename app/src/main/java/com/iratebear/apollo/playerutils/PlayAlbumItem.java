package com.iratebear.apollo.playerutils;

import com.iratebear.apollo.imageutils.ImageItem;
import com.spotify.protocol.types.ImageUri;

import java.util.List;

import kaaes.spotify.webapi.android.models.AlbumSimple;

public class PlayAlbumItem implements IPlayQueueItem{

    private AlbumSimple album;
    private ImageItem image;

    public PlayAlbumItem(AlbumSimple album) {

        this.album = album;
    }

    @Override
    public String getUri() {
        return album.uri;
    }

    @Override
    public String getImageUri() {
        return album.images.get(0).url;
    }

    @Override
    public ImageUri getImageURI() {
        return null;
    }

    @Override
    public ImageItem getImage() {
        return image;
    }

    @Override
    public void setImage(ImageItem image) {
        image = image;
    }

    @Override
    public String getTitle() {
        return album.name;
    }

    @Override
    public String getArtists() {
        return album.album_type;
    }

    @Override
    public String ownerUri() {
        return null;
    }
}

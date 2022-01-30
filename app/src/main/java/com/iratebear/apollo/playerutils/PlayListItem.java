package com.iratebear.apollo.playerutils;

import com.iratebear.apollo.MainActivity;
import com.iratebear.apollo.imageutils.ImageItem;
import com.spotify.protocol.types.ImageUri;
import com.spotify.protocol.types.ListItem;

import java.util.List;

public class PlayListItem implements IPlayQueueItem{

    private ListItem item;

    public PlayListItem(ListItem item) {

        this.item = item;
    }

    @Override
    public String getUri() {
        return item.uri;
    }

    @Override
    public String getImageUri() {
        //System.out.println(item.imageUri.raw);
        return item.imageUri.raw;
    }

    @Override
    public ImageUri getImageURI() {
        return item.imageUri;
    }

    @Override
    public ImageItem getImage() {
        return null;
    }

    @Override
    public void setImage(ImageItem image) {

    }

    @Override
    public String getTitle() {
        return item.title;
    }

    @Override
    public String getArtists() {
        return item.subtitle;
    }

    @Override
    public String ownerUri() {
        return null;
    }

}

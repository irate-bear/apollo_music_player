package com.iratebear.apollo.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iratebear.apollo.MainActivity;
import com.iratebear.apollo.PlayItemActivity;
import com.iratebear.apollo.R;
import com.iratebear.apollo.playerutils.IPlayQueueItem;
import com.iratebear.apollo.playerutils.PlayListItem;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.types.ListItem;
import com.spotify.protocol.types.ListItems;

import java.util.ArrayList;
import java.util.List;

public class ChildItemAdapter extends RecyclerView.Adapter<ChildItemAdapter.ChildViewHolder> {

    private ListItems items;
    private Bitmap[] imageCache;

    public ChildItemAdapter(ListItems items) {
        this.items = items;
        imageCache = new Bitmap[items.total];
    }

    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChildViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_fragment_child, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChildViewHolder holder, int position) {
        ListItem item = items.items[position];
        int n = position;
        holder.txtTitle.setText(item.title);
        holder.txtCreators.setText(item.subtitle);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openIntent(view.getContext(), item);
            }
        });

        if (imageCache[n] == null) {
            MainActivity.mSpotifyAppRemote.getImagesApi().getImage(item.imageUri).setResultCallback(new CallResult.ResultCallback<Bitmap>() {
                @Override
                public void onResult(Bitmap bitmap) {
                    imageCache[n] = bitmap;
                    holder.imageCoverArt.setImageBitmap(bitmap);
                }
            });

        } else {
            holder.imageCoverArt.setImageBitmap(imageCache[n]);
        }
    }

    private void openIntent(Context context, ListItem item) {
        MainActivity.mSpotifyAppRemote.getContentApi().getChildrenOfItem(item, 100, 0)
                .setResultCallback(new CallResult.ResultCallback<ListItems>() {
                    @Override
                    public void onResult(ListItems items) {
                        List<IPlayQueueItem> playQueueItemsTracks = new ArrayList<>();
                        for (ListItem data : items.items) {
                            playQueueItemsTracks.add(new PlayListItem(data));
                        }
                        PlayItemActivity.tracks = playQueueItemsTracks;
                        PlayItemActivity.player = MainActivity.player;
                        PlayItemActivity.playlist = new PlayListItem(item);
                        Intent intent = new Intent(context, PlayItemActivity.class);
                        context.startActivity(intent);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return items.total;
    }

    public class ChildViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle;
        TextView txtCreators;
        ImageView imageCoverArt;

        public ChildViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtHomeTitle);
            txtCreators = itemView.findViewById(R.id.txtHomeCreator);
            imageCoverArt = itemView.findViewById(R.id.imageHomeCoverArt);
        }
    }
}

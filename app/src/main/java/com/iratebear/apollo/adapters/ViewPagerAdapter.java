package com.iratebear.apollo.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.iratebear.apollo.MainActivity;
import com.iratebear.apollo.R;
import com.iratebear.apollo.imageutils.ImageItem;
import com.iratebear.apollo.playerutils.IPlayQueueItem;
import com.spotify.protocol.client.CallResult;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.PagerViewHolder> {
    private List<IPlayQueueItem> items;
    private Context ctx;

    public ViewPagerAdapter(Context ctx) {
        this.ctx = ctx;
        this.items = new ArrayList<>();
    }

    public void setItems(List<IPlayQueueItem> playQueueItems) {
        items = playQueueItems;
    }

    public List<IPlayQueueItem> getItems() {
        return items;
    }

    public int getItemIndex(String uri) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getUri().equals(uri)) {
                return i;
            }
        }
        return -1;
    }
    public int getColor(int index) {
        return items.get(index).getImage().getColor();
    }

    public void addItems(IPlayQueueItem playQueueItem) {
        items.add(playQueueItem);
    }

    @NonNull
    @Override
    public PagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.view_pager_item, parent, false);
        return new PagerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PagerViewHolder holder, int position) {
        int n = position;
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                if ((items.size() != 0) && (n < items.size())) {
                    items.get(n).setImage(new ImageItem(bitmap, bitmap.getPixel(200, 200)));
                }
                holder.imageView.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        if (items.get(n).getImageURI() != null) {
            MainActivity.mSpotifyAppRemote.getImagesApi().getImage(items.get(n).getImageURI())
                    .setResultCallback(new CallResult.ResultCallback<Bitmap>() {
                        @Override
                        public void onResult(Bitmap bitmap) {
                            if ((items.size() != 0) && (n < items.size())) {
                                items.get(n).setImage(new ImageItem(bitmap, bitmap.getPixel(200, 200)));
                            }
                            holder.imageView.setImageBitmap(bitmap);
                        }
                    });
        }
        else if (items.get(position).getImage() == null) {
            Picasso.get().load(items.get(position).getImageUri()).into(target);
        } else {
            holder.imageView.setImageBitmap(items.get(position).getImage().getBitmap());
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public static class PagerViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public PagerViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}

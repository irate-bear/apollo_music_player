package com.iratebear.apollo.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iratebear.apollo.MainActivity;
import com.iratebear.apollo.R;
import com.iratebear.apollo.RecyclerViewTouchListener;
import com.iratebear.apollo.playerutils.IPlayQueueItem;
import com.spotify.protocol.client.CallResult;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ListViewHeaderAdapter extends RecyclerView.Adapter<ListViewHeaderAdapter.ListViewHeaderHolder> {
    private List<IPlayQueueItem> items;
    private Context ctx;
    private RecyclerViewTouchListener.ClickListener listener;

    public ListViewHeaderAdapter(Context ctx, RecyclerViewTouchListener.ClickListener listener) {
        this.ctx = ctx;
        this.items = new ArrayList<>();
        this.listener = listener;
    }

    public void setItems(List<IPlayQueueItem> playQueueItems) {
        items = playQueueItems;
    }

    @Override
    public ListViewHeaderAdapter.ListViewHeaderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.play_queue_item_header, parent, false);
        return new ListViewHeaderHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHeaderHolder holder, int position) {
        int n = position;
        holder.txtName.setText(items.get(position).getArtists());
        holder.txtTitle.setText(items.get(position).getTitle());
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                holder.imageView.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                System.out.println(e.toString());
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        if(items.get(n).getImageUri().split(":")[0].equals("spotify")) {
            MainActivity.mSpotifyAppRemote.getImagesApi().getImage(items.get(n).getImageURI()).setResultCallback(new CallResult.ResultCallback<Bitmap>() {
                @Override
                public void onResult(Bitmap bitmap) {
                    holder.imageView.setImageBitmap(bitmap);
                }
            });
        } else {
            Picasso.get().load(items.get(n).getImageUri()).into(target);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public static class ListViewHeaderHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView imageView;
        TextView txtTitle;
        TextView txtName;
        Button btnPlay;
        WeakReference<RecyclerViewTouchListener.ClickListener> clickListener;

        public ListViewHeaderHolder(@NonNull View itemView, RecyclerViewTouchListener.ClickListener listener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewCoverArt);
            txtTitle = itemView.findViewById(R.id.txtQueueItemTitle);
            txtName = itemView.findViewById(R.id.txtQueueItemName);
            btnPlay = itemView.findViewById(R.id.btnQueueItemPlay);
            clickListener = new WeakReference<>(listener);
            btnPlay.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            clickListener.get().onClick(view, getBindingAdapterPosition());
        }
    }
}

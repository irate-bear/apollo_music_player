package com.iratebear.apollo.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iratebear.apollo.MainActivity;
import com.iratebear.apollo.R;
import com.iratebear.apollo.imageutils.ImageItem;
import com.iratebear.apollo.playerutils.IPlayQueueItem;
import com.spotify.protocol.client.CallResult;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

public class ListViewAdapter extends RecyclerView.Adapter<ListViewAdapter.ListViewHolder> {
    private List<IPlayQueueItem> playList;
    private Context context;
    private  boolean isPlayQueue;

    public ListViewAdapter(Context context, List<IPlayQueueItem> playList, boolean isPlayQueue) {
        this.context = context;
        this.playList = playList;
        this.isPlayQueue = isPlayQueue;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.play_queue_item, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {

        int n = position;

        holder.listItemTitle.setText(playList.get(position).getTitle());
        holder.listItemArtist.setText(playList.get(position).getArtists());

        if (MainActivity.player.GetCurrent() == n) {
            holder.listItemTitle.setTextColor(Color.WHITE);
            holder.listItemArtist.setTextColor(Color.WHITE);
        } else {
            holder.listItemTitle.setTextColor(-1275068417);
            holder.listItemArtist.setTextColor(-1275068417);
        }

        if (!isPlayQueue) {
            holder.btnReorder.setVisibility(View.INVISIBLE);
            holder.listItemTitle.setTextColor(Color.WHITE);
            holder.listItemArtist.setTextColor(Color.WHITE);
        } else {
            holder.btnReorder.setVisibility(View.VISIBLE);
        }

        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                if ((playList.size() != 0) && (n < playList.size())) {
                    playList.get(n).setImage(new ImageItem(bitmap, bitmap.getPixel(200, 200)));
                }
                holder.trackImage.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };

        if (playList.get(position).getImageUri().equals("")) {
            holder.trackImage.setImageResource(R.drawable.ic_black_sun);
        }
        else if(playList.get(n).getImageUri().split(":")[0].equals("spotify")) {
            MainActivity.mSpotifyAppRemote.getImagesApi().getImage(playList.get(n).getImageURI()).setResultCallback(new CallResult.ResultCallback<Bitmap>() {
                @Override
                public void onResult(Bitmap bitmap) {
                    holder.trackImage.setImageBitmap(bitmap);
                }
            });
        }
        else if (playList.get(position).getImage() == null) {
            Picasso.get().load(playList.get(position).getImageUri()).into(target);
        } else {
            holder.trackImage.setImageBitmap(playList.get(position).getImage().getBitmap());
        }

    }

    @Override
    public int getItemCount() {
        return playList.size();
    }

    public List<IPlayQueueItem> getPlayItems() {
        return playList;
    }

    public void setPlayItems(List<IPlayQueueItem> playList) {
        this.playList = playList;
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        TextView listItemTitle;
        TextView listItemArtist;
        ImageView trackImage;
        ImageButton btnReorder;
        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            listItemTitle = itemView.findViewById(R.id.queueItemTitle);
            listItemArtist = itemView.findViewById(R.id.queueItemArtist);
            trackImage = itemView.findViewById(R.id.trackCoverArt);
            btnReorder = itemView.findViewById(R.id.btnReOrder);
        }
    }
}

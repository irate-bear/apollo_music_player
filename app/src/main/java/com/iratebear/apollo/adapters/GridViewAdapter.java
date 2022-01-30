package com.iratebear.apollo.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import org.w3c.dom.Text;

import java.util.List;

public class GridViewAdapter extends RecyclerView.Adapter<GridViewAdapter.GridViewHolder> {

    private Context context;
    private List<IPlayQueueItem> items;

    public GridViewAdapter(Context context, List<IPlayQueueItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public GridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GridViewHolder(LayoutInflater.from(context).inflate(R.layout.home_fragment_child, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GridViewHolder holder, int position) {
        IPlayQueueItem item = items.get(position);
        holder.title.setText(item.getTitle());
        holder.name.setText(item.getArtists());
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                holder.cover.setImageBitmap(bitmap);
                item.setImage(new ImageItem(bitmap, bitmap.getPixel(200,200)));
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };

        if (item.getImage() != null) {
            holder.cover.setImageBitmap(item.getImage().getBitmap());
        }
        else if (item.getImageURI() != null) {
            MainActivity.mSpotifyAppRemote.getImagesApi().getImage(item.getImageURI()).setResultCallback(new CallResult.ResultCallback<Bitmap>() {
                @Override
                public void onResult(Bitmap bitmap) {
                    holder.cover.setImageBitmap(bitmap);
                    item.setImage(new ImageItem(bitmap, bitmap.getPixel(200,200)));
                }
            });
        }
        else {
            Picasso.get().load(item.getUri()).into(target);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class GridViewHolder extends  RecyclerView.ViewHolder {
        TextView title;
        TextView name;
        ImageView cover;
        public GridViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.txtHomeTitle);
            name = itemView.findViewById(R.id.txtHomeCreator);
            cover = itemView.findViewById(R.id.imageHomeCoverArt);
        }
    }
}

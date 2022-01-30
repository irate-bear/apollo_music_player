package com.iratebear.apollo.adapters;

import android.content.Context;
import android.content.Intent;
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
import com.iratebear.apollo.PlayItemActivity;
import com.iratebear.apollo.R;
import com.iratebear.apollo.imageutils.ImageItem;
import com.iratebear.apollo.playerutils.IPlayQueueItem;
import com.iratebear.apollo.playerutils.PlayListItem;
import com.iratebear.apollo.playerutils.PlayQueueItem;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.types.ListItem;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.Playlist;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.TrackSimple;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ListViewPlayItemAdapter extends RecyclerView.Adapter<ListViewPlayItemAdapter.ListViewHolder> {
    private List<IPlayQueueItem> playList;
    private Context context;
    private String type;
    private  boolean isPlayQueue;

    public ListViewPlayItemAdapter(Context context, List<IPlayQueueItem> playList, boolean isPlayQueue, String type) {
        this.context = context;
        this.playList = playList;
        this.isPlayQueue = isPlayQueue;
        this.type = type;
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                char val = type.charAt(0);
                switch (val) {
                    case 'T':
                        getTracks(playList.get(n));
                        break;
                    case 'A':
                        getAlbumTracks(playList.get(n));
                        break;
                    case 'P':
                        getPlaylistTracks(playList.get(n));
                        break;
                    default:
                        break;
                }

            }
        });

    }

    private void getPlaylistTracks(IPlayQueueItem item) {
        MainActivity.spotify.getPlaylist(item.ownerUri(), item.getUri().split(":")[2], new Callback<Playlist>() {
            @Override
            public void success(Playlist playlist, Response response) {
                List<IPlayQueueItem> items = new ArrayList<>();
                for (PlaylistTrack playlistTrack : playlist.tracks.items) {
                    items.add(new PlayQueueItem(playlistTrack.track, playlist.images.get(0).url));
                }
                openIntent(item, items);
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });
    }

    private void getTracks(IPlayQueueItem item) {
        List<IPlayQueueItem> items = new ArrayList<>();
        items.add(item);
        openIntent(item, items);
    }

    private void getAlbumTracks(IPlayQueueItem item) {
        MainActivity.spotify.getAlbum(item.getUri().split(":")[2], new Callback<Album>() {
            @Override
            public void success(Album album, Response response) {
                List<IPlayQueueItem> items = new ArrayList<>();
                for (TrackSimple track : album.tracks.items) {
                    items.add(new PlayQueueItem(track, album.images.get(0).url));
                }
                openIntent(item, items);
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });
    }

    private void openIntent(IPlayQueueItem item, List<IPlayQueueItem> tracks) {
        PlayItemActivity.tracks = tracks;
        PlayItemActivity.player = MainActivity.player;
        PlayItemActivity.playlist = item;
        Intent intent = new Intent(context, PlayItemActivity.class);
        context.startActivity(intent);
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

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iratebear.apollo.MainActivity;
import com.iratebear.apollo.R;
import com.iratebear.apollo.imageutils.ImageItem;
import com.iratebear.apollo.playerutils.IPlayQueueItem;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.types.ListItems;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;
import java.util.Locale;

public class ListViewSimpleAdapter extends  RecyclerView.Adapter<ListViewSimpleAdapter.SimpleListViewHolder> {
    private Context context;
    private List<String> items;

    public ListViewSimpleAdapter(Context context, List<String> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ListViewSimpleAdapter.SimpleListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ListViewSimpleAdapter.SimpleListViewHolder(LayoutInflater.from(context).inflate(R.layout.simple_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewSimpleAdapter.SimpleListViewHolder holder, int position) {
        String item = items.get(position);
        holder.title.setText(item.toUpperCase(Locale.ROOT));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class SimpleListViewHolder extends  RecyclerView.ViewHolder {
        TextView title;
        public SimpleListViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.txtSimpleTitle);
        }
    }
}

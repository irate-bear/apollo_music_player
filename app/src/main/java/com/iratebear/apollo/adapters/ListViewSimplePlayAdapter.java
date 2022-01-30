package com.iratebear.apollo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iratebear.apollo.R;
import com.iratebear.apollo.playerutils.IPlayQueueItem;

import java.util.List;
import java.util.Locale;

public class ListViewSimplePlayAdapter extends  RecyclerView.Adapter<ListViewSimplePlayAdapter.SimpleListViewHolder> {
    private Context context;
    private List<IPlayQueueItem> items;

    public ListViewSimplePlayAdapter(Context context, List<IPlayQueueItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ListViewSimplePlayAdapter.SimpleListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ListViewSimplePlayAdapter.SimpleListViewHolder(LayoutInflater.from(context).inflate(R.layout.simple_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewSimplePlayAdapter.SimpleListViewHolder holder, int position) {
        IPlayQueueItem item = items.get(position);
        holder.title.setText(item.getTitle());
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

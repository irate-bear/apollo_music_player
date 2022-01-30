package com.iratebear.apollo.adapters;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iratebear.apollo.R;
import com.iratebear.apollo.playerutils.IPlayQueueItem;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.SearchResultsViewHolder> {

    private Context context;
    private List<Pair<String, List<IPlayQueueItem>>> items;

    public SearchResultsAdapter(Context context, List<Pair<String, List<IPlayQueueItem>>> items) {

        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public SearchResultsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchResultsViewHolder(LayoutInflater.from(context).inflate(R.layout.home_fragment_parent, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultsViewHolder holder, int position) {
        Pair<String, List<IPlayQueueItem>> item = items.get(position);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        ListViewPlayItemAdapter adapter = new ListViewPlayItemAdapter(context, item.second, false, item.first);
        holder.textView.setText(item.first);
        holder.recyclerView.setLayoutManager(layoutManager);
        holder.recyclerView.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class SearchResultsViewHolder extends RecyclerView.ViewHolder{
        RecyclerView recyclerView;
        TextView textView;

        public SearchResultsViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.childRecyclerView);
            textView = itemView.findViewById(R.id.txtParentTitle);
        }
    }
}

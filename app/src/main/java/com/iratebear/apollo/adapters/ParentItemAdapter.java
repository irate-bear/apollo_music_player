package com.iratebear.apollo.adapters;

import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iratebear.apollo.MainActivity;
import com.iratebear.apollo.R;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.types.ListItem;
import com.spotify.protocol.types.ListItems;

public class ParentItemAdapter extends RecyclerView.Adapter<ParentItemAdapter.ParentViewHolder> {

    private ListItem[] categoryItems;
    private Context context;
    private boolean isLinear;

    public ParentItemAdapter(Context context, ListItem[] categoryItems, boolean isLinear) {
        this.categoryItems = categoryItems;
        this.context = context;
        this.isLinear = isLinear;
    }

    @NonNull
    @Override
    public ParentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ParentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.home_fragment_parent, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ParentViewHolder holder, int position) {
        ListItem item = categoryItems[position];
        holder.txtTitle.setText(item.title);

        RecyclerView.LayoutManager layoutManager;
        if (isLinear) {
            layoutManager = new LinearLayoutManager(holder.childRecyclerView.getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        } else {
            layoutManager = new GridLayoutManager(holder.childRecyclerView.getContext(), 2);
            holder.childRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                    super.getItemOffsets(outRect, view, parent, state);
                    outRect.left = 5;
                    outRect.right = 5;
                    outRect.bottom = 5;
                    outRect.top = 5;
                }
            });
        }

        MainActivity.mSpotifyAppRemote.getContentApi().getChildrenOfItem(item,
                50, 0).setResultCallback(new CallResult.ResultCallback<ListItems>() {
            @Override
            public void onResult(ListItems data) {
                ChildItemAdapter childItemAdapter = new ChildItemAdapter(data);
                holder.childRecyclerView.setLayoutManager(layoutManager);
                holder.childRecyclerView.setAdapter(childItemAdapter);
            }
        });

    }

    @Override
    public int getItemCount() {
        return categoryItems.length;
    }

    public class ParentViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle;
        RecyclerView childRecyclerView;
        public ParentViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtParentTitle);
            childRecyclerView = itemView.findViewById(R.id.childRecyclerView);
        }
    }
}

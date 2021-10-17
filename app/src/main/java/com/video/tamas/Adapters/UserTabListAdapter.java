package com.video.tamas.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.video.tamas.Activities.ViewUserProfileActivity;
import com.video.tamas.Models.search.User;
import com.video.tamas.R;

import java.util.List;

public class UserTabListAdapter extends RecyclerView.Adapter<UserTabListAdapter.UserTabViewHolder> {
    private Context context;
    private List<User> list;

    public UserTabListAdapter(Context context, List<User> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public UserTabViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_tab_list_layout, viewGroup, false);
        return new UserTabViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserTabViewHolder holder, int i) {
        User user = list.get(i);
        holder.textView.setText(user.getUsername());
        Picasso.get().load(user.getPhotoURL()).placeholder(R.drawable.ic_account_box_black_24dp).into(holder.imageView);
        holder.itemView.setOnClickListener(view -> {
            context.startActivity(new Intent(context, ViewUserProfileActivity.class).
                    putExtra("userId", user.getUserId()).
                    putExtra("isFollow", user.getUserFollow()).
                    putExtra("userName", user.getUsername())
            );
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class UserTabViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView;

        public UserTabViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tvUserTab);
            imageView = itemView.findViewById(R.id.ivUserTab);
        }
    }
}

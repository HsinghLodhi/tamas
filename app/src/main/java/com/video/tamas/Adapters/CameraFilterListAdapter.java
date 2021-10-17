package com.video.tamas.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.video.tamas.R;
import com.video.tamas.VideoRecordingUtil.Filters;
import com.video.tamas.VideoRecordingUtil.ImageFilter;

public class CameraFilterListAdapter extends RecyclerView.Adapter<CameraFilterListAdapter.FilterViewHolder> {
    private Context context;
    private Filters[] filters;
    private static OnSelectFilterListener onSelectFilterListener;


    public CameraFilterListAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public FilterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.camera_filter_list_layout, viewGroup, false);
        return new FilterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilterViewHolder holder, int i) {

        filters = Filters.values();
        holder.textView.setText(filters[i].name());
        holder.imageView.setImageBitmap(ImageFilter.getFilterInstance(filters[i], context));
        holder.itemView.setOnClickListener(view -> onSelectFilterListener.onSelectFilter(filters[i]));
    }

    @Override
    public int getItemCount() {
        return 15;
    }

    class FilterViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private ImageView imageView;

        public FilterViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textViewCameraFilterName);
            imageView = itemView.findViewById(R.id.ivCameraFilter);

        }
    }

    public interface OnSelectFilterListener {
        void onSelectFilter(Filters filters);
    }

    public static void setOnFilterListener(OnSelectFilterListener listener) {
        onSelectFilterListener = listener;
    }


}

package com.video.tamas.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.video.tamas.Models.TalentCategoryModel;
import com.video.tamas.R;

import java.util.List;

public class TalentCategoryListAdapter extends RecyclerView.Adapter<TalentCategoryListAdapter.TalentViewHolder> {
    private Context context;
    private List<TalentCategoryModel> talentCategoryModelList;
    private static OnMakeWithTalentCategoryListener onMakeWithTalentCategoryListener;
    private Dialog dialog;

    public TalentCategoryListAdapter(Context context, List<TalentCategoryModel> talentCategoryModelList, Dialog dialog) {
        this.context = context;
        this.talentCategoryModelList = talentCategoryModelList;
        this.dialog = dialog;
    }


    @NonNull
    @Override
    public TalentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.talent_cat_dialog_list_layout, viewGroup, false);
        return new TalentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TalentViewHolder holder, int position) {
        TalentCategoryModel talentCategoryModel = talentCategoryModelList.get(position);
        String catId = talentCategoryModel.getCatId();
        holder.tvTalentCatName.setText(talentCategoryModel.getCatName());
        holder.itemView.setOnClickListener(v -> {
            if (catId.equals("4")) {
                onMakeWithTalentCategoryListener.onMakeWithTalentCategory(true, position);
            } else {
                onMakeWithTalentCategoryListener.onMakeWithTalentCategory(false, position);
            }
            dialog.dismiss();
        });


    }

    @Override
    public int getItemCount() {
        return talentCategoryModelList.size();
    }


    class TalentViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTalentCatName;

        public TalentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTalentCatName = itemView.findViewById(R.id.tvTalentCatName);

        }
    }

    public interface OnMakeWithTalentCategoryListener {
        void onMakeWithTalentCategory(boolean makeType, int position);
    }

    public static void setOnMakeWithTalentCategoryListener(OnMakeWithTalentCategoryListener listener) {
        onMakeWithTalentCategoryListener = listener;
    }
}

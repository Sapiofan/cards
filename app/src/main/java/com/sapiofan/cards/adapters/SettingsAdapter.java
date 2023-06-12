package com.sapiofan.cards.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sapiofan.cards.R;

import java.util.List;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.ButtonViewHolder> {
    private List<String> settingsList;

    public SettingsAdapter(List<String> settingsList) {
        this.settingsList = settingsList;
    }

    @NonNull
    @Override
    public ButtonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.setting, parent, false);
        return new SettingsAdapter.ButtonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SettingsAdapter.ButtonViewHolder holder, int position) {
        String buttonText = settingsList.get(position);
        holder.button.setText(buttonText);
    }

    @Override
    public int getItemCount() {
        return settingsList.size();
    }

    public class ButtonViewHolder extends RecyclerView.ViewHolder {
        Button button;

        public ButtonViewHolder(@NonNull View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.button);

//            itemView.setOnClickListener(v -> {
//                int position = getAdapterPosition();
//
//            });
        }
    }
}

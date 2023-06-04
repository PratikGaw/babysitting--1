package project.babysitting.babysiting.ViewHolders;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import project.babysitting.babysiting.databinding.RowMessageBinding;

public class ChatViewHolder extends RecyclerView.ViewHolder {
    public RowMessageBinding binding;
    public ChatViewHolder(@NonNull RowMessageBinding itemView) {
        super(itemView.getRoot());
        this.binding = itemView;
    }
}

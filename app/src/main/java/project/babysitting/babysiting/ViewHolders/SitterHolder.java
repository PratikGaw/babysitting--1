package project.babysitting.babysiting.ViewHolders;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import project.babysitting.babysiting.databinding.SitterBinding;

public class SitterHolder extends RecyclerView.ViewHolder {
    public SitterBinding binding;
    public SitterHolder(@NonNull SitterBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}

package project.babysitting.babysiting.ViewHolders;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import project.babysitting.babysiting.databinding.UsersBinding;

public class UsersHolder extends RecyclerView.ViewHolder {
    public UsersBinding binding;
    public UsersHolder(@NonNull UsersBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}

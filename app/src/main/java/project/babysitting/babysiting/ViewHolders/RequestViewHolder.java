package project.babysitting.babysiting.ViewHolders;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import project.babysitting.babysiting.databinding.JobRequestLayoutBinding;

public class RequestViewHolder extends RecyclerView.ViewHolder {
    public JobRequestLayoutBinding binding;
    public RequestViewHolder(@NonNull JobRequestLayoutBinding itemView) {
        super(itemView.getRoot());
//        binding = JobRequestLayoutBinding.bind(itemView);
        this.binding = itemView;
    }
}

package project.babysitting.babysiting.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import project.babysitting.babysiting.Models.User;
import project.babysitting.babysiting.R;
import project.babysitting.babysiting.UserDatailsActivity;
import project.babysitting.babysiting.ViewHolders.UsersHolder;
import project.babysitting.babysiting.databinding.SitterBinding;
import project.babysitting.babysiting.databinding.UsersBinding;

public class UsersAdapter extends RecyclerView.Adapter<UsersHolder> {
    private ArrayList<User> uList;
    private Context context;

    public UsersAdapter(ArrayList<User> uList, Context context) {
        this.uList = uList;
        this.context = context;
    }

    @NonNull
    @Override
    public UsersHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        UsersBinding binding = UsersBinding.inflate(inflater,parent,false);
        return new UsersHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull UsersHolder holder, int position) {
        User user = uList.get(position);
        holder.binding.txtName.setText(user.getName());
        holder.binding.txtAddress.setText(user.getAddress());
        if (user.getImgUrl() != null){
            Picasso.get().load(user.getImgUrl()).into(holder.binding.profileImage);
        }else if(user.getGender().equals("Male")) {
            holder.binding.profileImage.setImageResource(R.drawable.man);
        }else {
            holder.binding.profileImage.setImageResource(R.drawable.female);
        }
        holder.binding.layoutDatails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, UserDatailsActivity.class);
                i.putExtra("uid",user.getUid());
                i.putExtra("gender",user.getGender());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return uList.size();
    }
}

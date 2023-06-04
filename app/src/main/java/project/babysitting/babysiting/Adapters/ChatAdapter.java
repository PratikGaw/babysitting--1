package project.babysitting.babysiting.Adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import project.babysitting.babysiting.Models.Message;
import project.babysitting.babysiting.ViewHolders.ChatViewHolder;
import project.babysitting.babysiting.databinding.JobRequestLayoutBinding;
import project.babysitting.babysiting.databinding.RowMessageBinding;
import soup.neumorphism.ShapeType;

public class ChatAdapter extends RecyclerView.Adapter<ChatViewHolder> {
    private Context context;
    private ArrayList<Message> messagesList;

    public ChatAdapter() {
    }

    public ChatAdapter(Context context, ArrayList<Message> messagesList) {
        this.context = context;
        this.messagesList = messagesList;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RowMessageBinding binding = RowMessageBinding.inflate(inflater,parent,false);
        return new ChatViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Message message = messagesList.get(position);
        holder.binding.txtMessage.setText(message.getMessage());
        if (message.getSenderId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            holder.binding.card1.setShapeType(ShapeType.FLAT);
            holder.binding.msgLayout.setGravity(Gravity.END);
        }else{
            holder.binding.card1.setShapeType(ShapeType.BASIN);
            holder.binding.msgLayout.setGravity(Gravity.START);
        }
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }
}

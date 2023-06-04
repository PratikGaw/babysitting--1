package project.babysitting.babysiting.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import project.babysitting.babysiting.ChatActivity;
import project.babysitting.babysiting.Models.Interests;
import project.babysitting.babysiting.R;
import project.babysitting.babysiting.databinding.JobRequestLayoutBinding;

public class UserRequestAdapter extends RecyclerView.Adapter<UserRequestAdapter.UserRequetViewHolder> {
    private Context context;
    private ArrayList<Interests> reqList;
    DatabaseReference sref;

    String name,type;

    public UserRequestAdapter(Context context, ArrayList<Interests> reqList) {
        this.context = context;
        this.reqList = reqList;
    }

    @NonNull
    @Override
    public UserRequetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        JobRequestLayoutBinding binding = JobRequestLayoutBinding.inflate(inflater,parent,false);
        return new UserRequetViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserRequetViewHolder holder, int position) {
        Interests interests = reqList.get(position);

        DocumentReference ref = FirebaseFirestore.getInstance().collection("BabySitter").document(interests.getSitterUid());
        sref = FirebaseDatabase.getInstance().getReference("Request").child(interests.getUid())
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                name = documentSnapshot.getString("name");
                String addr = documentSnapshot.getString("address");

                holder.binding.txtUserName.setText(name);
                holder.binding.txtUserAddress.setText(addr);
            }
        });


        if (interests.getStatus().equals("accept")){
            holder.binding.communicationLayout.setVisibility(View.VISIBLE);
            holder.binding.requestProccessLayout.setVisibility(View.GONE);
            holder.binding.btnCommunicate.setVisibility(View.VISIBLE);
            holder.binding.jobText.setVisibility(View.GONE);
        }else{
            holder.binding.communicationLayout.setVisibility(View.VISIBLE);
            holder.binding.requestProccessLayout.setVisibility(View.GONE);
            holder.binding.btnCommunicate.setVisibility(View.GONE);
            holder.binding.txtCommunication.setText("Pending");
            holder.binding.jobText.setText("Job request sent!");
            holder.binding.txtCommunication.setTextColor(context.getResources().getColor(R.color.txt_pending_color));
        }

        holder.binding.btnCommunicate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("reciverId",interests.getSitterUid());
                intent.putExtra("name",name);
                intent.putExtra("type",type);
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return reqList.size();
    }

    class UserRequetViewHolder extends RecyclerView.ViewHolder {
        public JobRequestLayoutBinding binding;
        public UserRequetViewHolder(@NonNull JobRequestLayoutBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}

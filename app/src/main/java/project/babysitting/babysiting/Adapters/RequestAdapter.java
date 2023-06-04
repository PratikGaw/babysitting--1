package project.babysitting.babysiting.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import project.babysitting.babysiting.ChatActivity;
import project.babysitting.babysiting.Models.Interests;
import project.babysitting.babysiting.ViewHolders.RequestViewHolder;
import project.babysitting.babysiting.databinding.JobRequestLayoutBinding;
import project.babysitting.babysiting.databinding.SitterBinding;

public class RequestAdapter extends RecyclerView.Adapter<RequestViewHolder> {
    private Context context;
    private ArrayList<Interests> reqList;
    DatabaseReference sref,userRef;

    String name,type;

    public RequestAdapter(Context context, ArrayList<Interests> reqList) {
        this.context = context;
        this.reqList = reqList;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        JobRequestLayoutBinding binding = JobRequestLayoutBinding.inflate(inflater,parent,false);
        return new RequestViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        Interests interests = reqList.get(position);
        DocumentReference ref = FirebaseFirestore.getInstance().collection("Users").document(interests.getUid());
        sref = FirebaseDatabase.getInstance().getReference("Request").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(interests.getUid());
        userRef = FirebaseDatabase.getInstance().getReference(interests.getUid()).child("MyRequest")
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
            holder.binding.jobText.setVisibility(View.GONE);
        }else{
            holder.binding.communicationLayout.setVisibility(View.GONE);
            holder.binding.requestProccessLayout.setVisibility(View.VISIBLE);
        }

        holder.binding.btnCommunicate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("reciverId",interests.getUid());
                intent.putExtra("name",name);
                context.startActivity(intent);
            }
        });

        holder.binding.btnAcceptRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,Object> updateStatus = new HashMap<>();
                updateStatus.put("status","accept");
                sref.updateChildren(updateStatus).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
//                        holder.binding.communicationLayout.setVisibility(View.VISIBLE);
//                        holder.binding.requestProccessLayout.setVisibility(View.GONE);
                        userRef.updateChildren(updateStatus).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                                holder.binding.communicationLayout.setVisibility(View.VISIBLE);
                                holder.binding.requestProccessLayout.setVisibility(View.GONE);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        holder.binding.btnCancelRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sref.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return reqList.size();
    }
}

package project.babysitting.babysiting.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import project.babysitting.babysiting.DatailsActivity;
import project.babysitting.babysiting.Models.Sitter;
import project.babysitting.babysiting.R;
import project.babysitting.babysiting.ViewHolders.SitterHolder;
import project.babysitting.babysiting.databinding.SitterBinding;

public class SitterAdapter extends RecyclerView.Adapter<SitterHolder> {
    private ArrayList<Sitter> sList;
    private Context context;

    private double rate = 0.0,count;
    private String value = "";

    public void setSearch(ArrayList<Sitter> searchList){
        this.sList = searchList;
        notifyDataSetChanged();
    }

    public SitterAdapter(ArrayList<Sitter> sList, Context context) {
        this.sList = sList;
        this.context = context;
    }

    @NonNull
    @Override
    public SitterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        SitterBinding binding = SitterBinding.inflate(inflater,parent,false);
        return new SitterHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SitterHolder holder, int position) {
        Sitter sitter = sList.get(position);
        holder.binding.txtName.setText(sitter.getName());
        holder.binding.txtAddress.setText(sitter.getAddress()+","+sitter.getPincode());
        if (sitter.getImgUrl() != null){
            Picasso.get().load(sitter.getImgUrl()).into(holder.binding.profileImage);
        }else if(sitter.getGender().equals("Male")) {
            holder.binding.profileImage.setImageResource(R.drawable.man);
        }else {
            holder.binding.profileImage.setImageResource(R.drawable.female);
        }
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Ratings")
                        .child(sitter.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    double totalRaters = Double.valueOf(snapshot.getChildrenCount());
                    ArrayList<Double> rateList = new ArrayList<>();
                    for (DataSnapshot ds : snapshot.getChildren()){
                        rate = Double.valueOf(ds.child("rate").getValue().toString());
                        rateList.add(rate);
                        double total = 0.0;
                        for (int i = 0; i < rateList.size(); i++) {
                            Log.d("rating", rateList.get(i)+"");
                            total += rateList.get(i);

                        }
                        total  = total / totalRaters;
                        Log.d("total", ""+total);
                        value = String.valueOf(total);
                        if (ds.child("sitterId").getValue().toString().equals(sitter.getUid())){
                            holder.binding.rating.setRating((float) total);
                        }
                    }
                }else {
                    value = "0.0";
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Faild to get"+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        holder.binding.layoutDatails.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.R)
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, DatailsActivity.class);
                i.putExtra("uid",sitter.getUid());
                i.putExtra("gender",sitter.getGender());
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return sList.size();
    }

//    @Override
//    public Filter getFilter() {
//        return new Filter() {
//            @Override
//            protected FilterResults performFiltering(CharSequence constraint) {
//                String pattern = constraint.toString().toLowerCase();
//                if(pattern.isEmpty()){
//                    sList = filterdSitterList;
//                } else {
//                    ArrayList<Sitter> filteredList = new ArrayList<>();
//                    for(Sitter tube: sList){
//                        if(tube.getName().toLowerCase().contains(pattern) || tube.getCity().toLowerCase().contains(pattern)) {
//                            filteredList.add(tube);
//                        }
//                    }
//                    filteredList = filterdSitterList;
//                }
//
//                FilterResults filterResults = new FilterResults();
//                filterResults.values = filterdSitterList;
//                return filterResults;
//            }
//
//            @Override
//            protected void publishResults(CharSequence constraint, FilterResults results) {
//                filterdSitterList = (ArrayList<Sitter>) results.values;
//                notifyDataSetChanged();
//            }
//        };
//    }

}

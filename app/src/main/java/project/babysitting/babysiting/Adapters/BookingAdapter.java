package project.babysitting.babysiting.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.Map;

import project.babysitting.babysiting.Models.Booking;
import project.babysitting.babysiting.R;
import project.babysitting.babysiting.ViewHolders.BookingViewHolder;
import project.babysitting.babysiting.configs.UserType;
import project.babysitting.babysiting.databinding.RowBookingBinding;
import project.babysitting.babysiting.databinding.RowMessageBinding;

public class BookingAdapter extends RecyclerView.Adapter<BookingViewHolder> {
    private ArrayList<Booking> bookList;
    private Context context;
    String uname,uaddr;

    public BookingAdapter() {
    }

    public BookingAdapter(ArrayList<Booking> bookList, Context context) {
        this.bookList = bookList;
        this.context = context;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RowBookingBinding binding = RowBookingBinding.inflate(inflater,parent,false);
        return new BookingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookList.get(position);
        DocumentReference ref = FirebaseFirestore.getInstance().collection("Users").document(booking.getUserId());
        ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                uname = documentSnapshot.getString("name");
                uaddr = documentSnapshot.getString("address");

                holder.binding.bookingUserName.setText("Booked By: "+uname);
                holder.binding.bookingUserAddress.setText("Where To Go: "+uaddr);
            }
        });
        holder.binding.bookingTitle.setText(booking.getTitle());
        holder.binding.bookingAmount.setText(booking.getAmount());
        holder.binding.bookingId.setText("Booking Id: "+booking.getBookingId());
        holder.binding.bookingDates.setText(booking.getCompleteDate());
        holder.binding.bookingTime.setText(booking.getTime());
        holder.binding.bookingHours.setText(booking.getHours());
        String type = new UserType().getType(context);
        if (type.equals("user")){
            holder.binding.bookingUserName.setVisibility(View.GONE);
            holder.binding.bookingUserAddress.setVisibility(View.GONE);
            holder.binding.layout2.setVisibility(View.GONE);
            holder.binding.bookingStatus.setVisibility(View.VISIBLE);
        }else{
            holder.binding.bookingUserName.setVisibility(View.VISIBLE);
            holder.binding.bookingUserAddress.setVisibility(View.VISIBLE);
            holder.binding.layout2.setVisibility(View.VISIBLE);
            holder.binding.bookingStatus.setVisibility(View.GONE);
        }
        if (booking.getStatus().equals("pending")){
            holder.binding.bookingStatus.setTextColor(context.getResources().getColor(R.color.txt_pending_color));
            holder.binding.bookingStatus.setText("Booking Pending");
        }else if(booking.getStatus().equals("reject")){
            holder.binding.layout2.setVisibility(View.GONE);
            holder.binding.bookingStatus.setVisibility(View.VISIBLE);
            holder.binding.bookingStatus.setTextColor(context.getResources().getColor(R.color.txt_reject_color));
            holder.binding.bookingStatus.setText("Booking Rejected");
        }else{
            holder.binding.layout2.setVisibility(View.GONE);
            holder.binding.bookingStatus.setVisibility(View.VISIBLE);
            holder.binding.bookingStatus.setTextColor(context.getResources().getColor(R.color.txt_accept_color));
            holder.binding.bookingStatus.setText("Booking Accepted");
        }




        holder.binding.btnAcceptBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Bookings")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(booking.getBookingId());
                DatabaseReference uRef = FirebaseDatabase.getInstance().getReference(booking.getUserId())
                        .child("MyBookings").child(booking.getBookingId());
                Map<String, Object> bookMap = new HashMap<>();
                bookMap.put("status","accept");
                userRef.updateChildren(bookMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        notifyDataSetChanged();
                        Toast.makeText(context, "Booking Accepted", Toast.LENGTH_SHORT).show();
                        uRef.updateChildren(bookMap);
                    }
                });
            }
        });

        holder.binding.btnCancelBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyDataSetChanged();
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Bookings")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(booking.getBookingId());
                DatabaseReference uRef = FirebaseDatabase.getInstance().getReference(booking.getUserId())
                        .child("MyBookings").child(booking.getBookingId());
                Map<String, Object> bookMap = new HashMap<>();
                bookMap.put("status","reject");
                userRef.updateChildren(bookMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Booking Accepted", Toast.LENGTH_SHORT).show();
                        uRef.updateChildren(bookMap);
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }
}

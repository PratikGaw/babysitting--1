package project.babysitting.babysiting.UserFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import project.babysitting.babysiting.Adapters.BookingAdapter;
import project.babysitting.babysiting.Models.Booking;
import project.babysitting.babysiting.R;
import project.babysitting.babysiting.databinding.FragmentMyBookingBinding;


public class MyBookingFragment extends Fragment {
    private FragmentMyBookingBinding binding;
    private BookingAdapter adapter;
    private ArrayList<Booking> bookingList;
    public MyBookingFragment() {
        // Required empty public constructor
    }
    

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMyBookingBinding.inflate(inflater, container, false);
        bookingList = new ArrayList<>();
        adapter = new BookingAdapter(bookingList,getContext());
        binding.rvMyBookings.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvMyBookings.setHasFixedSize(true);
        binding.rvMyBookings.setAdapter(adapter);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("MyBookings");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookingList.clear();
                adapter.notifyDataSetChanged();
                if (snapshot.exists()){
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Booking booking = ds.getValue(Booking.class);
                        bookingList.add(booking);
                    }
                    adapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(getActivity(), "Booking Not Availables", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        
        return binding.getRoot();
    }
}
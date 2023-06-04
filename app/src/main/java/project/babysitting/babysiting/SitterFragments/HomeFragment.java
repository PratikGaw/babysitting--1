package project.babysitting.babysiting.SitterFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import project.babysitting.babysiting.Adapters.BookingAdapter;
import project.babysitting.babysiting.Models.Booking;
import project.babysitting.babysiting.R;
import project.babysitting.babysiting.configs.UserType;
import project.babysitting.babysiting.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    FirebaseFirestore firestore;
    BookingAdapter adapter;
    ArrayList<Booking> bList;
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        firestore = FirebaseFirestore.getInstance();
        binding.rvSitter.setHasFixedSize(true);
        bList = new ArrayList<>();
        adapter = new BookingAdapter(bList,getContext());
        binding.rvSitter.setAdapter(adapter);
        binding.progressBar.setVisibility(ProgressBar.VISIBLE);
        String type = new UserType().getType(getContext());
        Log.d("userType", type);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Bookings")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bList.clear();
                adapter.notifyDataSetChanged();
                if (snapshot.exists()){
                    binding.progressBar.setVisibility(View.GONE);
                    for(DataSnapshot ds : snapshot.getChildren()){
                        Booking booking = ds.getValue(Booking.class);
                        bList.add(booking);
                    }
                }else{
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Not Any Booking available!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return binding.getRoot();
    }
}
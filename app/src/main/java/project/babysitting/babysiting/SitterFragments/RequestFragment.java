package project.babysitting.babysiting.SitterFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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

import project.babysitting.babysiting.Adapters.RequestAdapter;
import project.babysitting.babysiting.Models.Interests;
import project.babysitting.babysiting.R;
import project.babysitting.babysiting.databinding.FragmentRequestBinding;


public class RequestFragment extends Fragment {
    private FragmentRequestBinding binding;
    private ArrayList<Interests> reqList;
    private RequestAdapter adapter;
    private DatabaseReference reqRef;
    private FirebaseAuth auth;
    public RequestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRequestBinding.inflate(inflater, container, false);
        reqList = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        binding.rvUserRequest.setHasFixedSize(true);
        adapter = new RequestAdapter(getActivity(),reqList);
        binding.rvUserRequest.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        reqRef = FirebaseDatabase.getInstance().getReference("Request").child(auth.getCurrentUser().getUid());
        reqRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reqList.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    Interests interests = ds.getValue(Interests.class);
                    reqList.add(interests);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return binding.getRoot();
    }
}
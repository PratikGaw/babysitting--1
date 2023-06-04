package project.babysitting.babysiting.UserFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import project.babysitting.babysiting.Adapters.SitterAdapter;
import project.babysitting.babysiting.Models.Sitter;
import project.babysitting.babysiting.R;
import project.babysitting.babysiting.databinding.FragmentHomeBinding;
import project.babysitting.babysiting.databinding.FragmentUserHomeBinding;


public class UserHomeFragment extends Fragment {
    private FragmentUserHomeBinding binding;
    FirebaseFirestore firestore;
    SitterAdapter adapter;
    ArrayList<Sitter> sList;
    public UserHomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUserHomeBinding.inflate(inflater, container, false);
        firestore = FirebaseFirestore.getInstance();
        binding.rvSitter.setHasFixedSize(true);
        sList = new ArrayList<>();
        adapter = new SitterAdapter(sList, getContext());
        binding.rvSitter.setAdapter(adapter);
        binding.progressBar.setVisibility(ProgressBar.VISIBLE);

        firestore.collection("BabySitter").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.isEmpty()){
                            Toast.makeText(getContext(), "Data Not Found", Toast.LENGTH_SHORT).show();
                        }else{
                            binding.progressBar.setVisibility(View.GONE);
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                Sitter s = d.toObject(Sitter.class);
                                sList.add(s);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        binding.edtSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchSiiter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return binding.getRoot();
    }

    private void searchSiiter(CharSequence s) {
        ArrayList<Sitter> searchedList = new ArrayList<>();
        for (Sitter sitter : sList){
            if (sitter.getName().toLowerCase().contains(s) || sitter.getCity().toLowerCase().contains(s)
                    || sitter.getPincode().toLowerCase().contains(s)){
                searchedList.add(sitter);
            }
        }
        if (searchedList.isEmpty()){
            Toast.makeText(getActivity(), "No Sitter Found!", Toast.LENGTH_SHORT).show();
        }else{
            adapter.setSearch(searchedList);
        }
    }
}
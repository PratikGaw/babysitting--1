package project.babysitting.babysiting.configs;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import project.babysitting.babysiting.OnBoardingActivity;
import project.babysitting.babysiting.R;
import project.babysitting.babysiting.SitterFragments.HomeFragment;
import project.babysitting.babysiting.SitterFragments.SitterAboutUsFragment;
import project.babysitting.babysiting.SitterFragments.SitterContactUsFragment;
import project.babysitting.babysiting.databinding.MenuSitterBinding;

public class MenuForSitter extends BottomSheetDialogFragment {
    private MenuSitterBinding binding;
    private BottomSheetBehavior sheetBehavior;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = MenuSitterBinding.inflate(inflater,container,false);
        binding.btnMyBookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container_fragment,new HomeFragment())
                        .commit();
                dismiss();
            }
        });
        binding.btnAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container_fragment,new SitterAboutUsFragment())
                        .commit();
                dismiss();
            }
        });

        binding.btnCantactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container_fragment,new SitterContactUsFragment())
                        .commit();
                dismiss();
            }
        });
        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference tokenRef = FirebaseDatabase.getInstance().getReference("Tokens").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                tokenRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            tokenRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    FirebaseAuth.getInstance().signOut();
                                    Intent i = new Intent(getActivity(), OnBoardingActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    i.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);
                                    getActivity().finish();
                                }
                            });
                        }else {
                            FirebaseAuth.getInstance().signOut();
                            Intent i = new Intent(getActivity(),OnBoardingActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            i.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            getActivity().finish();
                            dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        return binding.getRoot();
    }

    public MenuForSitter() {
        super();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }



}

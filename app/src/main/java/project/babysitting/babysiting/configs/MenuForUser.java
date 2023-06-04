package project.babysitting.babysiting.configs;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import project.babysitting.babysiting.OnBoardingActivity;
import project.babysitting.babysiting.R;
import project.babysitting.babysiting.SitterFragments.SitterAboutUsFragment;
import project.babysitting.babysiting.SitterFragments.SitterContactUsFragment;
import project.babysitting.babysiting.UserFragments.MyBookingFragment;
import project.babysitting.babysiting.databinding.MenuUserBinding;

public class MenuForUser extends BottomSheetDialogFragment {
    MenuUserBinding binding;
    Permissions permissions;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = MenuUserBinding.inflate(inflater,container,false);
        permissions = new Permissions();
        binding.btnMyBookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment,new MyBookingFragment()).commit();
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
                                    ((Activity)getContext()).finish();
                                    dismiss();
                                }
                            });
                        }else {
                            FirebaseAuth.getInstance().signOut();
                            Intent i = new Intent(getActivity(),OnBoardingActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            i.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            ((Activity)getContext()).finish();
                            dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()){
                binding.storagePermisionSwitch.setChecked(true);
            }else {
                binding.storagePermisionSwitch.setChecked(false);
            }
        }else {
            int permission = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permission == PackageManager.PERMISSION_GRANTED){
                binding.storagePermisionSwitch.setChecked(true);
            }else {
                binding.storagePermisionSwitch.setChecked(false);
            }
        }


        binding.storagePermisionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        if (Environment.isExternalStorageManager()){
                            permissions.storagePermisionsForGreaterThanAndroid11(getActivity(),220);
                        }else {
                            permissions.storagePermisionsForLessThanAndroid11(getActivity(),220);
                        }
                    }
                }else {
                    Toast.makeText(getContext(), "Noting able to disable permission go your settings or app info and disable permisions!", Toast.LENGTH_LONG).show();
                }
            }
        });

        binding.notificationPermisionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (isChecked) {
                        permissions.notificationPermisionsForAndroid11(getActivity(),230);
                    } else {
                        Toast.makeText(getContext(), "Go to App info and off notification", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        return binding.getRoot();
    }
}

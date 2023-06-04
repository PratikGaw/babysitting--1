package project.babysitting.babysiting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

import project.babysitting.babysiting.FirebaseMessaging.Tokens;
import project.babysitting.babysiting.SitterFragments.HomeFragment;
import project.babysitting.babysiting.SitterFragments.ProfileFragment;
import project.babysitting.babysiting.SitterFragments.RequestFragment;
import project.babysitting.babysiting.configs.LoadingBar;
import project.babysitting.babysiting.configs.MenuForSitter;
import project.babysitting.babysiting.databinding.ActivitySitterHomeBinding;
import project.babysitting.babysiting.databinding.MenuSitterBinding;

public class SitterHomeActivity extends AppCompatActivity {
    ActivitySitterHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySitterHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportFragmentManager().beginTransaction().add(R.id.container_fragment,new HomeFragment()).commit();

        generateDeviceToken();

        binding.bottomNav.btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment,new HomeFragment()).commit();
            }
        });

        binding.bottomNav.btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment,new RequestFragment()).commit();
            }
        });

        binding.bottomNav.btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment,new ProfileFragment()).commit();
            }
        });

        LoadingBar loadingBar = new LoadingBar(this);

        binding.bottomNav.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingBar.showDialog("Logged Out");
                DatabaseReference tokenRef = FirebaseDatabase.getInstance().getReference("Tokens").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                tokenRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            Map<String,Object> tokenMap = new HashMap<>();
                            tokenMap.put("token","12345");
                            tokenRef.updateChildren(tokenMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    loadingBar.dissmissDialog();
                                    FirebaseAuth.getInstance().signOut();
                                    Intent i = new Intent(SitterHomeActivity.this,OnBoardingActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    i.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);
                                    finish();
                                }
                            });
                        }else {
                            FirebaseAuth.getInstance().signOut();
                            Intent i = new Intent(SitterHomeActivity.this,OnBoardingActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            i.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
//                FirebaseAuth.getInstance().signOut();
//                Intent i = new Intent(SitterHomeActivity.this,OnBoardingActivity.class);
//                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                i.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(i);
//                finish();
            }
        });

        binding.bottomNav.btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MenuForSitter menuForSitter = new MenuForSitter();
                menuForSitter.show(getSupportFragmentManager(),"SitterMenu");
            }
        });

    }

    private void generateDeviceToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                Log.d("token", s);
                DatabaseReference tokenRef = FirebaseDatabase.getInstance().getReference("Tokens")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                Tokens tokens = new Tokens();
                tokens.setToken(s);
                tokenRef.setValue(tokens);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SitterHomeActivity.this, "token"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import project.babysitting.babysiting.FirebaseMessaging.Tokens;
import project.babysitting.babysiting.UserFragments.UserHomeFragment;
import project.babysitting.babysiting.UserFragments.UserProfileFragment;
import project.babysitting.babysiting.UserFragments.UserRequestFragment;
import project.babysitting.babysiting.configs.LoadingBar;
import project.babysitting.babysiting.configs.MenuForUser;
import project.babysitting.babysiting.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding binding;
    private FirebaseAuth fAuth;
    private FirebaseFirestore firestore;
    private DocumentReference sitterRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.container_fragment,new UserHomeFragment()).commit();

        generateDeviceToken();

        binding.bottomNav.btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment,new UserHomeFragment()).commit();
            }
        });

        binding.bottomNav.btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment,new UserRequestFragment()).commit();
            }
        });

        binding.bottomNav.btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment,new UserProfileFragment()).commit();
            }
        });
        LoadingBar loadingBar = new LoadingBar(this);
        binding.bottomNav.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingBar.showDialog("Logged Out!");
                DatabaseReference tokenRef = FirebaseDatabase.getInstance().getReference("Tokens").child(fAuth.getCurrentUser().getUid());
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
                                    Intent i = new Intent(HomeActivity.this,OnBoardingActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    i.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);
                                    finish();
                                }
                            });
                        }else {
                            loadingBar.dissmissDialog();
                            FirebaseAuth.getInstance().signOut();
                            Intent i = new Intent(HomeActivity.this,OnBoardingActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            i.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        loadingBar.dissmissDialog();
                    }
                });
//                FirebaseAuth.getInstance().signOut();
//                Intent i = new Intent(HomeActivity.this,OnBoardingActivity.class);
//                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                i.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(i);
//                finish();
            }
        });

        binding.bottomNav.btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MenuForUser menuForUser = new MenuForUser();
                menuForUser.show(getSupportFragmentManager(),"UserMenu");
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
                Toast.makeText(HomeActivity.this, "token"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void readUsers(){
        sitterRef = firestore.collection("BabySitter").document(Objects.requireNonNull(fAuth.getCurrentUser()).getUid());
        sitterRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()){
                String data = documentSnapshot.getString("isChat");
                String name = documentSnapshot.getString("name");
                String email = documentSnapshot.getString("email");
                assert data != null;
                Log.d("chat", name+" "+email);
                if(data.equals("no")) {
//                    Intent intent = new Intent(HomeActivity.this, ChatActivity.class);
//                    startActivity(intent);
                }else if(data.equals("yes")){
                    Toast.makeText(HomeActivity.this, "Chatting Completed", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(HomeActivity.this, "Not Exits", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
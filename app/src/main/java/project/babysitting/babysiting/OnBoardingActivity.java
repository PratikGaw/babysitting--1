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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import project.babysitting.babysiting.configs.LoadingBar;
import project.babysitting.babysiting.databinding.ActivityOnBoardingBinding;

public class OnBoardingActivity extends AppCompatActivity {
    private ActivityOnBoardingBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private DocumentReference userRef;
    private DocumentReference sitterRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOnBoardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        if (auth.getCurrentUser() != null){
            LoadingBar bar = new LoadingBar(OnBoardingActivity.this);
            bar.showDialog("LoggedIn..");
        }

        binding.btnUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OnBoardingActivity.this,RegisterActivity.class);
                i.putExtra("type","user");
                startActivity(i);
            }
        });

        binding.btnBabySitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OnBoardingActivity.this,RegisterActivity.class);
                i.putExtra("type","sitter");
                startActivity(i);
            }
        });

        binding.txOldUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OnBoardingActivity.this,LoginActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onStart() {
        FirebaseUser user = auth.getCurrentUser();
        if(user != null){
            checkUser();
        }
        super.onStart();

    }

    private void checkUser(){
        userRef = firestore.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    Intent i = new Intent(OnBoardingActivity.this,HomeActivity.class);
                    startActivity(i);
                    finish();
                }else{
                    checkSitter();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(OnBoardingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkSitter(){
        sitterRef = firestore.collection("BabySitter").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        sitterRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    Intent i = new Intent(OnBoardingActivity.this,SitterHomeActivity.class);
                    startActivity(i);
                    finish();
                }else{
                    Log.d("user", "User Not Exits");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(OnBoardingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
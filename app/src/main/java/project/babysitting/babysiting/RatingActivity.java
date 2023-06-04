package project.babysitting.babysiting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import project.babysitting.babysiting.Models.Rating;
import project.babysitting.babysiting.databinding.ActivityRatingBinding;

public class RatingActivity extends AppCompatActivity {
    private ActivityRatingBinding binding;
    private String sitterId;
    private DocumentReference ref;
    private DatabaseReference sitterRatingRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRatingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sitterId = getIntent().getStringExtra("sitterId");
        binding.btnRatingAndFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.edtFeedback.getText().toString().trim().isEmpty()){
                    Toast.makeText(RatingActivity.this, "Enter Any Feedback", Toast.LENGTH_SHORT).show();
                }else{
                    binding.pb.setVisibility(View.VISIBLE);
                    ref = FirebaseFirestore.getInstance().collection("BabySitters").document(sitterId);
                    sitterRatingRef = FirebaseDatabase.getInstance().getReference("Ratings").child(sitterId)
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    Rating rating = new Rating();
                    rating.setRate(binding.rating.getRating()+"");
                    rating.setFeedback(binding.edtFeedback.getText().toString().trim());
                    rating.setSitterId(sitterId);
                    rating.setRatingId(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    sitterRatingRef.setValue(rating).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            binding.edtFeedback.setText("");
                            binding.pb.setVisibility(View.GONE);
                            binding.txtThanks.setVisibility(View.VISIBLE);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            binding.pb.setVisibility(View.GONE);
                            Toast.makeText(RatingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
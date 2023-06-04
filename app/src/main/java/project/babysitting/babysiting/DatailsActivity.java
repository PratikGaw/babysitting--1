package project.babysitting.babysiting;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import project.babysitting.babysiting.Models.Interests;
import project.babysitting.babysiting.databinding.ActivityDatailsBinding;

@RequiresApi(api = Build.VERSION_CODES.R)
public class DatailsActivity extends AppCompatActivity {
    private ActivityDatailsBinding binding;
    private FirebaseFirestore firestore;
    private FirebaseAuth fAuth;
    private String name,email,phone,addr,pincode,gender,experiance,skills,price,description,city;
    int storage_req = 111;
    private String[] permisions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDatailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permisions,storage_req);
        }

        fAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        String id = getIntent().getStringExtra("uid").trim();
        DocumentReference ref = firestore.collection("BabySitter").document(id);
        ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        name = documentSnapshot.getString("name");
                        email = documentSnapshot.getString("email");
                        phone = documentSnapshot.getString("phone");
                        addr = documentSnapshot.getString("address");
                        pincode = documentSnapshot.getString("pincode");
                        gender = documentSnapshot.getString("gender");
                        String img = documentSnapshot.getString("imgUrl");
                        experiance = documentSnapshot.getString("experiance");
                        price = documentSnapshot.getString("price");
                        skills = documentSnapshot.getString("skills");
                        description = documentSnapshot.getString("description");
                        city = documentSnapshot.getString("city");
                        String sitterId = documentSnapshot.getString("uid");
                        ArrayList<String> expList = new ArrayList<>(Arrays.asList(experiance.split(",")));
                        ArrayList<String> priceList = new ArrayList<>(Arrays.asList(price.split(",")));
                        ArrayList<String> skillsList = new ArrayList<>(Arrays.asList(skills.split(",")));
                        binding.txtExperiance.setText("");
                        binding.txtPrice.setText("");

                        int count = 1;
                        for (int i = 0; i < expList.size(); i++){
                            binding.txtExperiance.append(count+". "+expList.get(i).trim()+"\n");
                            count++;
                        }
                        count = 1;
                        for (int i = 0; i < priceList.size(); i++){
                            binding.txtPrice.append(count+". "+priceList.get(i).trim()+"\n");
                            count++;
                        }
                        count = 1;
                        for (int i = 0; i < skillsList.size(); i++){
                            binding.txtSkills.append(count+". "+skillsList.get(i).trim()+"\n");
                            count++;
                        }
                        if (documentSnapshot.get("imgUrl") != null){
                            Picasso.get().load(img).into(binding.profilePic);
                        }else if(gender.equals("Male")) {
                            binding.profilePic.setImageResource(R.drawable.man);
                        }else {
                            binding.profilePic.setImageResource(R.drawable.female);
                        }
                        binding.txtAddress.setText("Address: "+addr+","+city+","+pincode+"\n");
                        binding.txtDesc.setText(description);
                        binding.txtName.setText("Name: "+name);
                        binding.txtEmail.setText("Email:- "+email);
                       // binding.txtPhone.setText("Phone/MobileNo.:- "+phone);
                        binding.txtGender.setText("Gender: "+gender);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DatailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        binding.btnGeneratePdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generatePdf();
            }
        });
        
        binding.btnSendInterest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendInterest();
            }
        });
    }

    private void sendInterest() {
        String id = getIntent().getStringExtra("uid").trim();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Request");
        DatabaseReference uref = FirebaseDatabase.getInstance().getReference().child(fAuth.getCurrentUser().getUid()).child("MyRequest");
        Interests interests = new Interests();
        interests.setUid(fAuth.getCurrentUser().getUid());
        interests.setInterId(""+System.currentTimeMillis());
        interests.setSitterUid(id);
        interests.setStatus("pending");
        ref.child(id).child(fAuth.getCurrentUser().getUid()).setValue(interests).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(DatailsActivity.this, "Intereste sent", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DatailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        uref.child(id).setValue(interests).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(DatailsActivity.this, "Intereste sent", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DatailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void generatePdf() {
        binding.btnGeneratePdf.setVisibility(View.GONE);
        binding.btnSendInterest.setVisibility(View.GONE);
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(binding.getRoot().getWidth(), binding.getRoot().getHeight(), 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
       // canvas.translate(-binding.rvRegistrators.getScrollX(), -binding.rvRegistrators.getScrollY());
        binding.getRoot().draw(canvas);

        document.finishPage(page);
        // Save the document to a file
        File mFile = new File(Environment.getExternalStorageDirectory(), "BabySitter");
        if (!mFile.exists()){
            mFile.mkdir();
        }
        File file = new File(mFile, "chetan.pdf");
        try {
            document.writeTo(new FileOutputStream(file));
            Toast.makeText(this, "PDF saved successfully!", Toast.LENGTH_SHORT).show();
            binding.btnGeneratePdf.setVisibility(View.VISIBLE);
            binding.btnSendInterest.setVisibility(View.VISIBLE);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving PDF!", Toast.LENGTH_SHORT).show();
            binding.btnGeneratePdf.setVisibility(View.VISIBLE);
            binding.btnSendInterest.setVisibility(View.VISIBLE);
        }
    }
}
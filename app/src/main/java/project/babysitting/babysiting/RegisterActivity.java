package project.babysitting.babysiting;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import project.babysitting.babysiting.Models.Sitter;
import project.babysitting.babysiting.Models.User;
import project.babysitting.babysiting.configs.Auth;
import project.babysitting.babysiting.configs.LoadingBar;
import project.babysitting.babysiting.configs.UserType;
import project.babysitting.babysiting.databinding.ActivityRegisterBinding;


@SuppressWarnings("deprecation")
public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    String genderFromRadio = "Male";
    LoadingBar loadingBar;
    FirebaseStorage storage;
    private StorageReference sref;
    private Uri filePath;

    int PICK_IMAGE_REQUEST = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth = FirebaseAuth.getInstance();
        Auth auth = new Auth();
        firestore = FirebaseFirestore.getInstance();
        loadingBar = new LoadingBar(this);
        storage = FirebaseStorage.getInstance();
        sref = storage.getReference();

        String type = getIntent().getStringExtra("type").toString();
        if(type.equals("user")){
            binding.cardExperience.setVisibility(View.GONE);
            binding.cardPrice.setVisibility(View.GONE);
            binding.cardDesc.setVisibility(View.GONE);
            binding.cardSkills.setVisibility(View.GONE);
            binding.cardImage.setVisibility(View.GONE);
        }
        binding.profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        binding.radioGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                genderFromRadio = (R.id.radioMale == checkedId) ? "Male" : "Female";
            }
        });
        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.edtEmail.getText().toString().trim();
                String pass = binding.edtPassword.getText().toString().trim();
                String name = binding.edtName.getText().toString().trim();
                String phone = binding.edtPhone.getText().toString().trim();
                String addr = binding.edtAddress.getText().toString().trim();
                String pincode = binding.edtPincode.getText().toString().trim();

                String desc = binding.edtDesc.getText().toString().trim();
                String price = binding.edtPrice.getText().toString().trim();
                String skills = binding.edtSki.getText().toString().trim();
                String experiance = binding.edtExperiance.getText().toString().trim();
                String city = binding.edtCity.getText().toString().trim();


                if(email.isEmpty() && pass.isEmpty() && name.isEmpty() && phone.isEmpty() && addr.isEmpty() && pincode.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Some Fields are empty?", Toast.LENGTH_SHORT).show();
                }else{
                    if (type.equals("user")){
                        userRegistration(name,email,pass,phone,addr,pincode,genderFromRadio);
                        new UserType().setType(getApplicationContext(),"user");
                    }else if(type.equals("sitter")){
                        if(desc.isEmpty() && price.isEmpty() && skills.isEmpty() && experiance.isEmpty()){
                            Toast.makeText(RegisterActivity.this, "Some Fields are empty?", Toast.LENGTH_SHORT).show();
                        }else if(filePath == null){
                            Toast.makeText(RegisterActivity.this, "Please Select Image?", Toast.LENGTH_SHORT).show();
                        }else {
                            new UserType().setType(getApplicationContext(),"sitter");
                            sitterRegistration(name,email,pass,phone,addr,pincode,genderFromRadio,city,desc,experiance,price,skills);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            Picasso.get().load(filePath).into(binding.profilePic);
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    private void userRegistration(String name,String email,String password,
                                  String phone,String address,String pincode,String gender){
        loadingBar.showDialog("User Registration");
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    User user = new User();
                    user.setName(name);
                    user.setEmail(email);
                    user.setGender(gender);
                    user.setPhone(phone);
                    user.setAddress(address);
                    user.setPincode(pincode);
                    user.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    user.setType("user");
                    DocumentReference userRef = firestore.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    userRef.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            loadingBar.dissmissDialog();
                            Intent i = new Intent(RegisterActivity.this,HomeActivity.class);
                            startActivity(i);
                            finish();
                            Toast.makeText(RegisterActivity.this, "Registration Succesfull!", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loadingBar.dissmissDialog();
                            Toast.makeText(RegisterActivity.this, "reg: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    loadingBar.dissmissDialog();
                    Toast.makeText(RegisterActivity.this, "create: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void sitterRegistration(String name,String email,String password,
                                    String phone,String address,String pincode,String gender,String city,String desc,
                                    String exp,String price,String skills){
        loadingBar.showDialog("Registration");
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    LoadingBar loadingBar = new LoadingBar(RegisterActivity.this);
                    loadingBar.showDialog("Uploading");
                    StorageReference iref = sref.child("ProfileImages/"+ UUID.randomUUID().toString());
                    iref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            iref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Sitter sitter = new Sitter();
                                    sitter.setName(name);
                                    sitter.setEmail(email);
                                    sitter.setGender(gender);
                                    sitter.setPhone(phone);
                                    sitter.setAddress(address);
                                    sitter.setPincode(pincode);
                                    sitter.setType("sitter");
                                    sitter.setIsChat("no");
                                    sitter.setCity(city);
                                    sitter.setExperiance(exp);
                                    sitter.setSkills(skills);
                                    sitter.setDescription(desc);
                                    sitter.setPrice(price);
                                    sitter.setImgUrl(uri.toString());
                                    sitter.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    DocumentReference userRef = firestore.collection("BabySitter").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    userRef.set(sitter).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            loadingBar.dissmissDialog();
                                            Intent i = new Intent(RegisterActivity.this,HomeActivity.class);
                                            startActivity(i);
                                            finish();
                                            Toast.makeText(RegisterActivity.this, "Registration Succesfull!", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            loadingBar.dissmissDialog();
                                            Toast.makeText(RegisterActivity.this, "reg: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    loadingBar.dissmissDialog();
                                    Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loadingBar.dissmissDialog();
                            Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progress = (100.0*snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            String str = String.valueOf((int) progress);
                            loadingBar.showDialog(str+"%");
                        }
                    });

                }else{
                    loadingBar.dissmissDialog();
                    Toast.makeText(RegisterActivity.this, "create: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
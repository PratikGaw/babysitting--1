package project.babysitting.babysiting.UserFragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import project.babysitting.babysiting.R;
import project.babysitting.babysiting.configs.LoadingBar;
import project.babysitting.babysiting.databinding.FragmentProfileBinding;
import project.babysitting.babysiting.databinding.FragmentUserProfileBinding;

@SuppressWarnings("deprecation")
public class UserProfileFragment extends Fragment {
    private FragmentUserProfileBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private DocumentReference ref;
    private String name,email,phone,addr,pincode,gender;
    FirebaseStorage storage;
    private StorageReference sref;
    private Uri filePath;

    int PICK_IMAGE_REQUEST = 101;

    public UserProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUserProfileBinding.inflate(inflater, container, false);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        sref = storage.getReference();
        binding.edtEmail.setEnabled(false);
        ref = firestore.collection("Users").document(auth.getCurrentUser().getUid());

        binding.btnSelectImage.setOnClickListener(v -> chooseImage());
        ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    name = documentSnapshot.getString("name");
                    email = documentSnapshot.getString("email");
                    phone = documentSnapshot.getString("phone");
                    addr = documentSnapshot.getString("address");
                    pincode = documentSnapshot.getString("pincode");
                    gender = documentSnapshot.getString("gender");
                    String img = documentSnapshot.getString("imgUrl");
                    String gender = documentSnapshot.getString("gender");
                    if (documentSnapshot.get("imgUrl") != null){
                        Picasso.get().load(img).into(binding.profilePic);
                    }else {
                        if(gender.equalsIgnoreCase("Male")){
                            Picasso.get().load(R.drawable.man).into(binding.profilePic);
                        }else {
                            Picasso.get().load(R.drawable.female).into(binding.profilePic);
                        }
                        Log.d("image", "Field Not Exists");
                    }
                    binding.edtName.setText(name);
                    binding.edtEmail.setText(email);
                    binding.edtAddress.setText(addr);
                    binding.edtPhone.setText(phone);
                    binding.edtPincode.setText(pincode);

                }else {
                    Toast.makeText(getActivity(), "No Data Found!!", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        binding.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });
        return binding.getRoot();
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

    private void uploadImage() {
        if(filePath != null)
        {
            LoadingBar loadingBar = new LoadingBar(getActivity());
            loadingBar.showDialog("Uploading");
            StorageReference iref = sref.child("ProfileImages/"+ UUID.randomUUID().toString());
            iref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            iref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    loadingBar.dissmissDialog();
                                    Toast.makeText(getActivity(), "Uploaded", Toast.LENGTH_SHORT).show();
                                    Map<String,String> sitterMap = new HashMap<>();
                                    sitterMap.put("imgUrl",uri.toString());
                                    sitterMap.put("name",binding.edtName.getText().toString().trim());
                                    sitterMap.put("email",binding.edtEmail.getText().toString().trim());
                                    sitterMap.put("address",binding.edtAddress.getText().toString().trim());
                                    //     sitterMap.put("gender",binding.edtName.getText().toString().trim());
                                    sitterMap.put("pincode",binding.edtPincode.getText().toString().trim());
                                    sitterMap.put("phone",binding.edtPhone.getText().toString().trim());
                                    ref.set(sitterMap, SetOptions.merge());
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    loadingBar.dissmissDialog();
                                    Toast.makeText(getActivity(), "Firestore Image "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loadingBar.dissmissDialog();
                            Toast.makeText(getActivity(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener((OnProgressListener<? super UploadTask.TaskSnapshot>) taskSnapshot -> {
                        double progress = (100.0*taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        String str = String.valueOf((int) progress);
                        loadingBar.showDialog(str+"%");

                    });
        }else {
            Toast.makeText(getActivity(), "File Not Selected", Toast.LENGTH_SHORT).show();
        }
    }
}
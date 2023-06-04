package project.babysitting.babysiting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import project.babysitting.babysiting.Adapters.ChatAdapter;
import project.babysitting.babysiting.FirebaseMessaging.MyAsyncTask;
import project.babysitting.babysiting.Models.Booking;
import project.babysitting.babysiting.Models.Message;
import project.babysitting.babysiting.configs.UserType;
import project.babysitting.babysiting.databinding.ActivityChatBinding;
import project.babysitting.babysiting.databinding.LayoutBookingBinding;
import project.babysitting.babysiting.databinding.UpdateBookingBinding;

public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding binding;
    private String reciverId,reciverRoom,senderRoom,name,bookId;
    private DatabaseReference msgRefSender,msgRefReciver;
    int count = 0;
    private ArrayList<Message> messageList;
    private ChatAdapter adapter;

    String userToken,type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        type = new UserType().getType(ChatActivity.this);
        reciverId = getIntent().getStringExtra("reciverId");
        name = getIntent().getStringExtra("name");
        binding.txtUserName.setText(name);
        reciverRoom = reciverId + FirebaseAuth.getInstance().getCurrentUser().getUid();
        senderRoom = FirebaseAuth.getInstance().getCurrentUser().getUid() + reciverId;
        messageList = new ArrayList<>();
        if (type.equals("user")){
            binding.btnBook.setText("book");
            binding.btnRating.setVisibility(View.VISIBLE);
        }else{
            binding.btnBook.setText("update booking");
            binding.btnRating.setVisibility(View.VISIBLE);
            binding.btnRating.setVisibility(View.GONE);
        }

        DatabaseReference token = FirebaseDatabase.getInstance().getReference("Tokens")
                .child(reciverId);
        token.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userToken = snapshot.child("token").getValue().toString();
//                if (snapshot.exists()) {
//                    Log.d("rToke", userToken);
//                }else {
//                    userToken = "12345";
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.rvMessage.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setStackFromEnd(true);
        layoutManager.setSmoothScrollbarEnabled(true);
        //layoutManager.setReverseLayout(true);
        binding.rvMessage.setLayoutManager(layoutManager);
        adapter = new ChatAdapter(ChatActivity.this,messageList);
        msgRefSender = FirebaseDatabase.getInstance().getReference("chats").child(senderRoom);
        msgRefReciver = FirebaseDatabase.getInstance().getReference("chats").child(reciverRoom);
        binding.rvMessage.setAdapter(adapter);
        msgRefSender.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                count = (int)snapshot.getChildrenCount() + 1;
                messageList.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    Message message = ds.getValue(Message.class);
                    messageList.add(message);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        binding.btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.edtMessage.getText().toString().trim();
                if (msg.isEmpty()){
                    Toast.makeText(ChatActivity.this, "Please Enter Messag", Toast.LENGTH_SHORT).show();
                }else{
                    sendMessage(name,msg);
                    binding.edtMessage.setText("");
                    Message message = new Message(count+"",msg,FirebaseAuth.getInstance().getCurrentUser().getUid(),reciverId);
                    msgRefSender.child(count+"").setValue(message);
                    msgRefReciver.child(count+"").setValue(message);
                }
            }
        });

        binding.btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChatActivity.this,RatingActivity.class);
                i.putExtra("sitterId",reciverId);
                startActivity(i);
            }
        });

        binding.btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equals("user")){
                    showBookingDialog();
                }else{
                    showGenerateRequestDialog();
                    binding.btnRating.setVisibility(View.GONE);
                }
            }
        });
    }

    private void showGenerateRequestDialog() {
        UpdateBookingBinding updateBookingBinding = UpdateBookingBinding.inflate(LayoutInflater.from(ChatActivity.this));
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
        builder.setView(updateBookingBinding.getRoot());
        builder.setCancelable(false);
        updateBookingBinding.txtRequetFor.setText("New Booking Request For "+name);
        String reqRoom = reciverId + FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference bookRef = FirebaseDatabase.getInstance().getReference("BookingRequests")
                .child(reqRoom);
        String bookingId = ""+System.currentTimeMillis();
        ProgressDialog pd = new ProgressDialog(ChatActivity.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Loading...");
        builder.setPositiveButton("Generate", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                pd.show();
                String edtTitle = updateBookingBinding.edtWorkTitle.getText().toString().trim();
                String edtAmount = updateBookingBinding.edtTotalAmount.getText().toString().trim();
                String edtDateTo = updateBookingBinding.edtDateTo.getText().toString().trim();
                String edtDateFrom = updateBookingBinding.edtDateFrom.getText().toString().trim();
                String edtTime = updateBookingBinding.edtTime.getText().toString().trim();
                String edtTotalHours = updateBookingBinding.edtTotalHours.getText().toString().trim();
                if (edtTitle.isEmpty() && edtAmount.isEmpty() && edtDateTo.isEmpty() && edtDateFrom.isEmpty() &&
                    edtTime.isEmpty() && edtTotalHours.isEmpty()){
                    Toast.makeText(ChatActivity.this, "Some Fields are empty!", Toast.LENGTH_SHORT).show();
                }else{
                   Booking booking = new Booking(bookingId,edtTitle,edtDateTo,edtDateFrom,edtTime,edtTotalHours,edtAmount);
                   bookRef.setValue(booking).addOnSuccessListener(new OnSuccessListener<Void>() {
                       @Override
                       public void onSuccess(Void unused) {
                           pd.dismiss();
                           Toast.makeText(ChatActivity.this, "Booking Request Generated", Toast.LENGTH_SHORT).show();
                       }
                   });
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showBookingDialog() {
        LayoutBookingBinding bookingBinding = LayoutBookingBinding.inflate(LayoutInflater.from(ChatActivity.this));
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
        builder.setView(bookingBinding.getRoot());
        String reqRoom =  FirebaseAuth.getInstance().getCurrentUser().getUid() + reciverId;
        Log.d("roomId", reqRoom);
        DatabaseReference bookRef = FirebaseDatabase.getInstance().getReference("BookingRequests")
                .child(reqRoom);
        bookRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               if (snapshot.exists()){
                   Booking booking = snapshot.getValue(Booking.class);
                   bookingBinding.bookingTitle.setText(booking.getTitle());
                   bookingBinding.bookingAmount.setText("Total Amount Are:- "+booking.getAmount()+"Rs");
                   bookingBinding.bookingId.setText("Booking Id: "+booking.getBookingId());
                   bookId = booking.getBookingId();
                   bookingBinding.bookingDates.setText("Working Dates are: "+booking.getDateTo() + " To "+booking.getDateFrom());
                   bookingBinding.bookingTime.setText("Working Time Are: "+booking.getTime());
                   bookingBinding.bookingHours.setText("Total Working Hours are: "+booking.getHours()+"hrs");
               }else{
                   bookingBinding.bookingTitle.setText("");
                   bookingBinding.bookingAmount.setText("");
                   bookingBinding.bookingId.setText("Ask Sitter to create booking request for You!");
                   bookingBinding.bookingDates.setText("");
                   bookingBinding.bookingTime.setText("");
                   bookingBinding.bookingHours.setText("");
                   Toast.makeText(ChatActivity.this, "Ask Sitter to create booking request for You!", Toast.LENGTH_LONG).show();
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        builder.setPositiveButton("book", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ProgressDialog pd = new ProgressDialog(ChatActivity.this);
                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pd.setMessage("Loading...");
                pd.show();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Bookings")
                        .child(reciverId).child(bookId);
                DatabaseReference myreference = FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("MyBookings").child(bookId);
                Booking booking = new Booking();
                booking.setBookingId(bookId);
                booking.setTitle(bookingBinding.bookingTitle.getText().toString());
                booking.setCompleteDate(bookingBinding.bookingDates.getText().toString());
                booking.setTime(bookingBinding.bookingTime.getText().toString());
                booking.setHours(bookingBinding.bookingHours.getText().toString());
                booking.setUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());
                booking.setSitterId(reciverId);
                booking.setStatus("pending");
                reference.setValue(booking).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        myreference.setValue(booking);
                        pd.dismiss();
                        Toast.makeText(ChatActivity.this, "Successfully Booked! check Booking status on My Bookings", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();

    }



    private void sendMessage(String title,String body) {
        new MyAsyncTask().execute(userToken, title, body);
    }
}
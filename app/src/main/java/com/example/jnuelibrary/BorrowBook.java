package com.example.jnuelibrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BorrowBook extends AppCompatActivity {

    TextView borrowUserNameTV, borrowBookNameTV;
    private String borrowBookID;
    private String borrowBookName;
    private int borrowBookQuantity;
    private String uID;
    DatabaseReference databaseReferenceBook;
    DatabaseReference databaseReferenceUser;
    DatabaseReference databaseReferenceBorrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_book);

        borrowUserNameTV = findViewById(R.id.borrowUserNameTV);
        borrowBookNameTV = findViewById(R.id.borrowBookNameTV);

        borrowBookID = getIntent().getStringExtra("borrowBookID");
        //borrowBookQuantity = Integer.parseInt(getIntent().getStringExtra("borrowBookQuantity"));
        borrowBookQuantity = getIntent().getIntExtra("borrowBookQuantity",0);
        Toast.makeText(this, borrowBookID, Toast.LENGTH_SHORT).show();

        uID = FirebaseAuth.getInstance().getUid();

        databaseReferenceBook = FirebaseDatabase.getInstance().getReference("Books");
        databaseReferenceUser = FirebaseDatabase.getInstance().getReference("Users");
        databaseReferenceBorrow = FirebaseDatabase.getInstance().getReference("BorrowInformation");

        databaseReferenceUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                borrowBookName = dataSnapshot.child(uID).child("name").getValue().toString();

                borrowUserNameTV.setText(borrowBookName);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReferenceBook.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String borrowBookName = dataSnapshot.child(borrowBookID).child("bname").getValue().toString();

                borrowBookNameTV.setText(borrowBookName);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void BookBorrowConform(View view) {
        Toast.makeText(this, "hello...", Toast.LENGTH_SHORT).show();

        databaseReferenceBook.child(borrowBookID)
                .child("bquantity").setValue(borrowBookQuantity-1).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    BorrowInformation borrowInformation = new BorrowInformation(borrowBookName, borrowBookID);

                    databaseReferenceBorrow.child("01")
                            .setValue(borrowInformation).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(BorrowBook.this, "Book Information saved Successfully", Toast.LENGTH_SHORT).show();

                            }
                            else {
                                //display a failure message
                                Toast.makeText(BorrowBook.this, "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();


                            }
                        }
                    });

                }
                else {
                    //display a failure message
                    Toast.makeText(BorrowBook.this, "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}

package it.massimomazzetti.firebaseesonero;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;


public class HomepageActivity extends AppCompatActivity {

    public final static String MESSAGE_PATH="messages";
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private Holder holder;
    private String format;
    private String texts;
    private ArrayList<String> s = new ArrayList<>();

    /**
     * function onCreate: initialize Realtime Database and Authentication
     * @param bundle
     */
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_homepage);
        mAuth = FirebaseAuth.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
        format = simpleDateFormat.format(new Date());
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(MESSAGE_PATH);
        holder = new Holder();
        setTitle(R.string.Home_Page);
    }

    /**
     * function onBackPressed: on back button pressed signOut the user
     */
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        signOut();
        holder.llData.removeAllViews();
    }

    /**
     * class Holder: contains references to all view objects
     */
    class Holder implements View.OnClickListener {

        TextView tvName;
        EditText etInsert;
        Button btnSend, btnSignOut, btnDelete, btnGet;
        LinearLayout llData;

        /**
         * public Constructor initializes references
         */
        public Holder() {
            tvName = findViewById(R.id.tvName);
            llData= findViewById(R.id.llData);
            etInsert = findViewById(R.id.etInsert);
            btnSend = findViewById(R.id.btnSend);
            btnSignOut = findViewById(R.id.btnSignout);
            btnDelete = findViewById(R.id.btnDelete);
            btnGet= findViewById(R.id.btnGet);

            //add listeners to buttons
            btnGet.setOnClickListener(this);
            btnSend.setOnClickListener(this);
            btnSignOut.setOnClickListener(this);
            btnDelete.setOnClickListener(this);

            //check if user is anonymous
            if(!mAuth.getCurrentUser().isAnonymous()){
                tvName.setText(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail());
            }
            else{
                tvName.setText(R.string.guest);
            }

            //add listener to retrieve data from the firebase database
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    s.clear();
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                        Message mes = snapshot.getValue(Message.class);
                        texts = mes.getText();
                        s.add(texts);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

        }

        /**
         * function onClick: implements method for buttons
         * @param v
         */
        @Override
        public void onClick(View v) {

            //if the send button is pressed writes data into the database
            if (v.getId() == R.id.btnSend) {
                if (!holder.etInsert.getText().toString().equals("") && !mAuth.getCurrentUser().isAnonymous()) {
                    Message mex = new Message(holder.etInsert.getText().toString(), format, holder.tvName.getText().toString());
                    myRef.push().setValue(mex);
                    holder.etInsert.setText("");
                }
                if(mAuth.getCurrentUser().isAnonymous()){
                    Toast.makeText(HomepageActivity.this, R.string.guest_warning, Toast.LENGTH_LONG).show();
                }
            } else if (v.getId() == R.id.btnSignout) {
                signOut();
            } else if (v.getId() == R.id.btnDelete) {
                deleteAccount();
            } else if(v.getId()==R.id.btnGet){  //if get button is pressed reads data and displays it in a linearLayout
                 llData.removeAllViews();
                 TextView textView;
                 for(int i=0; i<s.size(); i++){
                     textView = new TextView(HomepageActivity.this);
                     textView.setText(s.get(i));
                     llData.addView(textView);
                 }
             }
        }
    }

    /**
     * function deleteAccount: delete user account
     */
    public void deleteAccount() {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    /**
     * function signOut: sign out the current user
     */
    public void signOut() {
            mAuth.getInstance().signOut();
            AuthUI.getInstance().signOut(HomepageActivity.this);
            Toast.makeText(getApplicationContext(), R.string.sign_out_toast, Toast.LENGTH_SHORT).show();
            finish();
    }

}

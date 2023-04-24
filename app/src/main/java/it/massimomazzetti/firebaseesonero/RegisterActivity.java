package it.massimomazzetti.firebaseesonero;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Holder holder;
    private final String TAG = "555";
    private String email, password;
    private FirebaseDatabase database;

    /**
     * function onCreate: initialize data and Authentication
     * @param bundle
     */
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        holder = new Holder();
        setTitle(R.string.registration);
    }

    /**
     * class Holder: contains references to all view objects
     */
    class Holder implements View.OnClickListener {

        EditText etEmail, etPassword, etPasswordCheck;
        Button btnRegister;
        TextView tvRegStatus;

        /**
         * public Constructor initializes references
         */
        public Holder() {
            etPassword = findViewById(R.id.etPassword);
            etPasswordCheck = findViewById(R.id.etPasswordCheck);
            tvRegStatus = findViewById(R.id.tvRegStatus);
            etEmail = findViewById(R.id.etEmail);
            btnRegister = findViewById(R.id.btnRegister);
            btnRegister.setOnClickListener(this);
        }

        /**
         * function onClick: implements method for buttons
         * @param v
         */
        @Override
        public void onClick(View v) {
            holder.tvRegStatus.setText("");
            if (holder.etEmail.getText().toString().equals("") || holder.etPassword.getText().toString().equals("") || holder.etPasswordCheck.getText().toString().equals(""))
                holder.tvRegStatus.setText(R.string.missing_field);
            else if (holder.etPassword.getText().toString().length() < 6)
                holder.tvRegStatus.setText(R.string.short_password);
            else {
                if (holder.etPassword.getText().toString().equals(holder.etPasswordCheck.getText().toString())) {
                    email = holder.etEmail.getText().toString();
                    password = holder.etPassword.getText().toString();
                    try {
                        createAccount(email, password);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else holder.tvRegStatus.setText(R.string.mismatch);
            }
        }
    }

    /**
     * function createAccount: creates an account throught Authentication function createUserWithEmailAndPassword()
     * @param email
     * @param password
     */
    private void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).
                addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "createUserWithEmail:success");
                    Toast.makeText(getApplicationContext(),
                            R.string.registration_success,
                            Toast.LENGTH_SHORT).show();
                    mAuth.signOut();
                    finish();
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    Toast.makeText(getApplicationContext(),
                            R.string.registration_failed, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
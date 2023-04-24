package it.massimomazzetti.firebaseesonero;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class RecoverActivity extends AppCompatActivity {

    Holder holder;
    final String TAG = "555";

    /**
     * function onCreate: initializes data
     * @param bundle
     */
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_reimposta);
        holder = new Holder();
        setTitle(R.string.Pwd_recovery);
    }

    /**
     * class Holder: contains references to all view objects
     */
    class Holder implements View.OnClickListener {

        EditText etEmail;
        Button btnReimposta;

        /**
         * public Constructor initializes references
         */
        public Holder() {
            etEmail = findViewById(R.id.etEmail);
            btnReimposta = findViewById(R.id.btnRegister);
            btnReimposta.setOnClickListener(this);
        }

        /**
         * function onClick: implements method for buttons
         * @param v
         */
        @Override
        public void onClick(View v) {
            try {
                resetPassword(etEmail.getText().toString());
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * function resetPassword: reset password if forgotten through Authentication
     * @param email
     */
    public void resetPassword(String email) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                        }
                    }
                });
    }
}

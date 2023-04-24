package it.massimomazzetti.firebaseesonero;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Holder holder;
    String email, password;
    private static final String TAG = "455";
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;

    /**
     * function onCreate: initializes Authentication and Google services
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        holder = new Holder();
        mAuth = FirebaseAuth.getInstance();
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        setTitle(R.string.sign_in);

    }

    /**
     * class Holder: contains references to all view objects
     */
    class Holder implements View.OnClickListener {
        Button btnRegister, btnLogin, btnRecover, btnGuestLogin;
        EditText etEmail, etPassword;
        SignInButton btnGoogleLogin;

        /**
         * public Constructor initializes references
         */
        public Holder() {
            btnLogin = findViewById(R.id.btnLogin);
            btnGuestLogin = findViewById(R.id.btnGuestLogin);
            btnRegister = findViewById(R.id.btnRegister);
            etEmail = findViewById(R.id.etEmail);
            etPassword = findViewById(R.id.etPassword);
            btnGoogleLogin = findViewById(R.id.btnGoogleLogin);
            btnRecover = findViewById(R.id.btnRecover);

            //add listeners to buttons
            btnRecover.setOnClickListener(this);
            btnRegister.setOnClickListener(this);
            btnLogin.setOnClickListener(this);
            btnGoogleLogin.setOnClickListener(this);
            btnGuestLogin.setOnClickListener(this);
        }

        /**
         * function onClick: implements method for buttons
         * @param v
         */
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btnLogin) {
                email = holder.etEmail.getText().toString();
                password = holder.etPassword.getText().toString();
                try {
                    login(email, password);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (v.getId() == R.id.btnRegister) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
            if (v.getId() == R.id.btnGoogleLogin) {
                signIn();
            }
            if (v.getId() == R.id.btnRecover) {
                Intent intent = new Intent(MainActivity.this, RecoverActivity.class);
                startActivity(intent);
            }
            if (v.getId() == R.id.btnGuestLogin) {
                signInAnonymously();
            }
        }
    }

    /**
     * function login: sign in user through Authentication function signInWithEmailAndPassword()
     * @param email
     * @param password
     */
    public void login(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).
                addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this,
                            R.string.login_success, Toast.LENGTH_SHORT).show();
                    Intent data =
                            new Intent(MainActivity.this, HomepageActivity.class);
                    data.putExtra("name", holder.etEmail.getText().toString());
                    startActivity(data);
                } else {

                    Toast.makeText(MainActivity.this,
                            R.string.login_failed, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * function signIn: sign in user through google services
     */
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * function onActivityResult: checks google login results
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    /**
     * function firebaseAuthWithGoogle: login in firebase through generated idToken
     * @param idToken
     */
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent data = new Intent(MainActivity.this, HomepageActivity.class);
                            startActivity(data);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }

                    }
                });
    }


    /**
     * function signInAnonymously: sign in as an anonymous user
     */
    private void signInAnonymously() {
        mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "signInAnonymously:success");
                    Toast.makeText(MainActivity.this, R.string.guest_login_success,
                            Toast.LENGTH_SHORT).show();
                    Intent data = new Intent(MainActivity.this, HomepageActivity.class);
                    startActivity(data);

                } else {
                    Log.w(TAG, "signInAnonymously:failure", task.getException());
                    Toast.makeText(MainActivity.this, R.string.guest_login_fail,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
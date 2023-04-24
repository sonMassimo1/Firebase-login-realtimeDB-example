package it.massimomazzetti.firebaseesonero;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import java.util.Arrays;
import java.util.List;

public class WelcomeActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1234;

    /**
     * function onCreate: initializes data
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        new Holder();

    }

    /**
     * function onActivityResult: firebaseUI return values
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                Toast.makeText(WelcomeActivity.this,R.string.ui_sign_in_success,
                        Toast.LENGTH_SHORT).show();
                startActivity(new Intent(WelcomeActivity.this, HomepageActivity.class));

            } else {
                // failed sign in
                Toast.makeText(WelcomeActivity.this, R.string.ui_sign_in_failed,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * class Holder: contains references to all view objects
     */
    class Holder implements View.OnClickListener {

        Button btnFireBaseUI, btnFireBaseSDK;

        /**
         * public Constructor initializes references
         */
        public Holder() {
            btnFireBaseSDK = findViewById(R.id.FireBaseSDK);
            btnFireBaseUI = findViewById(R.id.btnFirebaseUI);
            btnFireBaseSDK.setOnClickListener(this);
            btnFireBaseUI.setOnClickListener(this);

        }

        /**
         * function onClick: implements method for buttons
         * @param v
         */
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btnFirebaseUI) {
                    // Choose authentication providers
                    List<AuthUI.IdpConfig> providers = Arrays.asList(
                            new AuthUI.IdpConfig.EmailBuilder().build(),
                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                            new AuthUI.IdpConfig.AnonymousBuilder().build());
                    // Create and launch sign-in intent
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(providers)
                                    .build(),
                            RC_SIGN_IN);
            }
            if (v.getId() == R.id.FireBaseSDK) {
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }
    }
}

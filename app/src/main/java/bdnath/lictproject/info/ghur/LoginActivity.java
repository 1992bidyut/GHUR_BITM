package bdnath.lictproject.info.ghur;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import bdnath.lictproject.info.ghur.ProfileWork.RegistationActivity;
import bdnath.lictproject.info.ghur.SharedPreference.LoginPreferences;

public class LoginActivity extends AppCompatActivity {
    private EditText emailET, passwordET;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private LoginPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar bar=getSupportActionBar();
        bar.hide();

        emailET=findViewById(R.id.emailET);
        passwordET=findViewById(R.id.passwordET);
        preferences=new LoginPreferences(this);
        auth=FirebaseAuth.getInstance();
        currentUser=auth.getCurrentUser();
        if(preferences.getStatus()){
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
        }
    }

    public void logIn(View view) {
        String emailID=emailET.getText().toString();
        String password=passwordET.getText().toString();
        if (emailID.isEmpty()){
            emailET.setError(getString(R.string.SET_ERROR_MSG));
            return;
        }
        if (password.isEmpty()){
            passwordET.setError(getString(R.string.SET_ERROR_MSG));
            return;
        }
        logInWithFirebase(emailID,password);
    }

    public void register(View view) {
        startActivity(new Intent(LoginActivity.this,RegistationActivity.class));

    }

    private void logInWithFirebase(final String emailID, final String password){
        Task<AuthResult> task= auth.signInWithEmailAndPassword(emailID,password);
        task.addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    currentUser=auth.getCurrentUser();
                    preferences.registerAdmin(emailID,password);
                    preferences.setStatus(true);
                    Toast.makeText(LoginActivity.this,"Logged in as: "+currentUser.getEmail(),Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                }
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this,"Login Failed: "+e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }
}

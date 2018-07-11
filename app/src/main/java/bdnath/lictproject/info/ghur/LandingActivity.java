package bdnath.lictproject.info.ghur;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import bdnath.lictproject.info.ghur.SharedPreference.LoginPreferences;

public class LandingActivity extends AppCompatActivity {
    private LoginPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar bar=getSupportActionBar();
        bar.hide();
        preferences=new LoginPreferences(this);
        setContentView(R.layout.activity_landing);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(preferences.getStatus()){
                    startActivity(new Intent(LandingActivity.this,MainActivity.class));
                }else {
                    Intent intent = new Intent(LandingActivity.this,LoginActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        },1000);
    }
}

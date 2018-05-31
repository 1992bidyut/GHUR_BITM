package bdnath.lictproject.info.ghur.SharedPreference;

import android.content.Context;
import android.content.SharedPreferences;


public class LoginPreferences {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static final String ADMIN_EMAIL = "admin_email";
    private static final String ADMIN_PASSWORD = "admin_password";
    private static final String ISLOGGEDIN = "isLoggedIn";
    private static final String EMPLOYEE = "employee";
    private static final String DEFAULT_MESSAGE = "User not found";


    public LoginPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

    }

    public String registerAdmin(String email, String pass){
        editor.putString(ADMIN_EMAIL,email);
        editor.putString(ADMIN_PASSWORD,pass);
        editor.commit();

        return "Registered Successfully";
    }

    public String getAdminEmail() {
        return sharedPreferences.getString(ADMIN_EMAIL,DEFAULT_MESSAGE);
    }

    public String getAdminPassword() {
        return sharedPreferences.getString(ADMIN_PASSWORD,DEFAULT_MESSAGE);
    }

    public void setStatus(boolean status){
        editor.putBoolean(ISLOGGEDIN,status);
        editor.commit();
    }

    public boolean getStatus(){

        return sharedPreferences.getBoolean(ISLOGGEDIN,false);
    }
}

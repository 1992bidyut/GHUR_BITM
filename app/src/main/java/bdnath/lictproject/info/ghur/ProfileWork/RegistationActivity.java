package bdnath.lictproject.info.ghur.ProfileWork;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import bdnath.lictproject.info.ghur.LoginActivity;
import bdnath.lictproject.info.ghur.R;

public class RegistationActivity extends AppCompatActivity {
    private ImageView profileImage;
    private EditText fullName;
    private EditText email;
    private EditText pass1;
    private EditText pass2;
    private EditText city;
    private EditText dob;
    private EditText country;
    private RadioGroup radioGroup;

    private String profileImagePath;
    private String gender;
    private int year, month, day, hour, minute;
    private Calendar calendar;

    private FirebaseAuth auth;
    private FirebaseUser user;

    private DatabaseReference roofRef;
    private DatabaseReference profileRef;

    private StorageReference storageReference;
    private StorageReference userStorageReference;

    private Uri proImagePath=null;
    private Bitmap proBitmap;
    private String photoPath = null;

    private String userName;
    private String userEmail;
    private String userDoB;
    private String userCity;
    private String userCountry;
    private String password1;
    private String password2;


    private bdnath.lictproject.info.ghur.FireBasePojoClass.UserInfo info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registation);

        profileImage=findViewById(R.id.profileImage);
        fullName=findViewById(R.id.fullName);
        radioGroup=findViewById(R.id.radioGroup);
        city=findViewById(R.id.city);
        dob=findViewById(R.id.dob);
        country=findViewById(R.id.country);
        pass1=findViewById(R.id.password1);
        pass2=findViewById(R.id.password2);
        email=findViewById(R.id.emailID);

        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        RadioButton rb=findViewById(R.id.male);
        rb.setChecked(true);
        gender=rb.getText().toString();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb=findViewById(checkedId);
                gender=rb.getText().toString();
                Toast.makeText(RegistationActivity.this,gender,Toast.LENGTH_LONG).show();
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 101);
            }
        });
        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(RegistationActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

                                calendar.set(i,i1,i2);
                                String date = sdf.format(calendar.getTime());
                                dob.setText(date);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==101&&resultCode == RESULT_OK){
            proImagePath = data.getData();
            try {
                proBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(proImagePath));
                profileImage.setImageBitmap(proBitmap);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    private void uploadProPic(){
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();
        storageReference= FirebaseStorage.getInstance().getReference();
        userStorageReference=storageReference.child(user.getUid());
        StorageReference proImgStorRef=userStorageReference.child("profile_image/profile.jpg");
            proImgStorRef.putFile(proImagePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            downloadProPic();
                            Toast.makeText(RegistationActivity.this,"Upload completed",
                                    Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Toast.makeText(RegistationActivity.this,"ERROR: "+exception.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress=(100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage(((int)progress)+"% Uploaded...");
                }
            });

    }

    private void downloadProPic(){
        StorageReference proImgStorRef=userStorageReference.child("profile_image/profile.jpg");
        proImgStorRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.jpg'
                profileImagePath=uri.toString();
                insertProfileInfo();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }
    private void insertProfileInfo(){
        roofRef= FirebaseDatabase.getInstance().getReference();
        profileRef=roofRef.child("profile");


        info=new bdnath.lictproject.info.ghur.FireBasePojoClass.UserInfo(userName,userEmail,gender,userDoB,userCity,userCountry,profileImagePath);
        profileRef.child(user.getUid()).setValue(info);
        startActivity(new Intent(RegistationActivity.this,LoginActivity.class));
    }

    public void create(View view) {
        String password;
        password1=pass1.getText().toString();
        password2=pass2.getText().toString();
        userName=fullName.getText().toString();
        userEmail=email.getText().toString();
        userDoB=dob.getText().toString();
        userCity=city.getText().toString();
        userCountry=country.getText().toString();


        if(userName.isEmpty()){
            fullName.setError("Input your Full name");
            return;
        }
        if(userEmail.isEmpty()){
            email.setError("Input your email address");
            return;
        }
        if(userDoB.isEmpty()){
            dob.setError("Input your Date of Birth");
            return;
        }
        if(userCity.isEmpty()){
            city.setError("Input your city name");
            return;
        }
        if(userCountry.isEmpty()){
            country.setError("Input your country name");
            return;
        }
        if (proImagePath==null){
            Toast.makeText(RegistationActivity.this,"Please upload your profile picture",Toast.LENGTH_LONG).show();
            return;
        }

        if (password1.isEmpty()){
            pass1.setError("Input your password");
            return;
        }
        if (password2.isEmpty()){
            pass2.setError("Input your same password");
            return;
        }
        /*if (proImagePath.toString().isEmpty()){
            Toast.makeText(RegistationActivity.this,"Please upload your profile picture",Toast.LENGTH_LONG).show();
            return;
        }*/
        if (password1.equals(password2)){
            password=pass2.getText().toString();
            Task<AuthResult> task= auth.createUserWithEmailAndPassword(userEmail,password);
            task.addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        user=auth.getCurrentUser();
                        Toast.makeText(RegistationActivity.this,"Sign in as: "+user.getEmail(),Toast.LENGTH_SHORT).show();
                        uploadProPic();
                    }
                }
            });
            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(RegistationActivity.this,"Sign in Failed: "+e.getMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }else {
            pass2.setError("password does not match!!!");
            return;
        }

    }

    public void cancle(View view) {
        startActivity(new Intent(RegistationActivity.this,LoginActivity.class));
    }

}

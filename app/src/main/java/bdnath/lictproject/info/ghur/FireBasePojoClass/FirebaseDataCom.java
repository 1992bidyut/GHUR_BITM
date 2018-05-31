package bdnath.lictproject.info.ghur.FireBasePojoClass;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.net.URL;

import bdnath.lictproject.info.ghur.MainActivity;
import retrofit2.http.Url;

public class FirebaseDataCom {
    private FirebaseAuth auth;
    private FirebaseUser currenUser;
    private StorageReference storageReference;
    private StorageReference userStorageReference;
    private FirebaseDatabase database;
    private DatabaseReference proRootRef;

    private Uri proImagePath=null;
    private Bitmap proBitmap;
    private String photoPath;

    private UserInfo userInfo=null;

    private Context context;


    public FirebaseDataCom(Context context) {
        this.context=context;
        auth= FirebaseAuth.getInstance();
        currenUser=auth.getCurrentUser();

        storageReference= FirebaseStorage.getInstance().getReference();
        userStorageReference=storageReference.child(currenUser.getUid());

        database=FirebaseDatabase.getInstance();
        proRootRef=database.getReference().child("profile");
    }

    public void uploadProPic(Uri proImagePath){
        final ProgressDialog progressDialog=new ProgressDialog(context);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();
        StorageReference proImgStorRef=userStorageReference.child("profile_image/profile.jpg");
        proImgStorRef.putFile(proImagePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        Toast.makeText(context,"Upload completed",
                                Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        progressDialog.dismiss();
                        Toast.makeText(context,"ERROR: "+exception.getMessage(),
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

    public Uri downloadProPic(){
        StorageReference proImgStorRef=userStorageReference.child("profile_image/profile.jpg");
        proImgStorRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.jpg'
                proImagePath =uri;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                return;
            }
        });
        return proImagePath;
    }

    public UserInfo getUserInformation(){
        proRootRef.child(currenUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userInfo=dataSnapshot.getValue(UserInfo.class);
                //Toast.makeText(context,userInfo.getFullName(),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                return;
            }
        });
        return userInfo;
    }
}

/*  private StorageReference storageReference;
    private StorageReference userStorageReference;*/
  /*  private Uri proImagePath;
    private Bitmap proBitmap;*/
   /*storageReference= FirebaseStorage.getInstance().getReference();
        userStorageReference=storageReference.child(currenUser.getUid());*/
    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==101&&resultCode == RESULT_OK){
            proImagePath = data.getData();
            try {
                ImageView proImg=nav_view.findViewById(R.id.profileIMG);
                proBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(proImagePath));
                proImg.setImageBitmap(proBitmap);
                uploadProPic();
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
        StorageReference proImgStorRef=userStorageReference.child("profile_image/profile.jpg");
        proImgStorRef.putFile(proImagePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this,"Upload completed",
                                Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this,"ERROR: "+exception.getMessage(),
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

    private void downloadProPic(final ImageView proImg){
        StorageReference proImgStorRef=userStorageReference.child("profile_image/profile.jpg");
        proImgStorRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.jpg'
                Picasso.get().load(Uri.parse(uri.toString())).into(proImg);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }*/

    ///////////////////////////////////////////////////////////////////////////////////////
            /*proImg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 101);
                return false;
            }
        });*/
//////////////////////////////////
/////////////////////////////////
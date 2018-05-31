package bdnath.lictproject.info.ghur;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

import bdnath.lictproject.info.ghur.Events.AddEventFragment;
import bdnath.lictproject.info.ghur.Events.EventDetailFragment;
import bdnath.lictproject.info.ghur.Events.EventUpdateFragment;
import bdnath.lictproject.info.ghur.Events.EventsViewFragment;
import bdnath.lictproject.info.ghur.FireBasePojoClass.UserInfo;
import bdnath.lictproject.info.ghur.Gallery.GalleryFragment;
import bdnath.lictproject.info.ghur.Location.LocationFragment;
import bdnath.lictproject.info.ghur.FireBasePojoClass.EventHandler;
import bdnath.lictproject.info.ghur.ProfileWork.EditProfileFragment;
import bdnath.lictproject.info.ghur.ProfileWork.ProfileViewFragment;
import bdnath.lictproject.info.ghur.SharedPreference.LoginPreferences;
import bdnath.lictproject.info.ghur.Weather.WeatherFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,AddEventFragment.MainViewListener,
        EventsViewFragment.DetailViewListener,EventDetailFragment.EventUpdateListener,
        ProfileViewFragment.EditProfileListener,EditProfileFragment.GoMainListener{

    private LoginPreferences preferences;
    private FirebaseAuth auth;
    private FirebaseUser currenUser;

    private FirebaseDatabase database;
    private DatabaseReference rootRef;

    private View nav_view;

    private Uri proImagePath=null;


    //FirebaseDataCom firebaseDataCom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences=new LoginPreferences(this);

        auth= FirebaseAuth.getInstance();
        currenUser=auth.getCurrentUser();

        database=FirebaseDatabase.getInstance();
        rootRef=database.getReference().child("profile");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Fragment fragment=new EventsViewFragment();
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer,fragment);
        transaction.addToBackStack(null);;
        transaction.commit();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Press longer to add new Event.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });
        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Fragment fragment=new AddEventFragment();
                FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmentContainer,fragment);
                transaction.addToBackStack(null);
                transaction.commit();
                return false;
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        ////////////////////////////////////
        /////////////////////////////////////
        nav_view=navigationView.getHeaderView(0);
        TextView userEmail=nav_view.findViewById(R.id.userEmail);
        final TextView name=nav_view.findViewById(R.id.userName);
        userEmail.setText(currenUser.getEmail());
        final ImageView proImg=nav_view.findViewById(R.id.profileIMG);

        rootRef.child(currenUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserInfo info=dataSnapshot.getValue(UserInfo.class);
                name.setText(info.getFullName());
                Picasso.get().load(Uri.parse(info.getProfileImageUrl())).fit().into(proImg);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        navigationView.setNavigationItemSelectedListener(this);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action_viewProfile:
                Fragment fragment=new ProfileViewFragment();
                FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmentContainer,fragment);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            case R.id.action_logout:
                auth.signOut();
                preferences.setStatus(false);
                preferences.registerAdmin(null,null);
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        fragmentDisplay(id);
        return true;
    }

    private void fragmentDisplay(int id){
        Fragment fragment=null;
        switch (id){
            case R.id.nav_camera:
                //openCamera();
                break;
            case R.id.nav_gallery:
                fragment=new GalleryFragment();
                break;
            case R.id.nav_location:
                fragment=new LocationFragment();
                break;
            case R.id.nav_weather:
                fragment=new WeatherFragment();
                 break;
        }
        if (fragment!=null){
            FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainer,fragment);
            transaction.addToBackStack(null);;
            transaction.commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public void goMainView() {
        Fragment fragment=new EventsViewFragment();
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer,fragment);
        transaction.addToBackStack(null);;
        transaction.commit();
    }


    @Override
    public void goDetailViewWithData(EventHandler handler) {
        Fragment fragment=new EventDetailFragment();
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();

        Bundle bundle=new Bundle();
        bundle.putSerializable("obj",handler);
        fragment.setArguments(bundle);

        transaction.replace(R.id.fragmentContainer,fragment);
        transaction.addToBackStack(null);;
        transaction.commit();
    }

    @Override
    public void getEventUpdateFragment(EventHandler handler) {

        Fragment fragment=new EventUpdateFragment();
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();

        Bundle bundle=new Bundle();
        bundle.putSerializable("obj",handler);
        fragment.setArguments(bundle);

        transaction.replace(R.id.fragmentContainer,fragment);
        transaction.addToBackStack(null);;
        transaction.commit();
    }

    @Override
    public void getEditFragment(UserInfo info) {
        Fragment fragment=new EditProfileFragment();
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();

        Bundle bundle=new Bundle();
        bundle.putSerializable("obj",info);
        fragment.setArguments(bundle);

        transaction.replace(R.id.fragmentContainer,fragment);
        transaction.addToBackStack(null);;
        transaction.commit();
    }

    @Override
    public void getMainView() {
        Fragment fragment=new EventsViewFragment();
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer,fragment);
        transaction.addToBackStack(null);;
        transaction.commit();
    }

    @Override
    public void getProfileView() {
        Fragment fragment=new ProfileViewFragment();
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer,fragment);
        transaction.addToBackStack(null);;
        transaction.commit();
    }


    /*public void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager()) != null){
            File file = null;
            try {
                file = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(file != null){
                photoPath = file.getAbsolutePath();
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intent,111);
            }
        }
    }

    private File createImageFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageName = "GHUR/JPEG_"+timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(imageName,".jpg",storageDir);
        return imageFile;
    }*/

}

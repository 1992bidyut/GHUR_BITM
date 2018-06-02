package bdnath.lictproject.info.ghur;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.util.Log;
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

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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
import com.google.maps.android.clustering.ClusterManager;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bdnath.lictproject.info.ghur.Events.AddEventFragment;
import bdnath.lictproject.info.ghur.Events.EventDetailFragment;
import bdnath.lictproject.info.ghur.Events.EventUpdateFragment;
import bdnath.lictproject.info.ghur.Events.EventsViewFragment;
import bdnath.lictproject.info.ghur.FireBasePojoClass.GalleryHandeler;
import bdnath.lictproject.info.ghur.FireBasePojoClass.UserInfo;
import bdnath.lictproject.info.ghur.Gallery.GalleryFragment;
import bdnath.lictproject.info.ghur.Location.LocationFragment;
import bdnath.lictproject.info.ghur.FireBasePojoClass.EventHandler;
import bdnath.lictproject.info.ghur.Location.MarkerItem;
import bdnath.lictproject.info.ghur.ProfileWork.EditProfileFragment;
import bdnath.lictproject.info.ghur.ProfileWork.ProfileViewFragment;
import bdnath.lictproject.info.ghur.ProfileWork.RegistationActivity;
import bdnath.lictproject.info.ghur.SharedPreference.LoginPreferences;
import bdnath.lictproject.info.ghur.Weather.WeatherFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,AddEventFragment.MainViewListener,
        EventsViewFragment.DetailViewListener,EventDetailFragment.EventUpdateListener,
        ProfileViewFragment.EditProfileListener,EditProfileFragment.GoMainListener, OnMapReadyCallback{

    private LoginPreferences preferences;
    private FirebaseAuth auth;
    private FirebaseUser currenUser;

    private DatabaseReference roofRef;
    private DatabaseReference galleryRef;
    private StorageReference storageReference;
    private StorageReference userStorageReference;
    private FirebaseDatabase database;
    private DatabaseReference profileRoot;

    private Uri proImagePath=null;
    private String photoPath = null;

    private View nav_view;
    private GoogleMapOptions mapOptions;
    private GoogleMap map;
    private ClusterManager<MarkerItem>clusterManager;


    //FirebaseDataCom firebaseDataCom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences=new LoginPreferences(this);

        auth= FirebaseAuth.getInstance();
        currenUser=auth.getCurrentUser();

        database=FirebaseDatabase.getInstance();
        profileRoot=database.getReference().child("profile");

        storageReference= FirebaseStorage.getInstance().getReference();
        userStorageReference=storageReference.child(currenUser.getUid());

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

        profileRoot.child(currenUser.getUid()).addValueEventListener(new ValueEventListener() {
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
            case R.id.nav_home:
                fragment=new EventsViewFragment();
                break;
            case R.id.nav_camera:
                openCamera();
                break;
            case R.id.nav_gallery:
                fragment=new GalleryFragment();
                break;
            case R.id.nav_location:
                //fragment=new LocationFragment();
                getMapWorkFragment();
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


    public void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager()) != null){
            File file=null;
            try {
                file=createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (file!=null){
                photoPath=file.getAbsolutePath();
                proImagePath = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,proImagePath);
                startActivityForResult(intent,111);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==111&&resultCode==RESULT_OK){
            /*Bundle extras=data.getExtras();
            Bitmap bitmap= (Bitmap) extras.get("data");*/
            uploadProPic();
        }
    }
    private File createImageFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageName = "GHUR_"+timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(imageName,".jpg",storageDir);
        return imageFile;
    }

    private void uploadProPic(){
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        final String imageName = "GHUR_"+timeStamp;
        StorageReference proImgStorRef=userStorageReference.child("gallery/"+imageName+".jpg");
        proImgStorRef.putFile(proImagePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        downloadProPic(imageName);
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
    private void downloadProPic(String imageName){
        StorageReference proImgStorRef=userStorageReference.child("gallery/"+imageName+".jpg");
        proImgStorRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.jpg'
                String profileImagePath=uri.toString();
                Toast.makeText(MainActivity.this,profileImagePath,Toast.LENGTH_LONG).show();
                insertProfileInfo(profileImagePath);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }
   private void insertProfileInfo(String url){
        roofRef=FirebaseDatabase.getInstance().getReference();
       galleryRef=roofRef.child("galleryURLs");
       GalleryHandeler handler=new GalleryHandeler(url,galleryRef.push().getKey(),photoPath);
       galleryRef.child(currenUser.getUid()).push().setValue(handler);

       Fragment fragment=new GalleryFragment();
       FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
       transaction.replace(R.id.fragmentContainer,fragment);
       transaction.addToBackStack(null);;
       transaction.commit();
    }

    //MapWork///
    private void getMapWorkFragment(){
        mapOptions = new GoogleMapOptions();
        mapOptions.zoomControlsEnabled(true);
        //mapOptions.mapType(GoogleMap.)

        SupportMapFragment mapFragment = SupportMapFragment.newInstance(mapOptions);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragmentContainer, mapFragment);
        ft.commit();
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        final List<MarkerItem>items=new ArrayList<>();
        clusterManager=new ClusterManager<MarkerItem>(this,map);
        map.setOnCameraIdleListener(clusterManager);
        map.setOnMarkerClickListener(clusterManager);
        LatLng kb = new LatLng(23.750854, 90.393527);

        // map.addMarker(new MarkerOptions().title("BDBL").position(kb));
        // map.moveCamera(CameraUpdateFactory.newLatLngZoom(kb, 15));
        //////////////////
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission
                    .ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},111);
            return;
        }
        map.setMyLocationEnabled(true);

        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                MarkerItem markerItem=new MarkerItem(latLng);
                items.add(markerItem);
                Marker marker=map.addMarker(new MarkerOptions().title("Custom").position(latLng));
                LatLng endlatlng=marker.getPosition();
                Toast.makeText(MainActivity.this,"lat:"+endlatlng.latitude,Toast.LENGTH_SHORT).show();
            }
        });

    }
}

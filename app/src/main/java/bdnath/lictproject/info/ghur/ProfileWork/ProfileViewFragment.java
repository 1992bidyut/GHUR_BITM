package bdnath.lictproject.info.ghur.ProfileWork;


import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import bdnath.lictproject.info.ghur.FireBasePojoClass.UserInfo;
import bdnath.lictproject.info.ghur.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileViewFragment extends Fragment {
    private ImageView profileImage;
    private TextView fullName;
    private TextView email;
    private TextView city;
    private TextView dob;
    private TextView country;
    private TextView gender;
    private Button updateProfile;

    private FirebaseDatabase database;
    private DatabaseReference rootRef;
    private FirebaseAuth auth;
    private FirebaseUser currenUser;
    private EditProfileListener listener;
    private UserInfo info;


    public ProfileViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_profile_view, container, false);

        profileImage=view.findViewById(R.id.profileImage);
        fullName=view.findViewById(R.id.fullName);
        city=view.findViewById(R.id.city);
        dob=view.findViewById(R.id.dob);
        country=view.findViewById(R.id.country);
        email=view.findViewById(R.id.emailID);
        gender=view.findViewById(R.id.gender);
        updateProfile=view.findViewById(R.id.profile_update);

        listener= (EditProfileListener) getActivity();

        database=FirebaseDatabase.getInstance();
        rootRef=database.getReference().child("profile");

        auth= FirebaseAuth.getInstance();
        currenUser=auth.getCurrentUser();
        rootRef.child(currenUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                info=dataSnapshot.getValue(UserInfo.class);
                fullName.setText(info.getFullName());
                email.setText(info.getEmail());
                dob.setText(info.getdOb());
                city.setText(info.getCity());
                country.setText(info.getCountry());
                gender.setText(info.getGender());
                Picasso.get().load(Uri.parse(info.getProfileImageUrl())).fit().into(profileImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.getEditFragment(info);
            }
        });
        return view;
    }
public interface EditProfileListener{
        public void getEditFragment(UserInfo info);
}
}

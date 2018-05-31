package bdnath.lictproject.info.ghur.ProfileWork;


import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import bdnath.lictproject.info.ghur.Events.AddEventFragment;
import bdnath.lictproject.info.ghur.FireBasePojoClass.EventHandler;
import bdnath.lictproject.info.ghur.FireBasePojoClass.UserInfo;
import bdnath.lictproject.info.ghur.R;
import bdnath.lictproject.info.ghur.RegistationActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfileFragment extends Fragment {
    private ImageView profileImage;
    private EditText fullName;
    private TextView email;
    private EditText city;
    private EditText dob;
    private EditText country;
    private RadioGroup radioGroup;
    private Button actionDone;
    private Button actionCancle;

    private int year, month, day, hour, minute;
    private Calendar calendar;

    private FirebaseAuth auth;
    private FirebaseUser user;

    private DatabaseReference roofRef;
    private DatabaseReference profileRef;


    private String userName;
    private String userEmail;
    private String userDoB;
    private String userCity;
    private String userCountry;
    private String gender;

    private View view;
    private UserInfo info;
    private GoMainListener listener;

    public EditProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_edit_profile, container, false);

        fullName=view.findViewById(R.id.fullName);
        radioGroup=view.findViewById(R.id.radioGroup);
        city=view.findViewById(R.id.city);
        dob=view.findViewById(R.id.dob);
        country=view.findViewById(R.id.country);
        email=view.findViewById(R.id.emailID);
        actionDone=view.findViewById(R.id.action_done);
        actionCancle=view.findViewById(R.id.action_cancle);
        profileImage=view.findViewById(R.id.profileImage);

        listener= (GoMainListener) getActivity();

        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);


        try {
            Bundle bundle=getArguments();
            info= (UserInfo) bundle.getSerializable("obj");
            Picasso.get().load(Uri.parse(info.getProfileImageUrl())).fit().into(profileImage);
            fullName.setText(info.getFullName());
            email.setText(info.getEmail());
            dob.setText(info.getdOb());
            city.setText(info.getCity());
            country.setText(info.getCountry());
            gender=info.getGender();
            if (gender.equals("Male")){
                RadioButton rb=view.findViewById(R.id.male);
                rb.setChecked(true);
                gender=rb.getText().toString();
            }
            if (gender.equals("Female")){
                RadioButton rb=view.findViewById(R.id.female);
                rb.setChecked(true);
                gender=rb.getText().toString();
            }


        }catch (Exception e){
            Log.d("Exception for obj:",e.getMessage());
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb=view.findViewById(checkedId);
                gender=rb.getText().toString();
                Toast.makeText(getContext(),gender,Toast.LENGTH_LONG).show();
            }
        });

        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
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

        actionDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName=fullName.getText().toString();
                userDoB=dob.getText().toString();
                userCity=city.getText().toString();
                userCountry=country.getText().toString();

                if(userName.isEmpty()){
                    fullName.setError("Input your Full name");
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
                info.setCity(userCity);
                info.setCountry(userCountry);
                info.setdOb(userDoB);
                info.setFullName(userName);
                info.setGender(gender);

                roofRef= FirebaseDatabase.getInstance().getReference();
                profileRef=roofRef.child("profile");
                profileRef.child(user.getUid()).setValue(info);
                listener.getProfileView();
            }
        });

        actionCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.getMainView();
            }
        });

        return view;
    }

    public interface GoMainListener{
        public void getMainView();
        public void getProfileView();
    }

}

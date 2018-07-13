package bdnath.lictproject.info.ghur.Gallery;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import bdnath.lictproject.info.ghur.FireBasePojoClass.GalleryHandeler;
import bdnath.lictproject.info.ghur.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class GalleryFragment extends Fragment {

    private RecyclerView gallerylist;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference gallaryRoofRef;
    private DatabaseReference galleryRef;
    private List<GalleryHandeler>handelers=new ArrayList<>();
    private GalleryAdapter adapter;

    public GalleryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_gallery, container, false);
        gallerylist=view.findViewById(R.id.gallery_list);

        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        firebaseDatabase=FirebaseDatabase.getInstance();
        gallaryRoofRef=FirebaseDatabase.getInstance().getReference();
        galleryRef=gallaryRoofRef.child("galleryURLs");
        galleryRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                handelers.clear();
                for (DataSnapshot d:dataSnapshot.getChildren()){
                    GalleryHandeler h=d.getValue(GalleryHandeler.class);
                    handelers.add(h);
                }
                adapter=new GalleryAdapter(getContext(),handelers);

                LinearLayoutManager llm = new LinearLayoutManager(getContext());
                llm.setOrientation(LinearLayoutManager.VERTICAL);

                GridLayoutManager glm = new GridLayoutManager(getContext(),3);
                glm.setOrientation(GridLayoutManager.VERTICAL);
                gallerylist.setLayoutManager(glm);
                gallerylist.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

}

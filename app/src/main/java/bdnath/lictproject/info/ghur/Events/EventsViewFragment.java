package bdnath.lictproject.info.ghur.Events;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

import bdnath.lictproject.info.ghur.FireBasePojoClass.EventHandler;
import bdnath.lictproject.info.ghur.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventsViewFragment extends Fragment {
    private DatabaseReference roofRef;
    private DatabaseReference eventRef;
    private DatabaseReference eventExpenseRoot;

    private FirebaseUser currentUser;
    private FirebaseAuth auth;
    private DetailViewListener listener;

    private ListView eventList;
    private EventListAdapter adapter;

    private List<EventHandler> eventHandlerList = new ArrayList<>();


    public EventsViewFragment() {
        // Required empty public constructor
    }

    public interface DetailViewListener{
        public void goDetailViewWithData(EventHandler handler);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_events_view, container, false);
        eventList=view.findViewById(R.id.eventList);

        auth=FirebaseAuth.getInstance();
        currentUser=auth.getCurrentUser();
        roofRef= FirebaseDatabase.getInstance().getReference();
        eventRef=roofRef.child("Events");
        eventRef.keepSynced(true);
        eventExpenseRoot=eventRef.child("Expenses");

        eventRef.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                eventHandlerList.clear();
                for(DataSnapshot d:dataSnapshot.getChildren()){
                    EventHandler h=d.getValue(EventHandler.class);
                    eventHandlerList.add(h);
                }
                adapter=new EventListAdapter(getContext(),eventHandlerList);
                eventList.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EventHandler handler=eventHandlerList.get(position);
                listener= (DetailViewListener) getActivity();
                listener.goDetailViewWithData(handler);
            }
        });

        eventList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                builder.setTitle("Do you want to delete this item?");
                builder.setCancelable(false);
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deletItem(position);
                    }
                });
                builder.setNegativeButton("Cancle",null);
                builder.show();
                return false;
            }
        });


        return view;
    }
    public void deletItem(int position){
        String sId = eventHandlerList.get(position).getEventID();
        eventRef.child(currentUser.getUid()).child(sId).removeValue();
        eventExpenseRoot.child(sId).removeValue();
    }

}

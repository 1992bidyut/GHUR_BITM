package bdnath.lictproject.info.ghur.Events;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import bdnath.lictproject.info.ghur.FireBasePojoClass.EventHandler;
import bdnath.lictproject.info.ghur.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventDetailFragment extends Fragment {
    private TextView eventName;
    private TextView eventPlace;
    private TextView eventStartTime;
    private TextView eventEndTime;
    private TextView eventExpense;
    private TextView eventDetail;
    private Button eventUpdate;

    private EventHandler handler;
    private EventUpdateListener listener;


    public EventDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_event_detail, container, false);
        eventDetail=view.findViewById(R.id.eventDetail);
        eventExpense=view.findViewById(R.id.eventExpense);
        eventName=view.findViewById(R.id.eventName);
        eventPlace=view.findViewById(R.id.eventPlace);
        eventStartTime=view.findViewById(R.id.eventStartDate);
        eventEndTime=view.findViewById(R.id.eventEndDate);
        eventUpdate=view.findViewById(R.id.event_update);
        listener= (EventUpdateListener) getActivity();


        try {
            Bundle bundle=getArguments();
            handler= (EventHandler) bundle.getSerializable("obj");

            eventStartTime.setText(handler.getEventStartDate());
            eventEndTime.setText(handler.getEventEndDate());
            eventPlace.setText(handler.getEventPlace());
            eventName.setText(handler.getEventTitle());
            eventExpense.setText(String.valueOf(handler.getEventCost()));
            eventDetail.setText(handler.getEventDetail());
        }catch (Exception e){
            Log.d("Exception for obj:",e.getMessage());
        }

        eventUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.getEventUpdateFragment(handler);
            }
        });

        return view;
    }
public interface EventUpdateListener{
        public void getEventUpdateFragment(EventHandler handler);
}
}

package bdnath.lictproject.info.ghur.Events;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import bdnath.lictproject.info.ghur.FireBasePojoClass.EventHandler;
import bdnath.lictproject.info.ghur.R;

public class EventListAdapter extends ArrayAdapter<EventHandler>{
    private Context context;
    private List<EventHandler>eventHandlerList;


    public EventListAdapter(@NonNull Context context,List<EventHandler> eventHandlerList) {
        super(context, R.layout.event_list_row, eventHandlerList);
        this.context=context;
        this.eventHandlerList=eventHandlerList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView=inflater.inflate(R.layout.event_list_row,parent,false);

        ImageView imageView=convertView.findViewById(R.id.event_coverIMG);
        TextView titleTV=convertView.findViewById(R.id.event_Title);
        TextView date=convertView.findViewById(R.id.event_cost);
        TextView cost=convertView.findViewById(R.id.event_cost);
        TextView location=convertView.findViewById(R.id.event_location);
        titleTV.setText(eventHandlerList.get(position).getEventTitle());
        date.setText(eventHandlerList.get(position).getEventStartDate());
        cost.setText(String.valueOf(eventHandlerList.get(position).getEventCost()));
        location.setText(eventHandlerList.get(position).getEventPlace());
        return convertView;
    }
}

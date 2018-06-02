package bdnath.lictproject.info.ghur.Events;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
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

import bdnath.lictproject.info.ghur.FireBasePojoClass.EventExpenseHandeler;
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
    private Button expences;

    private EventHandler handler;
    private EventUpdateListener listener;
    private DatabaseReference roofRef;
    private DatabaseReference eventRef;
    private DatabaseReference eventExpenseRoot;
    private DatabaseReference eventExpenseRef;
    private FirebaseUser user;
    private FirebaseAuth auth;
    private EventExpenseHandeler expenseHandeler;
    private float totalExpenseAmount;


    public EventDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
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
        expences=view.findViewById(R.id.expenses);

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

        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        roofRef= FirebaseDatabase.getInstance().getReference();
        eventRef=roofRef.child("Events");
        eventExpenseRoot=eventRef.child("Expenses");
        eventExpenseRef=eventExpenseRoot.child(handler.getEventID());

        eventUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.getEventUpdateFragment(handler);
            }
        });
        expences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                builder.setTitle("Expenses");
                builder.setCancelable(false);
                LayoutInflater inflater= LayoutInflater.from(getContext());
                LinearLayout root= (LinearLayout) inflater.inflate(R.layout.event_budget_custom_dialog,null);
                builder.setView(root);

                final EditText originalBudget=root.findViewById(R.id.original_budget);
                final EditText extraMoney=root.findViewById(R.id.extra_money);
                final EditText expenseType=root.findViewById(R.id.expense_type);
                final EditText expenseMoney=root.findViewById(R.id.expense_money);
                final TextView totalAmount=root.findViewById(R.id.total_amount);
                final TextView expenseTotal=root.findViewById(R.id.expense_total);
                final ListView expenseList=root.findViewById(R.id.expenseList);

                Button addExtra=root.findViewById(R.id.add_extra);
                Button addExpense=root.findViewById(R.id.add_expense);

                originalBudget.setText(String.valueOf(handler.getEventCost()));
                extraMoney.setText(String.valueOf(handler.getExtraExpense()));
                totalExpenseAmount=handler.getExtraExpense()+handler.getEventCost();
                totalAmount.setText(String.valueOf(totalExpenseAmount));

                addExpense.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        float expAmount=0;
                        String expType=expenseType.getText().toString();
                        try {
                            expAmount=Float.valueOf(expenseMoney.getText().toString());
                        }catch (NumberFormatException e){

                        }
                        if (expType.isEmpty()){
                            expenseType.setError("Empty case");
                            return;
                        }
                        if (expAmount==0){
                            expenseMoney.setError("Empty amount");
                            return;
                        }

                        expenseHandeler=new EventExpenseHandeler(expType,expAmount);
                        eventExpenseRef.push().setValue(expenseHandeler);
                    }
                });

                eventExpenseRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ExpenseListAdapter adapter;
                        float totalExpense=0;
                        float totalBudget=Float.valueOf(totalAmount.getText().toString());
                        List<EventExpenseHandeler>handelersList= new ArrayList<>();
                        for(DataSnapshot d:dataSnapshot.getChildren()){
                            EventExpenseHandeler h=d.getValue(EventExpenseHandeler.class);
                            totalExpense=totalExpense+h.getExpenseAmount();
                            handelersList.add(h);
                        }
                        expenseTotal.setText(String.valueOf(totalExpense));
                        if (totalExpense>totalBudget-500){
                            expenseTotal.setBackgroundColor(Color.RED);
                            expenseTotal.setTextColor(Color.WHITE);
                        }else {
                            expenseTotal.setBackgroundColor(Color.WHITE);
                            expenseTotal.setTextColor(Color.BLACK);
                        }

                        adapter=new ExpenseListAdapter(getContext(),handelersList);
                        expenseList.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                addExtra.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        float extAmount=Float.valueOf(extraMoney.getText().toString());
                        totalExpenseAmount=handler.getEventCost()+extAmount;
                        totalAmount.setText(String.valueOf(totalExpenseAmount));
                        handler.setExtraExpense(extAmount);
                        eventRef.child(user.getUid()).child(handler.getEventID()).setValue(handler);
                        if (Float.valueOf(expenseTotal.getText().toString())>totalExpenseAmount-500){
                            expenseTotal.setBackgroundColor(Color.RED);
                            expenseTotal.setTextColor(Color.WHITE);
                        }else {
                            expenseTotal.setBackgroundColor(Color.WHITE);
                            expenseTotal.setTextColor(Color.BLACK);
                        }
                    }
                });
                builder.setPositiveButton("Ok",null);
                builder.show();
            }
        });

        return view;
    }
public interface EventUpdateListener{
        public void getEventUpdateFragment(EventHandler handler);
}
}

package bdnath.lictproject.info.ghur.Events;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import bdnath.lictproject.info.ghur.FireBasePojoClass.EventExpenseHandeler;
import bdnath.lictproject.info.ghur.R;

public class ExpenseListAdapter extends ArrayAdapter<EventExpenseHandeler> {
    private Context context;
    private List<EventExpenseHandeler>expenseHandelers;

    public ExpenseListAdapter(@NonNull Context context, List<EventExpenseHandeler>expenseHandelers) {
        super(context, R.layout.expense_list_row, expenseHandelers);
        this.context=context;
        this.expenseHandelers=expenseHandelers;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView=inflater.inflate(R.layout.expense_list_row,parent,false);
        TextView expCase=convertView.findViewById(R.id.expenseCaseLV);
        TextView expAmount=convertView.findViewById(R.id.expenseAmounLV);

        expCase.setText(expenseHandelers.get(position).getExpenseCase());
        expAmount.setText(String.valueOf(expenseHandelers.get(position).getExpenseAmount()));

        return convertView;
    }
}

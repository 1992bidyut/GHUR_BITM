package bdnath.lictproject.info.ghur.FireBasePojoClass;

public class EventExpenseHandeler {
    private String expenseCase;
    private float expenseAmount;

    public EventExpenseHandeler() {
    }

    public EventExpenseHandeler(String expenseCase, float expenseAmount) {
        this.expenseCase = expenseCase;
        this.expenseAmount = expenseAmount;
    }

    public String getExpenseCase() {
        return expenseCase;
    }

    public void setExpenseCase(String expenseCase) {
        this.expenseCase = expenseCase;
    }

    public float getExpenseAmount() {
        return expenseAmount;
    }

    public void setExpenseAmount(float expenseAmount) {
        this.expenseAmount = expenseAmount;
    }
}

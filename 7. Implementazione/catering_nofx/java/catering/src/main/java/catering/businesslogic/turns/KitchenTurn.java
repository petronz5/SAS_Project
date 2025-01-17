package catering.businesslogic.turns;

import java.sql.Date;

public class KitchenTurn extends Turn {
    private Date perfDate;
    private boolean kitchenFull;

    public KitchenTurn(Date expirationDate, String preparationPlace, Date perfDate, boolean kitchenFull,
                       boolean recurrence, int staffLimit, int currentStaff, Date endDate) {
        super(expirationDate, preparationPlace, null, null, recurrence, staffLimit, currentStaff, endDate);
        this.perfDate = perfDate;
        this.kitchenFull = kitchenFull;
    }

    public Date getPerfDate() {
        return perfDate;
    }

    public void setPerfDate(Date perfDate) {
        this.perfDate = perfDate;
    }

    public boolean isKitchenFull() {
        return kitchenFull;
    }

    public void setKitchenFull(boolean kitchenFull) {
        this.kitchenFull = kitchenFull;
    }

    public void modify(String place, Date expirationDate, Date endDate, Date perfDate, boolean kitchenFull) {
        super.modify(place, expirationDate, endDate);
        this.perfDate = perfDate;
        this.kitchenFull = kitchenFull;
    }
}

package catering.businesslogic.turns;

import java.sql.Date;
import java.sql.Time;

public class ServiceTurn extends Turn {
    private Time prepTime;
    private Time rigTime;

    public ServiceTurn(Date expirationDate, String preparationPlace, Time startTime, Time endTime,
                       boolean recurrence, int staffLimit, int currentStaff, Date endDate,
                       Time prepTime, Time rigTime) {
        super(expirationDate, preparationPlace, startTime, endTime, recurrence, staffLimit, currentStaff, endDate);
        this.prepTime = prepTime;
        this.rigTime = rigTime;
    }

    public Time getPrepTime() {
        return prepTime;
    }

    public void setPrepTime(Time prepTime) {
        this.prepTime = prepTime;
    }

    public Time getRigTime() {
        return rigTime;
    }

    public void setRigTime(Time rigTime) {
        this.rigTime = rigTime;
    }

    public void modify(Time prepTime, Time rigTime, String place, Date expirationDate, Date endDate, Time timeStart, Time timeEnd) {
        this.prepTime = prepTime;
        this.rigTime = rigTime;
        super.modify(place, expirationDate, endDate);
        super.setStartTime(timeStart);
        super.setEndTime(timeEnd);
    }
}

package catering.businesslogic.turns;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;

public class Turn {
    private Date expirationDate;
    private String preparationPlace;
    private Time startTime;
    private Time endTime;
    private boolean recurrence;
    private int staffLimit;
    private int currentStaff;
    private Date endDate;
    private ArrayList<Cook> involvedCooks;

    public Turn(Date expirationDate, String preparationPlace, Time startTime, Time endTime,
                boolean recurrence, int staffLimit, int currentStaff, Date endDate) {
        this.expirationDate = expirationDate;
        this.preparationPlace = preparationPlace;
        this.startTime = startTime;
        this.endTime = endTime;
        this.recurrence = recurrence;
        this.staffLimit = staffLimit;
        this.currentStaff = currentStaff;
        this.endDate = endDate;
        this.involvedCooks = new ArrayList<>();
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getPreparationPlace() {
        return preparationPlace;
    }

    public void setPreparationPlace(String preparationPlace) {
        this.preparationPlace = preparationPlace;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public boolean isRecurrence() {
        return recurrence;
    }

    public void setRecurrence(boolean recurrence) {
        this.recurrence = recurrence;
    }

    public int getStaffLimit() {
        return staffLimit;
    }

    public void setStaffLimit(int staffLimit) {
        this.staffLimit = staffLimit;
    }

    public int getCurrentStaff() {
        return currentStaff;
    }

    public void setCurrentStaff(int currentStaff) {
        this.currentStaff = currentStaff;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void addCook(Cook cook) {
        if (this.involvedCooks.size() < staffLimit) {
            this.involvedCooks.add(cook);
            this.currentStaff++;
        }
    }

    public void removeCook(Cook cook) {
        if (this.involvedCooks.contains(cook)) {
            this.involvedCooks.remove(cook);
            this.currentStaff--;
        }
    }

    public boolean containsCook(Cook cook) {
        return this.involvedCooks.contains(cook);
    }

    public boolean isFull() {
        return this.currentStaff >= this.staffLimit;
    }

    public void modify(String preparationPlace, Date expirationDate, Date endDate) {
        this.preparationPlace = preparationPlace;
        this.expirationDate = expirationDate;
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "Turn{" +
                "expirationDate=" + expirationDate +
                ", preparationPlace='" + preparationPlace + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", recurrence=" + recurrence +
                ", staffLimit=" + staffLimit +
                ", currentStaff=" + currentStaff +
                ", endDate=" + endDate +
                ", involvedCooks=" + involvedCooks +
                '}';
    }
}

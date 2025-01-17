package catering.businesslogic.turns;

public class Cook {
    private String name;
    private int badge;

    public Cook(String name, int badge) {
        this.name = name;
        this.badge = badge;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBadge() {
        return badge;
    }

    public void setBadge(int badge) {
        this.badge = badge;
    }

    @Override
    public String toString() {
        return "Cook{name='" + name + "', badge=" + badge + "}";
    }
}

package catering.businesslogic.turns;

import catering.persistence.PersistenceManager;

import java.util.concurrent.atomic.AtomicReference;

public class Cook {
    private int id;
    private String name;
    private int badge;

    public Cook(String name, int badge) {
        this.name = name;
        this.badge = badge;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public static Cook loadCookById(int cookId) {
        AtomicReference<Cook> cookRef = new AtomicReference<>(null);
        String query = "SELECT * FROM catering.Cooks WHERE id = " + cookId;

        PersistenceManager.executeQuery(query, rs -> {
            // NON fare if (rs.next()), perché siamo già dentro un while(rs.next()) esterno
            Cook c = new Cook(rs.getString("name"), rs.getInt("badge"));
            c.setId(rs.getInt("id"));
            cookRef.set(c);
        });

        return cookRef.get();
    }

}

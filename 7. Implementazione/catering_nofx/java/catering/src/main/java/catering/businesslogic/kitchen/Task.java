package catering.businesslogic.kitchen;

import catering.businesslogic.recipe.Recipe;
import catering.persistence.BatchUpdateHandler;
import catering.persistence.PersistenceManager;
import catering.persistence.ResultHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Task {
    private int id;
    private int portions;
    private String quantity;
    private int estimatedTime;
    private boolean completed;

    private Recipe recipe;

    public Task(Recipe recipe) {
        this.recipe = recipe;
        this.portions = 0;
        this.quantity = "";
        this.estimatedTime = 0;
        this.completed = false;
    }

    public int getId() {
        return id;
    }

    public int getPortions() {
        return portions;
    }

    public void setPortions(int portions) {
        this.portions = portions;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public int getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(int estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    // STATIC METHODS FOR PERSISTENCE
    public static void saveNewTask(Task task) {
        String insert = "INSERT INTO catering.Tasks (recipe_id, portions, quantity, estimated_time, completed) " +
                "VALUES (?, ?, ?, ?, ?)";
        PersistenceManager.executeBatchUpdate(insert, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, task.recipe.getId());
                ps.setInt(2, task.portions);
                ps.setString(3, task.quantity);
                ps.setInt(4, task.estimatedTime);
                ps.setBoolean(5, task.completed);
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                if (rs.next()) {
                    task.id = rs.getInt(1);
                }
            }
        });
    }

    public static void saveModifiedTask(Task task) {
        if (task.id <= 0) {
            throw new IllegalArgumentException("Task ID is invalid or not set");
        }

        String update = "UPDATE catering.Tasks SET portions = ?, quantity = ?, estimated_time = ?, completed = ? " +
                "WHERE id = ?";
        PersistenceManager.executeUpdate(update, ps -> {
            ps.setInt(1, task.portions);
            ps.setString(2, task.quantity);
            ps.setInt(3, task.estimatedTime);
            ps.setBoolean(4, task.completed);
            ps.setInt(5, task.id);
        });
    }

    public static void deleteTaskAssignment(Task task) {
        String delete = "DELETE FROM catering.Tasks WHERE id = ?";
        PersistenceManager.executeUpdate(delete, ps -> ps.setInt(1, task.id));
    }

    public static void saveAssignedTask(Task task) {
        // Logic to save task assignment (shift, cook, etc.)
    }

    public static ArrayList<Task> loadTasksForSheet(int sheetId) {
        ArrayList<Task> tasks = new ArrayList<>();
        String query = "SELECT * FROM catering.Tasks WHERE summary_sheet_id = " + sheetId;

        PersistenceManager.executeQuery(query, rs -> {
            Task task = new Task(null); // Puoi iniziare con un Recipe null
            task.id = rs.getInt("id");
            task.portions = rs.getInt("portions");
            task.quantity = rs.getString("quantity");
            task.estimatedTime = rs.getInt("estimated_time");
            task.completed = rs.getBoolean("completed");
            tasks.add(task);
        });

        return tasks;
    }

}

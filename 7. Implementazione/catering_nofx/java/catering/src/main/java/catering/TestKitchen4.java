package catering;

import catering.businesslogic.CatERing;
import catering.businesslogic.UseCaseLogicException;
import catering.businesslogic.kitchen.SummarySheet;
import catering.businesslogic.kitchen.Task;
import catering.businesslogic.turns.Cook;
import catering.businesslogic.turns.Turn;

import java.sql.Date;
import java.sql.Time;

public class TestKitchen4 {
    public static void main(String[] args) {
        try {
            // Fake login
            CatERing.getInstance().getUserManager().fakeLogin("Lidia");
            System.out.println(CatERing.getInstance().getUserManager().getCurrentUser());

            // Open an existing summary sheet
            SummarySheet sheet = CatERing.getInstance().getTaskMgr().openSummarySheet(1);
            System.out.println("Opened summary sheet.");

            // Retrieve task, cook, and turn
            Task taskToAssign = sheet.getTasks().get(0);
            Cook cook = new Cook("Paolo", 123);  // Esempio di cuoco
            Turn turn = new Turn(
                    Date.valueOf("2025-01-17"), // expirationDate
                    "Main Kitchen",            // preparationPlace
                    Time.valueOf("08:00:00"),  // startTime
                    Time.valueOf("16:00:00"),  // endTime
                    false,                     // recurrence
                    5,                         // staffLimit
                    0,                         // currentStaff
                    Date.valueOf("2025-01-18") // endDate
            );

            // Assign the task
            CatERing.getInstance().getTaskMgr().assignTask(taskToAssign, turn, cook, "5kg", 120, 20);
            System.out.println("Task assigned to cook " + cook.getName() + " for turn.");

        } catch (UseCaseLogicException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
}

package catering;

import catering.businesslogic.CatERing;
import catering.businesslogic.UseCaseLogicException;
import catering.businesslogic.kitchen.SummarySheet;
import catering.businesslogic.kitchen.Task;

public class TestKitchen3 {
    public static void main(String[] args) {
        try {
            // Fake login
            CatERing.getInstance().getUserManager().fakeLogin("Lidia");
            System.out.println(CatERing.getInstance().getUserManager().getCurrentUser());

            // Open an existing summary sheet
            SummarySheet sheet = CatERing.getInstance().getTaskMgr().openSummarySheet(1);
            System.out.println("Opened summary sheet.");

            // Modify a task
            Task taskToModify = sheet.getTasks().get(0);
            CatERing.getInstance().getTaskMgr().modifyTask(taskToModify, "5kg", 120, 20);
            System.out.println("Task modified: " + taskToModify.getQuantity() + ", " + taskToModify.getEstimatedTime() + " minutes, " + taskToModify.getPortions() + " portions");

        } catch (UseCaseLogicException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
}

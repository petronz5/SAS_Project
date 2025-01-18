package catering;

import catering.businesslogic.CatERing;
import catering.businesslogic.kitchen.SummarySheet;
import catering.businesslogic.kitchen.Task;
import catering.businesslogic.recipe.Recipe;
import catering.businesslogic.UseCaseLogicException;

import java.util.ArrayList;

public class TestKitchen_OpenSummaryById_AddTask {
    public static void main(String[] args) {
        try {
            // 1) LOGIN
            CatERing.getInstance().getUserManager().fakeLogin("Lidia");
            System.out.println("Logged in as: " + CatERing.getInstance().getUserManager().getCurrentUser());

            // 2) Carichiamo un SummarySheet per ID
            //    Ad esempio, se in DB hai un record in SummarySheets con id=25
            int summarySheetId = 25;  // cambia con un ID reale
            SummarySheet sheet = SummarySheet.loadSummarySheetById(summarySheetId);
            System.out.println("Caricato SummarySheet con ID=" + summarySheetId);

            // 3) Carichiamo una ricetta (o più). Se vuoi caricarla per ID specifico:
            int recipeId = 1;
            Recipe r = Recipe.loadRecipeById(recipeId);
            if (r == null) {
                System.err.println("Ricetta con ID=" + recipeId + " non trovata in DB");
                return;
            }
            System.out.println("Caricata recipe: " + r.getName());

            // 4) Usiamo TaskManager per aggiungere la Task
            CatERing.getInstance().getTaskMgr().setCurrentSummarySheet(sheet);
            Task newTask = CatERing.getInstance().getTaskMgr().addTask(r);
            System.out.println("Aggiunta nuova Task con ID=" + newTask.getId()
                    + " su SummarySheet con ID=" + summarySheetId);

        } catch (UseCaseLogicException e) {
            System.err.println("UseCaseLogicException: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

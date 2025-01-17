package catering;

import catering.businesslogic.CatERing;
import catering.businesslogic.UseCaseLogicException;
import catering.businesslogic.kitchen.SummarySheet;
import catering.businesslogic.kitchen.Task;
import catering.businesslogic.recipe.Recipe;

import java.util.ArrayList;

public class TestKitchen2 {
    public static void main(String[] args) {
        try {
            // Fake login
            CatERing.getInstance().getUserManager().fakeLogin("Lidia");
            System.out.println(CatERing.getInstance().getUserManager().getCurrentUser());

            // Open an existing summary sheet
            SummarySheet sheet = CatERing.getInstance().getTaskMgr().openSummarySheet(1);
            System.out.println("Opened summary sheet.");

            // Retrieve recipes
            ArrayList<Recipe> recipes = CatERing.getInstance().getRecipeManager().getRecipes();
            Recipe selectedRecipe = recipes.get(0);

            // Add a task
            Task task = CatERing.getInstance().getTaskMgr().addTask(selectedRecipe);
            System.out.println("Added task: " + task.getRecipe().getName());

        } catch (UseCaseLogicException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
}

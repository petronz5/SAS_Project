package catering;

import catering.businesslogic.CatERing;
import catering.businesslogic.kitchen.SummarySheet;
import catering.businesslogic.kitchen.Task;
import catering.businesslogic.recipe.Recipe;
import catering.businesslogic.turns.Cook;
import catering.businesslogic.turns.Turn;
import catering.businesslogic.UseCaseLogicException;
import catering.persistence.TaskPersistence;

import java.util.ArrayList;

public class TestKitchen_FullWorkflow {
    public static void main(String[] args) {
        try {
            // 1) Login
            CatERing.getInstance().getUserManager().fakeLogin("Lidia");
            System.out.println("Logged in as: " + CatERing.getInstance().getUserManager().getCurrentUser());

            // 2) Aggiungi TaskPersistence come event receiver
            CatERing.getInstance().getTaskMgr().addEventReceiver(new TaskPersistence());

            // 3) Carica un SummarySheet per ID
            int summarySheetId = 28;
            SummarySheet sheet = CatERing.getInstance().getTaskMgr().openSummarySheetById(summarySheetId);

            // 4) Visualizza le Task esistenti
            ArrayList<Task> tasks = sheet.getTasks();
            System.out.println("Task esistenti:");
            for (Task t : tasks) {
                System.out.println(t);
            }

            // 5) Creazione manuale della Task
            int recipeId = 1;  // Assicurati che esista
            Recipe recipe = Recipe.loadRecipeById(recipeId);
            if (recipe == null) {
                System.err.println("Recipe con ID=" + recipeId + " non trovata.");
                return;
            }
            System.out.println("Caricata Recipe: " + recipe.getName());

            // Creiamo manualmente la Task
            Task newTask = new Task(recipe);
            newTask.setSummarySheetId(summarySheetId);
            newTask.setPortions(5); // Numero di porzioni
            newTask.setQuantity("1kg"); // Quantità
            newTask.setEstimatedTime(60); // Tempo stimato
            newTask.setCompleted(false); // Stato completamento
            Task.saveNewTask(newTask); // Salvataggio nel database

            sheet.getTasks().add(newTask);

            System.out.println("Aggiunta manualmente nuova Task con ID: " + newTask.getId());
            System.out.println("Dettagli Task: " + newTask);

            // 6) Modifica della Task appena creata
            System.out.println("Modifico Task con ID=" + newTask.getId());
            Task modifiedTask = CatERing.getInstance().getTaskMgr().modifyTask(newTask, "2kg", 30, 10);
            System.out.println("Task modificata: " + modifiedTask);

            // 7) Assegna la Task appena creata a un Cook e un Turn
            int cookId = 2;  // Assicurati che esista
            Cook cook = Cook.loadCookById(cookId);
            if (cook == null) {
                System.err.println("Cook con ID=" + cookId + " non trovato.");
                return;
            }
            System.out.println("Caricato Cook: " + cook);

            int turnId = 2;  // Assicurati che esista
            Turn turn = Turn.loadTurnById(turnId);
            if (turn == null) {
                System.err.println("Turn con ID=" + turnId + " non trovato.");
                return;
            }
            turn.addCook(cook);
            System.out.println("Caricato Turn: " + turn);

            Task assignedTask = CatERing.getInstance().getTaskMgr().assignTask(newTask, turn, cook, "3kg", 15, 45);
            System.out.println("Task assegnata: " + assignedTask);

            // 8) Segna la Task come completata
            Task completedTask = CatERing.getInstance().getTaskMgr().regCompletedTask(assignedTask);
            System.out.println("Task completata: " + completedTask);


        } catch (UseCaseLogicException e) {
            System.err.println("UseCaseLogicException: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

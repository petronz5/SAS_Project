package catering;

import catering.businesslogic.CatERing;
import catering.businesslogic.UseCaseLogicException;
import catering.businesslogic.event.EventInfo;
import catering.businesslogic.event.ServiceInfo;
import catering.businesslogic.kitchen.SummarySheet;
import catering.businesslogic.kitchen.Task;
import catering.businesslogic.recipe.Recipe;
import catering.businesslogic.turns.Cook;
import catering.businesslogic.turns.Turn;

import java.util.ArrayList;

public class TestKitchen_AlternateWorkflow {
    public static void main(String[] args) {
        try {
            // 1) Effettua il login (fake login)
            CatERing catERing = CatERing.getInstance();
            catERing.getUserManager().fakeLogin("Lidia");
            System.out.println("Logged in as: " + catERing.getUserManager().getCurrentUser());

            // 2) Carica l'evento corrente e il servizio associato
            EventInfo currentEvent = catERing.getEventManager().getCurrentEvent();
            if (currentEvent == null) {
                System.err.println("Nessun evento trovato.");
                return;
            }
            System.out.println("Current event: " + currentEvent);

            ArrayList<ServiceInfo> services = currentEvent.getServices();
            if (services.isEmpty()) {
                System.err.println("Nessun servizio trovato per l'evento corrente.");
                return;
            }
            // Seleziona il primo servizio (oppure implementa una logica più sofisticata)
            ServiceInfo service = services.get(0);
            System.out.println("Selected service: " + service);

            // 3) Apri il SummarySheet utilizzando service ed event
            SummarySheet sheet = catERing.getTaskMgr().openSummarySheet(service, currentEvent);
            if (sheet == null) {
                System.err.println("SummarySheet non trovato per service ID=" + service.getId() + " e event ID=" + currentEvent.getId());
                return;
            }
            System.out.println("Opened SummarySheet with ID: " + sheet.getId());

            // 4) Visualizza le Task esistenti
            ArrayList<Task> tasks = sheet.getTasks();
            System.out.println("Existing tasks in the SummarySheet:");
            if (tasks.isEmpty()) {
                System.out.println("Nessuna task esistente.");
            } else {
                for (Task t : tasks) {
                    System.out.println(t);
                }
            }

            // 5) Aggiungi una nuova Task utilizzando una Recipe esistente
            int recipeId = 1; // Assicurati che esista una Recipe con questo ID nel DB
            Recipe recipe = Recipe.loadRecipeById(recipeId);
            if (recipe == null) {
                System.err.println("Recipe con ID=" + recipeId + " non trovata.");
                return;
            }
            System.out.println("Loaded Recipe: " + recipe.getName());

            Task newTask = catERing.getTaskMgr().addTask(recipe);
            System.out.println("New task added with ID: " + newTask.getId());

            // 6) Modifica la nuova Task: ad esempio, modifica la quantità, il tempo stimato e le porzioni
            Task modifiedTask = catERing.getTaskMgr().modifyTask(newTask, "2kg", 30, 10);
            System.out.println("Modified task: " + modifiedTask);

            // 7) Assegna la Task a un Cook e a un Turn caricati dal DB
            int cookId = 2; // Assicurati che esista un Cook con questo ID
            Cook cook = Cook.loadCookById(cookId);
            if (cook == null) {
                System.err.println("Cook con ID=" + cookId + " non trovato.");
                return;
            }
            System.out.println("Loaded Cook: " + cook);

            int turnId = 2; // Assicurati che esista un Turn con questo ID
            Turn turn = Turn.loadTurnById(turnId);
            if (turn == null) {
                System.err.println("Turn con ID=" + turnId + " non trovato.");
                return;
            }
            System.out.println("Loaded Turn: " + turn);

            turn.addCook(cook);

            Task assignedTask = catERing.getTaskMgr().assignTask(modifiedTask, turn, cook, "3kg", 15, 45);
            System.out.println("Assigned task: " + assignedTask);

            // 8) Segna la Task assegnata come completata
            Task completedTask = catERing.getTaskMgr().regCompletedTask(assignedTask);
            System.out.println("Completed task: " + completedTask);

            /*
            // 9) Elimina la Task completata
            catERing.getTaskMgr().deleteAssignment(completedTask, null, null);
            System.out.println("Deleted task with ID: " + completedTask.getId());

             */

            // 10) Concludi il workflow
            System.out.println("Workflow terminato");

        } catch (UseCaseLogicException e) {
            System.err.println("UseCaseLogicException: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

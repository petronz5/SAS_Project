package catering;

import catering.businesslogic.CatERing;
import catering.businesslogic.kitchen.SummarySheet;
import catering.businesslogic.kitchen.Task;
import catering.businesslogic.turns.Cook;
import catering.businesslogic.turns.Turn;
import catering.businesslogic.UseCaseLogicException;

import java.util.ArrayList;

public class TestKitchen_AssignTaskWithRealCookTurn {
    public static void main(String[] args) {
        try {
            // 1) Login
            CatERing.getInstance().getUserManager().fakeLogin("Lidia");
            System.out.println("Logged in as: " + CatERing.getInstance().getUserManager().getCurrentUser());

            // 2) Carichiamo un SummarySheet per ID
            int sheetId = 28;  // Cambia con un ID esistente
            SummarySheet sheet = SummarySheet.loadSummarySheetById(sheetId);
            System.out.println("Caricato SummarySheet con ID=" + sheetId);

            // 3) Seleziona la prima Task (se esiste)
            ArrayList<Task> tasks = sheet.getTasks();
            if (tasks.isEmpty()) {
                System.out.println("Nessuna Task presente in SummarySheet ID=" + sheetId);
                return;
            }
            Task t = tasks.get(0);
            System.out.println("Selezionata Task con ID=" + t.getId());

            // 4) Carichiamo un Cook REALE dal DB
            int cookId = 2;  // esempio del terzo cuoco
            Cook realCook = Cook.loadCookById(cookId);
            if (realCook == null) {
                System.err.println("Cook con ID=" + cookId + " non trovato in DB.");
                return;
            }
            System.out.println("Caricato Cook: " + realCook);

            // 5) Carichiamo un Turn REALE dal DB
            int turnId = 2;  // esempio
            Turn realTurn = Turn.loadTurnById(turnId);
            if (realTurn == null) {
                System.err.println("Turn con ID=" + turnId + " non trovato in DB.");
                return;
            }
            System.out.println("Caricato Turn con ID=" + realTurn.getId());

            // 6) Assegna la Task
            // Impostiamo il currentSummarySheet
            CatERing.getInstance().getTaskMgr().setCurrentSummarySheet(sheet);

            Task assigned = CatERing.getInstance().getTaskMgr().assignTask(t,
                    realTurn,
                    realCook,
                    "2kg",   // quantity
                    30,      // time
                    12       // portions
            );
            System.out.println("Task assegnata con ID=" + assigned.getId()
                    + ", quantity=" + assigned.getQuantity());

            // 7) Se in TaskPersistence hai implementato saveAssignedTask(Task task) che scrive in TaskAssignments,
            //    controlla se compare un record in DB

        } catch (UseCaseLogicException e) {
            System.err.println("UseCaseLogicException: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package catering;

import catering.businesslogic.CatERing;
import catering.businesslogic.kitchen.SummarySheet;
import catering.businesslogic.kitchen.Task;
import catering.businesslogic.UseCaseLogicException;

import java.util.ArrayList;

public class TestKitchen_ModifyDeleteTask {
    public static void main(String[] args) {
        try {
            // 1) Login
            CatERing.getInstance().getUserManager().fakeLogin("Lidia");
            System.out.println("User logged in: " + CatERing.getInstance().getUserManager().getCurrentUser());

            // 2) Apri un SummarySheet esistente (es. event_id=1, se ci sono Task)
            int eventId = 1;
            SummarySheet sheet = CatERing.getInstance().getTaskMgr().openSummarySheet(eventId);
            System.out.println("Aperto SummarySheet con ID=" + sheet);

            // 3) Prendi la lista di Task esistenti
            ArrayList<Task> tasks = sheet.getTasks();
            if (tasks.isEmpty()) {
                System.err.println("Questo SummarySheet non ha Task. Impossibile modificarne/eliminarne.");
                return;
            }
            // Selezioniamo la prima Task
            Task t = tasks.get(0);
            System.out.println("Selezionata Task con ID=" + t.getId());

            // 4) Modifica la Task (es. quantity="3kg", time=25, portions=10)
            Task modified = CatERing.getInstance().getTaskMgr().modifyTask(t, "3kg", 25, 10);
            System.out.println("Task modificata: quantity=" + modified.getQuantity()
                    + ", estimatedTime=" + modified.getEstimatedTime()
                    + ", portions=" + modified.getPortions());

            // 5) Eliminiamo la Task
            CatERing.getInstance().getTaskMgr().deleteAssignment(modified, null, null);
            System.out.println("Task con ID=" + modified.getId() + " eliminata.");

            // 6) Controlla su DB: la tabella Tasks per verificare che la riga sia sparita
        } catch (UseCaseLogicException e) {
            System.err.println("UseCaseLogicException: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package catering;

import catering.businesslogic.CatERing;
import catering.businesslogic.event.EventInfo;
import catering.businesslogic.event.ServiceInfo;
import catering.businesslogic.kitchen.SummarySheet;
import catering.businesslogic.recipe.Recipe;
import catering.businesslogic.UseCaseLogicException;

import java.util.ArrayList;

public class TestKitchen_CreateSummarySheet {
    public static void main(String[] args) {
        try {
            // 1) Login (fake)
            CatERing.getInstance().getUserManager().fakeLogin("Lidia");
            System.out.println("User logged in: " + CatERing.getInstance().getUserManager().getCurrentUser());

            // 2) Carica gli Eventi
            ArrayList<EventInfo> events = CatERing.getInstance().getEventManager().getEventInfo();
            if (events.isEmpty()) {
                System.err.println("Nessun evento disponibile nel DB. Interrompo.");
                return;
            }
            // Prendiamo ad esempio il primo Event
            EventInfo selectedEvent = events.get(0);
            System.out.println("Selected Event: " + selectedEvent);

            // 3) Carichiamo i servizi di questo Event
            ArrayList<ServiceInfo> services = selectedEvent.getServices();
            if (services.isEmpty()) {
                System.err.println("L'evento con ID=" + selectedEvent.getId() + " non ha servizi collegati.");
                return;
            }
            // Prendiamo il primo Service
            ServiceInfo selectedService = services.get(0);
            System.out.println("Selected Service: " + selectedService);

            // 4) Generiamo un nuovo SummarySheet
            SummarySheet newSheet = CatERing.getInstance().getTaskMgr().generateSummarySheet(selectedService, selectedEvent);
            System.out.println("Creato nuovo SummarySheet con ID=" + newSheet);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

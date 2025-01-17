package catering;

import catering.businesslogic.CatERing;
import catering.businesslogic.UseCaseLogicException;
import catering.businesslogic.event.ServiceInfo;
import catering.businesslogic.kitchen.SummarySheet;
import catering.businesslogic.recipe.Recipe;

import java.util.ArrayList;

public class TestKitchen1 {
    public static void main(String[] args) {
        try {
            // Fake login
            CatERing.getInstance().getUserManager().fakeLogin("Lidia");
            System.out.println(CatERing.getInstance().getUserManager().getCurrentUser());

            // Retrieve services
            ArrayList<ServiceInfo> services = CatERing.getInstance().getEventManager().getAllServices();
            ServiceInfo selectedService = services.get(0);

            // Create a new summary sheet
            SummarySheet sheet = CatERing.getInstance().getTaskMgr().generateSummarySheet(selectedService, selectedService.getReferredEvent());
            System.out.println("Summary sheet created for service: " + selectedService.getName());

        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
}

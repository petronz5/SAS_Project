package catering.businesslogic.event;

import catering.businesslogic.UseCaseLogicException;

import java.util.ArrayList;

public class EventManager {
    public ArrayList<EventInfo> getEventInfo() {
        return EventInfo.loadAllEventInfo();
    }

    public ArrayList<ServiceInfo> getAllServices() {
        ArrayList<ServiceInfo> allServices = new ArrayList<>();
        ArrayList<EventInfo> events = getEventInfo();

        for (EventInfo event : events) {
            allServices.addAll(event.getServices());
        }

        return allServices;
    }
    public EventInfo getCurrentEvent() throws UseCaseLogicException {
        ArrayList<EventInfo> events = getEventInfo();
        if (events.isEmpty()) {
            throw new UseCaseLogicException();
        }
        return events.get(0);  // Seleziona il primo evento (oppure gestisci meglio la selezione)
    }

}

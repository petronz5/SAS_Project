package catering.businesslogic.event;

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
}

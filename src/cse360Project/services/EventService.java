package cse360Project.services;

import java.util.ArrayList;
import java.util.List;

public class EventService {
    private static EventService instance;
    private final List<Runnable> groupUpdateListeners = new ArrayList<>();
    
    private EventService() {}
    
    public static EventService getInstance() {
        if (instance == null) {
            instance = new EventService();
        }
        return instance;
    }
    
    public void addGroupUpdateListener(Runnable listener) {
        groupUpdateListeners.add(listener);
    }
    
    public void removeGroupUpdateListener(Runnable listener) {
        groupUpdateListeners.remove(listener);
    }

    public void removeAllGroupUpdateListeners() {
        groupUpdateListeners.clear();
    }
    
    public void notifyGroupUpdate() {
        for (Runnable listener : groupUpdateListeners) {
            listener.run();
        }
    }
} 
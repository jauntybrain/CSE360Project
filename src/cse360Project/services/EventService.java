package cse360Project.services;

import java.util.ArrayList;
import java.util.List;

/*******
 * <p>
 * EventService Class.
 * </p>
 * 
 * <p>
 * Description: Manages event listeners and notifies them of changes.
 * </p>
 * 
 * <p>
 * Copyright: CSE 360 Team Th02 © 2024
 * </p>
 * 
 * @version 1.00 2025-11-20 Phase three
 * 
 */
public class EventService {
    private static EventService instance;

    private final List<Runnable> helpArticlesPageListeners = new ArrayList<>();
    private final List<Runnable> articleGroupsPageListeners = new ArrayList<>();

    /**
     * Initializes the EventService.
     */
    private EventService() {
    }

    /**
     * Gets the instance of the EventService.
     * 
     * @return the EventService instance.
     */
    public static EventService getInstance() {
        if (instance == null) {
            instance = new EventService();
        }
        return instance;
    }

    /**
     * Adds a listener to the help articles page.
     * 
     * @param listener the listener to add.
     */
    public void addHelpArticlesPageListener(Runnable listener) {
        helpArticlesPageListeners.add(listener);
    }

    /**
     * Removes a listener from the help articles page.
     * 
     * @param listener the listener to remove.
     */
    public void removeHelpArticlesPageListener(Runnable listener) {
        helpArticlesPageListeners.remove(listener);
    }

    /**
     * Removes all listeners from the help articles page.
     */
    public void removeAllHelpArticlesPageListeners() {
        helpArticlesPageListeners.clear();
    }

    /**
     * Notifies all listeners of the help articles page.
     */
    public void notifyHelpArticlesPage() {
        for (Runnable listener : helpArticlesPageListeners) {
            listener.run();
        }
    }

    /**
     * Adds a listener to the article groups page.
     * 
     * @param listener the listener to add.
     */
    public void addArticleGroupsPageListener(Runnable listener) {
        articleGroupsPageListeners.add(listener);
    }

    /**
     * Removes a listener from the article groups page.
     * 
     * @param listener the listener to remove.
     */
    public void removeArticleGroupsPageListener(Runnable listener) {
        articleGroupsPageListeners.remove(listener);
    }

    /**
     * Removes all listeners from the article groups page.
     */
    public void removeAllArticleGroupsPageListeners() {
        articleGroupsPageListeners.clear();
    }

    /**
     * Notifies all listeners of the article groups page.
     */
    public void notifyArticleGroupsPage() {
        for (Runnable listener : articleGroupsPageListeners) {
            listener.run();
        }
    }
}
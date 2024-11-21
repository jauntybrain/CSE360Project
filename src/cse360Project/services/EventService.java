package cse360Project.services;

import java.util.ArrayList;
import java.util.List;

public class EventService {
    private static EventService instance;
    private final List<Runnable> helpArticlesPageListeners = new ArrayList<>();
    private final List<Runnable> articleGroupsPageListeners = new ArrayList<>();

    private EventService() {
    }

    public static EventService getInstance() {
        if (instance == null) {
            instance = new EventService();
        }
        return instance;
    }

    public void addHelpArticlesPageListener(Runnable listener) {
        helpArticlesPageListeners.add(listener);
    }

    public void removeHelpArticlesPageListener(Runnable listener) {
        helpArticlesPageListeners.remove(listener);
    }

    public void removeAllHelpArticlesPageListeners() {
        helpArticlesPageListeners.clear();
    }

    public void notifyHelpArticlesPage() {
        for (Runnable listener : helpArticlesPageListeners) {
            listener.run();
        }
    }

    public void addArticleGroupsPageListener(Runnable listener) {
        articleGroupsPageListeners.add(listener);
    }

    public void removeArticleGroupsPageListener(Runnable listener) {
        articleGroupsPageListeners.remove(listener);
    }

    public void removeAllArticleGroupsPageListeners() {
        articleGroupsPageListeners.clear();
    }

    public void notifyArticleGroupsPage() {
        for (Runnable listener : articleGroupsPageListeners) {
            listener.run();
        }
    }
}
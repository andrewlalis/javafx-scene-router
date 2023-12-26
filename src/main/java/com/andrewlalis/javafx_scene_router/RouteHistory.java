package com.andrewlalis.javafx_scene_router;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * A component that tracks navigation history through a series of routes, and
 * provides facilities for navigating forward and backward through the history,
 * as well as pushing new routes into the history.
 * <p>
 *     This history is designed to work much like a typical web browser, where
 *     a linear history is maintained, such that you can move backward and
 *     forward along the history, but once you navigate to a new page, any
 *     forward-history is cleared.
 * </p>
 */
public class RouteHistory {
    private final List<RouteHistoryItem> items = new ArrayList<>();
    private int currentItemIndex = -1;

    /**
     * Constructs a new history instance.
     */
    public RouteHistory() {}

    /**
     * Pushes a new route after the current place in the history, and clears
     * all forward-routes beyond that.
     * @param route The route to push.
     * @param context The context object associated with the route.
     */
    public void push(String route, Object context) {
        int nextIndex = currentItemIndex + 1;
        items.subList(nextIndex, items.size()).clear();
        items.add(nextIndex, new RouteHistoryItem(route, context));
        currentItemIndex = nextIndex;
    }

    /**
     * Gets the current context object, or null if none is set.
     * @return The context object associated with the current route.
     * @param <T> The type to implicitly cast to. Note that this may result in
     *           an unchecked exception if you attempt to coerce to an invalid
     *           type.
     */
    @SuppressWarnings("unchecked")
    public <T> T getCurrentContext() {
        if (currentItemIndex >= 0 && currentItemIndex < items.size()) {
            return (T) items.get(currentItemIndex).context();
        }
        return null;
    }

    /**
     * Checks if it's possible to navigate back in the history.
     * @return True if it is possible to go back.
     */
    public boolean canGoBack() {
        return currentItemIndex > 0;
    }

    /**
     * Attempts to go back in the history.
     * @return If successful, the previous history item that we went back to;
     * empty otherwise.
     */
    public Optional<RouteHistoryItem> back() {
        if (canGoBack()) {
            RouteHistoryItem prev = items.get(currentItemIndex - 1);
            currentItemIndex--;
            return Optional.of(prev);
        }
        return Optional.empty();
    }

    /**
     * Checks if it's possible to navigate forward in the history.
     * @return True if it is possible to go forward.
     */
    public boolean canGoForward() {
        return currentItemIndex + 1 < items.size();
    }

    /**
     * Attempts to go forward in the history.
     * @return If successful, the next history item that we went forward to;
     * empty otherwise.
     */
    public Optional<RouteHistoryItem> forward() {
        if (canGoForward()) {
            RouteHistoryItem next = items.get(currentItemIndex + 1);
            currentItemIndex++;
            return Optional.of(next);
        }
        return Optional.empty();
    }

    /**
     * Clears the history completely.
     */
    public void clear() {
        items.clear();
        currentItemIndex = -1;
    }

    /**
     * Clears any history ahead of the current route item, such that the user
     * cannot navigate forward.
     */
    public void clearForward() {
        if (currentItemIndex + 1 < items.size()) {
            items.subList(currentItemIndex + 1, items.size()).clear();
        }
    }

    /**
     * Gets a list of "breadcrumbs", or a representation of the current history
     * and indication of where we are in that history.
     * @return The list of breadcrumbs.
     */
    public List<BreadCrumb> getBreadCrumbs() {
        if (items.isEmpty()) return Collections.emptyList();
        List<BreadCrumb> breadCrumbs = new ArrayList<>(items.size());
        for (int i = 0; i < items.size(); i++) {
            var item = items.get(i);
            breadCrumbs.add(new BreadCrumb(
                    item.route(),
                    item.route(),
                    item.context(),
                    i == currentItemIndex
            ));
        }
        return breadCrumbs;
    }

    /**
     * Gets an unmodifiable view of the items in this history.
     * @return This history's items.
     */
    public List<RouteHistoryItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    /**
     * Gets the current item index.
     * @return The current item index.
     */
    public int getCurrentItemIndex() {
        return currentItemIndex;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("RouteHistory:\n");
        for (int i = 0; i < items.size(); i++) {
            var item = items.get(i);
            sb.append(String.format("%4d route = \"%s\", context = %s", i, item.route(), item.context()));
            if (i == currentItemIndex) {
                sb.append(" <--- Current Item");
            }
            if (i + 1 < items.size()) {
                sb.append(String.format("%n"));
            }
        }
        return sb.toString();
    }
}

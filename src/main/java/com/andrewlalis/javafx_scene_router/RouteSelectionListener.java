package com.andrewlalis.javafx_scene_router;

/**
 * A listener that's notified when the router it's attached to navigates to
 * a specific, pre-defined route. Usually used to do something once the user
 * has navigated to a route.
 */
public interface RouteSelectionListener {
    /**
     * Called when a specific, pre-defined route is selected.
     * @param context The context that was provided when the user navigated to
     *                the route. This may be null.
     */
    void onRouteSelected(Object context);
}

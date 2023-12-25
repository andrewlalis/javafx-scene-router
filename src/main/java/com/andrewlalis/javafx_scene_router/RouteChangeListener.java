package com.andrewlalis.javafx_scene_router;

/**
 * A listener that's invoked when a router's route is updated.
 */
public interface RouteChangeListener {
    /**
     * Called when a router's route changes.
     * @param route The route that was navigated to.
     * @param context The context at the new route.
     * @param oldRoute The previous route.
     * @param oldContext The context at the previous route.
     */
    void routeChanged(String route, Object context, String oldRoute, Object oldContext);
}

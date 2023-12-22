package com.andrewlalis.javafx_scene_router;

/**
 * An entry that stores information about a point in a user's route history.
 * @param route The route.
 * @param context The context object associated with the route.
 */
public record RouteHistoryItem(String route, Object context) {}

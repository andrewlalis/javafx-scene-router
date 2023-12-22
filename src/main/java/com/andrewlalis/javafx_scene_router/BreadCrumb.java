package com.andrewlalis.javafx_scene_router;

/**
 * A breadcrumb entry that represents one item in a route history.
 * @param label The display label.
 * @param route The route.
 * @param context The context object for this route.
 * @param current Whether the history this was generated from is at this route right now.
 */
public record BreadCrumb(String label, String route, Object context, boolean current) {}

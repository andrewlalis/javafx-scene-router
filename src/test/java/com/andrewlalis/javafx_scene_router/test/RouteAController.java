package com.andrewlalis.javafx_scene_router.test;

import com.andrewlalis.javafx_scene_router.RouteSelectionListener;

public class RouteAController implements RouteSelectionListener {
    public int routeSelectedCount = 0;

    @Override
    public void onRouteSelected(Object context) {
        routeSelectedCount++;
        System.out.println("Route A selected.");
    }
}

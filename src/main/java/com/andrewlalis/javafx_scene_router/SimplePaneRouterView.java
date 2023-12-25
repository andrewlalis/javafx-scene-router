package com.andrewlalis.javafx_scene_router;

import javafx.scene.Parent;
import javafx.scene.layout.Pane;

/**
 * A simple router view implementation that uses a JavaFX Pane, which shows
 * the route contents in their preferred minimal size.
 */
public class SimplePaneRouterView implements RouterView {
    private final Pane pane = new Pane();

    /**
     * Shows the node in the pane.
     * @param node The node to show.
     */
    @Override
    public void showRouteNode(Parent node) {
        pane.getChildren().setAll(node);
    }

    /**
     * Gets the pane that's used internally to show the contents.
     * @return The pane.
     */
    public Pane getPane() {
        return pane;
    }
}

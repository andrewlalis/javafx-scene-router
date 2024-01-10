package com.andrewlalis.javafx_scene_router;

import javafx.scene.Parent;
import javafx.scene.layout.Pane;

/**
 * A simple implementation of {@link RouterView} which simply keeps a reference
 * to a {@link Pane} and displays any route nodes inside that pane by setting
 * its only child to the given route node.
 * @param <T> The pane type.
 */
public class SimpleRouterView<T extends Pane> implements RouterView {
    /**
     * The pane in which route nodes are displayed.
     */
    private final T pane;

    /**
     * Constructs this router view to use the given pane.
     * @param pane The pane to use.
     */
    public SimpleRouterView(T pane) {
        this.pane = pane;
    }

    @Override
    public void showRouteNode(Parent node) {
        this.pane.getChildren().clear();
        this.pane.getChildren().add(node);
    }

    /**
     * Gets the pane used by this router view.
     * @return The pane used by this router view.
     */
    public T getPane() {
        return this.pane;
    }
}

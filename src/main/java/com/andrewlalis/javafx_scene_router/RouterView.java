package com.andrewlalis.javafx_scene_router;

import javafx.scene.Parent;

/**
 * An interface through which a router can show a route's node in the application.
 * <p>
 *     Usually, this will be implemented by adding the node to some sort of
 *     container, like a Pane or Stage.
 * </p>
 */
public interface RouterView {
    /**
     * Shows the node as the current one, discarding any previous route content.
     * @param node The node to show.
     */
    void showRouteNode(Parent node);
}

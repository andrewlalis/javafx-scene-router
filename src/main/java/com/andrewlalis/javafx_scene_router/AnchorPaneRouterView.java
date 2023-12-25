package com.andrewlalis.javafx_scene_router;

import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

/**
 * A router view implementation using the JavaFX {@link AnchorPane}, so that
 * route contents can (optionally) be grown to fill all available space.
 */
public class AnchorPaneRouterView implements RouterView {
    private final AnchorPane anchorPane = new AnchorPane();
    private final boolean expandContents;

    /**
     * Constructs this router view with an explicit setting for whether to
     * expand contents of the view.
     * @param expandContents Whether to expand the contents of this view to
     *                       fill all available space.
     */
    public AnchorPaneRouterView(boolean expandContents) {
        this.expandContents = expandContents;
    }

    /**
     * Constructs this router view with the default behavior of allowing route
     * contents to fill all available space.
     */
    public AnchorPaneRouterView() {
        this(true);
    }

    /**
     * Shows the node in this view's anchor pane. If this view has been set to
     * expand contents, then the given node will be anchored to all 4 sides of
     * the anchor pane.
     * @param node The node to show.
     */
    @Override
    public void showRouteNode(Parent node) {
        if (expandContents) {
            AnchorPane.setTopAnchor(node, 0.0);
            AnchorPane.setRightAnchor(node, 0.0);
            AnchorPane.setBottomAnchor(node, 0.0);
            AnchorPane.setLeftAnchor(node, 0.0);
        }
        anchorPane.getChildren().setAll(node);
    }

    /**
     * Gets the underlying pane used for rendering.
     * @return The anchor pane this view uses.
     */
    public AnchorPane getAnchorPane() {
        return anchorPane;
    }
}

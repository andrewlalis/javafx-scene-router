package com.andrewlalis.javafx_scene_router;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A router that shows different content in a pane depending on which route is
 * selected.
 * <p>
 *     The router has a mapping of "routes" (think, Strings) to JavaFX Parent
 *     nodes. When a route is selected, the router will lookup the mapped node,
 *     and put that node into the pre-defined pane or consumer function.
 * </p>
 * <p>
 *     The router maintains a {@link RouteHistory} so that it's possible to
 *     navigate backward and forward, much like a web browser would.
 * </p>
 * <p>
 *     Note that this router is intended to be used by a single JavaFX
 *     application, and is <strong>not threadsafe!</strong> Use a separate
 *     router for each separate JavaFX application you create.
 * </p>
 */
public class SceneRouter {
    private final Pane viewPane = new Pane();
    private final Map<String, Parent> routeMap = new HashMap<>();
    private final RouteHistory history = new RouteHistory();

    /**
     * Constructs the router.
     */
    public SceneRouter() {}

    /**
     * Maps the given route to a node, so that when the route is selected, the
     * given node is shown.
     * @param route The route.
     * @param node The node to show.
     * @return This router.
     */
    public SceneRouter map(String route, Parent node) {
        routeMap.put(route, node);
        return this;
    }

    /**
     * Maps the given route to a node that is loaded from a given FXML resource.
     * @param route The route.
     * @param fxml The FXML classpath resource to load from.
     * @param controllerCustomizer A function that takes controller instance
     *                             from the loaded FXML and customizes it. This
     *                             may be null.
     * @return This router.
     */
    public SceneRouter map(String route, URL fxml, Consumer<?> controllerCustomizer) {
        return map(route, loadNode(fxml, controllerCustomizer));
    }

    /**
     * Maps the given route to a node that is loaded from a given FXML resource.
     * @param route The route.
     * @param fxml The FXML classpath resource to load from.
     * @return This router.
     */
    public SceneRouter map(String route, URL fxml) {
        return map(route, fxml, null);
    }

    /**
     * Navigates to a given route, with a given context object.
     * @param route The route to navigate to.
     * @param context The context that should be available at that route.
     */
    public void navigate(String route, Object context) {
        Platform.runLater(() -> {
            history.push(route, context);
            setCurrentNode(getMappedNode(route));
        });
    }

    /**
     * Navigates to a given route, without any context.
     * @param route The route to navigate to.
     */
    public void navigate(String route) {
        navigate(route, null);
    }

    /**
     * Attempts to navigate back.
     */
    public void navigateBack() {
        Platform.runLater(() -> history.back()
                .ifPresent(prev -> setCurrentNode(getMappedNode(prev.route())))
        );
    }

    /**
     * Attempts to navigate forward.
     */
    public void navigateForward() {
        Platform.runLater(() -> history.forward()
                .ifPresent(next -> setCurrentNode(getMappedNode(next.route())))
        );
    }

    /**
     * Gets the context object for the current route.
     * @return The context object, or null.
     * @param <T> The type of the object.
     */
    public <T> T getContext() {
        return history.getCurrentContext();
    }

    /**
     * Gets the internal history representation of this router.
     * @return The history used by this router.
     */
    public RouteHistory getHistory() {
        return history;
    }

    /**
     * Gets the view pane that this router renders to. Take this and put it
     * somewhere in your view's hierarchy to show the router's content.
     * @return The router's view pane.
     */
    public Pane getViewPane() {
        return viewPane;
    }

    private Parent getMappedNode(String route) {
        Parent node = routeMap.get(route);
        if (node == null) throw new IllegalArgumentException("Route " + route + " is not mapped to any node.");
        return node;
    }

    private void setCurrentNode(Parent node) {
        viewPane.getChildren().setAll(node);
    }

    private <T> Parent loadNode(URL resource, Consumer<T> controllerCustomizer) {
        FXMLLoader loader = new FXMLLoader(resource);
        try {
            Parent p = loader.load();
            if (controllerCustomizer != null) {
                T controller = loader.getController();
                if (controller == null) throw new IllegalStateException("No controller found when loading " + resource.toString());
                controllerCustomizer.accept(controller);
            }
            return p;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

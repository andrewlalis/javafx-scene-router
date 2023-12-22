package com.andrewlalis.javafx_scene_router;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A router that shows different content in a pane depending on which route is
 * selected. Each router must be initialized with a JavaFX pane, or a consumer
 * function that's called to set the content each time a new route is selected.
 * <p>
 *     The router has a mapping of "routes" (think, Strings) to JavaFX Parent
 *     nodes. When a route is selected, the router will lookup the mapped node,
 *     and put that node into the pre-defined pane or consumer function.
 * </p>
 * <p>
 *     The router maintains a {@link RouteHistory} so that it's possible to
 *     navigate backward and forward, much like a web browser would.
 * </p>
 */
public class SceneRouter {
    private final Consumer<Parent> setter;
    private final Map<String, Parent> routeMap = new HashMap<>();
    private final RouteHistory history = new RouteHistory();

    /**
     * Constructs the router to show route content in the given pane.
     * @param pane The pane to show route content in.
     */
    public SceneRouter(Pane pane) {
        this(p -> pane.getChildren().setAll(p));
    }

    /**
     * Constructs the router to supply route content to the given consumer, so
     * that it may place the content somewhere. For example, you might like to
     * use this if you'd like to have a router place content in the center of a
     * {@link javafx.scene.layout.BorderPane}, like so:
     * <p><code>var router = new SceneRouter(myBorderPane::setCenter);</code></p>
     * @param setter The consumer that is supplied route content to show.
     */
    public SceneRouter(Consumer<Parent> setter) {
        this.setter = setter;
    }

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
    public SceneRouter map(String route, String fxml, Consumer<?> controllerCustomizer) {
        return map(route, loadNode(fxml, controllerCustomizer));
    }

    /**
     * Maps the given route to a node that is loaded from a given FXML resource.
     * @param route The route.
     * @param fxml The FXML classpath resource to load from.
     * @return This router.
     */
    public SceneRouter map(String route, String fxml) {
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
            setter.accept(getMappedNode(route));
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
                .ifPresent(prev -> setter.accept(getMappedNode(prev.route())))
        );
    }

    /**
     * Attempts to navigate forward.
     */
    public void navigateForward() {
        Platform.runLater(() -> history.forward()
                .ifPresent(next -> setter.accept(getMappedNode(next.route())))
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

    private Parent getMappedNode(String route) {
        Parent node = routeMap.get(route);
        if (node == null) throw new IllegalArgumentException("Route " + route + " is not mapped to any node.");
        return node;
    }

    private <T> Parent loadNode(String fxml, Consumer<T> controllerCustomizer) {
        FXMLLoader loader = new FXMLLoader(SceneRouter.class.getResource(fxml));
        try {
            Parent p = loader.load();
            if (controllerCustomizer != null) {
                T controller = loader.getController();
                if (controller == null) throw new IllegalStateException("No controller found when loading " + fxml);
                controllerCustomizer.accept(controller);
            }
            return p;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

package com.andrewlalis.javafx_scene_router;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.*;
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
    public interface RouteChangeListener {
        void routeChanged(String route, Object context, String oldRoute, Object oldContext);
    }

    private final Pane viewPane = new Pane();
    private final Map<String, Parent> routeMap = new HashMap<>();
    private final RouteHistory history = new RouteHistory();
    private final ObservableList<BreadCrumb> breadCrumbs = FXCollections.observableArrayList();
    private final StringProperty currentRouteProperty = new SimpleStringProperty(null);

    private final List<RouteChangeListener> routeChangeListeners = new ArrayList<>();
    private final Map<String, List<RouteSelectionListener>> routeSelectionListeners = new HashMap<>();

    /**
     * Constructs the router.
     */
    public SceneRouter() {}

    /**
     * Maps the given route to a node, so that when the route is selected, the
     * given node is shown.
     * <p>
     *     Note that by supplying a pre-loaded JavaFX node, the SceneRouter is
     *     no longer able to check if the node's controller implements
     *     {@link RouteSelectionListener}, and so you'll need to register the
     *     controller manually with {@link #addRouteSelectionListener(String, RouteSelectionListener)}
     *     in order to have the controller be notified when its contents are
     *     shown.
     * </p>
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
        return map(route, loadNode(route, fxml, controllerCustomizer));
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
        String oldRoute = currentRouteProperty.get();
        Object oldContext = history.getCurrentContext();
        Platform.runLater(() -> {
            history.push(route, context);
            setCurrentNode(route, oldRoute, oldContext);
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
        String oldRoute = currentRouteProperty.get();
        Object oldContext = history.getCurrentContext();
        Platform.runLater(() -> history.back().ifPresent(prev -> setCurrentNode(prev.route(), oldRoute, oldContext)));
    }

    /**
     * Attempts to navigate forward.
     */
    public void navigateForward() {
        String oldRoute = currentRouteProperty.get();
        Object oldContext = history.getCurrentContext();
        Platform.runLater(() -> history.forward().ifPresent(next -> setCurrentNode(next.route(), oldRoute, oldContext)));
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

    /**
     * Gets a property that refers to the router's current route.
     * @return The route property.
     */
    public StringProperty currentRouteProperty() {
        return currentRouteProperty;
    }

    /**
     * Gets an observable list of {@link BreadCrumb} that is updated each time
     * the router's navigation history is updated.
     * @return The list of breadcrumbs.
     */
    public ObservableList<BreadCrumb> getBreadCrumbs() {
        return breadCrumbs;
    }

    /**
     * Adds a listener that will be notified each time the current route changes.
     * @param listener The listener that will be notified.
     */
    public void addRouteChangeListener(RouteChangeListener listener) {
        routeChangeListeners.add(listener);
    }

    /**
     * Adds a listener that will be notified when the route changes to a
     * specified route.
     * @param route The route to listen for.
     * @param listener The listener to use.
     */
    public void addRouteSelectionListener(String route, RouteSelectionListener listener) {
        List<RouteSelectionListener> listenerList = routeSelectionListeners.computeIfAbsent(route, s -> new ArrayList<>());
        listenerList.add(listener);
    }

    private Parent getMappedNode(String route) {
        Parent node = routeMap.get(route);
        if (node == null) throw new IllegalArgumentException("Route " + route + " is not mapped to any node.");
        return node;
    }

    /**
     * Internal method to actually set this router's view pane to a particular
     * node. This is called any time a route changes.
     * @param route The route to go to.
     * @param oldRoute The previous route that the router was at.
     * @param oldContext The context of the previous route.
     */
    private void setCurrentNode(String route, String oldRoute, Object oldContext) {
        viewPane.getChildren().setAll(getMappedNode(route));
        breadCrumbs.setAll(history.getBreadCrumbs());
        currentRouteProperty.set(route);
        for (var listener : routeChangeListeners) {
            listener.routeChanged(route, getContext(), oldRoute, oldContext);
        }
        for (var listener : routeSelectionListeners.getOrDefault(route, Collections.emptyList())) {
            listener.onRouteSelected(getContext());
        }
    }

    private <T> Parent loadNode(String route, URL resource, Consumer<T> controllerCustomizer) {
        FXMLLoader loader = new FXMLLoader(resource);
        try {
            Parent p = loader.load();
            T controller = loader.getController();
            if (controller instanceof RouteSelectionListener rsl) {
                addRouteSelectionListener(route, rsl);
            }
            if (controllerCustomizer != null) {
                if (controller == null) throw new IllegalStateException("No controller found when loading " + resource.toString());
                controllerCustomizer.accept(controller);
            }
            return p;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

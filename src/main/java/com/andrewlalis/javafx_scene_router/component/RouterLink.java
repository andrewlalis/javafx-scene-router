package com.andrewlalis.javafx_scene_router.component;

import com.andrewlalis.javafx_scene_router.SceneRouter;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;

/**
 * A hyperlink that, when clicked, navigates the user to a specified route.
 */
public class RouterLink extends Hyperlink {
    @FXML
    private final StringProperty routeProperty = new SimpleStringProperty(this, "route", null);
    @FXML
    private final ObjectProperty<SceneRouter> routerProperty = new SimpleObjectProperty<>(this, "router", null);

    private Object context;

    // Route property.
    public StringProperty routeProperty() {
        return routeProperty;
    }

    public final void setRoute(String route) {
        this.context = null;
        routeProperty.set(route);
        setOnAction(event -> {
            SceneRouter router = getRouter();
            String currentRoute = getRoute();
            if (router != null && currentRoute != null) {
                router.navigate(currentRoute, context);
            }
        });
    }

    public final void setRoute(String route, Object context) {
        this.context = context;
        setRoute(route);
    }

    public final String getRoute() {
        return routeProperty.get();
    }

    // Router property.
    public ObjectProperty<SceneRouter> routerProperty() {
        return routerProperty;
    }

    public final void setRouter(SceneRouter router) {
        routerProperty.set(router);
    }

    public final SceneRouter getRouter() {
        return routerProperty.get();
    }
}

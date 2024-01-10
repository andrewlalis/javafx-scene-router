/**
 * The JavaFX-Scene-Router module. Require this to use the library.
 */
module com.andrewlalis.javafx_scene_router {
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.controls;

    exports com.andrewlalis.javafx_scene_router;
    exports com.andrewlalis.javafx_scene_router.component;
    opens com.andrewlalis.javafx_scene_router.component to javafx.fxml;
}

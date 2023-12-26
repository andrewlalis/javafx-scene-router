module com.andrewlalis.javafx_scene_router.test {
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.controls;

    requires com.andrewlalis.javafx_scene_router;

    requires org.junit.jupiter.api;

    exports com.andrewlalis.javafx_scene_router.test to javafx.fxml, org.junit.platform.commons;
}
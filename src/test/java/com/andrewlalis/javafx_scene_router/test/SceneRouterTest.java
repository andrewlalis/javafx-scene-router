package com.andrewlalis.javafx_scene_router.test;

import com.andrewlalis.javafx_scene_router.AnchorPaneRouterView;
import com.andrewlalis.javafx_scene_router.SceneRouter;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

public class SceneRouterTest {
    @Test
    public void testNavigate() {
        var router = getSampleRouter();
        // Make some assertions prior to navigation.
        assertTrue(router.getHistory().getItems().isEmpty());
        assertFalse(router.navigateBack().join());
        assertFalse(router.navigateForward().join());
        assertNull(router.currentRouteProperty().get());

        // Test some basic navigation.
        var contextA = "CONTEXT";
        router.navigate("A", contextA).join();
        assertEquals(1, router.getHistory().getItems().size());
        assertEquals("A", router.currentRouteProperty().get());
        assertEquals(contextA, router.getContext());
        assertEquals(1, router.getBreadCrumbs().size());
        assertTrue(router.getBreadCrumbs().getFirst().current());

        router.navigate("B").join();
        assertEquals(2, router.getHistory().getItems().size());
        assertEquals("B", router.currentRouteProperty().get());
        assertNull(router.getContext());
        assertEquals(2, router.getBreadCrumbs().size());
        assertTrue(router.getBreadCrumbs().getLast().current());
        assertFalse(router.getBreadCrumbs().getFirst().current());

        // Test that navigating back and forward works.
        assertTrue(router.navigateBack().join());
        assertEquals("A", router.currentRouteProperty().get());
        assertEquals(contextA, router.getContext());

        assertTrue(router.navigateForward().join());
        assertEquals("B", router.currentRouteProperty().get());
        assertNull(router.getContext());
        assertFalse(router.navigateForward().join());

        // Test that navigateBackAndClear works.
        assertTrue(router.navigateBackAndClear().join());
        assertEquals("A", router.currentRouteProperty().get());
        assertEquals(1, router.getHistory().getItems().size());
    }

    private SceneRouter getSampleRouter() {
        CompletableFuture<SceneRouter> future = new CompletableFuture<>();
        Platform.startup(() -> {
            SceneRouter router = new SceneRouter(new AnchorPaneRouterView());
            router.map("A", SceneRouterTest.class.getResource("/routeA.fxml"));
            router.map("B", new BorderPane(new Label("Hello from route B")));
            router.map("C", new HBox(new Label("Hello from route C")));
            future.complete(router);
        });
        return future.join();
    }
}

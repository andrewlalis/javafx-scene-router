package com.andrewlalis.javafx_scene_router.test;

import com.andrewlalis.javafx_scene_router.RouteHistory;
import com.andrewlalis.javafx_scene_router.RouteHistoryItem;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RouteHistoryTest {
    @Test
    public void testPush() {
        var history = new RouteHistory();
        assertTrue(history.getItems().isEmpty());
        assertEquals(-1, history.getCurrentItemIndex());
        history.push("test", "Hello");
        assertEquals(history.getItems().size(), 1);
        assertEquals(0, history.getCurrentItemIndex());
        assertEquals("Hello", history.getCurrentContext());
    }

    @Test
    public void testGetCurrentContext() {
        var history = new RouteHistory();
        assertNull(history.getCurrentContext());
        history.push("test", 5);
        assertEquals(Integer.valueOf(5), history.getCurrentContext());
        history.push("test2", null);
        assertNull(history.getCurrentContext());
        history.back();
        assertEquals(Integer.valueOf(5), history.getCurrentContext());
    }

    @Test
    public void testBack() {
        var history = new RouteHistory();
        assertFalse(history.canGoBack());
        assertTrue(history.back().isEmpty());
        history.push("a", "a");
        assertFalse(history.canGoBack());
        history.push("b", "b");
        assertTrue(history.canGoBack());
        var prev = history.back();
        assertTrue(prev.isPresent());
        Assertions.assertEquals(new RouteHistoryItem("a", "a"), prev.get());
        assertFalse(history.canGoBack());
        assertTrue(history.back().isEmpty());
    }

    @Test
    public void testForward() {
        var history = new RouteHistory();
        assertFalse(history.canGoForward());
        assertTrue(history.forward().isEmpty());
        history.push("a", "a");
        history.push("b", "b");
        history.push("c", "c");
        assertFalse(history.canGoForward());
        assertTrue(history.forward().isEmpty());
        history.back();
        assertTrue(history.canGoForward());
        var next = history.forward();
        assertTrue(next.isPresent());
        assertEquals(new RouteHistoryItem("c", "c"), next.get());
    }

    @Test
    public void testClear() {
        var history = new RouteHistory();
        history.push("a", "a");
        history.clear();
        assertFalse(history.canGoBack());
        assertFalse(history.canGoForward());
        assertNull(history.getCurrentContext());
        assertTrue(history.getItems().isEmpty());
        assertEquals(-1, history.getCurrentItemIndex());
    }

    @Test
    public void testClearForward() {
        var history = new RouteHistory();
        history.push("a", "a");
        history.push("b", "b");
        history.push("c", "c");
        assertEquals(3, history.getItems().size());
        assertEquals(2, history.getCurrentItemIndex());
        history.back();
        assertEquals(3, history.getItems().size());
        assertEquals(1, history.getCurrentItemIndex());
        history.clearForward();
        assertEquals(1, history.getCurrentItemIndex());
        assertEquals(2, history.getItems().size());
    }

    @Test
    public void testGetBreadCrumbs() {
        var h1 = new RouteHistory();
        var b1 = h1.getBreadCrumbs();
        assertTrue(b1.isEmpty());

        var h2 = new RouteHistory();
        h2.push("a", "a");
        var b2 = h2.getBreadCrumbs();
        assertEquals(b2.size(), 1);
        assertEquals("a", b2.get(0).route());
        assertTrue(b2.get(0).current());
    }
}

# JavaFX Scene Router

A router implementation for navigating between "pages" in your JavaFX
application!

On the web, we tend to take for granted the fact that you can click on a link,
go to a new page, then go back, go forward again, click on something else, and
it all works seamlessly to route you through the internet. In desktop apps,
that's less common, since most apps are simple enough to be built with a single
component tree.

However, sometimes you'll want a web-like experience with your desktop app, and
for that purpose, I've created javafx-scene-router. It allows you to initialize
a router that controls the content of a Pane or similar, and depending on what
route is selected, different content will be shown in that pane.

## Usage

Add the following dependency to your `pom.xml`:
```xml
<dependency>
    <groupId>com.andrewlalis</groupId>
    <artifactId>javafx-scene-router</artifactId>
    <version>LATEST_VERSION</version>
</dependency>
```
> Replace `LATEST_VERSION` with the most recent version found on [maven central](https://central.sonatype.com/artifact/com.andrewlalis/javafx-scene-router).

Then, most often you'll create a singleton instance of `SceneRouter`, probably
in your app's main class like so:

```java
import com.andrewlalis.javafx_scene_router.SceneRouter;

public class MyJavaFXApp extends Application {
    public static final SceneRouter router = new SceneRouter();

    public static void main(String[] args) {
        // Setup the router's routes before starting the app:
        router.map("accounts", MyJavaFXApp.class.getResource("/accounts-view.fxml"));
        router.map("account",  MyJavaFXApp.class.getResource("/account.fxml"));
        router.map("settings", MyJavaFXApp.class.getResource("/settings.fxml"));
        
        launch(args);
    }
}
```

From here, it's just a matter of attaching the router's view to one of
your scene's nodes, and mapping string route names to other nodes or FXML
files.

For example, suppose my app has a main scene with a `MainController` class.
We'd hook up the router's view to that scene's center view, assuming you're
using an AnchorPaneRouterView for your router (the default choice).

```java
public class MainController {
    @FXML
    public BorderPane borderPane;

    @FXML
    public void initialize() {
        AnchorPaneRouterView view = (AnchorPaneRouterView) MyJavaFXApp.router.getView();
        borderPane.setCenter(view.getAnchorPane());
    }
}
```

Finally, we can use the router from anywhere in our app to control which "page"
is being displayed:

```java
public class MainController {
    // Rest of the class omitted.
    
    @FXML
    public void onBackButton() {
        MyJavaFXApp.router.navigateBack();
    }
    
    @FXML
    public void onAccountClicked() {
        Account acc = getClickedAccount();
        MyJavaFXApp.router.navigate("account", acc);
    }
}
```

## Reactivity

The SceneRouter has been designed to be used in reactive JavaFX projects, and
includes a few ways of doing this:

- The `currentRouteProperty` can be bound to, or have a listener attached, to
update each time the current route changes.
- You can `getBreadCrumbs()` to get an observable list of breadcrumbs that
changes each time the route's history changes.
- You can use `addRouteChangeListener` to add a listener that's notified each
time the route has changed, with additional context and the previous route.
- You can use `addRouteSelectionListener` to add a listener for a specific
route, that will be notified only when the router selects that route.
- You can make any of your controllers for route nodes implement `RouteSelectionListener`,
in which case they'll automatically be registered using `addRouteSelectionListener`.
Note that this **does not** apply to routes mapped using a pre-loaded node, as
in the `map(String route, Parent node)` method. Only methods which take a `URL`
to a resource work with this.

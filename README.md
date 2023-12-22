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

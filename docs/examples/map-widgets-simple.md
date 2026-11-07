# Map Widget Example

This example creates a custom focusable widget, adds three instances to a map display, and gives the player a map item that starts the display. Each widget draws its own icon, text, fill, and border. The focused widget uses different colors so focus is visible while navigating.

```java
public class LabeledWidget extends MapWidget {
    private final String label;
    private boolean active;

    public LabeledWidget(String label) {
        this.label = label;
        this.setFocusable(true);
    }

    @Override
    public void onActivate() {
        active = !active;
        invalidate();
    }

    @Override
    public void onDraw() {
        byte background = isFocused()
                ? MapColorPalette.getColor(80, 120, 40)
                : MapColorPalette.getColor(48, 48, 48);
        byte foreground = isFocused()
                ? MapColorPalette.COLOR_WHITE
                : MapColorPalette.getColor(190, 190, 190);
        byte border = isFocused()
                ? MapColorPalette.getColor(255, 230, 80)
                : MapColorPalette.getColor(120, 120, 120);

        view.fillRectangle(0, 0, getWidth(), getHeight(), background);
        view.drawRectangle(0, 0, getWidth(), getHeight(), border);

        // A small custom tag-shaped icon.
        view.fillRectangle(6, 8, 12, 12, foreground);
        view.fillRectangle(9, 11, 6, 6, background);
        view.drawLine(18, 8, 22, 12, foreground);
        view.drawLine(18, 19, 22, 15, foreground);

        view.draw(MapFont.MINECRAFT, 27, 11, foreground, label);
        byte stateColor = active
                ? MapColorPalette.getColor(100, 255, 100)
                : MapColorPalette.getColor(255, 100, 100);
        view.draw(MapFont.MINECRAFT, 82, 11, stateColor, active ? "YES" : "NO");
    }
}
```

`setFocusable(true)` makes the widget eligible for navigation and focus. When widgets are attached, the first eligible widget is focused automatically if none has focus; `isFocused()` then selects the highlighted palette colors. Pressing Space calls `onActivate()`, which toggles the widget's state and invalidates it so `onDraw()` updates the `YES`/`NO` indicator. This override intentionally does not call `super.onActivate()`: the default implementation changes the active navigation scope, which is not wanted for a button-like press.

The display positions three instances vertically and enables map steering input while the player holds the item:

```java
public class WidgetMenuDisplay extends MapDisplay {
    @Override
    public void onAttached() {
        setReceiveInputWhenHolding(true);

        addWidget(new LabeledWidget("First")
                .setBounds(6, 8, 116, 30));
        addWidget(new LabeledWidget("Second")
                .setBounds(6, 45, 116, 30));
        addWidget(new LabeledWidget("Third")
                .setBounds(6, 82, 116, 30));
    }
}
```

Create the map item and add it to the player's inventory, for example from a command handler:

```java
ItemStack item = MapDisplay.createMapItem(WidgetMenuDisplay.class);
player.getInventory().addItem(item);
```

Use A/D and W/S to move focus between the widgets. Press Space to toggle the focused widget's `YES`/`NO` state.

## Playtesting with a unit test

`MapDisplayDebugWindow` can open this display in a standalone window, tick it at 20 TPS, and forward keyboard controls. A simple test can create the display directly and keep the window open until it is closed:

```java
@Test
public void testWidgetMenu() {
    MapDisplayDebugWindow.showForeverAutoScale(new WidgetMenuDisplay());
}
```

The debug window is intended for playtesting simple widgets without starting a Minecraft client. It requires a graphical, non-headless test environment. Because the display is not attached by a plugin in this test, avoid plugin-instance texture loading such as `loadTexture()`; load widget resources by class instead:

```java
MapTexture icon = MapTexture.loadResource(LabeledWidget.class, "textures/icon.png");
```

For complex displays, keep plugin-specific logic outside widgets and provide the widget with only the data or behavior it needs. This helps keep widgets easy to exercise in the debug window.

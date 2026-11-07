# Simple Map Widgets

`MapWidget` is a reusable interactive component for a `MapDisplay`. Widgets form a tree, own a rectangular drawing area, and provide drawing, focus, activation, and input callbacks. The root manages layout updates and W/A/S/D navigation, so plugins can build map-based menus without implementing a separate input router.

## Key files

- [`MapWidget`](../src/main/java/com/bergerkiller/bukkit/common/map/widgets/MapWidget.java): widget bounds, children, lifecycle, drawing, focus, and input callbacks.
- [`MapWidgetRoot`](../src/main/java/com/bergerkiller/bukkit/common/map/widgets/MapWidgetRoot.java): root widget, focus state, and navigation lifecycle.
- [`MapWidgetNavigator`](../src/main/java/com/bergerkiller/bukkit/common/map/util/MapWidgetNavigator.java): directional focus selection among visible, enabled widgets.
- [`MapWidgetButton`](../src/main/java/com/bergerkiller/bukkit/common/map/widgets/MapWidgetButton.java), [`MapWidgetText`](../src/main/java/com/bergerkiller/bukkit/common/map/widgets/MapWidgetText.java), [`MapWidgetWindow`](../src/main/java/com/bergerkiller/bukkit/common/map/widgets/MapWidgetWindow.java), and [`MapWidgetTabView`](../src/main/java/com/bergerkiller/bukkit/common/map/widgets/MapWidgetTabView.java): examples of reusable controls and containers.
- [`MapPlayerInput`](../src/main/java/com/bergerkiller/bukkit/common/map/MapPlayerInput.java): logical key states and transitions.

## Adding widgets

Add a widget to a display with `MapDisplay.addWidget()` or add child widgets to another widget with `addWidget()`. Set its bounds with `setBounds()` or the position/size methods. Child positions are relative to their parent by default; absolute positioning and parent clipping can be configured when needed. The widget tree is attached with the display and detached when removed or when its display stops.

Override `onAttached()` and `onDetached()` for widget setup and cleanup, `onTick()` for state updates, and `onDraw()` for rendering. The root automatically redraws invalidated widgets, clearing the widget's previous area first. Call `invalidate()` when state changes so the widget will be drawn again; avoid drawing continuously from `onTick()` when nothing changed. `onBoundsChanged()` is called when its area changes.

The widget's `view` canvas is scoped to its bounds and parent clipping rules. Children draw over their parent. `setDepthOffset()` adjusts a widget's z-depth relative to its parent, and `setVisible()` / `setEnabled()` control display and interaction eligibility.

## Focus and steering controls

Map widget navigation is powered by map steering input. The display must intercept input for the player, for example with `setReceiveInputWhenHolding(true)`, before these controls are available. Intercepting input prevents normal player movement while active; see [Map Displays: input and interaction callbacks](map-displays.md#input-and-interaction-callbacks).

| Control | Logical key | Default widget behavior |
|---|---|---|
| A / D | `LEFT` / `RIGHT` | Move focus to a neighboring eligible widget. |
| W / S | `UP` / `DOWN` | Move focus to a neighboring eligible widget. |
| Space | `ENTER` | Activate the focused widget by calling `onActivate()`. |
| Shift | `BACK` | Deactivate the current widget and return to an eligible parent. |

Only visible, enabled, positive-sized focusable widgets participate in navigation. The navigator chooses a candidate in the requested direction based on widget bounds. Override `navigateNextWidget()` to change that behavior. A widget can respond to focus changes with `onFocus()` and `onBlur()`, and to activation with `onActivate()` and `onDeactivate()`.

Focus and activation are different. Focus selects the widget that receives Enter and focused input. The *activated widget* sets the scope for focus navigation: only its focusable descendants are navigable, not focusable widgets elsewhere in the tree. This lets a container open a modal or menu by calling `activate()`; W/A/S/D navigation is then confined to that container's descendants. Deactivating it returns navigation to its parent scope.

Enter also invokes `onActivate()` on the focused widget. The default `onActivate()` sets that widget as the activated navigation scope. If an override handles Enter as a button press or action, do not call `super.onActivate()` unless changing the navigation scope is also intended. For a container that should become the navigation scope when Enter is pressed, preserve the default behavior with `super.onActivate()`.

Input and interaction events propagate from the root to the focused input widget. Override `onKeyPressed()`, `onKeyReleased()`, or `onKey()` for key behavior; `onLeftClick()` and `onRightClick()` for map clicks; `onItemDrop()` for handling item drops; and `onBlockInteract()` for world block interactions. `getNextInputWidget()` can be used to identify the next widget that receives propagated events.

`MapPlayerInput` is also available directly from the display when an interface needs raw states rather than focus navigation. It exposes pressed/released state, logical direction values, repeat timing, and transitions for A/D, W/S, Space, and Shift.

## Common widget patterns

| Widget | Purpose |
|---|---|
| [`MapWidget`](../src/main/java/com/bergerkiller/bukkit/common/map/widgets/MapWidget.java) | Base class for custom widgets. Provides bounds, child composition, lifecycle and drawing callbacks, focus/navigation, and input handling. |
| [`MapWidgetButton`](../src/main/java/com/bergerkiller/bukkit/common/map/widgets/MapWidgetButton.java) | Focusable button with optional text and icon. Override `onActivate()` to handle the button action. |
| [`MapWidgetText`](../src/main/java/com/bergerkiller/bukkit/common/map/widgets/MapWidgetText.java) | Displays text with configurable font, color, alignment, shadow, and automatic sizing. |
| [`MapWidgetWindow`](../src/main/java/com/bergerkiller/bukkit/common/map/widgets/MapWidgetWindow.java) | Minecraft-styled panel with a title and child-widget area; a useful base for dialogs and popup menus. |
| [`MapWidgetTabView`](../src/main/java/com/bergerkiller/bukkit/common/map/widgets/MapWidgetTabView.java) | Container that displays one tab page at a time. Each `MapWidgetTabView.Tab` can own child widgets; selecting another tab swaps the visible page. |
| [`MapWidgetAnvil`](../src/main/java/com/bergerkiller/bukkit/common/map/widgets/MapWidgetAnvil.java) | Opens a per-viewer interactive Minecraft anvil window for text input and optional left/middle/right buttons. Override `onTextChanged()`, `onClick()`, and `onClose()` as needed. |
| [`MapWidgetSubmitText`](../src/main/java/com/bergerkiller/bukkit/common/map/widgets/MapWidgetSubmitText.java) | Specialized anvil input dialog with accept and cancel actions. Override `onAccept(text)` and `onCancel()` to handle the result. |
Widgets can be nested to compose larger interfaces. `setRetainChildWidgets(true)` is available for container widgets that should preserve their children when detached and attached again.

For a complete custom widget and display example, including focus-sensitive drawing and giving the player the display item, see [Map Widget Example](examples/map-widgets-simple.md). For popup menus made from nested windows and buttons, see [Map Widget Menus](examples/map-widgets-menu.md).

## Playtesting widgets in a unit test

`MapDisplayDebugWindow` opens a standalone 128-by-128 Swing window for a `MapDisplay`, ticks it at 20 TPS, and forwards W/A/S/D, Space, and Shift input. Use `MapDisplayDebugWindow.showForeverAutoScale(display)` in a test to playtest a display without a Minecraft client. Close the window to end the test. This is primarily intended for quickly trying simple displays and widgets, not as a substitute for in-game testing; it also requires a graphical, non-headless environment.

The test display has no plugin instance. Widgets that need bundled images should load them by class, for example `MapTexture.loadResource(MyWidget.class, "textures/icon.png")`, rather than calling `MapDisplay.loadTexture()` or `MapTexture.loadPluginResource()` with a plugin instance. For complex displays, keep plugin-specific logic outside the widgets and inject only the data or behavior the widget needs. This makes the widgets easier to playtest independently.

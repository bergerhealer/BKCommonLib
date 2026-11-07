# Map Widget Menus

Complex map menus can be composed from reusable widget classes. This example starts with two buttons on the display. Each opens a different `MapWidgetWindow` subclass as a popup; each window adds its own buttons when attached and activates itself so navigation is scoped to the popup's contents. Press Shift (the `BACK` key) while the popup is active to close it and return to the parent menu.

The shared popup base class makes each window focusable, places it at a fixed absolute position on the 128-by-128 map, and gives it a depth offset of 3 so it renders above the main display widgets. It also handles the `BACK` key only while that popup is active, removing itself to close and allowing navigation to return to its parent:

```java
import com.bergerkiller.bukkit.common.events.map.MapKeyEvent;
import com.bergerkiller.bukkit.common.map.MapDisplay;
import com.bergerkiller.bukkit.common.map.MapPlayerInput;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetWindow;
import org.bukkit.inventory.ItemStack;

public abstract class PopupMenu extends MapWidgetWindow {
    public PopupMenu() {
        setFocusable(true);
        setPositionAbsolute(true);
        setBounds(16, 20, 96, 88);
        setDepthOffset(3);
    }

    @Override
    public void onKeyPressed(MapKeyEvent event) {
        if (event.getKey() == MapPlayerInput.Key.BACK && isActivated()) {
            removeWidget();
            return;
        }
        super.onKeyPressed(event);
    }
}

public class FirstPopup extends PopupMenu {
    public FirstPopup() {
        getTitle().setText("First menu");
    }

    @Override
    public void onAttached() {
        addWidget(new MapWidgetButton() {
            @Override
            public void onActivate() {
                System.out.println("First popup action");
            }
        }.setText("Do first thing").setBounds(8, 24, 80, 20));

        activate();
    }
}

public class SecondPopup extends PopupMenu {
    public SecondPopup() {
        getTitle().setText("Second menu");
    }

    @Override
    public void onAttached() {
        addWidget(new MapWidgetButton() {
            @Override
            public void onActivate() {
                System.out.println("Second popup action");
            }
        }.setText("Do second thing").setBounds(8, 24, 80, 20));

        activate();
    }
}
```

The windows are focusable so `activate()` can make each window itself the active navigation scope. Its focusable descendants then receive W/A/S/D and Enter navigation, rather than the buttons behind the popup. Their `onActivate()` overrides intentionally do not call `super.onActivate()`, because these actions should not replace the popup's navigation scope. `BACK` is intercepted at the active popup and removes it. `removeWidget()` detaches the popup and restores the parent's navigation scope.

The main display adds two buttons. Each creates its corresponding popup and adds it to the display; the popup's `onAttached()` then builds its contents and activates its navigation scope:

```java
public class MenuDisplay extends MapDisplay {
    @Override
    public void onAttached() {
        setReceiveInputWhenHolding(true);

        addWidget(new MapWidgetButton() {
            @Override
            public void onActivate() {
                getDisplay().addWidget(new FirstPopup());
            }
        }.setText("Open first").setBounds(12, 24, 104, 24));

        addWidget(new MapWidgetButton() {
            @Override
            public void onActivate() {
                getDisplay().addWidget(new SecondPopup());
            }
        }.setText("Open second").setBounds(12, 62, 104, 24));
    }
}
```

Create and give the map item as usual:

```java
ItemStack item = MapDisplay.createMapItem(MenuDisplay.class);
player.getInventory().addItem(item);
```

This is a minimal popup example: both windows occupy the same screen region, and the sample actions print to the server console. A production menu can add state and distinct window placement as needed.

# Rendering 3D Models on Map Canvases

This example renders a 3D item model and block model from a resource pack onto small `MapTexture` canvases, then draws those textures onto a `MapDisplay` layer. Both models use an isometric transform and directional lighting. This is different from `MapCanvas.drawItem()`, which produces a flat inventory-slot icon.

Load the resource pack once during plugin startup, before a display tries to render models:

```java
@Override
public void onEnable() {
    MapResourcePack.VANILLA.load();
}
```

In the display, resolve the models and render them to textures:

```java
public class ModelShowcase extends MapDisplay {
    @Override
    public void onAttached() {
        MapCanvas canvas = getLayer();

        Model itemModel = MapResourcePack.VANILLA.getItemModel(
                new ItemStack(Material.DIAMOND_PICKAXE));
        Model blockModel = MapResourcePack.VANILLA.getBlockModel(Material.GRASS_BLOCK);

        canvas.draw(renderIsometricModel(itemModel), 12, 28);
        canvas.draw(renderIsometricModel(blockModel), 68, 28);

        canvas.draw(MapFont.MINECRAFT, 12, 84, MapColorPalette.COLOR_WHITE, "Item");
        canvas.draw(MapFont.MINECRAFT, 68, 84, MapColorPalette.COLOR_WHITE, "Block");
    }

    private static MapTexture renderIsometricModel(Model model) {
        MapTexture texture = MapTexture.createEmpty(48, 48);

        // Ambient light keeps shaded faces visible; directional light adds shape.
        texture.setLightOptions(0.2f, 0.8f, new Vector3(-1.0, 1.0, -1.0));

        Matrix4x4 transform = new Matrix4x4();
        transform.translate(24.0, 0.0, 24.0);
        transform.scale(1.5);
        transform.translate(8.0, 8.0, 8.0);
        transform.rotateX(-60.0);
        transform.rotateY(225.0);
        transform.translate(-8.0, -8.0, -8.0);

        texture.drawModel(model, transform);
        return texture;
    }
}
```

`getItemModel()` resolves the model selected for the supplied item stack, including its resource-pack item-model configuration. `getBlockModel()` resolves the default block state for the material; use `BlockData` or `BlockRenderOptions` instead when the rendered block should reflect a particular state, such as a direction or color.

Minecraft block and item models use a 16-unit model space. The transform scales and centers that model in the 48-by-48 texture, then applies the isometric pitch and yaw. Adjust the transform for a different camera angle or framing. `setLightOptions(ambient, directional, direction)` controls model shading: ambient light lifts all faces, while directional light varies with each face's orientation. The example uses a normalized light direction toward the upper-left/front; increase ambient light for a flatter, more evenly lit sprite.

## Camera Projection

When drawing multiple models into one canvas, create a base transform that acts as the camera. Translate it to the canvas center and choose a scale based on the canvas dimensions. Then copy that transform for each model and translate the model to its relative 3D position:

```java
private static void drawScene(MapCanvas canvas, Model[] models, Vector3[] positions) {
    Matrix4x4 camera = new Matrix4x4();
    camera.translate(canvas.getWidth() * 0.5, 0.0, canvas.getHeight() * 0.5);
    camera.scale(2.0);
    camera.rotateX(-60.0);
    camera.rotateY(225.0);

    canvas.setLightOptions(0.2f, 0.8f, new Vector3(-1.0, 1.0, -1.0));
    for (int i = 0; i < models.length; i++) {
        Vector3 position = positions[i];
        Matrix4x4 modelTransform = camera.clone();
        modelTransform.translate(position.x - 8.0, position.y - 8.0, position.z - 8.0);
        canvas.drawModel(models[i], modelTransform);
    }
}
```

In this example, each position is the center of a model in the scene's 3D coordinate system; subtracting `(8, 8, 8)` centers the model's 16-unit local space on that position. The camera rotation projects the scene onto the map canvas, while its initial translation and scale control framing. Adjust the scale to fit the scene, and use the same camera transform for every model so their relative positions and sizes remain consistent.

Cache rendered textures or resolved models when drawing an unchanged scene repeatedly, rather than rebuilding them every tick.

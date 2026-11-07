# CommonTag (NBT) API

`CommonTag` wraps Minecraft's version-dependent NBT tags with a cross-version API. Many BKCommonLib utilities accept or return `CommonTag`, `CommonTagCompound`, or `CommonTagList`, so understanding these types is useful even when you are not directly reading or writing NBT. Most code works with `CommonTagCompound` and its typed child tags. Unless a snippet initializes it, `data` below refers to a `CommonTagCompound`.

## Key files

- [`CommonTag`](../src/main/java/com/bergerkiller/bukkit/common/nbt/CommonTag.java): Base wrapper for NBT values, data conversion, cloning, and uncompressed stream I/O.
- [`CommonTagCompound`](../src/main/java/com/bergerkiller/bukkit/common/nbt/CommonTagCompound.java): Map-like compound tag with typed child access, value conversion, and compressed or uncompressed stream and file I/O.
- [`CommonTagList`](../src/main/java/com/bergerkiller/bukkit/common/nbt/CommonTagList.java): List-like tag collection and converted value access.
- [`NBTUtil`](../src/main/java/com/bergerkiller/bukkit/common/utils/NBTUtil.java): Utilities for transferring NBT to and from Bukkit entities, inventories, equipment, and block states.

## Tags and values

NBT stores values in typed tags. `CommonTag` is the base wrapper; concrete values are represented by tag types such as `CommonTagCompound` and `CommonTagList`. A compound maps string keys to child tags and implements `Map<String, CommonTag>`. Accordingly, the Map-style `get()` and `put()` methods operate on tag objects:

```java
CommonTagCompound data = new CommonTagCompound();
data.put("count", new CommonTag(12));
CommonTag countTag = data.get("count");
int count = countTag.getData(Integer.class);
```

For most plugin code, use the value-oriented `getValue()` and `putValue()` methods instead. They automatically convert between ordinary Java values and the matching NBT tag representation. Typed getters can use a class or a non-null default value:

```java
data.putValue("count", 12);
int count = data.getValue("count", 0);
```

The default value is a conversion target and fallback; it does not insert a missing key into the compound. `CommonTag.getData()` and its typed overloads provide the corresponding conversion for an individual tag. Lists have similar value-oriented methods, including `getValue(index, type)`, `setValue(index, value)`, and `addValue(value)`.

Use the Map-style API when code intentionally constructs or passes tag objects. Use value-oriented methods when reading or writing regular Java data. BKCommonLib APIs use both patterns: utility methods frequently accept or return compounds and lists directly, while typed tag access keeps NBT tag types available when needed.

## Read-only empty compounds

`CommonTagCompound.EMPTY` is a shared, read-only empty compound. `getCompoundOrEmpty(key)` returns it when the key is absent or does not contain a compound, instead of returning `null`. This makes nested reads safe to chain:

```java
String name = data.getCompoundOrEmpty("display")
        .getCompoundOrEmpty("metadata")
        .getValue("name", String.class);
```

If either compound is absent, the final value is `null`. The empty compound is intended for reading only; attempting to modify it throws `UnsupportedOperationException`. Use `get(key, CommonTagCompound.class)` when absence should be represented by `null`, or `createCompound(key)` when a writable child should be created as needed. `CommonTagList` has matching `EMPTY`, `getListOrEmpty()`, and `createList()` APIs.

Some APIs return read-only tag views for existing data as well as for absent data. For example, `CommonItemStack.getCustomData()` is read-only and returns `CommonTagCompound.EMPTY` when no custom data is present. Use `getCustomDataCopy()` or `updateCustomData()` when you intend to change item custom data.

## Serialize to streams and bytes

`CommonTagCompound` reads and writes binary NBT through streams. By default, `writeToStream()` and `readFromStream()` use uncompressed NBT. Pass `true` to the overloads to use GZIP compression; reading must use the same compression setting as writing. The stream methods do not close caller-owned streams.

```java
ByteArrayOutputStream bytes = new ByteArrayOutputStream();
data.writeToStream(bytes, true); // GZIP-compressed NBT

ByteArrayInputStream input = new ByteArrayInputStream(bytes.toByteArray());
CommonTagCompound decoded = CommonTagCompound.readFromStream(input, true);
```

For uncompressed NBT, call `data.writeToStream(bytes)` and `CommonTagCompound.readFromStream(input)` without the boolean, or pass `false` to both overloads. This works the same way with a file or network stream; choose compression to match the format expected by the consumer. `writeToFile(file, compressed)` and `readFromFile(file, compressed)` provide corresponding file APIs.

`CommonTag` also has uncompressed stream methods for non-compound root tags. Use the `CommonTagCompound` methods when the serialized root is a compound.

For recipes covering tag and value access, nested data, read-only empty compounds, item custom data, and byte serialization, see the [CommonTag examples](examples/commontag.md).

## Relationships and conventions

- NBT types are version-dependent internally; use `CommonTag` wrappers rather than importing or storing server-specific NBT classes in plugin code.
- Many BKCommonLib APIs expose NBT directly. For example, `CommonItemStack` provides custom data, `NBTUtil` transfers NBT for Bukkit objects, and `BlockStateChange` exposes block metadata.
- `CommonTagCompound` and `CommonTagList` are mutable unless marked read-only. Clone a tag when an independent copy is required.
- Use `getCompoundOrEmpty()` for null-free reads, `get()` for nullable tag access, and `createCompound()` for create-if-absent writes.

For project terminology, see the [glossary](glossary.md). For configuration nodes—which provide a separate YAML-oriented data tree—see the [Configuration API](configuration.md).

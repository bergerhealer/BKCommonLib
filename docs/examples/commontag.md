# CommonTag API Examples

These examples show common ways to build, read, update, and serialize NBT with BKCommonLib. Snippets omit imports. Unless initialized in the example, `data` is a `CommonTagCompound`.

For the API model and the distinction between tag-based and value-based access, see the [CommonTag API](../commontag.md).

## Store and read Java values

Use `putValue()` and `getValue()` for ordinary Java values. A typed getter with a default converts the stored value and returns the default if the key is absent or conversion fails; it does not add the default to the compound.

```java
CommonTagCompound data = new CommonTagCompound();
data.putValue("enabled", true);
data.putValue("count", 12);
data.putValue("owner", "server");

boolean enabled = data.getValue("enabled", false);
int count = data.getValue("count", Integer.class, 0);
String owner = data.getValue("owner", String.class);
String missing = data.getValue("missing", String.class);
```

`missing` is `null`. Use a non-null default when a fallback is required. `putValue(key, null)` removes the key.

## Work with tag objects directly

`CommonTagCompound` implements `Map<String, CommonTag>`, so its `get()` and `put()` methods take and return tag objects. Use this form when preserving or manipulating tag types is important.

```java
CommonTagCompound data = new CommonTagCompound();
data.put("count", new CommonTag(12));

CommonTag countTag = data.get("count");
int count = countTag.getData(Integer.class);
```

For common plugin code, value access is usually shorter:

```java
data.putValue("count", 12);
int count = data.getValue("count", 0);
```

## Create nested compounds and lists

Use `createCompound()` and `createList()` when a writable child should be stored at the key if it does not already exist. Value-oriented list methods accept ordinary values and wrap them as tags.

```java
CommonTagCompound data = new CommonTagCompound();
CommonTagCompound display = data.createCompound("display");
display.putValue("name", "Travel Pass");

CommonTagList lore = display.createList("lore");
lore.addValue("Valid for one journey");
lore.addValue("Keep this item safe");
```

To read a value from the list, convert it at access:

```java
String firstLine = data.getCompoundOrEmpty("display")
        .getListOrEmpty("lore")
        .getValue(0, String.class);
```

## Read optional nested compounds

`getCompoundOrEmpty()` returns the shared read-only `CommonTagCompound.EMPTY` when a child is absent or is not a compound. This avoids null checks at each level:

```java
String title = data.getCompoundOrEmpty("display")
        .getCompoundOrEmpty("text")
        .getValue("title", String.class);
```

If a nested compound is missing, `title` is `null`. Do not modify a result that might be `EMPTY`; use `createCompound()` when you need to create or update a child. The equivalent list-reading methods are `getListOrEmpty()` and `createList()`.

## Copy and update item custom data

`CommonItemStack.getCustomData()` returns a read-only view, including when there is no custom data. Use the copy or callback APIs for edits:

```java
CommonItemStack item = getItemToUpdate();

CommonTagCompound customData = item.getCustomDataCopy();
customData.putValue("plugin:uses", 3);
item.setCustomData(customData);
```

Alternatively, update the data in place through a callback that receives a writable copy:

```java
item.updateCustomData(tag -> {
    int uses = tag.getValue("plugin:uses", 0);
    tag.putValue("plugin:uses", uses + 1);
});
```

This avoids accidentally mutating the read-only view returned by `getCustomData()`.

## Encode and decode bytes

Compound tags can be written to and read from a byte array through standard Java streams. The default methods use uncompressed NBT:

```java
CommonTagCompound data = new CommonTagCompound();
data.putValue("count", 12);

ByteArrayOutputStream bytes = new ByteArrayOutputStream();
data.writeToStream(bytes);
byte[] encoded = bytes.toByteArray();

ByteArrayInputStream input = new ByteArrayInputStream(encoded);
CommonTagCompound decoded = CommonTagCompound.readFromStream(input);
```

Use the boolean overload with `true` on both sides when the byte format should be GZIP-compressed:

```java
ByteArrayOutputStream bytes = new ByteArrayOutputStream();
data.writeToStream(bytes, true);
byte[] compressed = bytes.toByteArray();

ByteArrayInputStream input = new ByteArrayInputStream(compressed);
CommonTagCompound decoded = CommonTagCompound.readFromStream(input, true);
```

The read and write compression settings must match. These methods leave the supplied streams open; close streams at the point where your code owns their lifecycle.

## Pass tags to BKCommonLib APIs

Many BKCommonLib APIs accept or return CommonTag compounds instead of server-specific NBT classes. Keep the tag wrapper when passing data between APIs; use `getValue()` and `putValue()` for individual plugin-owned fields.

For example, `NBTUtil.saveBlockState()` returns a `CommonTagCompound`, and APIs such as `BlockStateChange` expose their metadata in the same form. This lets code inspect or pass version-dependent NBT without depending on the underlying Minecraft tag implementation.

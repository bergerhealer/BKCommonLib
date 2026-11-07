# SignChangeTracker Examples

These examples show how to [detect sign changes and read or edit front/back text](../sign-change-tracker.md) through `SignChangeTracker`.

## Detect and handle changes

Call `update()` at the point where you need to verify the latest state. It returns `true` for a changed or removed sign; check `isRemoved()` before accessing its text.

```java
if (tracker.update()) {
    if (tracker.isRemoved()) {
        stopTracking(tracker);
    } else {
        String[] frontLines = tracker.getLines(SignSide.FRONT);
        onSignTextChanged(frontLines);
    }
}
```

The tracker does not update itself. Calling `update()` may synchronously load the sign's chunk when it needs to reacquire the backing sign entity, so only poll signs relevant to the current feature.

## Read and write one side

Use the `SignSide` overloads when the side is selected dynamically:

```java
SignSide side = SignSide.BACK;
String line = tracker.getLine(side, 0);
tracker.setLine(side, 0, line + "!");
```

`SignSide.BACK` reads as empty text and ignores writes on Minecraft versions before 1.20. Check `side.isSupported()` if unsupported-side behavior is not acceptable.

## Use a side-specific accessor

`accessLinesOfSide()` adapts a two-sided `SignLineAccessor` to `SignSideLineAccessor`, which is convenient when passing one side to another method:

```java
SignSideLineAccessor front = tracker.accessLinesOfSide(SignSide.FRONT);
printLines(front.getLines());
front.setLine(0, "Ready");
```

The side-specific accessor delegates to the tracker; it does not automatically call `update()`. Use `tracker.liveUpdatingAccessor()` if each read should refresh first, or call `tracker.update()` once before reading several lines.

## Store state for both sides

`SignSideMap<T>` avoids maintaining parallel front/back fields when the value is indexed by side:

```java
SignSideMap<Integer> counters = new SignSideMap<>(0, 0);
counters.setSide(SignSide.FRONT, counters.side(SignSide.FRONT) + 1);
int backCount = counters.back();
```

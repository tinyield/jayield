# JAYield

 A minimalistic extensible lazy sequence implementation interoperable with Java
 `Stream`, which provides an idiomatic `yield` like generator.
 
## Example

An auxiliary `collapse()`, which merges series of adjacent elements is written 
with JAYield as in the following example:

```java
static <U> Traversable<U> collapse(Series<U> src) {
    return yield -> {
        final Object[] prev = {null};
        src.traverse(item -> {
            if (prev[0] == null || !prev[0].equals(item))
                yield.ret((U) (prev[0] = item));
        });
    };
}
```

This method can be chained in a query like this:

```java
Integer[] arrange = {7, 7, 8, 9, 9, 11, 11, 7};
Object[] actual = Series
                    .of(arrange)
                    .traverseWith(n -> collapse(n))
                    .toArray();
```

This is close to C\# idiom where you write an equivalent `Collapse` extension 
method as:

```csharp
IEnumerable <T> Collapse <T>( this IEnumerable <T> src) {
    IEnumerator <T> iter = src. GetEnumerator ();
    T prev = null;
    while(iter.MoveNext ()) {
        if(prev == null || !prev.Equals(iter.Current ))
        yield return prev = iter.Current;
    }
}
```

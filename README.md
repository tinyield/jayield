# JAYield

[![Build](https://sonarcloud.io/api/badges/gate?key=com.github.jayield%3Ajayield)](https://sonarcloud.io/dashboard?id=com.github.jayield%3Ajayield)
[![Coverage](https://sonarcloud.io/api/badges/measure?key=com.github.jayield%3Ajayield&metric=coverage)](https://sonarcloud.io/component_measures/domain/Coverage?id=com.github.jayield%3Ajayield)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.jayield/jayield/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.jayield/jayield)

A minimalistic extensible lazy sequence implementation interoperable with Java
`Stream`, which provides an idiomatic `yield` like generator.
 
## Example

An auxiliary `collapse()` method, which merges series of adjacent elements is written 
with JAYield in the following way:

```java
static <U> Traverser<U> collapse(Query<U> src) {
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
Object[] actual = Query
                    .of(arrange)
                    .then(n -> collapse(n))
                    .filter(n -> n%2 != 0)
                    .map(Object::toString)
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


## Installation

In order to include it to your Maven project, simply add this dependency:

```xml
<dependency>
    <groupId>com.github.jayield</groupId>
    <artifactId>jayield</artifactId>
    <version>0.1.0</version>
</dependency>
```

You can also download the artifact directly from [Maven
Central Repository](http://repo1.maven.org/maven2/com/github/jayield/jayield/)


## License

This project is licensed under [Apache License,
version 2.0](https://www.apache.org/licenses/LICENSE-2.0)

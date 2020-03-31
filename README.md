# JAYield

[![Build Status](https://sonarcloud.io/api/project_badges/measure?project=com.github.jayield%3Ajayield&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.github.jayield%3Ajayield)
[![Coverage Status](https://sonarcloud.io/api/project_badges/measure?project=com.github.jayield%3Ajayield&metric=coverage)](https://sonarcloud.io/component_measures?id=com.github.jayield%3Ajayield&metric=Coverage)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.jayield/jayield/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.jayield/jayield)

A minimalistic extensible lazy sequence implementation interoperable with Java
`Stream`, which provides an idiomatic `yield` like generator.

Notice how it looks a JAYield custom `collapse()` method that merges series of adjacent elements.
It has a similar shape to that one written in any language providing the `yield` operator
such as C\#.

<table class="table">
    <tr class="row">
        <td>
 
```java
private static Object prev = null;
static <U> Traverser<U>  collapse(Query<U> src) {
  return yield -> {
    src.traverse(item -> {
      if (prev == null || !prev.equals(item))
      yield.ret((U) (prev = item));
    });
  };
}
```

</td>
<td>

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

</td>
</tr>
</table>

This method can be chained in a query like this:

```java
Integer[] arrange = {7, 7, 8, 9, 9, 11, 11, 7};
Object[] actual = Query
                    .of(arrange)
                    .then(src -> collapse(src))
                    .filter(n -> n%2 != 0)
                    .map(Object::toString)
                    .toArray();
```


## Installation

In order to include it to your Maven project, simply add this dependency:

```xml
<dependency>
    <groupId>com.github.jayield</groupId>
    <artifactId>jayield</artifactId>
    <version>1.0.3</version>
</dependency>
```

You can also download the artifact directly from [Maven
Central Repository](http://repo1.maven.org/maven2/com/github/jayield/jayield/)


## License

This project is licensed under [Apache License,
version 2.0](https://www.apache.org/licenses/LICENSE-2.0)

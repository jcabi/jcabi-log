<img src="http://img.jcabi.com/logo.png" width="200px" height="48px" />

More details are here: [log.jcabi.com](http://log.jcabi.com/index.html)

`Logger` is a convenient static wrapper of [slf4j](http://www.slf4j.org/)
(don't forget to include one of [SLF4J Bindings](http://www.slf4j.org/manual.html#binding)
into the project):

```java
import com.jcabi.log.Logger;
class Foo {
  void bar(int value) {
    Logger.debug(this, "method #bar(%d) was called", value);
  }
}
```

You need just this dependency:

```xml
<dependency>
  <groupId>com.jcabi</groupId>
  <artifactId>jcabi-log</artifactId>
  <version>0.12.2</version>
</dependency>
```

## Questions?

If you have any questions about the framework, or something doesn't work as expected,
please [submit an issue here](https://github.com/jcabi/jcabi-log/issues/new).
If you want to discuss, please use our [Google Group](https://groups.google.com/forum/#!forum/jcabi).

## How to contribute?

Fork the repository, make changes, submit a pull request.
We promise to review your changes same day and apply to
the `master` branch, if they look correct.

Please run Maven build before submitting a pull request:

```
$ mvn clean install -Pqulice
```

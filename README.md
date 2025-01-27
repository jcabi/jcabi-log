<img alt="logo" src="https://www.jcabi.com/logo-square.svg" width="64px" height="64px" />

[![EO principles respected here](https://www.elegantobjects.org/badge.svg)](https://www.elegantobjects.org)
[![DevOps By Rultor.com](http://www.rultor.com/b/jcabi/jcabi-log)](http://www.rultor.com/p/jcabi/jcabi-log)
[![We recommend IntelliJ IDEA](https://www.elegantobjects.org/intellij-idea.svg)](https://www.jetbrains.com/idea/)

[![mvn](https://github.com/jcabi/jcabi-log/actions/workflows/mvn.yml/badge.svg)](https://github.com/jcabi/jcabi-log/actions/workflows/mvn.yml)
[![PDD status](http://www.0pdd.com/svg?name=jcabi/jcabi-log)](http://www.0pdd.com/p?name=jcabi/jcabi-log)
[![codecov](https://codecov.io/gh/jcabi/jcabi-log/branch/master/graph/badge.svg)](https://codecov.io/gh/jcabi/jcabi-log)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.jcabi/jcabi-log/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.jcabi/jcabi-log)
[![Javadoc](https://javadoc.io/badge/com.jcabi/jcabi-log.svg)](http://www.javadoc.io/doc/com.jcabi/jcabi-log)
[![Hits-of-Code](https://hitsofcode.com/github/jcabi/jcabi-log)](https://hitsofcode.com/view/github/jcabi/jcabi-log)

More details are here: [log.jcabi.com](https://log.jcabi.com/index.html)

Read this blog post:
[_Get Rid of Java Static Loggers_](https://www.yegor256.com/2014/05/23/avoid-java-static-logger.html)

`Logger` is a convenient static wrapper of
[slf4j](http://www.slf4j.org/)
(don't forget to include one of
[SLF4J Bindings](http://www.slf4j.org/manual.html#binding)
into the project):

```java
import com.jcabi.log.Logger;
class Foo {
  void bar(int value) {
    Logger.debug(this, "method #bar(%d) was called", value);
  }
}
```

Besides standard `%s` placeholders inside the format string, you can use
other custom ones, which help formatting common values faster:

* `%[file]s` --- absolute file name ➜ file name relative to current directory
* `%[text]s` --- any string ➜ pretty looking text, short enough, and escaped
* `%[exception]s` --- `Exception` ➜ stacktrace
* `%[list]s` --- `Iterable` ➜ pretty formatted list, in one line
* `%[size]s` --- size in bytes ➜ Kb, Mb, Gb, Tb, and so on
* `%[ms]s` --- milliseconds ➜ ms, sec, min, hours, etc.
* `%[nano]s` --- nanoseconds ➜ µs, ms, sec, min, hours, etc.
* `%[type]s` --- `Class<?>` ➜ name of it
* `%[secret]s` --- any string ➜ stars
* `%[dom]s` --- `org.w3c.domDocument` ➜ pretty printed/formatted XML

You are welcome to
[suggest](https://github.com/jcabi/jcabi-log/blob/master/src/main/java/com/jcabi/log/DecorsManager.java)
your own "decors".

## How to contribute?

Fork the repository, make changes, submit a pull request.
We promise to review your changes same day and apply to
the `master` branch, if they look correct.

Please run Maven build before submitting a pull request:

```bash
mvn clean install -Pqulice
```

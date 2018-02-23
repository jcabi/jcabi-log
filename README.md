<img src="http://img.jcabi.com/logo-square.png" width="64px" height="64px" />

[![EO principles respected here](https://cdn.rawgit.com/yegor256/elegantobjects.github.io/master/badge.svg)](http://www.elegantobjects.org)
[![Managed by Zerocracy](https://www.0crat.com/badge/C3RUBL5H9.svg)](https://www.0crat.com/p/C3RUBL5H9)
[![DevOps By Rultor.com](http://www.rultor.com/b/jcabi/jcabi-log)](http://www.rultor.com/p/jcabi/jcabi-log)
[![We recommend IntelliJ IDEA](http://www.elegantobjects.org/intellij-idea.svg)](https://www.jetbrains.com/idea/)

[![Build Status](https://travis-ci.org/jcabi/jcabi-log.svg?branch=master)](https://travis-ci.org/jcabi/jcabi-log)
[![PDD status](http://www.0pdd.com/svg?name=jcabi/jcabi-log)](http://www.0pdd.com/p?name=jcabi/jcabi-log)
[![Build status](https://ci.appveyor.com/api/projects/status/tk5nksux8peh2n8o/branch/master?svg=true)](https://ci.appveyor.com/project/yegor256/jcabi-log/branch/master)
[![Coverage Status](https://coveralls.io/repos/jcabi/jcabi-log/badge.svg?branch=master&service=github)](https://coveralls.io/github/jcabi/jcabi-log?branch=master)

[![jpeek report](http://i.jpeek.org/com.jcabi/jcabi-log/badge.svg)](http://i.jpeek.org/com.jcabi/jcabi-log/)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.jcabi/jcabi-log/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.jcabi/jcabi-log)
[![Javadoc](https://javadoc.io/badge/com.jcabi/jcabi-log.svg)](http://www.javadoc.io/doc/com.jcabi/jcabi-log)
[![Dependencies](https://www.versioneye.com/user/projects/561ac156a193340f280010de/badge.svg?style=flat)](https://www.versioneye.com/user/projects/561ac156a193340f280010de)

More details are here: [log.jcabi.com](http://log.jcabi.com/index.html)

Read this blog post: [Get Rid of Java Static Loggers](http://www.yegor256.com/2014/05/23/avoid-java-static-logger.html)

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

## Questions?

If you have any questions about the framework, or something doesn't work as expected,
please [submit an issue here](https://github.com/jcabi/jcabi-log/issues/new).

## How to contribute?

Fork the repository, make changes, submit a pull request.
We promise to review your changes same day and apply to
the `master` branch, if they look correct.

Please run Maven build before submitting a pull request:

```
$ mvn clean install -Pqulice
```

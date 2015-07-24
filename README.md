# JFortschritt

[![Build Status](https://travis-ci.org/stefanbirkner/jfortschritt.svg?branch=master)](https://travis-ci.org/stefanbirkner/jfortschritt)

JFortschritt is a small library for writing progress bars to the command-line.

JFortschritt is published under the
[MIT license](http://opensource.org/licenses/MIT). It needs Java 7,
because this is the version that is supported by most applications that I
encounter. Please let me know if you need JFortschritt to support an older
version of Java.


## Installation

JFortschritt is available from [Maven Central](http://search.maven.org/).

    <dependency>
      <groupId>com.github.stefanbirkner</groupId>
      <artifactId>jfortschritt</artifactId>
      <version>0.1.0</version>
    </dependency>


## Usage

Currently the library provides only one type progress bar: the
`SingleStepProgressBar`. First create a new progress bar.

    import com.github.stefanbirkner.jfortschritt.*;

    ...

    SingleStepProgressBar progressBar = new SingleStepProgressBarBuilder()
      .setNumberOfSteps(42)
      .toProgressBar();

Start the progress bar:

    progressBar.start();

It immediately prints an empty progress bar:

    [>                                                              ] (0/42)

The width of the line is always 72 chars. Now move the progress bar forward.

    progressBar.moveForward();

and see the result

    [==>                                                            ] (1/42)

This is how a progress bar looks like at the end. (It renders a new line
character, too. Thus additional text starts at a new line.)

    [=============================================================>] (42/42)
    


## Contributing

You have three options if you have a feature request, found a bug or
simply have a question about JFortschritt.

* [Write an issue.](https://github.com/stefanbirkner/jfortschritt/issues/new)
* Create a pull request. (See [Understanding the GitHub Flow](https://guides.github.com/introduction/flow/index.html))
* [Write an email to mail@stefan-birkner.de](mailto:mail@stefan-birkner.de)


## Development Guide

JFortschritt is build with [Maven](http://maven.apache.org/). If you want to
contribute code than

* Please write a test for your change.
* Ensure that you didn't break the build by running `mvn test`.
* Fork the repo and create a pull request. (See [Understanding the GitHub Flow](https://guides.github.com/introduction/flow/index.html))

The basic coding style is described in the
[EditorConfig](http://editorconfig.org/) file `.editorconfig`.

JFortschritt supports [Travis CI](https://travis-ci.org/) for continuous
integration. Your pull request will be automatically build by Travis
CI.


## Release Guide

* Select a new version according to the
  [Semantic Versioning 2.0.0 Standard](http://semver.org/).
* Set the new version in `pom.xml` and in the `Installation` section of
  this readme.
* Commit the modified `pom.xml` and `README.md`.
* Run `mvn clean deploy` with JDK 6 or 7.
* Add a tag for the release: `git tag jfortschritt-X.X.X`

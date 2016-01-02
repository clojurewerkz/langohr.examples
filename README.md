# Langohr Examples

This is a repository with examples used in documentation guides for Langohr, a [Clojure RabbitMQ client](http://clojurerabbitmq.info).

## Running Examples

The project uses [Leiningen 2](https://github.com/technomancy/leiningen/blob/master/doc/TUTORIAL.md). Make
sure you have it installed.

Every example is an individual runnable namespace. Run them like so:

    lein run -m "clojurewerkz.langohr.examples.hello-world"
    lein run -m "clojurewerkz.langohr.examples.blabbr"
    lein run -m "clojurewerkz.langohr.examples.weathr"
    lein run -m "clojurewerkz.langohr.examples.redelivery"

and so on.


## License

Copyright (C) 2011-2016 Michael S. Klishin.

Distributed under the Eclipse Public License, the same as Clojure.

# A minimal GraalVM Clojure web application:

* Environment: [yogthos/config](https://github.com/yogthos/config)
* HTTP Server: [HTTP Kit](https://github.com/http-kit/http-kit)
* HTML templating: [Hiccup](https://github.com/weavejester/hiccup)
* Resource management: [Mount](https://github.com/tolitius/mount)
* Routing: [Reitit](https://metosin.github.io/reitit/)


### [Postgres with `clojure.java.jdbc`](https://github.com/yogthos/graal-web-app-example/tree/postgres)

### [Authentication and Sessions](https://github.com/yogthos/graal-web-app-example/tree/auth)

## Requirements

* [GraalVM](https://github.com/oracle/graal/releases)
* [Leiningen](https://leiningen.org/)

## Usage

The HTTP port for the application is declared in the `config.edn` file:

```clojure
{:port 3000}
```

Run in dev mode:

    lein run

Start the nREPL

    lein nrepl

Compile native binary by running:

    lein native-image

run the app:

    target/app

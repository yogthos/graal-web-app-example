# A GraalVM Clojure web application:

* Environment: [yogthos/config](https://github.com/yogthos/config)
* HTTP Server: [HTTP Kit](https://github.com/http-kit/http-kit)
* HTML templating: [Hiccup](https://github.com/weavejester/hiccup)
* Resource management: [Mount](https://github.com/tolitius/mount)
* Routing: [Reitit](https://metosin.github.io/reitit/)
* Migrations: [Migratus](https://github.com/yogthos/migratus)
* DB access: [clojure.java.jdbc](https://github.com/clojure/java.jdbc) + Postgres

## Requirements

* [GraalVM](https://github.com/oracle/graal/releases)
* [Leiningen](https://leiningen.org/)

## Usage

The HTTP port and the database connection are declared in the `config.edn` file:

```clojure
{:db {:dbtype   "postgres"
      :dbname   "myapp_dev"
      :host     "localhost"
      :port     5432
      :user     "db_user_name_here"
      :password "db_user_password_here"}
 :port 3000}
```

Run in dev mode:

    lein run

Start the nREPL

    lein nrepl

Compile native binary by running:

    lein native-image

run the app:

    target/app

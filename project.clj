(defproject http-kit-graal "0.1.0"
  :description "HTTP Kit/Reiti with GraalVM"
  :url "https://github.com/yogthos/graal-web-app-example"
  :license {:name "MIT license"
            :url  "https://opensource.org/licenses/MIT"}

  :dependencies [[hiccup "1.0.5"]
                 [http-kit "2.3.0"]
                 [metosin/reitit "0.3.9"]
                 [ring/ring-defaults "0.3.2"]                 
                 [mount "0.1.16"]
                 [org.clojure/clojure "1.9.0"]
                 [yogthos/config "1.1.4"]]

  :plugins [[io.taylorwood/lein-native-image "0.3.0"]
            [nrepl/lein-nrepl "0.3.2"]]

  :repl-options {:init-ns http-kit-graal.core}

  :aot :all

  :main server.core

  :native-image {:name     "app"
                 :jvm-opts ["-Dclojure.compiler.direct-linking=true"]
                 :opts     ["--enable-url-protocols=http"
                            "--report-unsupported-elements-at-runtime"
                            "--initialize-at-build-time"
                            "--allow-incomplete-classpath"
                            ;;avoid spawning build server
                            "--no-server"
                            "-H:ConfigurationResourceRoots=resources"
                            ~(str "-H:ResourceConfigurationFiles="
                               (System/getProperty "user.dir")
                               (java.io.File/separator)
                               "resource-config.json")]})

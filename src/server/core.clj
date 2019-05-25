(ns server.core
  (:require 
   [clojure.java.jdbc :as jdbc]
   [config.core :refer [load-env]]
   [hiccup.page :as hiccup]
   [json-html.core :as json-html]
   [jsonista.core :as json]
   [migratus.core :as migratus]
   [mount.core :refer [defstate] :as mount]
   [org.httpkit.server :as http]
   [reitit.ring :as ring]
   [ring.middleware.defaults
    :refer [wrap-defaults
            api-defaults]]
   [ring.middleware.resource
    :refer [wrap-resource]]
   [ring.util.http-response :refer :all]
   [ring.util.response :as response])
  (:gen-class))

;; Graal does not support reflection calls
(set! *warn-on-reflection* true)

(defstate env :start (load-env))

(defn json-response [response]
  (-> response
      (json/write-value-as-string)
      (ok)
      (header "content-type" "application/json")))

(def handler
  (ring/ring-handler
    (ring/router
      [["/"
        (fn [request]
          (-> (hiccup/html5
               [:head (hiccup/include-css "screen.css")]
               [:div.content
                [:h2 "Welcome to Graal 🔥🔥🔥"]
                [:p "your address is: " (:remote-addr request)]
                [:ul 
                 [:li [:a {:href "/json"} "JSON example"]]
                 [:li [:a {:href "/users"} "DB query example"]]]])
              (ok)
              (header "content-type" "text/html")))]
       ["/json"
        (fn [request]
          (-> request
              (select-keys [:server-port :server-name :remote-addr :scheme :uri :headers])
              (json-response)))]
       ["/users"
        (fn [_]
          (-> (hiccup/html5
               [:head [:style (-> "json.human.css" clojure.java.io/resource slurp)]]
               [:div
                [:h2 "USERS"]
                (json-html/edn->html
                 (jdbc/query (:db env) ["select * from users"]))])
              (ok)
              (header "content-type" "text/html")))]])))

(defmethod response/resource-data :resource
  [^java.net.URL url]
  (let [conn (.openConnection url)]
    {:content        (.getInputStream conn)
     :content-length (let [len (.getContentLength conn)] (if-not (pos? len) len))}))

(defn parse-port [args]
  (or (when-not (empty? args) (Integer/parseInt (first args))) 3000))

(defn -main [& args]
  (let [port (parse-port args)]
    (mount/start)
    (migratus/migrate {:store :database :db (:db env)})
    (-> handler
        (wrap-resource "public")
        (wrap-defaults api-defaults)
        (http/run-server {:port port}))
    (println "🔥 started on port:" port "🔥")))


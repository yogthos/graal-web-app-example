(ns server.core
  (:require [org.httpkit.server :as http]
            [reitit.ring :as ring]
            [ring.middleware.defaults
             :refer [wrap-defaults
                     api-defaults]]
            [ring.middleware.resource
             :refer [wrap-resource]]
            [ring.util.response :as response])
  (:gen-class))

;; Graal does not support reflection calls
(set! *warn-on-reflection* true)

(def handler
  (ring/ring-handler
    (ring/router
      [["/"
        (fn [request]
          {:status  200
           :headers {"content-type" "text/html"}
           :body    (str "Hello " (:remote-addr request) " 🔥🔥🔥")})]])))

(defmethod response/resource-data :resource
  [^java.net.URL url]
  (let [conn (.openConnection url)]
    {:content        (.getInputStream conn)
     :content-length (let [len (.getContentLength conn)] (if-not (pos? len) len))}))

(defn parse-port [args]
  (or (when-not (empty? args) (Integer/parseInt (first args))) 3000))

(defn -main [& args]
  (let [port (parse-port args)]
    (-> handler
        (wrap-resource "public")
        (wrap-defaults api-defaults)
        (http/run-server {:port port}))
    (println "🔥 started on port:" port "🔥")))


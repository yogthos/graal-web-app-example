(ns server.core
  (:require 
   [clojure.java.jdbc :as jdbc]
   [config.core :refer [load-env]]
   [buddy.hashers :as hashers]
   [hiccup.page :as hiccup]
   [json-html.core :as json-html]
   [jsonista.core :as json]
   [migratus.core :as migratus]
   [mount.core :refer [defstate] :as mount]
   [org.httpkit.server :as http]
   [reitit.ring :as ring]   
   [ring.middleware.defaults
    :refer [wrap-defaults api-defaults site-defaults]]
   [ring.util.http-response :refer :all]
   [ring.util.response :as response] 
   [server.params :refer [wrap-body]]
   [server.session :refer [wrap-session]])
  (:import )
  (:gen-class))

;; Graal does not support reflection calls
(set! *warn-on-reflection* true)

(defstate env :start (load-env))

(defn html-response [response]
  (-> response
      (ok)
      (header "content-type" "text/html")))

(defn json-response [response]
  (-> response
      (json/write-value-as-string)
      (ok)
      (header "content-type" "application/json")))

(def handler
  (ring/ring-handler
    (ring/router
      [["/"
        {:get (fn [{:keys [session] :as request}]
                (-> (hiccup/html5
                     [:head (hiccup/include-css "screen.css")]
                     [:div.content
                      [:h2 "Welcome to Graal 🔥🔥🔥"]
                      [:p "your address is: " (:remote-addr request)]
                      (if-let [user (:user session)]
                        [:div
                         [:p "Logged in as " (:userid user)]
                         [:form {:action "/logout"}
                          [:button {:type "submit"} "logout"]]]
                        [:form {:action "/login" :method "POST"}
                         [:label "userid"]
                         [:input {:type     :text
                                  :name     "userid"
                                  :required true}]
                         [:label "password"]
                         [:input {:type :password
                                  :name "pass"
                                  :required true}]
                         [:button {:type "submit"} "login"]])
                      [:ul 
                       [:li [:a {:href "/json"} "JSON example"]]
                       [:li [:a {:href "/users"} "DB query example"]]]])
                    (html-response)))}]
       ["/login"
        {:post (fn [{:keys [body session] :as request}]                 
                 (let [user (first (jdbc/query (:db env) ["select * from users where userid=?" (:userid body)]))]
                   (if (hashers/check (:pass body) (:pass user))
                     (assoc (found "/") :session (assoc session :user {:userid "bob"}))
                     (found "/"))))}]
       ["/logout"
        {:get (fn [_] (assoc (found "/") :session nil))}]
       ["/json"
        {:get (fn [request]
           (-> request
             (select-keys [:server-port :server-name :remote-addr :scheme :uri :headers])
             (json-response)))}]
       ["/users"
        {:get (fn [_]
                (-> (hiccup/html5
                     [:head [:style (-> "json.human.css" clojure.java.io/resource slurp)]]
                     [:div
                      [:h2 "USERS"]
                      (json-html/edn->html (jdbc/query (:db env) ["select userid, first_name, last_name, email from users"]))])
                    (html-response)))}]])))

(defmethod response/resource-data :resource
  [^java.net.URL url]
  (let [conn (.openConnection url)]
    {:content        (.getInputStream conn)
     :content-length (let [len (.getContentLength conn)] (if-not (pos? len) len))}))

(defstate server
  :start (let [port (:port env)]
           (println "🔥 starting on port:" port "🔥")           
           (-> handler
               (wrap-session)  
               (wrap-body)
               (wrap-defaults (assoc api-defaults
                                     :cookies   true
                                     :params    nil
                                     :static    {:resources "public"}))
               (http/run-server {:port port})))
  :stop (when server
          (server :timeout 100)))

(defn -main [& args]
  (mount/start)
  (migratus/migrate {:store :database
                     :db    (:db env)}))


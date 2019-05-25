(ns server.session
  (:require
   [ring.middleware.session.store :as store])
  (:import java.util.UUID))

(defonce atom-store (atom {}))

(deftype MemoryStore []
  store/SessionStore
  (read-session [_ key]
    (@atom-store key))
  (write-session [_ key data]
    (swap! atom-store assoc key data)
    key)
  (delete-session [_ key]
    (swap! atom-store dissoc key)
    nil))

(defonce session-store (MemoryStore.))

(defn update-session [response session-id]
  (when (contains? response :session)
    (store/write-session session-store session-id (:session response))))

;; Ring session handling doesn't appear to work with Graal 
(defn wrap-session [handler]
(fn
  ([request] (let [session-id (get-in request [:headers "cookie"])
                   session    (store/read-session session-store session-id)]
               (let [response (handler (assoc request :session session))]
                 (update-session response session-id)
                 response)))
  ([request respond raise]
   (let [session-id (get-in request [:headers "cookie"])
         session    (store/read-session session-store session-id)]
     (handler (assoc request :session session)
              (fn [response]
                (update-session response session-id)
                (respond response))
              raise)))))

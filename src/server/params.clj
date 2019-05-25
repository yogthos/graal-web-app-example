(ns server.params
  (:require
   [ring.util.codec :as codec])
  (:import java.io.InputStream
           java.net.URLEncoder
           java.net.URLDecoder))

;; URLEncoder requires type hints with Graal
(extend-protocol codec/FormEncodeable
  String
  (form-encode* [unencoded encoding]
    (URLEncoder/encode ^String unencoded ^String encoding)))

;; URLDecoder requires type hints with Graal
;; https://github.com/ring-clojure/ring-codec/blob/b01fcee9ffe35da85eeeb555ebecb24414d7d9a6/src/ring/util/codec.clj#L133

(defn form-decode-str
  "Decode the supplied www-form-urlencoded string using the specified encoding,
  or UTF-8 by default."
  ([encoded]
   (form-decode-str encoded "UTF-8"))
  ([^String encoded ^String encoding]
   (try
     (URLDecoder/decode encoded encoding)
     (catch Exception _ nil))))

(defn form-decode
  [^String encoded]
(if-not (.contains encoded "=")
  (form-decode-str encoded)
  (reduce
   (fn [m ^String param]
     (if-let [[k v] (.split param "=" 2)]
       (codec/assoc-conj m (keyword (form-decode-str k)) (form-decode-str (or v "")))
       m))
   {}
   (.split encoded "&"))))

(defn wrap-body [handler]
  (fn [request]
    (handler
     (if (instance? InputStream (:body request))
       (update request :body #(some-> % slurp not-empty form-decode))
       request))))
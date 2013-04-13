(ns titan1.web
    (:use compojure.core)
    (:use ring.middleware.json)
    (:use ring.util.response))

(defroutes handler
  (GET "/" []
    (response {"hello" "carl"}))

  (PUT "/" [name]
    (response {"hello" name})))

(def app
    (-> handler
          wrap-json-response
          wrap-json-params))


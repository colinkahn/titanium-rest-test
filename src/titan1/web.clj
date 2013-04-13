(ns titan1.web
    (:use compojure.core)
    (:use ring.middleware.json)
    (:use ring.util.response)
    (:require [titan1.core :as t]))

(defroutes handler
  (GET "/" []
    (response {"hello" "carl"}))

  (GET "/:name/coworkers" [name]
    (response
      (t/get-coworkers name)))

  (PUT "/" {{name :name} :body} 
      (t/add-coworker! "Colin" name)
      (response {"added" name}))

  (PUT "/test" {body :body} 
    (println body)
    (response body))
)

(def app
    (-> handler
          wrap-json-response
          (wrap-json-body {:keywords? true})))

(t/make-test-graph)

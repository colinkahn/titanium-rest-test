(ns titan1.web
    (:use compojure.core)
    (:use ring.middleware.json)
    (:use ring.util.response)
    (:require [titan1.core :as t]))

(defroutes handler
  (GET "/" []
    (response {:hello "World"}))

  (GET "/workers" []
    (response 
      {:workers (t/get-workers)}
    ))

  (GET "/:id/coworkers" [id]
    (response
      (t/get-coworkers id)))

  (PUT "/" {{ids :ids} :body} 
      (apply t/make-coworkers! (t/find-by-id ids))
      (response {"added" ids}))
)

(def app
    (-> handler
          wrap-json-response
          (wrap-json-body {:keywords? true})))

(t/make-test-graph)

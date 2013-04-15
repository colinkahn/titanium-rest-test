(ns titan1.core
  [:require 
          [clojurewerkz.titanium.graph :as tg]
          [clojurewerkz.titanium.edges :as te]
          [clojurewerkz.titanium.vertices :as tv]
          [clojurewerkz.titanium.types :as tt]
          [clojurewerkz.titanium.query :as tq]]
  [:require [ogre.tinkergraph :as g]]
  [:require [ogre.core :as q]])

(defn nice-id [m]
  (clojure.set/rename-keys m {:__id__ :id}))

(defn maps-with-ids! [p]
  (map #(-> % tv/to-map nice-id) (q/into-vec! p)))

(defn get-coworkers [id]
  (tg/transact!
    (q/query (tv/find-by-id id) 
        (q/--> :coworker) maps-with-ids!)))

(defn make-coworkers! [& w]
  (for [v1 w v2 w]
    (if (not= v1 v2) 
      (te/connect! v1 :coworker v2))))

(defn make-test-graph
  "Make a temporary graph in memory"
  []
  (tg/open-in-memory-graph)
  (tg/transact!
    ; TODO: update to alpha4-SNAPSHOT and change this
    ; will become `create-property-key`
    (tt/create-vertex-key
      :name String {:indexed true :unique-direction :out})
    (let [ck (tv/create! {:name "Colin"})
          nc (tv/create! {:name "Nathaniel"})
          rg (tv/create! {:name "Ryan"})]
      (make-coworkers! ck rg nc)
      (te/connect! ck :employee nc)
      (te/connect! rg :employee nc)
      (te/connect! nc :boss ck)
      (te/connect! nc :boss rg)
    )))

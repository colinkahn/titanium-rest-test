(ns titan1.core
  [:require 
          [clojurewerkz.titanium.graph :as tg]
          [clojurewerkz.titanium.edges :as te]
          [clojurewerkz.titanium.vertices :as tv]
          [clojurewerkz.titanium.types :as tt]
          [clojurewerkz.titanium.query :as tq]]
  [:require [ogre.tinkergraph :as g]]
  [:require [ogre.core :as q]])

(defmacro oq [[v] & body]
  `(binding [g/*graph* (tg/get-graph)] 
    (q/query ~v 
      ~@body)))

(defn f-kv 
  "Simplies finding a vertex"
  [k v]
  (tg/transact!
    (first 
      (tv/find-by-kv k v))))

(defn make-test-graph 
  "Make a temporary graph in memory"
  []
  (tg/open-in-memory-graph)
  (tg/transact! 
    (tt/create-vertex-key
      ; TODO: update to alpha4-SNAPSHOT and change this 
      ; will become `create-property-key`
      :name String {:indexed true :unique-direction :out})
    (def v1 (tv/create! {:name "Colin"}))
    (def v2 (tv/create! {:name "Nathaniel"}))
    (def v3 (tv/create! {:name "Ryan"}))
    (te/connect! v1 :employee v2)
    (te/connect! v3 :employee v2)
    (te/connect! v1 :coworker v3)
    (te/connect! v2 :boss v1)
    (te/connect! v2 :boss v3)
    )) 

(defn get-coworkers [name]
  (oq [(f-kv :name name)]
      (q/--> :coworker)
      q/map
      q/all-into-maps!))

(defn add-coworker! [existing new]
  (let [v1 (f-kv :name existing)]
    (tg/transact!
      (def v2 (tv/create! {:name new}))
      (te/connect! v1 :coworker v2))))

(defn make-real-graph 
  "Make a graph"
  [dir]
  (tg/open dir)
  (tg/transact! 
    (tt/create-vertex-key
      ; TODO: update to alpha4-SNAPSHOT and change this 
      ; will become `create-property-key`
      :name String {:indexed true})
    (def v1 (tv/create! {:name "Colin"}))
    (def v2 (tv/create! {:name "Nathaniel"}))
    (def v3 (tv/create! {:name "Ryan"}))
    (te/connect! v1 :employee v2)
    (te/connect! v3 :employee v2)
    (te/connect! v1 :coworker v3)
    (te/connect! v2 :boss v1)
    (te/connect! v2 :boss v3)
    )) 

(ns titan1.core
  [:require 
          [clojurewerkz.titanium.graph :as tg]
          [clojurewerkz.titanium.edges :as te]
          [clojurewerkz.titanium.vertices :as tv]
          [clojurewerkz.titanium.types :as tt]
          [clojurewerkz.titanium.query :as tq]
          [potemkin :as po]
          [ogre.tinkergraph :as g]
          [ogre.core :as q]])

(po/import-macro tg/transact!)
(po/import-fn tv/find-by-id)
(po/import-fn tv/find-by-kv)
(po/import-fn tv/create!)

(defn nice-id [m]
  (clojure.set/rename-keys m {:__id__ :id}))

(defn json-friendly! [v]
    (doall (map #(-> % tv/to-map nice-id) v)))

(defn get-workers []
  (tg/transact!
    (json-friendly! (tv/find-by-kv :type "worker"))))

(defn maps-with-ids! [p]
  (-> p q/into-vec! json-friendly!))

(defn get-coworkers [id]
  (tg/transact!
    (q/query (tv/find-by-id id) 
        (q/--> :coworker) maps-with-ids!)))

(defn make-coworkers! [& w]
  (for [v1 w v2 w]
    (if (not= v1 v2) 
      (te/connect! v1 :coworker v2))))

(def graph-config
  {"storage.backend" "cassandra"
   "storage.hostname" "localhost"})

(defn make-test-graph
  "Make a temporary graph in memory"
  []
  (tg/open graph-config)
  (tg/transact!
    (tt/defkey-once
      :type String {:indexed-vertex? true :unique-direction :out})
    (let [ck (tv/create! {:name "Colin" :type "worker"})
          nc (tv/create! {:name "Nathaniel" :type "worker"})
          rg (tv/create! {:name "Ryan" :type "worker"})]
      (te/connect! ck :employee nc)
      (te/connect! rg :employee nc)
      (te/connect! nc :boss ck)
      (te/connect! nc :boss rg))))

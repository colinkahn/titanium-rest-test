(defproject titan1 "0.1.0"
  :plugins [[lein-ring "0.8.3"]]
  :ring {:handler titan1.web/app}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [clojurewerkz/titanium "1.0.0-alpha3"]
                 [compojure "1.1.5"]
                 [ring/ring-json "0.2.0"]
                 ])

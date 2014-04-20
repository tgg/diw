(ns diw.core
  (:gen-class))

(load "protocols")
(load "concepts")
(load "graph")
(load "simuli")
(load "triggers")

(defn simu [src svc env]
  (println (str "[" src "]") "Simulus on" (str svc)))

(defn seen? [dest env]
  (do
    (println (str "[" dest "]" ) "Saw event")
    true))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (check (Link. #'simu 'source 'service 'destination #'seen?) {}))

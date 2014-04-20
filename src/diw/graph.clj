(in-ns 'diw.core)

(use '[clojure.set :only (difference)])

;; diw concepts map to the following graph theory elements:
;; - a link is an "edge"
;; - a service is a "vertex"
;; - a component is a logical set of "vertex" (e.g. same shape)
;; - a system is an oriented "graph"
;;
;; Data structure chosen to represent the graph akin to an adjacency list
;; { :component1 [link1->2_1 link1->2_2 link1->3]
;;   :component2 [link2->3] .. }
;;
;; - cannot be a set because nothing prevents two components from having two
;;   vertices between them
;; - read discussion in Cormen about maps

;; TODO:
;; - given an oriented graph, find:
;;   - its entry points (i.e. vertices with outgoing edges but no ingoing edge)
;;   - the sub-graphs
;;
;; (def ls->d (Link. #'simu :source 'service :destination #'seen?))
;; (def ld->e (Link. #'simu :destination 'service2 :end #'seen?))
;; (def ls->e (Link. #'simu :source 'service3 :end #'seen?))
;; (def g {:source [ls->d ls->e] :destination [ld->e]})
;; (clojure.set/difference (into #{} (map #(.src %) (flatten (vals g)))) (into #{} (map #(.dest %) (flatten (vals g)))))

(defn source-node [graph]
  (let [edges (flatten (vals graph))
        sources (set (map #(.src %) edges))
        destinations (set (map #(.src %) edges))]
    (difference sources destinations)))

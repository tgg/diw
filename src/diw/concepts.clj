(in-ns 'diw.core)

(defn- depends [component]
  (:depends component))

(defn- provides [component]
  (:provides component))

(defn- filter-kind [kind ns]
  (filter #(= (:kind (meta (var-get %))) kind)
          (vals (ns-publics ns))))

(defn- all-with-kind [kind]
  (reduce concat
          (map (fn [ns] (filter-kind kind ns)) (all-ns))))

(defn all-components []
  (all-with-kind 'diw/component))

(defmacro defcomponent
  ([name]
     `(defcomponent ~name ~(str name)))
  ([name ^String comment & opts]
     `(def ~name
        (with-meta
          ~(merge-with
            (fn [a b] a)
            {:name (str name)
             :comment comment}
            (when opts (apply hash-map opts)))
          {:kind 'component}))))

;; TODO: keyword destructuring
(defn- -deflink [source target simulus service expectation]
  (Link. simulus source service target expectation))

(comment
  (defcomponent ds "Data Services"
    :provides {:product (ws-resource "/Product")
               :marketdata (ws-resource "/MarketData")})

  (defcomponent tm "Trade Manager"
    :depends  [ds :product]
    :provides {:trade (ws-resource "/Trade")})

  (defcomponent dispatcher "Event Dispatcher")

  (defcomponent rm "Report Manager"
    :depends [ds tm]
    :provides {:rt (jms-resource "Q.IN")
               :eod (ws-resource "/EODReport")})

  (deflink dispatcher rm
    :simulus #'gen-input
    :on :rt
    :triggers #'expectation)

  (defsystem stack
    :components [ds tm dispatcher rm]
    :flavors [:dev :int :pre :prod])

  (defflavor stack :dev
    {:ds {:product {:url "http://localhost:1234/DS"}
         :marketdata {:url "http://localhost:1234/DS"}}
     :tm {:trade {:url "http://localhost/TM"}}
     :rm {:rt {:url "jms://queue:4567"}
         :eod {:url "http://localhost/RM"}}})
)

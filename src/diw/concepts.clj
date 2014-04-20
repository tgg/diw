(in-ns 'diw.core)

(defn- depends [component]
  (or (:depends component) []))

(defn- provides [component]
  (or (:provides component) {}))

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

;; protocols.
(defn- service-env [component service flavor]
  (service (component flavor)))

(defprotocol ICheckable
  "Check wether a concept works as expected"
  (check [concept env] "Checks wether concept works as it should on env"))

(deftype JMSResource [name]
  ICheckable
  (check [this env]
    (do
      (println "Connecting to queue:" name "on:" (:url env))
      true)))

(deftype WSResource [component service suffix]
  ICheckable
  (check [this env]
    (let [url (str (:url (service-env component service env)) suffix)]
      (println "Connecting to WebService at: " url)
      (try
        true
        (catch Throwable _ false)))))

(deftype Link [src dest f svc p]
  ICheckable
  (check [this env]
    (try
      (do
        (println "Checking link from" (str src) "to" (str dest) "via" (str svc))
        (f src svc env)
        (p dest env))
      (catch Throwable _ false))))

;;
;; WS needs a check time: the environment it is attached to.
;; WS needs at creation time the service and component it is attached to.
;;
;; use keywords? symbols? namespace? Inspire from leiningen
;;
;; Invocation could rely on a templating mechanism e.g. Mustache or enlive
;;  http://edtsech.github.io/2012/09/clojure-templating.html
;;  https://github.com/yogthos/Selmer
;; Checks: is-up? version?
;;
;; logging: https://github.com/ptaoussanis/timbre
;; https://github.com/clojure-cookbook/clojure-cookbook
;; http://http-kit.org/
;;
;; Wrong: expansion for service and component: how to do ws-resource?

(defcomponent tm "Trade Manager"
    :dependencies  [
                    [ds/product] ;; product service of component ds; any version
                    [em :version ">=0.0.1"] ;; all services of component em; version >= 0.0.1
                   ]
    :services [[trade-query (ws-resource "/Trade")]
               [trade-push (jms-resource "")]])

(with-local-vars [component "ttt"] (str @component "  bb"))

(defmacro ws-resource [suffix]
  `(WSResource. (@component @service ~suffix)))

;; TODO: keyword destructuring
(defn- -deflink [source target simulus service expectation]
  (Link. simulus source service target expectation))

;; What's wrong here: inconsistency between keyword, variable and symbol
(comment
  (defcomponent ds "Data Services"
    :provides {:product (ws-resource "/Product")
               :marketdata (ws-resource "/MarketData")})

  (defcomponent tm "Trade Manager"
    :depends  [{ds :product}]
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

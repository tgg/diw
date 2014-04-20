(in-ns 'diw.core)

(require '[clj-soap.core :as soap])

(defn- service-env [component service flavor]
  (service (component flavor)))

(defprotocol ICheckable
  "Check wether a concept works as expected"
  (check [concept env] "Checks wether concept works as it should on env"))

(deftype WSResource [component service suffix]
  ICheckable
  (check [this env]
    (let [url (str (:url (service-env component service env)) suffix)]
      (println "Connecting to WebService at: " url)
      (try
        (soap/client-fn url) true
        (catch Throwable _ false)))))

(deftype JMSResource [name]
  ICheckable
  (check [this env]
    (do
      (println "Connecting to queue:" name "on:" (:url env))
      true)))

(deftype Link [src dest f svc p]
  ICheckable
  (check [this env]
    (try
      (do
        (println "Checking link from" (str src) "to" (str dest) "via" (str svc))
        (f src svc env)
        (p dest env))
      (catch Throwable _ false))))

(comment
  (defn simu [src svc env]
    (println (str "[" src "]") "Simulus on" (str svc)))

  (defn seen? [dest env]
    (do
      (println (str "[" dest "]" ) "Saw event")
      true))

  (check (Link. 'source 'destination #'simu 'service #'seen?) {})

  (def dev
    {:ds {:product {:url "http://localhost:1234/DS"}
         :marketdata {:url "http://localhost:1234/DS"}}
     :tm {:trade {:url "http://localhost/TM"}}
     :rm {:rt {:url "jms://queue:4567"}
          :eod {:url "http://localhost/RM"}}})

  (check (WSResource. :ds :marketdata "/Truc") dev)
)

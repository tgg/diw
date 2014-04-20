(ns diw.svc
  (:gen-class))

(import '(org.apache.cxf.jaxws.endpoint.dynamic JaxWsDynamicClientFactory))
(def f (JaxWsDynamicClientFactory/newInstance))
(def c (.  f createClient "http://www.webservicex.net/CurrencyConvertor.asmx?WSDL"))
(. c invoke "ConversionRate" (object-array ["USD" "EUR"]))


(bean (.get (.. c2 getEndpoint getService getServiceInfos) 0))


(map #(.getBindings %) (.. c getEndpoint getService getServiceInfos))

cf SOAPHelper et ComplexClient dans src/apache-cxf-2.7.1-src/distribution/src/main/release/samples/wsdl_first_dynamic_client/src/main/java/demo/hw/client/
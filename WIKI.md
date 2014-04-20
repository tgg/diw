## What is it? ##

Diw (Does It Work) is a Clojure based library allowing you to:

 - define a complex system; then
 - test it


## Concepts ##

 - A **Resource**:
   - can be used by components;
   - can be checked for availability
 - A **Link**:
   - is oriented from a source component to a target component;
   - can be traversed using a resource;
   - can be checked by substituting the source component with diw and ensuring that expected effect can be observed in the target component
 - A **Service**:
   - is provided by a component thru a resource;
   - can be checked for availability by connection and invocation to the remote resource
 - A **Component**:
   - depends on other components and services;
   - provides a set of services (possibly empty);
   - can be checked by checking every dependency and provided service.
 - A **System**:
   - a set of components
   - can be checked by checking its components and links between them;
 - A **Flavor**:
   - contains paramterization of the system for a given environment

## Example ##

Below is the definition of a complete system:

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
      [ds {:product {:url "http://localhost:1234/DS"}
           :marketdata {:url "http://localhost:1234/DS"}}
       tm {:trade {:url "http://localhost/TM"}}
       rm {:rt {:url "jms://queue:4567"}
           :eod {:url "http://localhost/RM"}}])

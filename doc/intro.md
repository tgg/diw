# Introduction to diw

TODO: write [great documentation](http://jacobian.org/writing/great-documentation/what-to-write/)

## Concepts

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

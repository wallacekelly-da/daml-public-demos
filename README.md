# Daml Public Demos by Wallace Kelly

How to create unique ids, given these limitations?

* As of Daml 2.8, there is no GUID generation.
* `getTime` returns with `seconds` precision, not `msecs` precision.

Here is one implementation.

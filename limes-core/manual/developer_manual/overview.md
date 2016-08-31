# Overview
![LIMES workflow](../images/uml2.png "fig:")


As you can see from the flow diagram above, LIMES 1.0 mainly consists of 2 separate execution pipelines:

- `LSPipeline` executes a given *Link Specification* on input KB `S` and `T`, resulting in a `Mapping`
- `MLPipeline` uses *Machine Learning* algorithms on KB `S` and `T`, also resulting in a `Mapping`
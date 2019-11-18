# LSPipeline

The `LSPipeline` consists of three basic building blocks: `Rewriter`, `Planner` and `ExecutionEngine`. 

- A `Rewriter` aims to simplify a given LS by removing potential redundancy, that eventually speeds up its execution.
- A `Planner` generates the plan of an input LS. For an *atomic LS*, it generates a simple `Plan` that consists of a *RUN* command. For a *complex LS*, it determines which *atomic LS* should be executed first, how to process intermediary results and identifies dependencies between atomic LSs. For example, a `Planner` can decide to first run some *atomic LS* and then filter the results using another *atomic LS* instead of running it independently and merging the results.
- An `ExecutionEngine` is responsible for executing the `Plan` of an input LS. It takes as input a LS and a `Planner`, it executes  the `Plan` and returns the set of links as a `AMapping`.
- All packages have the same hierarchy and they include the following set of classes:
	- An interface i.e. `IExecutionEngine` that includes all basic functions that an engine must implement.
	- An abstract class i.e. `ExecutionEngine` that implements the `IExecutionEngine` interface and must be extended by all engine classes.
	- A factory class i.e. `ExecutionEngineFactory` that returns an object of an engine given an input engine name. 


This code example, taken from the `LSPipeline` class, demonstrates the usage pattern:

	Rewriter rw = RewriterFactory.getRewriter(rewriterType);
	LinkSpecification rwLs = rw.rewrite(ls);
	// Planning execution of the LS
	Planner planner = ExecutionPlannerFactory.getPlanner(executionPlannerType, sourceCache, targetCache);
	// Execute the ExecutionPlan obtained from the LS
	ExecutionEngine engine = ExecutionEngineFactory.getEngine(executionEngineType, sourceCache, targetCache, sourceVar, targetVar);
	return engine.execute(rwLs, planner);


- If you want to create a new engine or planner or rewriter class, you must follow a set of steps:

	* The name of the new class must end with the word "Engine" or "Planner" or "Rewriter".
	* The new class must extend the ExecutionEngine or Planner or Rewriter abstract class and as a result implement all overriden methods from the corresponding interface. No unsupported functions are allowed.
	* Go to the corresponding Factory class of the package and include a label for the new class at the enum class of allowed names. Then, create a `public static final` field of String type using the same name of engine/planner/rewriter as the one used in the enum class i.e. `public static final String HELIOS = "helios";` and finally then add the choice of returing this engine/planner/rewriter at the i.e. `getPlannerType` and `getPlanner` functions following the pattern used for other engines/planners/rewriters.

# Execution (optional)
Three LIMES execution parameters could be set here:
* `REWRITER`: LIMES 1.0.0 implements the `DEFAULT` rewriter.
* `PLANNER`: the user can choose between:
	* `CANONICAL`: It generates an immutable plan in a static manner.
	* `HELIOS`: It generates an immutable plan in a dynamic manner.
	* `DYNAMIC`: It generates an mutable plan in a dynamic manner.
	* `DEFAULT`: same as `CANONICAL`.
* `ENGINE`: the user can choose between:
	* `SIMPLE`: It executes each independent part of the plan sequentially.
	* `DEFAULT`: same as `SIMPLE`.

if not set, the `DEFAULT` value for each parameter will used be will

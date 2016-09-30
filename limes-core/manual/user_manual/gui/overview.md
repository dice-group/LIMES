#Overview of the GUI elements
<img src="../../images/LimesOverview.png" width="700" alt ="Overview">
## Menu Bar 
The menu bar contains three drop-down menus:
* *File*
* *Layout*
* *Learn*

### File
The file drop-down menu gives the possibility to:
* *New*: Create a new configuration 
* *Load Config*: Load a [configuration file](../configuration_file/index.md). Since machine learning is implemented seperately 
in the GUI, only configurations with metric expressions are accepted. If you load a configuration with machine learning 
instructions, as much information as possible will be saved, but you have to run the machine learning through the GUI.
* *Save Config*: Save a configuration to a file (only possible, after loading a configuration or creating a new configuration)
* *Exit*
### Layout
Handles the layout of the current [metric](../configuration_file/metric/index.md) 
* *Refresh Layout*: Rearranges the nodes of the metric in a tree-like structure
* *Delete Graph*: Delete the current metric leaving only an output node

### Learn
All the machine learning functionality of the GUI can be accessed through this drop-down menu:
* *[Active Learning](user_manual/gui/machine_learning/active_learning.md)*
* *[Batch Learning](user_manual/gui/machine_learning/batch_learning.md)*
* *[Unsupervised Learning](user_manual/gui/machine_learning/unsupervised_learning.md)*

These features are only available when a configuration is loaded

### Toolbox
<img src="../../images/ToolBox.png" height="400" alt ="ToolBox">

On the left you can find the toolbox containing everything you need to build your own metric after you loaded/made a configuration
* *Source/Target Properties*: The properties you want to link (if you did not load or create a configuration these are empty)
* *Measures*: All the [measures](../configuration_file/metric/measures/index.md) you can use to link properties
* *Operators*: All the operators you can use to combine measures

### Metric Builder
<img src="../../images/MetricBuilder.png" width="600" alt ="MetricBuilder">

Here you can link the various nodes to create the metric you want
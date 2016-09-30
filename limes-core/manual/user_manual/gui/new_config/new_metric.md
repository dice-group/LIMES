#How to create a new Linkspecification
## Get the nodes you need into the metric builder
Clicking on elements from the toolbox makes nodes appear in the metric builder. Click on both `rdfs:label` properties you have.
Now we need a measure to check the similarity between those properties. Let's choose `cosine` for example.

## Start Linking
Right clicking the nodes creates a small context menu for the node. If you click *Link To* you can
link the node with an appropriate other node. The following links are permitted:
* property -> measure
* measure -> output
* measure -> operator
* operator -> output

Also operator and measure need two nodes that link to them. The context menu also gives you the possibility to *Close* it or *Delete*
the node. If you want to delete a link, just right-click the arrow. 
Let's link our properties with `cosine` and the measure with `output`.

## Define Thresholds
If you want you can define a [Acceptance Threshold](../../configuration_file/acceptance.md) and [Verification Threshold](../../configuration_file/review.md) 
(specifying files is not yet implemented here). 

## Running your Linkspecification
If you followed the steps, your Linkspecification should look something like this:

<img src="../../../images/BuildMetric.png" width="600" alt ="Finished Metric">

If you want to *Run* it, just click on the button in the bottom right corner.

## Results
After the progress popup vanished you should see your results in a new window.

<img src="../../../images/ResultView.png" width="800" ="Results">

In the top left you have the possibility to save them into a file. The relation between them will be defined as `owl:sameAs`.


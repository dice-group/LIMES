#Machine Learning in the GUI
Since finding a good metric expression can be hard, we also have implemented machine learning algorithms in the GUI. There are
three different types of algorithms you can use:
* Active Learning
* Batch Learning
* Unsupervised Learning

To use any of these algorithms you have to either [create a new config](new_config/create_new_config.md) or load one from file.
In the menu bar click on *Learn* and choose the type you want to use. A new window will pop up. In the top left corner you will
find a drop-down menu, showing you which algorithms implement the chosen learning type. After you click on your desired algorithm,
the window will fill with elements you can use the set the parameters.

<img src="../../images/MachineLearning.png" width="600" alt ="overview of machine learning window">

##Active Learning
If you are happy with the parameters you must click on *Learn* in the bottom right corner. After the progress popup vanishes you
will see a new window, where the algorithm wants you to label link candidates as matches or non-matches.

<img src="../../images/activelearning.png" width="700" alt ="active learning window asking user to label examples">

You can click on *Learn* again and another iteration starts. If you don't want another iteration, you can click on *Get Results*
and a new view with results will pop up. This time you also have the possibility to *Save Linkspecification* in the bottom left corner.
This will put the metric to the metric builder and you can save this configuration if you want.

##Batch Learning
This learning type only takes one iteration and you have to provide a file containing the training mapping. 

<img src="../../images/BatchLearningInput.png" width="400" alt ="batch learning window asking for a training mapping file">

The file can be either CSV or some kind of RDF. For CSV, the first line contains the properties on which you want to match, and
the following lines the matched properties of the instance, that are matches. For example:
```
id1,id2
http://www.okkam.org/oaie/person1-Person2190,http://www.okkam.org/oaie/person2-Person2191
```


If you use RDF a mapping has `owl:sameAs` as predicate. For example:
```
<http://linkedgeodata.org/triplify/node3635863841> <http://www.w3.org/2002/07/owl#sameAs> <http://linkedgeodata.org/triplify/node3635863841> .
``` 

Of course the more training data you provide the better the algorithm can learn. After you click on *Save* the learning will start.

##Unsupervised Learning
This is the type that needs the least effort from the user. You just click on *Learn* and after the algorithm is finished, 
the results will be presented.
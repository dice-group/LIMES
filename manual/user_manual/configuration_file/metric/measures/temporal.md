# Temporal Measures

The temporal relations between event resources can be found by using the following relations:

* `Tmp_Concurrent`: given a source and a target KB, `Tmp_Concurrent` links the source and the target events that have the same begin date and were produced by the same machine. For example: Tmp_Concurrent(x.beginDate1|machine1,y.beginDate2|machine2)|1.0 
* `Tmp_Predecessor`: given a source and a target KB, `Tmp_Predecessor` links the source events to the set of target events that happen exactly before them. For example: Tmp_Predecessor(x.beginDate1,y.beginDate2)|1.0. If the `Tmp_Predecessor` measure is used in a complex LS, the `CANONICAL` planner should be used. 
* `Tmp_Successor`: given a source and a target KB, `Tmp_Successor` links the source events to the set of target events that happen exactly after them. For example: Tmp_Successor(x.beginDate1,y.beginDate2)|1.0. If the `Tmp_Successor` measure is used in a complex LS, the `CANONICAL` planner should be used. 



Allen's temporal relations (https://en.wikipedia.org/wiki/Allen's_interval_algebra):

* `Tmp_After`
* `Tmp_Before`
* `Tmp_During`
* `Tmp_During_Reverse`
* `Tmp_Equals`
* `Tmp_Finishes`
* `Tmp_Finishes`
* `Tmp_Is_Finished_By`
* `Tmp_Is_Met_By`
* `Tmp_Is_Overlapped_By`
* `Tmp_Is_Started_By`
* `Tmp_Meets`
* `Tmp_Overlaps`
* `Tmp_Starts`


Example of atomic LS that consists of the temporal measure  `Tmp_Finishes` and a threshold `theta = 1.0`:

`Tmp_Finishes(x.beginDate1|endDate1, y.beginDate2|endDate2) | 0.8`

where `beginDate1` and `beginDate2` are properties of the source and target KB reps. whose values indicate the begin of a temporal event instance and `endDate1` and `endDate2` are properties of the source and target KB reps. whose values indicate the end of a temporal event instance. Both begin and end properties for both source and target MUST be included in an atomic LS whose measure is temporal. Also, the acceptable values for all properties are in the format: `2015-04-22T11:29:51+02:00`.

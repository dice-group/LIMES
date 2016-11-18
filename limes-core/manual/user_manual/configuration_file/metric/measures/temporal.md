# Temporal Measures

The temporal relations between event resources can be found by using the following relations:

* `Tmp_Concurrent`
* `Tmp_Predecessor`
* `Tmp_Successor`
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

where `beginDate1` and `beginDate2` are properties of the source and target KB reps. whose values that indicate the begin of a temporal event instance and `endDate1` and `endDate2` are properties of the source and target KB reps. whose values indicate the end of a temporal event instance. Both begin and end properties for both source and target MUST be included in an atomic LS whose measure is temporal. Also, the acceptable values for all properties are in the format: `2015-04-22T11:29:51+02:00`.

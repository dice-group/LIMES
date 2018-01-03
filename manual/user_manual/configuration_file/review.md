# Review Condition
Setting the condition upon which links must be reviewed manually is very similar to setting the acceptance condition as shown below.

    <REVIEW>
        <THRESHOLD>0.95</THRESHOLD>
        <FILE>reviewme.nt</FILE>
        <RELATION>owl:sameAs</RELATION>
    </REVIEW>

All instances that have a similarity between the threshold set in `REVIEW` (0.95 in our example) and the threshold set in `ACCEPTANCE` (0.98 in our example) will be written in the review file and linked via the relation set in `REVIEW`. 
 
The LIMES configuration file should be concluded with `</LIMES>`


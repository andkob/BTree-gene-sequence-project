# Team 06

# Team Members

Last Name       | First Name      | GitHub User Name
--------------- | --------------- | --------------------
Kobus           | Andrew          | andkob
Wargo           | Damon           | Damon-W-BSU
Tolman          | Caleb           | calebtolman

# Test Results
How many of the dumpfiles matched (using the check-dumpfiles.sh script)?

When running the following commands: \
sh create-btrees test0.gbk \
sh check-dumpfiles.sh test0.gbk \
100% of the dumpfiles matched

When running the same commands with test5.gbk, 

How many of the query files results matched (using the check-queries.sh script)?

# Cache Performance Results
| gbk file | degree | sequence length | cache | cache size | createBTree run time | searchBTree run time |
| -------- | ------ | --------------- | ----- | ---------- | -------------------- | -------------------- |
| test5.gbk|  101   |     20          |  no   |    0       |        29.52s        |        29.52s        |
| test5.gbk|  101   |     20          |  yes  |    100     |        12.56s        |        12.56s        |
| test5.gbk|  101   |     20          |  yes  |    500     |        10.22s        |        10.22s        |
| test5.gbk|  101   |     20          |  yes  |    5000    |        5.08s         |        5.08s         |

# AWS Notes
We did not run the code on AWS :(

# Reflection

Provide a reflection by each of the team member (in a separate subsection)

## Reflection (Andrew Kobus)
## Reflection (Damon Wargo)
The parts of this project that I worked on the most were GeneBankSearchDatabase, GeneBankFileReader, GeneBankCreateBtree, and the arguement files associated with the aforementioned files. So, my contributions were largely centered around the programs that helped the BTree as opposed to the actual BTree itself. 

I found the biggest challenge for this project to be the various methods of parsing input, dumping output, and syntax associated with SQL databases. The ouput had to do with GeneBankFileReader, which forced me to solve the issue of a shifting window for sequences that accounted for all possible characters and whitespace. Parsing the command line in any order also proved to be difficult, with there being a myriad of ways that a user could mess up input. SQL wasn't too much of an issue; the main learning curve came from understanding the syntax associated with SQL commands.

Overall, I wish we had had a little bit more guidance with the specific output each class was expected to produce. It was confusing to try and figure out exactly how the shell scripts ran and as a result wee ran out of time before 

## Reflection (Caleb Tolman)

# Additional Notes

GeneBankSearchBTree is functional, but we were not able to configure it to run succesfully with the given shell script


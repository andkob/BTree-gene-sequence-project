# Team 06

# Team Members

Last Name       | First Name      | GitHub User Name
--------------- | --------------- | --------------------
Kobus           | Andrew          | andkob
Wargo           | Damon           | Damon-W-BSU
Tolman          | Caleb           | calebtolman

# Test Results
### How many of the dumpfiles matched (using the check-dumpfiles.sh script)?

When running the following commands: \
sh create-btrees test0.gbk \
sh check-dumpfiles.sh test0.gbk \
100% of the dumpfiles matched

### How many of the query files results matched (using the check-queries.sh script)?

100% of the query file results matched when using test0.gbk

# Cache Performance Results
| gbk file | degree | sequence length | cache | cache size | createBTree run time | searchBTree run time |
| -------- | ------ | --------------- | ----- | ---------- | -------------------- | -------------------- |
| test5.gbk|  101   |     20          |  no   |    0       |         65.942s          |        312ms        |
| test5.gbk|  101   |     20          |  yes  |    100     |         68.194s          |        308ms       |
| test5.gbk|  101   |     20          |  yes  |    500     |         62.671s          |        306ms        |
| test5.gbk|  101   |     20          |  yes  |    5000    |         53.193s          |        316ms         |

# AWS Notes
We did not run the code on AWS :(

# Reflection


## Reflection (Andrew Kobus)
Working on the GeneBank B-Tree project has been a highly educational and somewhat challenging experience. Throughout this project, I have developed a deeper understanding of how B-Trees function, especially in the context of managing large datasets such as genetic information. Initially, the task of parsing gene bank files and correctly inserting sequences into a B-Tree felt daunting; however, implementing the process improved my ability to manage file I/O operations and understand binary search trees more intricately.

One of the most rewarding aspects of this project was implementing the B-Tree insertion and deletion operations. It required careful consideration of tree balance and node splitting, pushing me to think algorithmically and pay attention to efficiency and data integrity. Moreover, adding the debugging functionality and SQL database interaction brought practical insights into how such systems are integrated and used in real-world applications. The challenges I faced, particularly in debugging and optimizing the search and insert functionalities, taught me the importance of thorough testing and incremental development. This project has significantly enhanced my coding skills, problem-solving abilities, and understanding of complex data structures.

## Reflection (Damon Wargo)
The parts of this project that I worked on the most were GeneBankSearchDatabase, GeneBankFileReader, GeneBankCreateBtree, and the arguement files associated with the aforementioned files. So, my contributions were largely centered around the programs that helped the BTree as opposed to the actual BTree itself. 

I found the biggest challenge for this project to be the various methods of parsing input, dumping output, and syntax associated with SQL databases. The ouput had to do with GeneBankFileReader, which forced me to solve the issue of a shifting window for sequences that accounted for all possible characters and whitespace. Parsing the command line in any order also proved to be difficult, with there being a myriad of ways that a user could mess up input. SQL wasn't too much of an issue; the main learning curve came from understanding the syntax associated with SQL commands.

Overall, I wish we had had a little bit more guidance with the specific output each class was expected to produce. It was confusing to try and figure out exactly how the shell scripts ran and as a result wee ran out of time before 

## Reflection (Caleb Tolman)
This coding assignment on creating a GeneBank B-Tree has been a pivotal moment in my computer science education, reinforcing concepts that were previously abstract and encouraging a hands-on approach to learning data structures. The project not only solidified my understanding of B-Trees but also exposed me to practical applications of bioinformatics. Managing to read and extract DNA sequences from a gene bank file and then utilizing these sequences to build a functional B-Tree was a rewarding challenge that bridged the gap between theoretical data structures and real-world applications.

The integration of a caching mechanism and the creation of a dump file for debugging purposes were particularly insightful, showing me the importance of performance optimization in software development. The task of connecting the application to a SQL database for result verification highlighted the interconnectedness of different areas in computer science, such as database management and software development. Overcoming obstacles in these areas greatly improved my debugging skills and my ability to design more robust and efficient software. Reflecting on this experience, I am grateful for the practical skills gained and the direct application of these skills to solve complex problems. This project has certainly increased my confidence in my programming capabilities and my enthusiasm for tackling more complex projects in the future.

# Additional Notes

We have GeneBankCreateBTree configured to create an SQL database as soon as the dumpfile is created, i.e., when the debug level is 1. This means that with larger GBK files like test5.gbk, running GeneBankCreateBTree with debug level 1 will take a massive amount of time due to the reading/writing costs of scanning such large files multiple times with a scanner and loading each of the scanner results into the database.  

We implemented a cache with a hashtable as part of the implementation, but we weren't able to improve our runtimes. We aren't sure if this is an issue with our cache itself, or the way we implemented cache into the DiskRead() and DiskWrite() methods

plz be gentle <3
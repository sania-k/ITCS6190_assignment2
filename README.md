# Assignment 2: Document Similarity using MapReduce

**Name:** Sania Khan

**Student ID:** 801241037

## Approach and Implementation

### Mapper Design
The Mapper reads in an input and seperates the data into key value pairs for the goal of computing document similarity. It takes in the input dataset from `shared-folder/input/data/`, where each line contains a key valye of the document ID and then the words in that document. The mapper tokenizes each line, extracts the document ID, then processes the remaining tokens by removing non-alphabetic characters, converting them to lowercase, and filtering out words shorter than 3 characters. For each valid word, the key-value pair where the key is the word and the value is the document ID. This will allow the reducer to group all document IDs with the same word, which is needed for Jaccard similarity. 

### Reducer Design
The Reducer calculates the Jaccard similarity between pairs of documents based on shared words. It receives input key-value pairs where the key is a word emitted by the mapper and the values are the document IDs in which that word appears. For each word, the reducer iterates over the list of document IDs and updates a mapping from each document to the set of words it contains to make a list of all possible document pairs that share words. Then, the cleanup method goes through each pair and calculates the Jaccard similarity before outputting it such that the key is the document pair and the jaccard similarity is the value. Jaccard Similarity is calculated by going through each document pair, calculating the intersection size from the shared words, computing the union size of the word sets from both documents, and then calculating the Jaccard similarity as the intersection size divided by the union size.

### Overall Data Flow
The data flow starts at the input files, where each line represents a document that starts with a document ID and is followed by its text. Hadoop then splits this input to be processed by the Mapper. THe Mapper takes each line as input, tokenizes it, and outputs key-values that indicate which document contains which words. Hadoop groups all values by key and gives it to the Reducer. The Reducer revieves these pars and combines them by which documents share words. From here, it computes the Jaccard similarity and outputs a line per pair into the output document. 

---

## Setup and Execution

### 1. **Start the Hadoop Cluster**

Run the following command to start the Hadoop cluster:

```bash
docker compose up -d
```

### 2. **Build the Code**

Build the code using Maven:

```bash
mvn clean package
```

### 4. **Copy JAR to Docker Container**

Copy the JAR file to the Hadoop ResourceManager container:

```bash
docker cp target/DocumentSimilarity-0.0.1-SNAPSHOT.jar resourcemanager:/opt/hadoop-3.2.1/share/hadoop/mapreduce/
```

### 5. **Move Dataset to Docker Container**

Copy the datasets to the Hadoop ResourceManager container:

```bash
docker cp shared-folder/input/data/. resourcemanager:/opt/hadoop-3.2.1/share/hadoop/mapreduce/
```

### 6. **Connect to Docker Container**

Access the Hadoop ResourceManager container:

```bash
docker exec -it resourcemanager /bin/bash
```

Navigate to the Hadoop directory:

```bash
cd /opt/hadoop-3.2.1/share/hadoop/mapreduce/
```

### 7. **Set Up HDFS**

Create a folder in HDFS for the input dataset:

```bash
hadoop fs -mkdir -p /input/data
```

Copy the input datasets to the HDFS folder:

```bash
hadoop fs -put ./input*.txt /input/data
```

### 8. **Execute the MapReduce Job**

Run your MapReduce job using the following command: 

```bash
hadoop jar /opt/hadoop-3.2.1/share/hadoop/mapreduce/DocumentSimilarity-0.0.1-SNAPSHOT.jar com.example.controller.DocumentSimilarityDriver /input/data/input_small.txt /output_small
```
For this example I am running MapReduce on imput_small, but the same works for input_med and input_large as long as you change the file name.

### 9. **View the Output**

To view the output of your MapReduce job, use:

```bash
hadoop fs -cat /output_small/*
```

### 10. **Copy Output from HDFS to Local OS**

To copy the output from HDFS to your local machine:

1. Use the following command to copy from HDFS:
    ```bash
    hdfs dfs -get /output_small /opt/hadoop-3.2.1/share/hadoop/mapreduce/
    ```

2. use Docker to copy from the container to your local machine:
   ```bash
   exit 
   ```
    ```bash
    docker cp resourcemanager:/opt/hadoop-3.2.1/share/hadoop/mapreduce/output/ shared-folder/output/
    ```
3. Commit and push to your repo so that we can able to see your output


---

## Challenges and Solutions
My first issue arose because I had run out of free use of codespace, so I had to do everything locally on my computer. Most of it was already setup from previous assignments so there were no issues other than my laptop struggles to handle docker and often crashes. 

For designing the algorithm, I had no idea where to start so I based my code off of homework 4's word counting as a starting point. I read some documentation on how to write hadoop mapreduce functions as well. I had some confusion over Jaccard Simularity algorithm design so looked up stackoverflow and repositories on how other people implemented the logic. From there it was a lot of guessing and checking and debugging with the help of chatGPT.

As per instructions, I took down and rebuilt the docker container with only one datanode to see if there was any difference in performance. I was suprised to see it on average took less time to run the Hadoop job and retried this several times. My conclusion is that the combination of overhread from communicating between three nodes and my relatively small dataset sizes in comparison to what Hadoop is designed to handle makes it seem like it performs better with less nodes.

---
## Sample Input

**Input exerpt from `input_small.txt`**
```
Line1 Chapter 1: Prologue — Three Ways to Survive an Apocalypse
Line2 There are three ways to survive an apocalypse. I have forgotten some of them now, but one thing is certain: you, who are currently reading these words, will survive.
Line3 — Three Ways to Survive an Apocalypse [Complete]
Line4 A web novel platform filled the screen of my old cell phone. I scrolled down, and then up again, as I attempted to refresh the page over and over.
```
## Sample Output

**Output exerpt from `dataset.txt`**
```
"Document1, Document2 Similarity: 0.56"
"Document1, Document3 Similarity: 0.42"
"Document2, Document3 Similarity: 0.50"
```
## Obtained Output: 
`output/small/part-r-00000`:
```
Line44,Line60	Similarity: 0.08
Line44,Line61	Similarity: 0.05
Line1,Line2	Similarity: 0.17
Line44,Line63	Similarity: 0.07
Line4,Line43	Similarity: 0.04
Line33,Line37	Similarity: 0.13
Line4,Line42	Similarity: 0.05
Line33,Line38	Similarity: 0.07
Line4,Line40	Similarity: 0.04
Line33,Line33	Similarity: 0.04
Line33,Line34	Similarity: 0.04
Line33,Line35	Similarity: 0.08
Line33,Line36	Similarity: 0.03
Line1,Line3	Similarity: 0.57
Line1,Line7	Similarity: 0.67
Line22,Line29	Similarity: 0.03
Line22,Line28	Similarity: 0.06
Line22,Line27	Similarity: 0.04
Line44,Line54	Similarity: 0.11
Line4,Line53	Similarity: 0.03
Line4,Line52	Similarity: 0.04
Line4,Line51	Similarity: 0.04
Line4,Line47	Similarity: 0.02
Line4,Line45	Similarity: 0.04
Line44,Line59	Similarity: 0.09
Line22,Line25	Similarity: 0.04
Line22,Line24	Similarity: 0.09
Line22,Line23	Similarity: 0.05
Line22,Line22	Similarity: 0.11
Line22,Line38	Similarity: 0.04
Line22,Line37	Similarity: 0.09
Line4,Line61	Similarity: 0.06
Line44,Line46	Similarity: 0.06
Line33,Line59	Similarity: 0.13
Line4,Line64	Similarity: 0.04
Line44,Line47	Similarity: 0.03
Line4,Line63	Similarity: 0.08
Line4,Line62	Similarity: 0.04
Line44,Line45	Similarity: 0.08
Line6,Line6	Similarity: 0.25
Line4,Line58	Similarity: 0.04
Line33,Line55	Similarity: 0.03
Line33,Line56	Similarity: 0.06
Line4,Line56	Similarity: 0.08
Line4,Line55	Similarity: 0.04
Line33,Line58	Similarity: 0.06
Line33,Line51	Similarity: 0.06
Line33,Line52	Similarity: 0.03
Line33,Line53	Similarity: 0.08
Line4,Line59	Similarity: 0.03
Line33,Line54	Similarity: 0.05
Line22,Line35	Similarity: 0.06
Line22,Line33	Similarity: 0.10
Line33,Line50	Similarity: 0.06
Line22,Line31	Similarity: 0.04
Line33,Line48	Similarity: 0.03
Line33,Line44	Similarity: 0.10
Line33,Line45	Similarity: 0.03
Line33,Line46	Similarity: 0.03
Line33,Line47	Similarity: 0.08
Line33,Line40	Similarity: 0.07
Line33,Line42	Similarity: 0.11
Line33,Line43	Similarity: 0.09
Line22,Line47	Similarity: 0.04
Line22,Line46	Similarity: 0.04
Line22,Line45	Similarity: 0.04
Line22,Line44	Similarity: 0.04
Line22,Line43	Similarity: 0.08
Line22,Line42	Similarity: 0.10
Line22,Line40	Similarity: 0.04
Line14,Line5	Similarity: 0.07
Line11,Line38	Similarity: 0.02
Line14,Line6	Similarity: 0.05
Line11,Line37	Similarity: 0.06
Line14,Line2	Similarity: 0.03
Line14,Line4	Similarity: 0.07
Line57,Line8	Similarity: 0.11
Line11,Line30	Similarity: 0.02
Line19,Line32	Similarity: 0.07
Line11,Line34	Similarity: 0.02
Line14,Line9	Similarity: 0.08
Line11,Line33	Similarity: 0.06
Line11,Line35	Similarity: 0.05
Line11,Line27	Similarity: 0.03
Line11,Line26	Similarity: 0.02
Line11,Line29	Similarity: 0.06
Line11,Line28	Similarity: 0.04
Line19,Line28	Similarity: 0.05
Line11,Line20	Similarity: 0.07
Line11,Line22	Similarity: 0.05
Line11,Line25	Similarity: 0.02
Line11,Line24	Similarity: 0.08
Line11,Line16	Similarity: 0.02
Line11,Line15	Similarity: 0.06
Line11,Line17	Similarity: 0.02
Line11,Line12	Similarity: 0.02
Line11,Line11	Similarity: 0.14
Line11,Line14	Similarity: 0.02
Line11,Line13	Similarity: 0.03
Line35,Line6	Similarity: 0.04
Line35,Line4	Similarity: 0.06
Line35,Line5	Similarity: 0.05
Line38,Line44	Similarity: 0.18
Line38,Line47	Similarity: 0.03
Line49,Line62	Similarity: 0.09
Line1,Line20	Similarity: 0.04
Line10,Line4	Similarity: 0.03
Line1,Line24	Similarity: 0.08
Line10,Line3	Similarity: 0.27
Line11,Line61	Similarity: 0.05
Line10,Line2	Similarity: 0.13
Line11,Line60	Similarity: 0.04
Line48,Line8	Similarity: 0.10
Line17,Line60	Similarity: 0.03
Line11,Line63	Similarity: 0.04
Line11,Line62	Similarity: 0.02
Line17,Line61	Similarity: 0.09
Line10,Line7	Similarity: 0.29
Line10,Line6	Similarity: 0.05
Line17,Line62	Similarity: 0.05
Line11,Line64	Similarity: 0.02
Line17,Line63	Similarity: 0.05
Line17,Line64	Similarity: 0.02
Line10,Line9	Similarity: 0.07
Line38,Line61	Similarity: 0.05
Line11,Line59	Similarity: 0.05
Line38,Line60	Similarity: 0.08
Line38,Line63	Similarity: 0.17
Line1,Line10	Similarity: 0.25
Line11,Line50	Similarity: 0.02
Line56,Line6	Similarity: 0.07
Line1,Line15	Similarity: 0.02
Line11,Line52	Similarity: 0.02
Line11,Line51	Similarity: 0.04
Line11,Line54	Similarity: 0.02
Line1,Line18	Similarity: 0.11
Line11,Line53	Similarity: 0.06
Line11,Line56	Similarity: 0.02
Line11,Line55	Similarity: 0.02
Line11,Line58	Similarity: 0.02
Line49,Line56	Similarity: 0.22
Line43,Line6	Similarity: 0.20
Line43,Line5	Similarity: 0.08
Line43,Line8	Similarity: 0.09
Line49,Line58	Similarity: 0.17
Line38,Line54	Similarity: 0.05
Line38,Line52	Similarity: 0.08
Line38,Line59	Similarity: 0.09
Line38,Line56	Similarity: 0.08
Line49,Line53	Similarity: 0.13
Line11,Line40	Similarity: 0.02
Line11,Line43	Similarity: 0.02
Line11,Line42	Similarity: 0.02
Line11,Line45	Similarity: 0.02
Line11,Line44	Similarity: 0.02
Line11,Line47	Similarity: 0.01
Line22,Line59	Similarity: 0.09
Line61,Line64	Similarity: 0.13
Line61,Line63	Similarity: 0.09
Line61,Line62	Similarity: 0.09
Line17,Line35	Similarity: 0.04
Line17,Line36	Similarity: 0.03
Line17,Line37	Similarity: 0.11
Line17,Line38	Similarity: 0.03
Line28,Line59	Similarity: 0.12
Line28,Line57	Similarity: 0.04
Line51,Line8	Similarity: 0.11
Line28,Line58	Similarity: 0.03
Line51,Line6	Similarity: 0.07
Line22,Line58	Similarity: 0.04
Line28,Line55	Similarity: 0.04
Line28,Line56	Similarity: 0.04
Line22,Line56	Similarity: 0.04
Line28,Line53	Similarity: 0.06
Line22,Line55	Similarity: 0.04
Line28,Line54	Similarity: 0.10
Line22,Line54	Similarity: 0.03
Line17,Line31	Similarity: 0.03
Line28,Line51	Similarity: 0.08
Line28,Line52	Similarity: 0.04
Line22,Line53	Similarity: 0.06
Line22,Line52	Similarity: 0.04
Line17,Line33	Similarity: 0.12
Line22,Line51	Similarity: 0.08
Line28,Line50	Similarity: 0.03
Line2,Line2	Similarity: 0.09
Line2,Line3	Similarity: 0.17
Line55,Line56	Similarity: 0.17
Line2,Line7	Similarity: 0.18
Line55,Line57	Similarity: 0.08
Line55,Line58	Similarity: 0.06
Line22,Line61	Similarity: 0.06
Line17,Line24	Similarity: 0.08
Line22,Line60	Similarity: 0.04
Line17,Line25	Similarity: 0.08
Line17,Line26	Similarity: 0.03
Line17,Line27	Similarity: 0.03
Line33,Line62	Similarity: 0.06
Line17,Line28	Similarity: 0.11
Line5,Line6	Similarity: 0.09
Line33,Line63	Similarity: 0.06
Line17,Line29	Similarity: 0.12
Line33,Line64	Similarity: 0.06
Line55,Line59	Similarity: 0.04
Line28,Line64	Similarity: 0.03
Line33,Line60	Similarity: 0.03
Line33,Line61	Similarity: 0.08
Line28,Line62	Similarity: 0.12
Line17,Line20	Similarity: 0.12
Line22,Line64	Similarity: 0.08
Line28,Line63	Similarity: 0.08
Line22,Line63	Similarity: 0.08
Line17,Line22	Similarity: 0.09
Line17,Line23	Similarity: 0.03
Line22,Line62	Similarity: 0.08
Line28,Line61	Similarity: 0.16
Line55,Line62	Similarity: 0.07
Line55,Line63	Similarity: 0.08
Line55,Line64	Similarity: 0.07
Line52,Line6	Similarity: 0.07
Line50,Line64	Similarity: 0.05
Line52,Line8	Similarity: 0.13
Line50,Line61	Similarity: 0.04
Line55,Line61	Similarity: 0.05
Line17,Line57	Similarity: 0.05
Line17,Line58	Similarity: 0.02
Line17,Line59	Similarity: 0.14
Line28,Line37	Similarity: 0.08
Line28,Line38	Similarity: 0.04
Line28,Line35	Similarity: 0.09
Line1,Line58	Similarity: 0.06
Line28,Line36	Similarity: 0.07
Line28,Line33	Similarity: 0.10
Line28,Line31	Similarity: 0.07
Line17,Line51	Similarity: 0.08
Line17,Line52	Similarity: 0.03
Line28,Line32	Similarity: 0.03
Line17,Line53	Similarity: 0.04
Line17,Line54	Similarity: 0.07
Line17,Line55	Similarity: 0.03
Line17,Line56	Similarity: 0.03
Line50,Line52	Similarity: 0.06
Line50,Line53	Similarity: 0.04
Line50,Line50	Similarity: 0.09
Line50,Line57	Similarity: 0.12
Line17,Line47	Similarity: 0.09
Line50,Line55	Similarity: 0.06
Line28,Line47	Similarity: 0.07
Line28,Line44	Similarity: 0.04
Line28,Line45	Similarity: 0.04
Line17,Line40	Similarity: 0.03
Line28,Line42	Similarity: 0.14
Line28,Line43	Similarity: 0.11
Line28,Line40	Similarity: 0.04
Line17,Line42	Similarity: 0.12
Line17,Line43	Similarity: 0.05
Line17,Line44	Similarity: 0.08
Line17,Line45	Similarity: 0.03
Line46,Line62	Similarity: 0.05
Line35,Line38	Similarity: 0.10
Line35,Line37	Similarity: 0.03
Line35,Line36	Similarity: 0.04
Line35,Line35	Similarity: 0.06
Line2,Line33	Similarity: 0.02
Line2,Line37	Similarity: 0.08
Line2,Line39	Similarity: 0.04
Line46,Line50	Similarity: 0.16
Line46,Line52	Similarity: 0.06
Line46,Line57	Similarity: 0.19
Line24,Line29	Similarity: 0.09
Line13,Line6	Similarity: 0.05
Line24,Line28	Similarity: 0.11
Line2,Line50	Similarity: 0.03
Line46,Line53	Similarity: 0.04
Line13,Line2	Similarity: 0.09
Line46,Line55	Similarity: 0.06
Line13,Line5	Similarity: 0.06
Line2,Line52	Similarity: 0.04
Line46,Line56	Similarity: 0.06
Line13,Line4	Similarity: 0.03
Line2,Line43	Similarity: 0.03
Line28,Line28	Similarity: 0.10
Line28,Line29	Similarity: 0.09
Line2,Line47	Similarity: 0.06
Line2,Line46	Similarity: 0.06
Line24,Line24	Similarity: 0.05
Line24,Line27	Similarity: 0.07
Line2,Line48	Similarity: 0.03
Line24,Line26	Similarity: 0.04
Line13,Line17	Similarity: 0.07
Line32,Line4	Similarity: 0.03
Line57,Line62	Similarity: 0.06
Line17,Line17	Similarity: 0.19
Line13,Line13	Similarity: 0.07
Line13,Line14	Similarity: 0.08
Line13,Line15	Similarity: 0.06
Line13,Line16	Similarity: 0.23
Line39,Line8	Similarity: 0.25
Line2,Line30	Similarity: 0.03
Line2,Line20	Similarity: 0.02
Line2,Line22	Similarity: 0.05
Line2,Line24	Similarity: 0.02
Line2,Line26	Similarity: 0.04
Line34,Line5	Similarity: 0.25
Line13,Line39	Similarity: 0.13
Line60,Line60	Similarity: 0.14
Line37,Line5	Similarity: 0.10
Line37,Line4	Similarity: 0.03
Line37,Line6	Similarity: 0.08
Line11,Line5	Similarity: 0.04
Line24,Line54	Similarity: 0.03
Line24,Line53	Similarity: 0.03
Line11,Line4	Similarity: 0.06
Line24,Line56	Similarity: 0.04
Line24,Line55	Similarity: 0.04
Line11,Line6	Similarity: 0.04
Line13,Line30	Similarity: 0.09
Line24,Line58	Similarity: 0.07
Line24,Line59	Similarity: 0.12
Line13,Line35	Similarity: 0.03
Line13,Line36	Similarity: 0.04
Line13,Line37	Similarity: 0.06
Line24,Line50	Similarity: 0.03
Line13,Line32	Similarity: 0.04
Line24,Line52	Similarity: 0.04
Line13,Line33	Similarity: 0.03
Line11,Line2	Similarity: 0.04
Line24,Line51	Similarity: 0.08
Line13,Line28	Similarity: 0.06
Line13,Line29	Similarity: 0.07
Line57,Line58	Similarity: 0.06
Line24,Line64	Similarity: 0.03
Line35,Line64	Similarity: 0.04
Line13,Line24	Similarity: 0.03
Line35,Line63	Similarity: 0.09
Line35,Line62	Similarity: 0.04
Line13,Line25	Similarity: 0.05
Line35,Line61	Similarity: 0.07
Line13,Line27	Similarity: 0.04
Line35,Line60	Similarity: 0.10
Line24,Line61	Similarity: 0.09
Line13,Line20	Similarity: 0.09
Line24,Line60	Similarity: 0.04
Line13,Line22	Similarity: 0.03
Line24,Line63	Similarity: 0.08
Line24,Line62	Similarity: 0.04
Line46,Line46	Similarity: 0.09
Line46,Line48	Similarity: 0.11
Line54,Line63	Similarity: 0.05
Line43,Line44	Similarity: 0.13
Line2,Line60	Similarity: 0.04
Line54,Line61	Similarity: 0.07
Line43,Line46	Similarity: 0.17
Line2,Line62	Similarity: 0.07
Line54,Line62	Similarity: 0.10
Line43,Line45	Similarity: 0.14
Line43,Line48	Similarity: 0.12
Line2,Line54	Similarity: 0.03
Line13,Line50	Similarity: 0.04
Line2,Line53	Similarity: 0.03
Line24,Line31	Similarity: 0.04
Line35,Line59	Similarity: 0.10
Line43,Line47	Similarity: 0.05
Line2,Line56	Similarity: 0.03
Line13,Line51	Similarity: 0.05
Line35,Line58	Similarity: 0.04
Line13,Line52	Similarity: 0.05
Line24,Line33	Similarity: 0.10
Line35,Line56	Similarity: 0.04
Line2,Line58	Similarity: 0.03
Line2,Line57	Similarity: 0.07
Line35,Line55	Similarity: 0.05
Line24,Line35	Similarity: 0.09
Line35,Line54	Similarity: 0.03
Line24,Line38	Similarity: 0.08
Line2,Line59	Similarity: 0.03
Line24,Line37	Similarity: 0.03
Line35,Line53	Similarity: 0.03
Line35,Line52	Similarity: 0.05
Line13,Line57	Similarity: 0.15
Line13,Line58	Similarity: 0.04
Line35,Line51	Similarity: 0.04
Line13,Line59	Similarity: 0.03
Line13,Line53	Similarity: 0.11
Line13,Line54	Similarity: 0.07
Line13,Line55	Similarity: 0.05
Line13,Line56	Similarity: 0.15
Line43,Line51	Similarity: 0.06
Line43,Line50	Similarity: 0.11
Line54,Line57	Similarity: 0.05
Line54,Line54	Similarity: 0.07
Line43,Line53	Similarity: 0.04
Line43,Line52	Similarity: 0.13
Line43,Line55	Similarity: 0.14
Line43,Line54	Similarity: 0.04
Line43,Line57	Similarity: 0.13
Line43,Line56	Similarity: 0.13
Line43,Line59	Similarity: 0.08
Line24,Line43	Similarity: 0.03
Line43,Line58	Similarity: 0.05
Line24,Line42	Similarity: 0.09
Line24,Line45	Similarity: 0.04
Line13,Line40	Similarity: 0.05
Line35,Line47	Similarity: 0.05
Line24,Line44	Similarity: 0.08
Line35,Line45	Similarity: 0.05
Line24,Line47	Similarity: 0.02
Line35,Line44	Similarity: 0.10
Line35,Line43	Similarity: 0.04
Line35,Line42	Similarity: 0.05
Line54,Line59	Similarity: 0.07
Line13,Line46	Similarity: 0.08
Line35,Line40	Similarity: 0.05
Line13,Line47	Similarity: 0.02
Line13,Line48	Similarity: 0.09
Line13,Line42	Similarity: 0.11
Line13,Line43	Similarity: 0.09
Line13,Line44	Similarity: 0.05
Line13,Line45	Similarity: 0.05
Line24,Line40	Similarity: 0.04
Line30,Line34	Similarity: 0.11
Line30,Line37	Similarity: 0.04
Line30,Line39	Similarity: 0.09
Line41,Line58	Similarity: 0.17
Line41,Line56	Similarity: 0.22
Line41,Line53	Similarity: 0.13
Line32,Line35	Similarity: 0.03
Line30,Line33	Similarity: 0.03
Line30,Line46	Similarity: 0.05
Line30,Line48	Similarity: 0.06
Line41,Line49	Similarity: 0.50
Line30,Line47	Similarity: 0.03
Line32,Line47	Similarity: 0.02
Line41,Line45	Similarity: 0.29
Line15,Line64	Similarity: 0.04
Line13,Line61	Similarity: 0.03
Line13,Line62	Similarity: 0.09
Line15,Line62	Similarity: 0.04
Line30,Line5	Similarity: 0.08
Line15,Line63	Similarity: 0.04
Line13,Line63	Similarity: 0.10
Line15,Line60	Similarity: 0.02
Line15,Line61	Similarity: 0.05
Line30,Line41	Similarity: 0.09
Line30,Line43	Similarity: 0.06
Line13,Line64	Similarity: 0.04
Line10,Line14	Similarity: 0.04
Line10,Line15	Similarity: 0.06
Line10,Line16	Similarity: 0.04
Line10,Line17	Similarity: 0.10
Line30,Line57	Similarity: 0.06
Line52,Line63	Similarity: 0.07
Line52,Line64	Similarity: 0.06
Line52,Line61	Similarity: 0.05
Line52,Line62	Similarity: 0.07
Line15,Line53	Similarity: 0.06
Line15,Line54	Similarity: 0.06
Line15,Line51	Similarity: 0.02
Line16,Line2	Similarity: 0.03
Line30,Line50	Similarity: 0.11
Line15,Line52	Similarity: 0.02
Line30,Line53	Similarity: 0.04
Line15,Line50	Similarity: 0.02
Line16,Line4	Similarity: 0.03
Line16,Line5	Similarity: 0.07
Line16,Line6	Similarity: 0.05
Line15,Line59	Similarity: 0.11
Line10,Line10	Similarity: 0.07
Line15,Line57	Similarity: 0.02
Line10,Line11	Similarity: 0.02
Line15,Line58	Similarity: 0.04
Line15,Line55	Similarity: 0.02
Line15,Line56	Similarity: 0.02
Line10,Line13	Similarity: 0.12
Line10,Line25	Similarity: 0.10
Line10,Line26	Similarity: 0.06
Line10,Line28	Similarity: 0.10
Line10,Line29	Similarity: 0.07
Line52,Line53	Similarity: 0.05
Line52,Line55	Similarity: 0.18
Line15,Line42	Similarity: 0.04
Line15,Line43	Similarity: 0.06
Line15,Line40	Similarity: 0.02
Line52,Line56	Similarity: 0.07
Line52,Line57	Similarity: 0.07
Line52,Line58	Similarity: 0.13
Line52,Line59	Similarity: 0.04
Line21,Line24	Similarity: 0.05
Line10,Line20	Similarity: 0.12
Line15,Line6	Similarity: 0.06
Line15,Line47	Similarity: 0.03
Line10,Line22	Similarity: 0.07
Line10,Line23	Similarity: 0.07
Line15,Line44	Similarity: 0.04
Line15,Line4	Similarity: 0.05
Line15,Line45	Similarity: 0.02
Line15,Line5	Similarity: 0.05
Line10,Line24	Similarity: 0.10
Line63,Line64	Similarity: 0.06
Line15,Line31	Similarity: 0.02
Line15,Line32	Similarity: 0.02
Line15,Line37	Similarity: 0.03
Line15,Line38	Similarity: 0.04
Line15,Line35	Similarity: 0.05
Line15,Line36	Similarity: 0.02
Line15,Line33	Similarity: 0.08
Line17,Line8	Similarity: 0.03
Line17,Line6	Similarity: 0.05
Line17,Line4	Similarity: 0.04
Line15,Line20	Similarity: 0.10
Line17,Line5	Similarity: 0.03
Line17,Line2	Similarity: 0.08
Line15,Line28	Similarity: 0.07
Line15,Line29	Similarity: 0.07
Line15,Line27	Similarity: 0.04
Line15,Line24	Similarity: 0.13
Line15,Line25	Similarity: 0.04
Line15,Line22	Similarity: 0.05
Line15,Line23	Similarity: 0.02
Line15,Line17	Similarity: 0.07
Line15,Line18	Similarity: 0.02
Line15,Line15	Similarity: 0.12
Line15,Line16	Similarity: 0.04
Line48,Line64	Similarity: 0.06
Line48,Line62	Similarity: 0.06
Line48,Line61	Similarity: 0.04
Line37,Line40	Similarity: 0.04
Line37,Line43	Similarity: 0.07
Line37,Line42	Similarity: 0.09
Line37,Line45	Similarity: 0.04
Line40,Line59	Similarity: 0.04
Line37,Line44	Similarity: 0.04
Line37,Line47	Similarity: 0.07
Line40,Line56	Similarity: 0.07
Line40,Line55	Similarity: 0.08
Line40,Line58	Similarity: 0.06
Line40,Line52	Similarity: 0.08
Line40,Line51	Similarity: 0.07
Line40,Line53	Similarity: 0.05
Line33,Line4	Similarity: 0.02
Line33,Line5	Similarity: 0.07
Line48,Line52	Similarity: 0.07
Line37,Line50	Similarity: 0.03
Line33,Line6	Similarity: 0.06
Line37,Line52	Similarity: 0.04
Line48,Line50	Similarity: 0.18
Line33,Line8	Similarity: 0.04
Line37,Line51	Similarity: 0.04
Line37,Line54	Similarity: 0.03
Line37,Line53	Similarity: 0.06
Line37,Line56	Similarity: 0.04
Line37,Line55	Similarity: 0.04
Line37,Line58	Similarity: 0.03
Line48,Line59	Similarity: 0.04
Line48,Line57	Similarity: 0.13
Line37,Line59	Similarity: 0.06
Line48,Line55	Similarity: 0.07
Line40,Line63	Similarity: 0.07
Line40,Line62	Similarity: 0.07
Line40,Line64	Similarity: 0.06
Line26,Line29	Similarity: 0.05
Line26,Line28	Similarity: 0.04
Line40,Line61	Similarity: 0.05
Line25,Line6	Similarity: 0.14
Line25,Line5	Similarity: 0.09
Line48,Line49	Similarity: 0.09
Line48,Line48	Similarity: 0.11
Line38,Line4	Similarity: 0.04
Line40,Line45	Similarity: 0.08
Line37,Line37	Similarity: 0.11
Line26,Line47	Similarity: 0.03
Line40,Line43	Similarity: 0.06
Line20,Line4	Similarity: 0.11
Line20,Line5	Similarity: 0.08
Line40,Line42	Similarity: 0.09
Line20,Line6	Similarity: 0.07
Line10,Line36	Similarity: 0.05
Line10,Line37	Similarity: 0.03
Line62,Line63	Similarity: 0.06
Line62,Line64	Similarity: 0.06
Line26,Line58	Similarity: 0.07
Line29,Line59	Similarity: 0.10
Line29,Line58	Similarity: 0.04
Line29,Line57	Similarity: 0.04
Line29,Line56	Similarity: 0.04
Line29,Line55	Similarity: 0.05
Line29,Line54	Similarity: 0.03
Line10,Line32	Similarity: 0.04
Line29,Line53	Similarity: 0.11
Line10,Line33	Similarity: 0.03
Line29,Line52	Similarity: 0.05
Line29,Line51	Similarity: 0.09
Line26,Line52	Similarity: 0.10
Line29,Line50	Similarity: 0.04
Line10,Line47	Similarity: 0.02
Line59,Line59	Similarity: 0.06
Line51,Line53	Similarity: 0.10
Line51,Line52	Similarity: 0.07
Line51,Line58	Similarity: 0.06
Line51,Line56	Similarity: 0.07
Line51,Line55	Similarity: 0.08
Line29,Line48	Similarity: 0.04
Line29,Line47	Similarity: 0.02
Line51,Line59	Similarity: 0.13
Line26,Line61	Similarity: 0.05
Line29,Line45	Similarity: 0.05
Line10,Line42	Similarity: 0.12
Line29,Line43	Similarity: 0.04
Line10,Line43	Similarity: 0.04
Line29,Line42	Similarity: 0.05
Line10,Line44	Similarity: 0.05
Line29,Line40	Similarity: 0.05
Line10,Line59	Similarity: 0.03
Line59,Line6	Similarity: 0.13
Line37,Line61	Similarity: 0.06
Line37,Line63	Similarity: 0.04
Line37,Line62	Similarity: 0.08
Line37,Line64	Similarity: 0.04
Line51,Line61	Similarity: 0.04
Line59,Line62	Similarity: 0.08
Line59,Line63	Similarity: 0.08
Line59,Line64	Similarity: 0.04
Line51,Line64	Similarity: 0.06
Line51,Line63	Similarity: 0.07
Line59,Line60	Similarity: 0.04
Line51,Line62	Similarity: 0.06
Line59,Line61	Similarity: 0.13
Line18,Line58	Similarity: 0.07
Line10,Line53	Similarity: 0.04
Line10,Line54	Similarity: 0.12
Line10,Line57	Similarity: 0.05
Line59,Line8	Similarity: 0.05
Line12,Line6	Similarity: 0.07
Line12,Line5	Similarity: 0.09
Line10,Line61	Similarity: 0.07
Line10,Line62	Similarity: 0.05
Line29,Line64	Similarity: 0.04
Line29,Line63	Similarity: 0.04
Line29,Line62	Similarity: 0.04
Line29,Line61	Similarity: 0.10
Line45,Line62	Similarity: 0.07
Line45,Line61	Similarity: 0.05
Line43,Line62	Similarity: 0.12
Line43,Line61	Similarity: 0.08
Line45,Line64	Similarity: 0.07
Line43,Line64	Similarity: 0.05
Line45,Line63	Similarity: 0.08
Line43,Line63	Similarity: 0.06
Line34,Line37	Similarity: 0.05
Line50,Line8	Similarity: 0.08
Line50,Line6	Similarity: 0.06
Line56,Line64	Similarity: 0.06
Line56,Line63	Similarity: 0.14
Line42,Line5	Similarity: 0.13
Line42,Line8	Similarity: 0.17
Line42,Line6	Similarity: 0.18
Line56,Line62	Similarity: 0.13
Line45,Line46	Similarity: 0.06
Line56,Line61	Similarity: 0.04
Line45,Line49	Similarity: 0.29
Line18,Line19	Similarity: 0.20
Line29,Line37	Similarity: 0.06
Line34,Line53	Similarity: 0.07
Line29,Line36	Similarity: 0.04
Line34,Line50	Similarity: 0.09
Line29,Line35	Similarity: 0.03
Line29,Line34	Similarity: 0.06
Line29,Line33	Similarity: 0.05
Line29,Line30	Similarity: 0.04
Line56,Line57	Similarity: 0.07
Line45,Line51	Similarity: 0.08
Line56,Line59	Similarity: 0.04
Line56,Line58	Similarity: 0.19
Line45,Line55	Similarity: 0.09
Line45,Line53	Similarity: 0.17
Line45,Line52	Similarity: 0.08
Line45,Line59	Similarity: 0.04
Line45,Line58	Similarity: 0.21
Line45,Line56	Similarity: 0.27
Line55,Line8	Similarity: 0.14
Line18,Line20	Similarity: 0.04
Line55,Line6	Similarity: 0.08
Line18,Line24	Similarity: 0.09
Line47,Line6	Similarity: 0.06
Line23,Line54	Similarity: 0.07
Line23,Line59	Similarity: 0.05
Line54,Line6	Similarity: 0.05
Line12,Line33	Similarity: 0.03
Line12,Line30	Similarity: 0.13
Line12,Line36	Similarity: 0.06
Line12,Line37	Similarity: 0.04
Line12,Line34	Similarity: 0.13
Line12,Line35	Similarity: 0.04
Line23,Line42	Similarity: 0.17
Line23,Line43	Similarity: 0.09
Line23,Line47	Similarity: 0.03
Line12,Line47	Similarity: 0.06
Line3,Line7	Similarity: 0.80
Line3,Line6	Similarity: 0.08
Line6,Line64	Similarity: 0.06
Line6,Line61	Similarity: 0.09
Line6,Line63	Similarity: 0.07
Line6,Line62	Similarity: 0.13
Line46,Line8	Similarity: 0.08
Line23,Line33	Similarity: 0.04
Line23,Line37	Similarity: 0.05
Line12,Line13	Similarity: 0.05
Line12,Line29	Similarity: 0.04
Line4,Line4	Similarity: 0.17
Line23,Line28	Similarity: 0.05
Line4,Line5	Similarity: 0.05
Line4,Line6	Similarity: 0.13
Line23,Line25	Similarity: 0.11
Line12,Line20	Similarity: 0.03
Line12,Line25	Similarity: 0.23
Line39,Line43	Similarity: 0.18
Line42,Line61	Similarity: 0.11
Line31,Line38	Similarity: 0.07
Line39,Line48	Similarity: 0.33
Line42,Line62	Similarity: 0.17
Line31,Line35	Similarity: 0.09
Line39,Line46	Similarity: 0.17
Line42,Line63	Similarity: 0.08
Line42,Line64	Similarity: 0.07
Line31,Line33	Similarity: 0.06
Line39,Line52	Similarity: 0.11
Line20,Line28	Similarity: 0.13
Line20,Line29	Similarity: 0.15
Line39,Line50	Similarity: 0.17
Line53,Line6	Similarity: 0.05
Line20,Line24	Similarity: 0.16
Line39,Line55	Similarity: 0.13
Line20,Line25	Similarity: 0.11
Line20,Line26	Similarity: 0.04
Line20,Line27	Similarity: 0.07
Line42,Line51	Similarity: 0.18
Line39,Line57	Similarity: 0.22
Line42,Line52	Similarity: 0.09
Line42,Line53	Similarity: 0.05
Line42,Line54	Similarity: 0.12
Line42,Line55	Similarity: 0.10
Line42,Line56	Similarity: 0.08
Line42,Line58	Similarity: 0.07
Line42,Line59	Similarity: 0.15
Line20,Line20	Similarity: 0.17
Line20,Line22	Similarity: 0.08
Line20,Line23	Similarity: 0.04
Line45,Line6	Similarity: 0.08
Line45,Line5	Similarity: 0.11
Line53,Line64	Similarity: 0.04
Line53,Line61	Similarity: 0.03
Line53,Line63	Similarity: 0.05
Line53,Line62	Similarity: 0.09
Line16,Line55	Similarity: 0.06
Line16,Line54	Similarity: 0.04
Line16,Line53	Similarity: 0.17
Line16,Line52	Similarity: 0.06
Line16,Line51	Similarity: 0.05
Line12,Line50	Similarity: 0.12
Line16,Line50	Similarity: 0.05
Line26,Line4	Similarity: 0.05
Line12,Line53	Similarity: 0.05
Line16,Line59	Similarity: 0.03
Line16,Line58	Similarity: 0.15
Line16,Line57	Similarity: 0.11
Line16,Line56	Similarity: 0.25
Line16,Line64	Similarity: 0.05
Line16,Line63	Similarity: 0.05
Line16,Line62	Similarity: 0.05
Line12,Line61	Similarity: 0.04
Line16,Line61	Similarity: 0.04
Line12,Line60	Similarity: 0.07
Line27,Line64	Similarity: 0.06
Line27,Line63	Similarity: 0.06
Line23,Line61	Similarity: 0.06
Line27,Line62	Similarity: 0.06
Line23,Line62	Similarity: 0.10
Line27,Line61	Similarity: 0.04
Line16,Line33	Similarity: 0.03
Line16,Line30	Similarity: 0.11
Line27,Line56	Similarity: 0.06
Line27,Line59	Similarity: 0.04
Line27,Line58	Similarity: 0.05
Line27,Line53	Similarity: 0.04
Line27,Line52	Similarity: 0.07
Line27,Line55	Similarity: 0.07
Line16,Line39	Similarity: 0.15
Line16,Line37	Similarity: 0.03
Line16,Line36	Similarity: 0.05
Line27,Line51	Similarity: 0.06
Line16,Line35	Similarity: 0.04
Line16,Line43	Similarity: 0.10
Line16,Line42	Similarity: 0.06
Line16,Line41	Similarity: 0.25
Line16,Line40	Similarity: 0.06
Line27,Line45	Similarity: 0.07
Line27,Line42	Similarity: 0.08
Line27,Line43	Similarity: 0.06
Line16,Line49	Similarity: 0.15
Line16,Line48	Similarity: 0.11
Line16,Line46	Similarity: 0.05
Line27,Line40	Similarity: 0.07
Line16,Line45	Similarity: 0.20
Line29,Line4	Similarity: 0.10
Line5,Line63	Similarity: 0.09
Line5,Line64	Similarity: 0.08
Line5,Line61	Similarity: 0.05
Line5,Line62	Similarity: 0.08
Line5,Line56	Similarity: 0.09
Line27,Line35	Similarity: 0.09
Line5,Line58	Similarity: 0.07
Line27,Line37	Similarity: 0.04
Line5,Line59	Similarity: 0.05
Line27,Line31	Similarity: 0.06
Line27,Line33	Similarity: 0.03
Line16,Line17	Similarity: 0.05
Line29,Line5	Similarity: 0.11
Line29,Line6	Similarity: 0.04
Line58,Line6	Similarity: 0.06
Line5,Line52	Similarity: 0.10
Line5,Line53	Similarity: 0.12
Line5,Line55	Similarity: 0.11
Line5,Line50	Similarity: 0.07
Line5,Line51	Similarity: 0.09
Line16,Line22	Similarity: 0.03
Line40,Line6	Similarity: 0.07
Line27,Line28	Similarity: 0.07
Line16,Line20	Similarity: 0.06
Line40,Line5	Similarity: 0.10
Line27,Line29	Similarity: 0.09
Line16,Line29	Similarity: 0.08
Line16,Line28	Similarity: 0.07
Line16,Line27	Similarity: 0.05
Line16,Line25	Similarity: 0.05
Line16,Line24	Similarity: 0.03
Line47,Line63	Similarity: 0.06
Line47,Line64	Similarity: 0.03
Line47,Line61	Similarity: 0.05
Line47,Line62	Similarity: 0.03
Line36,Line48	Similarity: 0.06
Line28,Line4	Similarity: 0.06
Line28,Line5	Similarity: 0.04
Line28,Line3	Similarity: 0.04
Line28,Line8	Similarity: 0.05
Line28,Line6	Similarity: 0.12
Line25,Line34	Similarity: 0.13
Line25,Line35	Similarity: 0.04
Line25,Line33	Similarity: 0.06
Line25,Line36	Similarity: 0.13
Line25,Line37	Similarity: 0.08
Line14,Line15	Similarity: 0.02
Line14,Line14	Similarity: 0.08
Line14,Line17	Similarity: 0.02
Line14,Line16	Similarity: 0.09
Line25,Line30	Similarity: 0.06
Line25,Line31	Similarity: 0.06
Line14,Line29	Similarity: 0.04
Line25,Line29	Similarity: 0.09
Line27,Line6	Similarity: 0.06
Line27,Line5	Similarity: 0.08
Line14,Line20	Similarity: 0.06
Line3,Line43	Similarity: 0.07
Line27,Line4	Similarity: 0.08
Line25,Line28	Similarity: 0.12
Line14,Line28	Similarity: 0.03
Line14,Line27	Similarity: 0.05
Line14,Line22	Similarity: 0.11
Line14,Line24	Similarity: 0.07
Line24,Line7	Similarity: 0.04
Line24,Line6	Similarity: 0.08
Line24,Line9	Similarity: 0.05
Line24,Line8	Similarity: 0.05
Line24,Line3	Similarity: 0.04
Line20,Line57	Similarity: 0.03
Line20,Line58	Similarity: 0.06
Line24,Line5	Similarity: 0.04
Line20,Line59	Similarity: 0.08
Line64,Line64	Similarity: 0.10
Line24,Line4	Similarity: 0.12
Line25,Line57	Similarity: 0.07
Line25,Line54	Similarity: 0.10
Line25,Line59	Similarity: 0.04
Line14,Line37	Similarity: 0.03
Line20,Line53	Similarity: 0.09
Line20,Line54	Similarity: 0.12
Line20,Line55	Similarity: 0.04
Line14,Line38	Similarity: 0.06
Line20,Line56	Similarity: 0.07
Line14,Line33	Similarity: 0.03
Line20,Line50	Similarity: 0.06
Line25,Line53	Similarity: 0.10
Line20,Line51	Similarity: 0.03
Line14,Line35	Similarity: 0.04
Line25,Line50	Similarity: 0.12
Line20,Line52	Similarity: 0.03
Line53,Line54	Similarity: 0.04
Line53,Line53	Similarity: 0.07
Line53,Line56	Similarity: 0.28
Line53,Line55	Similarity: 0.11
Line14,Line40	Similarity: 0.06
Line25,Line43	Similarity: 0.06
Line14,Line42	Similarity: 0.06
Line31,Line60	Similarity: 0.07
Line31,Line61	Similarity: 0.04
Line53,Line58	Similarity: 0.13
Line53,Line57	Similarity: 0.10
Line25,Line47	Similarity: 0.06
Line53,Line59	Similarity: 0.06
Line20,Line64	Similarity: 0.03
Line25,Line42	Similarity: 0.08
Line14,Line43	Similarity: 0.05
Line20,Line61	Similarity: 0.11
Line20,Line62	Similarity: 0.07
Line14,Line45	Similarity: 0.06
Line20,Line63	Similarity: 0.11
Line36,Line60	Similarity: 0.07
Line20,Line35	Similarity: 0.05
Line36,Line62	Similarity: 0.06
Line20,Line36	Similarity: 0.03
Line20,Line37	Similarity: 0.11
Line36,Line64	Similarity: 0.06
Line20,Line38	Similarity: 0.07
Line22,Line4	Similarity: 0.03
Line31,Line59	Similarity: 0.04
Line22,Line5	Similarity: 0.05
Line22,Line6	Similarity: 0.08
Line47,Line47	Similarity: 0.10
Line58,Line64	Similarity: 0.05
Line58,Line63	Similarity: 0.06
Line42,Line43	Similarity: 0.15
Line22,Line9	Similarity: 0.06
Line42,Line44	Similarity: 0.09
Line58,Line62	Similarity: 0.05
Line58,Line61	Similarity: 0.04
Line42,Line45	Similarity: 0.10
Line14,Line51	Similarity: 0.05
Line42,Line47	Similarity: 0.03
Line14,Line53	Similarity: 0.08
Line14,Line52	Similarity: 0.06
Line14,Line59	Similarity: 0.03
Line14,Line58	Similarity: 0.10
Line20,Line33	Similarity: 0.12
Line20,Line34	Similarity: 0.04
Line14,Line55	Similarity: 0.06
Line14,Line54	Similarity: 0.04
Line20,Line30	Similarity: 0.03
Line14,Line56	Similarity: 0.18
Line47,Line52	Similarity: 0.03
Line47,Line50	Similarity: 0.03
Line47,Line51	Similarity: 0.03
Line20,Line47	Similarity: 0.06
Line58,Line59	Similarity: 0.04
Line36,Line53	Similarity: 0.04
Line36,Line54	Similarity: 0.05
Line47,Line58	Similarity: 0.03
Line47,Line59	Similarity: 0.07
Line36,Line57	Similarity: 0.06
Line47,Line56	Similarity: 0.03
Line23,Line6	Similarity: 0.11
Line47,Line54	Similarity: 0.05
Line14,Line62	Similarity: 0.11
Line14,Line61	Similarity: 0.04
Line14,Line64	Similarity: 0.10
Line14,Line63	Similarity: 0.11
Line31,Line44	Similarity: 0.07
Line20,Line42	Similarity: 0.12
Line20,Line43	Similarity: 0.06
Line25,Line60	Similarity: 0.07
Line20,Line44	Similarity: 0.07
Line20,Line45	Similarity: 0.04
Line25,Line61	Similarity: 0.14
Line20,Line40	Similarity: 0.03
Line25,Line62	Similarity: 0.06
```

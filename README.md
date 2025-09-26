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
hadoop jar /opt/hadoop-3.2.1/share/hadoop/mapreduce/DocumentSimilarity-0.0.1-SNAPSHOT.jar com.example.controller.DocumentSimilarityDriver /input/data/input.txt /output
```

### 9. **View the Output**

To view the output of your MapReduce job, use:

```bash
hadoop fs -cat /output/*
```

### 10. **Copy Output from HDFS to Local OS**

To copy the output from HDFS to your local machine:

1. Use the following command to copy from HDFS:
    ```bash
    hdfs dfs -get /output /opt/hadoop-3.2.1/share/hadoop/mapreduce/
    ```

2. use Docker to copy from the container to your local machine:
   ```bash
   exit 
   ```
    ```bash
    docker cp resourcemanager:/opt/hadoop-3.2.1/share/hadoop/mapreduce/output/ shared-folder/
    ```
3. Commit and push to your repo so that we can able to see your output


---

## Challenges and Solutions
My first issue arose because I had run out of free use of codespace, so I had to do everything locally on my computer. Most of it was already setup from previous assignments so there were no issues other than my laptop struggles to handle docker and often crashes. 
dock
For designing the algorithm, I had no idea where to start so I based my code off of homework 4's word counting as a starting point. I read some documentation on how to write hadoop mapreduce functions as well. I had some confusion over Jaccard Simularity algorithm design so looked up stackoverflow and repositories on how other people implemented the logic. From there it was a lot of guessing and checking and debugging with the help of chatGPT.

As per instructions, I took down and rebuilt the docker container with only one datanode to see if there was any difference in performance. When running on a single data node it took nearly four times as long to complete the job, most likely due to the lowered computational power.
---
## Sample Input

**Input example from `input/input.txt`**
```
Document1 This is a sample document containing words
Document2 Another document that also has words
Document3 Sample text with different words
```
## Sample Output

**Output example from `output/part-r-xxxxx.txt`**
```
"Document1, Document2 Similarity: 0.56"
"Document1, Document3 Similarity: 0.42"
"Document2, Document3 Similarity: 0.50"
```
## Obtained Output: 
`output/part-r-00000`:
```
Document3,Document3	Similarity: 0.42
Document2,Document2	Similarity: 0.37
Document2,Document3	Similarity: 0.25
Document1,Document3	Similarity: 0.12
Document1,Document2	Similarity: 0.17
Document1,Document1	Similarity: 0.29
```

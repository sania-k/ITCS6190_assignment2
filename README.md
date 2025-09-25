# Assignment 2: Document Similarity using MapReduce

**Name:** Sania Khan

**Student ID:** 801241037

## Approach and Implementation

### Mapper Design
[Explain the logic of your Mapper class. What is its input key-value pair? What does it emit as its output key-value pair? How does it help in solving the overall problem?]

### Reducer Design
[Explain the logic of your Reducer class. What is its input key-value pair? How does it process the values for a given key? What does it emit as the final output? How do you calculate the Jaccard Similarity here?]

### Overall Data Flow
[Describe how data flows from the initial input files, through the Mapper, shuffle/sort phase, and the Reducer to produce the final output.]

---

## Setup and Execution

### ` Note: The below commands are the ones used for the Hands-on. You need to edit these commands appropriately towards your Assignment to avoid errors. `

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

[Describe any challenges you faced during this assignment. This could be related to the algorithm design (e.g., how to generate pairs), implementation details (e.g., data structures, debugging in Hadoop), or environmental issues. Explain how you overcame these challenges.]

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

**Output from `small_dataset.txt`**
```
"Document1, Document2 Similarity: 0.56"
"Document1, Document3 Similarity: 0.42"
"Document2, Document3 Similarity: 0.50"
```
## Obtained Output: (Place your obtained output here.)

package com.example;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.StringTokenizer;

public class DocumentSimilarityMapper extends Mapper<Object, Text, Text, Text> {
    private Text word = new Text();
    private Text docID = new Text();

    @Override
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString().trim();
        if (line.isEmpty()) return;

        StringTokenizer itr = new StringTokenizer(line);
        if (!itr.hasMoreTokens()) return;

        String documentId = itr.nextToken();
        docID.set(documentId);
        
        while (itr.hasMoreTokens()) {
            String currentWord = itr.nextToken().replaceAll("[^a-zA-Z]", "").toLowerCase();
            if (currentWord.length() >= 3) {
                word.set(currentWord);
                context.write(word, docID); 
            }
        }
    }
}

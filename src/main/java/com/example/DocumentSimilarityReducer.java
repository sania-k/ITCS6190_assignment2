package com.example;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.*;

public class DocumentSimilarityReducer extends Reducer<Text, Text, Text, Text> {

    private Map<String, Set<String>> docToWords = new HashMap<>();
    private Map<String, Set<String>> pairToWords = new HashMap<>();

    @Override
    public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        List<String> docs = new ArrayList<>();
        for (Text val : values) {
            String doc = val.toString();
            docs.add(doc);
            
            docToWords.computeIfAbsent(doc, k -> new HashSet<>()).add(key.toString());
        }
        
        for (int i = 0; i < docs.size(); i++) {
            for (int j = i + 1; j < docs.size(); j++) {
                String doc1 = docs.get(i);
                String doc2 = docs.get(j);
                String pair = doc1.compareTo(doc2) < 0 ? doc1 + "," + doc2 : doc2 + "," + doc1;

                pairToWords.computeIfAbsent(pair, k -> new HashSet<>()).add(key.toString());
            }
        }
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        for (Map.Entry<String, Set<String>> entry : pairToWords.entrySet()) {
            String pair = entry.getKey();
            String[] docs = pair.split(",");

            String doc1 = docs[0];
            String doc2 = docs[1];

            Set<String> intersection = entry.getValue();
            int interSize = intersection.size();

            Set<String> union = new HashSet<>(docToWords.get(doc1));
            union.addAll(docToWords.get(doc2));
            int unionSize = union.size();

            double jaccard = (unionSize == 0) ? 0.0 : (double) interSize / unionSize;

            context.write(new Text(pair), new Text("Similarity: " + String.format("%.2f", jaccard)));
        }
    }
}

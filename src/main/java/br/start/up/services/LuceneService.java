package br.start.up.services;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LuceneService {

    private final Directory directory;
    private final Analyzer analyzer;

    private final IndexWriter writer;

    public LuceneService(Directory directory, Analyzer analyzer, IndexWriter writer) {
        this.directory = directory;
        this.analyzer = analyzer;
        this.writer = writer;
    }

    public void index(List<Map<String, String>> documents) throws IOException {

        for (Map<String, String> fields : documents) {

            Document doc = new Document();

            for (Map.Entry<String, String> entry : fields.entrySet()) {
                if(!entry.getKey().equals("id")){
                    doc.add(new TextField(entry.getKey(), entry.getValue(), Field.Store.YES));
                    continue;
                }
                doc.add(new StringField(entry.getKey(), entry.getValue(), Field.Store.YES));
            }

            writer.addDocument(doc);
        }

        writer.commit(); // commit em lote (importante)
    }

    public List<Map<String, String>> search(
            String value,
            String[] fields,
            Map<String, Float> boosts,
            int limit,
            boolean useAndOperator
    ) throws Exception {

        List<Map<String, String>> resultsList = new ArrayList<>();

        // 👉 usa writer se você tiver (realtime). Se não, troca por DirectoryReader.open(directory)
        try (DirectoryReader reader = DirectoryReader.open(directory)) {

            IndexSearcher searcher = new IndexSearcher(reader);

            MultiFieldQueryParser parser = new MultiFieldQueryParser(fields, analyzer, boosts);

            if (useAndOperator) {
                parser.setDefaultOperator(QueryParser.Operator.AND);
            }

            Query query = parser.parse(value);

            TopDocs results = searcher.search(query, limit);

            for (ScoreDoc scoreDoc : results.scoreDocs) {
                Document doc = searcher.doc(scoreDoc.doc);

                Map<String, String> result = new HashMap<>();
                doc.getFields().forEach(f -> result.put(f.name(), doc.get(f.name())));

                resultsList.add(result);
            }
        }

        return resultsList;
    }
}

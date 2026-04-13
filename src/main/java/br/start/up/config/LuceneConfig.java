package br.start.up.config;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.pt.PortugueseStemFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.synonym.SolrSynonymParser;
import org.apache.lucene.analysis.synonym.SynonymGraphFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Paths;
import java.text.ParseException;

@Configuration
public class LuceneConfig {

    @Bean
    public Directory directory() throws IOException {
        return FSDirectory.open(Paths.get("index"));
    }

    @Bean
    public SynonymMap synonymMap() throws IOException, ParseException {
        SolrSynonymParser parser = new SolrSynonymParser(true, true, new StandardAnalyzer());

        try (Reader reader = new InputStreamReader(
                getClass().getResourceAsStream("/synonyms.txt"))) {

            parser.parse(reader);
        }

        return parser.build();
    }

    @Bean
    public IndexWriter indexWriter(Directory directory, Analyzer analyzer) throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        config.setRAMBufferSizeMB(128); // melhora performance

        return new IndexWriter(directory, config);
    }

    @Bean
    public Analyzer analyzer(SynonymMap synonymMap) {
        return new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(String fieldName) {

                Tokenizer tokenizer = new StandardTokenizer();
                TokenStream filter = new LowerCaseFilter(tokenizer);

                filter = new ASCIIFoldingFilter(filter);
                filter = new SynonymGraphFilter(filter, synonymMap, true);
                filter = new PortugueseStemFilter(filter);

                return new TokenStreamComponents(tokenizer, filter);
            }
        };
    }
}

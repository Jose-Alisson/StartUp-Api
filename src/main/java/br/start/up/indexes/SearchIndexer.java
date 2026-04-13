package br.start.up.indexes;

import java.util.Map;

public interface SearchIndexer<T> {
    Map<String, String> toDocument(T entity);
}

package br.start.up.indexes.impl;

import br.start.up.indexes.SearchIndexer;
import br.start.up.model.Business;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class BusinessIndexer implements SearchIndexer<Business> {

    @Override
    public Map<String, String> toDocument(Business entity) {
        return Map.of(
                "name", entity.getName(),
                "description", entity.getDescription(),
                "category", entity.getCategory().getName(),
                "id", "" + entity.getId()
        );
    }
}

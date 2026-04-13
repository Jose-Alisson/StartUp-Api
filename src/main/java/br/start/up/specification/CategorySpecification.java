package br.start.up.specification;

import br.start.up.model.Business;
import br.start.up.model.Category;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

public class CategorySpecification {

    public static Specification<Category> idOrName(String term){
        return (root, query, builder) -> {

            if (term == null || term.isBlank()) {
                return null;
            }

            // Tenta converter para ID
            Long id = null;
            if (term.matches("\\d+")) {
                id = Long.parseLong(term);
            }

            // Se for ID → retorna filtro por ID
            if (id != null) {
                return builder.equal(root.get("id"), id);
            }

            // Caso contrário → busca por nome
            return builder.equal(
                    builder.lower(root.get("name")),
                    term.toLowerCase(Locale.ROOT)
            );
        };
    }
}

package fr.wilda.picocli.sdk.ai;

import java.nio.file.Files;
import java.nio.file.Path;

import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

public class InMemoryEmbeddingStoreProducer {
    @Produces
    @Singleton
    public EmbeddingStore create() {
        if (Files.exists(Path.of("src/main/resources/store.json"))) {
            return InMemoryEmbeddingStore
                    .fromFile("src/main/resources/store.json");
        } else {
            return new InMemoryEmbeddingStore();
        }

    }
}

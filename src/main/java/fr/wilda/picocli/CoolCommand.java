package fr.wilda.picocli;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import fr.wilda.picocli.sdk.ai.AIEndpointService;
import io.quarkus.picocli.runtime.annotations.TopCommand;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import static dev.langchain4j.data.document.splitter.DocumentSplitters.recursive;

@TopCommand
@Command(name = "coolCmd", mixinStandardHelpOptions = true)
public class CoolCommand implements Callable<Integer> {
  // Logger
  private static final Logger _LOG = LoggerFactory.getLogger(CoolCommand.class);

  @Inject
  AIEndpointService aiEndpointService;

  @Inject
  EmbeddingStore store;

  @Inject
  EmbeddingModel embeddingModel;


  // Question to ask
  @Parameters(paramLabel = "<question>", defaultValue = "Explain with few word what are you", description = "The question to ask.")
  private String question;

  @Override
  public Integer call() throws Exception {
    _LOG.info("\n🤖:\n");
    aiEndpointService.askAQuestion(question)
    .subscribe()
    .asStream()
    .forEach(token -> {
      try {
        TimeUnit.MILLISECONDS.sleep(150);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      _LOG.info(token);
    });
    _LOG.info("\n");
  
    return 0;
  }

  @Command
  void rag(@Parameters String filesPath) {
    _LOG.info("Starting ingestion...");

    store.removeAll();
    List<Document> list = FileSystemDocumentLoader.loadDocumentsRecursively(filesPath);
    _LOG.info("" + list.size());
    EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
            .embeddingStore(store)
            .embeddingModel(embeddingModel)
            .documentSplitter(recursive(8000, 30))
            .build();
    ingestor.ingest(list);
    _LOG.info("Documents ingested successfully");
    ((InMemoryEmbeddingStore) store).serializeToFile("src/main/resources/store.json");
  }
}

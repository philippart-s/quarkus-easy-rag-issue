package fr.wilda.picocli.sdk.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import io.smallrye.mutiny.Multi;
import jakarta.enterprise.context.ApplicationScoped;

@RegisterAiService
@ApplicationScoped
public interface AIEndpointService {
  // Add some instructions to my LLM
  @SystemMessage("""
                  Your goal is to help as much as possible when you are asked a question.
                  If you don't know how to answer, just answer "I don't know how to answer that question."
                  Keep your answers concise and simple.
                 """)
  @UserMessage("The question : {question}")
  Multi<String> askAQuestion(String question);
}

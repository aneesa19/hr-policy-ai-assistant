package com.aneesa.hr_policy_rag;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import org.springframework.util.ResourceUtils;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.model.bedrock.BedrockAnthropicMessageChatModel;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;

import java.io.File;

@SpringBootApplication
public class HrPolicyRagApplication {

	@Value("${anthropic.api.key}")
	private String apiKey;

	@Value("${aws.access.key}")
	private String awsAccessKey;

	@Value("${aws.secret.key}")
	private String awsSecretKey;

	@Value("${aws.region}")
	private String awsRegion;

	@Value("${aws.knowledge.base.id}")
	private String knowledgeBaseId;

	public static void main(String[] args) {
		SpringApplication.run(HrPolicyRagApplication.class, args);
	}

	@Bean
	public HrPolicyAssistant hrPolicyAssistant() throws Exception {

		ContentRetriever retriever = new BedrockKnowledgeBaseRetriever(
				awsAccessKey,
				awsSecretKey,
				awsRegion,
				knowledgeBaseId   // paste your actual KB ID here
		);

		ChatLanguageModel chatModel = new BedrockConverseChatModel(
				awsAccessKey,
				awsSecretKey,
				awsRegion,
				"us.anthropic.claude-haiku-4-5-20251001-v1:0",
				0.3,
				500
		);

		return AiServices.builder(HrPolicyAssistant.class)
				.chatLanguageModel(chatModel)
				.contentRetriever(retriever)
				.build();
//		File docDir = ResourceUtils.getFile("classpath:documents/hr-policy.txt");
//		Document document = FileSystemDocumentLoader.loadDocument(docDir.toPath());
//
//		System.out.println("DOCUMENT LOADED — Length: " + document.text().length());
//
//		InMemoryEmbeddingStore embeddingStore = new InMemoryEmbeddingStore<>();
//		EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
//
//		EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
//				.embeddingModel(embeddingModel)
//				.embeddingStore(embeddingStore)
//				.build();
//		ingestor.ingest(document);
//
//		EmbeddingStoreContentRetriever retriever = EmbeddingStoreContentRetriever.builder()
//				.embeddingStore(embeddingStore)
//				.embeddingModel(embeddingModel)
//				.maxResults(3)
//				.build();
//
//		ChatLanguageModel chatModel = new BedrockConverseChatModel(
//				awsAccessKey,
//				awsSecretKey,
//				awsRegion,
//				"us.anthropic.claude-haiku-4-5-20251001-v1:0",
//				0.3,
//				500
//		);
//
//
////		AnthropicChatModel chatModel = AnthropicChatModel.builder()
////				.apiKey(apiKey)
////				.modelName("claude-haiku-4-5-20251001")
////				.build();
//
//		return AiServices.builder(HrPolicyAssistant.class)
//				.chatLanguageModel(chatModel)
//				.contentRetriever(retriever)
//				.build();
	}
}
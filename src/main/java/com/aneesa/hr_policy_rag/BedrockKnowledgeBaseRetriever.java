package com.aneesa.hr_policy_rag;

import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.Query;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockagentruntime.BedrockAgentRuntimeClient;
import software.amazon.awssdk.services.bedrockagentruntime.model.*;

import java.util.ArrayList;
import java.util.List;

public class BedrockKnowledgeBaseRetriever implements ContentRetriever {

    private final BedrockAgentRuntimeClient client;
    private final String knowledgeBaseId;

    public BedrockKnowledgeBaseRetriever(String accessKey, String secretKey, String region, String knowledgeBaseId) {
        this.client = BedrockAgentRuntimeClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
        this.knowledgeBaseId = knowledgeBaseId;
    }

    @Override
    public List<Content> retrieve(Query query) {

        RetrieveRequest request = RetrieveRequest.builder()
                .knowledgeBaseId(knowledgeBaseId)
                .retrievalQuery(KnowledgeBaseQuery.builder()
                        .text(query.text())
                        .build())
                .build();

        RetrieveResponse response = client.retrieve(request);

        List<Content> contents = new ArrayList<>();
        for (KnowledgeBaseRetrievalResult result : response.retrievalResults()) {
            contents.add(Content.from(result.content().text()));
        }
        return contents;
    }
}
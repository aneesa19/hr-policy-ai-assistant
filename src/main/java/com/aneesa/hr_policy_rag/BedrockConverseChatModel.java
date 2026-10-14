package com.aneesa.hr_policy_rag;

import dev.langchain4j.data.message.*;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.output.Response;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;


import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.*;


import java.util.ArrayList;
import java.util.List;


public class BedrockConverseChatModel implements ChatLanguageModel {

    private final BedrockRuntimeClient client;
    private final String modelId;
    private final double temperature;
    private final int maxTokens;

    public BedrockConverseChatModel(String accessKey, String secretKey, String region,
                                    String modelId, double temperature, int maxTokens) {
        this.client = BedrockRuntimeClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
        this.modelId = modelId;
        this.temperature = temperature;
        this.maxTokens = maxTokens;
    }

    @Override
    public Response<AiMessage> generate(List<ChatMessage> messages) {

        List<Message> bedrockMessages = new ArrayList<>();
        StringBuilder systemText = new StringBuilder();

        for (ChatMessage message : messages) {
            if (message instanceof SystemMessage systemMessage) {
                systemText.append(systemMessage.text()).append("\n");
            } else if (message instanceof UserMessage userMessage) {
                StringBuilder userText = new StringBuilder();
                for (Content content : userMessage.contents()) {
                    if (content instanceof TextContent textContent) {
                        userText.append(textContent.text());
                    }
                }
                bedrockMessages.add(Message.builder()
                        .role(ConversationRole.USER)
                        .content(ContentBlock.fromText(userText.toString()))
                        .build());
            } else if (message instanceof AiMessage aiMessage) {
                bedrockMessages.add(Message.builder()
                        .role(ConversationRole.ASSISTANT)
                        .content(ContentBlock.fromText(aiMessage.text()))
                        .build());
            }
        }

        ConverseRequest.Builder requestBuilder = ConverseRequest.builder()
                .modelId(modelId)
                .messages(bedrockMessages)
                .inferenceConfig(InferenceConfiguration.builder()
                        .maxTokens(maxTokens)
                        .temperature((float) temperature)
                        .build());

        if (systemText.length() > 0) {
            requestBuilder.system(SystemContentBlock.builder().text(systemText.toString()).build());
        }

        ConverseResponse response = client.converse(requestBuilder.build());
        String answer = response.output().message().content().get(0).text();

        return Response.from(new AiMessage(answer));
    }
}
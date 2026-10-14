package com.aneesa.hr_policy_rag;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface HrPolicyAssistant {

    @SystemMessage("""
        You are a helpful HR Policy assistant.
        Answer questions based ONLY on the 
        HR policy document provided.
        If the answer is not in the document,
        say "I could not find this information 
        in the HR policy document."
        Be clear and concise in your answers.
        """)
    String answer(@UserMessage String question);
}
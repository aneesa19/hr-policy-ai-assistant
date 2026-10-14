package com.aneesa.hr_policy_rag;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hr")
public class HrPolicyController {

    private final HrPolicyAssistant assistant;

    public HrPolicyController(
            HrPolicyAssistant assistant) {
        this.assistant = assistant;
    }

    @PostMapping("/ask")
    public String ask(@RequestBody String question) {
        return assistant.answer(question);
    }
}
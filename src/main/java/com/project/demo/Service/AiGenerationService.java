package com.project.demo.Service;
import com.project.demo.dto.chat.StreamResponse;
import reactor.core.publisher.Flux;

import aj.org.objectweb.asm.commons.Remapper;

public interface AiGenerationService {

    Flux<StreamResponse> streamResponse(String message, Long projectId);
}

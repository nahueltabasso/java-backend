package com.post.service.app.services;

import com.post.service.app.models.documents.Reaction;
import com.post.service.app.models.dto.ReactionDTO;
import reactor.core.publisher.Mono;

public interface ReactionService {

    public ReactionDTO entityToDto(Reaction entity);
    public Reaction dtoToEntity(ReactionDTO dto);
    public Mono<ReactionDTO> save(ReactionDTO dto);
    public Mono<Long> getReactionsByPostId(String postId);
    public Mono<Void> deleteReaction(String postId, Long userProfileId);
    public Mono<Void> deleteAllByPost(String postId);
}

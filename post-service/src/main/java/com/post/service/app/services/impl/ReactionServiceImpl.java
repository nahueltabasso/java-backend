package com.post.service.app.services.impl;

import com.post.service.app.models.documents.Reaction;
import com.post.service.app.models.dto.ReactionDTO;
import com.post.service.app.models.repository.ReactionRepository;
import com.post.service.app.services.ReactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@Slf4j
public class ReactionServiceImpl implements ReactionService {

    @Autowired
    private ReactionRepository reactionRepository;

    @Override
    public ReactionDTO entityToDto(Reaction entity) {
        log.info("Enter to entityToDto()");
        return ReactionDTO.builder()
                .id(entity.getId())
                .postId(entity.getPostId())
                .userProfileId(entity.getUserProfileId())
                .userId(entity.getUserId())
                .createAt(entity.getCreateAt()).build();
    }

    @Override
    public Reaction dtoToEntity(ReactionDTO dto) {
        log.info("Enter to dtoToEntity()");
        return Reaction.builder()
                .id(dto.getId())
                .postId(dto.getPostId())
                .userProfileId(dto.getUserProfileId())
                .userId(dto.getUserId())
                .createAt(dto.getCreateAt()).build();
    }

    @Override
    public Mono<ReactionDTO> save(ReactionDTO dto) {
        log.info("Enter to save()");
        Reaction reaction = this.dtoToEntity(dto);
        reaction.setCreateAt(LocalDateTime.now());
        return this.reactionRepository.save(reaction)
                .map(this::entityToDto);
    }

    @Override
    public Mono<Long> getReactionsByPostId(String postId) {
        log.info("Enter to getReactionByPostId()");
        return this.reactionRepository.countByPostId(postId);
    }

    @Override
    public Mono<Void> deleteReaction(String postId, Long userProfileId) {
        log.info("Enter to deleteReaction()");
        return this.reactionRepository.findByPostIdAndUserProfileId(postId, userProfileId)
                .flatMap(reaction -> this.reactionRepository.delete(reaction).then());
    }

    @Override
    public Mono<Void> deleteAllByPost(String postId) {
        log.info("Enter to deleteAllByPost()");
        return this.reactionRepository.findAllByPostId(postId)
                .flatMap(reactions -> this.reactionRepository.delete(reactions)).then();
    }
}

package com.post.service.app.controllers;

import com.post.service.app.models.dto.ReactionDTO;
import com.post.service.app.services.ReactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@RequestMapping("/api/post/reaction")
@Slf4j
public class ReactionController {

    @Autowired
    private ReactionService reactionService;

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping
    public Mono<ResponseEntity<?>> create(@RequestBody ReactionDTO dto) {
        log.info("Enter to create()");

        return this.reactionService.save(dto)
                .map(r -> ResponseEntity
                        .created(URI.create("/api/post/reaction"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(r));
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/{postId}")
    public Mono<ResponseEntity<?>> getByPost(@PathVariable String postId) {
        log.info("Enter to getByPost()");
        return Mono.just(
                ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(this.reactionService.getReactionsByPostId(postId)));
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("/{postId}/{userProfileId}")
    public Mono<ResponseEntity<Void>> deleteReaction(@PathVariable String postId, @PathVariable Long userProfileId) {
        log.info("Enter to deleteReaction()");
        return this.reactionService.deleteReaction(postId, userProfileId).then(Mono.just(
                new ResponseEntity<Void>(HttpStatus.NO_CONTENT)
        ).defaultIfEmpty(ResponseEntity.notFound().build()));
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("/{postId}")
    public Mono<ResponseEntity<Void>> deleteAllByPost(@PathVariable String postId) {
        log.info("Enter to deleteReaction()");
        return this.reactionService.deleteAllByPost(postId).then(Mono.just(
                new ResponseEntity<Void>(HttpStatus.NO_CONTENT)
        ).defaultIfEmpty(ResponseEntity.notFound().build()));
    }
}

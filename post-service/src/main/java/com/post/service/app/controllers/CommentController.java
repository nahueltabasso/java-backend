package com.post.service.app.controllers;

import com.post.service.app.models.dto.CommentDTO;
import com.post.service.app.security.models.CommonUserDetailsDTO;
import com.post.service.app.services.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@RequestMapping("api/post/comment")
@Slf4j
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping
    public Mono<ResponseEntity<?>> create(@RequestBody CommentDTO dto,
                                          @AuthenticationPrincipal CommonUserDetailsDTO userDetails) {
        log.info("Enter to create()");
        this.commentService.setCurrentUser(userDetails);

        return this.commentService.save(dto)
                .map(comment -> ResponseEntity
                        .created(URI.create("/api/post/comment"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(comment));
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/{postId}")
    public Mono<ResponseEntity<?>> getByPost(@PathVariable String postId,
                                             @AuthenticationPrincipal CommonUserDetailsDTO userDetails) {
        log.info("Enter to getByPost()");
        this.commentService.setCurrentUser(userDetails);
        return Mono.just(
                ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(this.commentService.findByPost(postId)));
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping
    public Mono<ResponseEntity<?>> getAll(@AuthenticationPrincipal CommonUserDetailsDTO userDetails) {
        log.info("Enter to getAll()");
        this.commentService.setCurrentUser(userDetails);
        return Mono.just(
                ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(this.commentService.findAll()));
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("/{postId}")
    public Mono<ResponseEntity<Void>> deleteByPost(@PathVariable String postId) {
        log.info("Enter to delete()");
        return this.commentService.deleteByPost(postId).then(Mono.just(
                new ResponseEntity<Void>(HttpStatus.NO_CONTENT)
        ).defaultIfEmpty(ResponseEntity.notFound().build()));
    }


    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping
    public Mono<ResponseEntity<Void>> delete() {
        log.info("Enter to delete()");
        return this.commentService.delete().then(Mono.just(
                new ResponseEntity<Void>(HttpStatus.NO_CONTENT)
        ).defaultIfEmpty(ResponseEntity.notFound().build()));
    }

}

package com.post.service.app.controllers;

import com.post.service.app.models.dto.PostDTO;
import com.post.service.app.models.dto.PostFilterDTO;
import com.post.service.app.security.models.CommonUserDetailsDTO;
import com.post.service.app.services.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.net.URI;

@RestController
@RequestMapping("/api/post")
@Slf4j
public class PostController {

    @Autowired
    private PostService postService;

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping ("/search")
    public Mono<ResponseEntity<?>> search(@RequestBody PostFilterDTO filterDTO,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "5") int size,
                                          @AuthenticationPrincipal CommonUserDetailsDTO userDetails) {
        log.info("Enter to search()");
        this.postService.setCurrentUser(userDetails);
        Pageable paging = PageRequest.of(page, size);
        return Mono.just(
                ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(this.postService.search(filterDTO, paging)));
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping
    public Mono<ResponseEntity<?>> getAll(@AuthenticationPrincipal CommonUserDetailsDTO userDetails) {
        log.info("Enter to getAll()");
        this.postService.setCurrentUser(userDetails);
        return Mono.just(
                ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(this.postService.findAll()));
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/{id}")
    public Mono<ResponseEntity<PostDTO>> getById(@PathVariable String id,
                                                 @AuthenticationPrincipal CommonUserDetailsDTO userDetails) {
        log.info("Enter to getById()");
        this.postService.setCurrentUser(userDetails);
        return this.postService.findById(id).map(p -> ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(p))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<?>> create(@RequestPart("post") PostDTO postDTO,
                                          @RequestPart("file") Flux<FilePart> files) {
        log.info("Enter to create()");

        return this.postService.savePost(postDTO, files)
                .map(post -> ResponseEntity
                        .created(URI.create("/api/post"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(post));
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteById(@PathVariable String id) {
        log.info("Enter to deleteById()");
        return this.postService.deleteById(id).then(Mono.just(
                new ResponseEntity<Void>(HttpStatus.NO_CONTENT)
        ).defaultIfEmpty(ResponseEntity.notFound().build()));
    }

}

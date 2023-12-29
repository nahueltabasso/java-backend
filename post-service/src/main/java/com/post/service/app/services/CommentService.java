package com.post.service.app.services;

import com.post.service.app.models.documents.Comment;
import com.post.service.app.models.dto.CommentDTO;
import com.post.service.app.security.models.CommonUserDetailsDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CommentService {

    public CommentDTO entityToDto(Comment entity);
    public Comment dtoToEntity(CommentDTO dto);
    public void setCurrentUser(CommonUserDetailsDTO userDetailsDTO);
    public Mono<CommentDTO> save(CommentDTO commentDTO);
    public Flux<CommentDTO> findByPost(String postId);
    public Flux<CommentDTO> findAll();
    public Mono<Void> delete();
    public Mono<Void> deleteByPost(String postId);
}

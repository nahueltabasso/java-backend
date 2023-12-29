package com.post.service.app.services.impl;

import com.post.service.app.clients.UserClient;
import com.post.service.app.models.documents.Comment;
import com.post.service.app.models.dto.CommentDTO;
import com.post.service.app.models.dto.UserDTO;
import com.post.service.app.models.repository.CommentRepository;
import com.post.service.app.security.models.CommonUserDetailsDTO;
import com.post.service.app.services.CommentService;
import com.post.service.app.services.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@Slf4j
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PostService postService;
    @Autowired
    private UserClient userClient;
    private CommonUserDetailsDTO currentUser;

    @Override
    public CommentDTO entityToDto(Comment entity) {
        log.info("Enter to entityToDto()");
        CommentDTO dto = CommentDTO.builder()
                .id(entity.getId())
                .comment(entity.getComment())
                .createAt(entity.getCreateAt())
                .postDTO(this.postService.entityToDto(entity.getPost()))
                .userProfileId(entity.getUserProfileId())
                .userId(entity.getUserId())
                .build();

        UserDTO userDTO = this.userClient.getUserById(currentUser.getToken(), entity.getUserProfileId()).block();
        dto.setUserDTO(userDTO);
        return dto;
    }

    @Override
    public Comment dtoToEntity(CommentDTO dto) {
        log.info("Enter to dtoToEntity()");
        Comment entity = Comment.builder()
                .id(dto.getId())
                .comment(dto.getComment())
                .createAt(dto.getCreateAt())
                .post(this.postService.dtoToEntity(dto.getPostDTO()))
                .userProfileId(dto.getUserProfileId())
                .userId(dto.getUserId())
                .build();
        return entity;
    }

    @Override
    public void setCurrentUser(CommonUserDetailsDTO userDetailsDTO) {
        log.info("Enter to setCurrentUser()");
        this.currentUser = userDetailsDTO;
    }

    @Override
    public Mono<CommentDTO> save(CommentDTO commentDTO) {
        log.info("Enter to save()");
        Comment comment = this.dtoToEntity(commentDTO);
        comment.setCreateAt(LocalDateTime.now());
        return this.commentRepository.save(comment)
                .map(this::entityToDto);
    }

    @Override
    public Flux<CommentDTO> findByPost(String postId) {
        log.info("Enter to findByPost()");
        return this.commentRepository.findByPost(postId).map(this::entityToDto);
    }

    @Override
    public Flux<CommentDTO> findAll() {
        log.info("Enter to findAll()");
        return this.commentRepository.findAll().map(this::entityToDto);
    }

    @Override
    public Mono<Void> delete() {
        log.info("Enter to delete()");
        return this.commentRepository.deleteAll();
    }

    @Override
    public Mono<Void> deleteByPost(String postId) {
        log.info("Enter to deleteByPost()");
        return this.commentRepository.findByPost(postId)
                .flatMap(c -> this.commentRepository.delete(c)).then();
    }
}

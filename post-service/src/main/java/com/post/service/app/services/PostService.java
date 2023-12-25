package com.post.service.app.services;

import com.post.service.app.models.documents.Post;
import com.post.service.app.models.dto.PostDTO;
import com.post.service.app.models.dto.PostFilterDTO;
import com.post.service.app.security.models.CommonUserDetailsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PostService {

    public PostDTO entityToDto(Post entity);
    public Post dtoToEntity(PostDTO dto);
    public void setCurrentUser(CommonUserDetailsDTO userDetailsDTO);
    public Mono<Page<PostDTO>> search(PostFilterDTO filterDTO, Pageable pageable);
    public Mono<PostDTO> savePost(PostDTO postDTO, Flux<FilePart> files);
    public Flux<PostDTO> findAll();
    public Mono<PostDTO> findById(String id);
    public Mono<Void> deleteById(String id);
}

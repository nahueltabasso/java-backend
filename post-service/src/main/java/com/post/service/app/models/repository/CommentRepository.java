package com.post.service.app.models.repository;

import com.post.service.app.models.documents.Comment;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;

public interface CommentRepository extends ReactiveSortingRepository<Comment, String> {

    @Query("{ 'post.id' : ?0 }")
    public Flux<Comment> findByPost(String psostId);

}

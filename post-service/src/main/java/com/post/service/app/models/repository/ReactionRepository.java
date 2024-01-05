package com.post.service.app.models.repository;

import com.post.service.app.models.documents.Reaction;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReactionRepository extends ReactiveSortingRepository<Reaction, String> {

    @Query(value = "{postId: ?0}", count = true)
    public Mono<Long> countByPostId(String postId);

    @Query(value = "{userProfileId: ?0}", count = true)
    public Long countByUserProfileId(Long userProfileId);

    public Mono<Reaction> findByPostIdAndUserProfileId(String postId, Long userProfileId);

    public Flux<Reaction> findAllByPostId(String postId);
}

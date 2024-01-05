package com.post.service.app.models.repository;

import com.post.service.app.models.documents.Post;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;

public interface PostRepository extends ReactiveSortingRepository<Post, String>, ReactiveQueryByExampleExecutor<Post> {
}

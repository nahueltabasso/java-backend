package com.post.service.app.services.impl;

import com.post.service.app.clients.UserClient;
import com.post.service.app.exceptions.CustomException;
import com.post.service.app.exceptions.ErrorCode;
import com.post.service.app.models.documents.Post;
import com.post.service.app.models.dto.PostDTO;
import com.post.service.app.models.dto.PostFilterDTO;
import com.post.service.app.models.dto.UserDTO;
import com.post.service.app.models.repository.PostRepository;
import com.post.service.app.security.models.CommonUserDetailsDTO;
import com.post.service.app.services.FileService;
import com.post.service.app.services.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private FileService fileService;
    @Value("${paths.common-image-directory.location}")
    private String directoryPath;
    @Value("${cloudinary.active}")
    private Boolean cloudinaryActive;
    @Value("${cloudinary.host}")
    private String cloudinaryHost;
    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;
    @Autowired
    private UserClient userClient;
    private CommonUserDetailsDTO currentUser;

    @Override
    public PostDTO entityToDto(Post entity) {
        log.info("Enter to entityToDto()");
        PostDTO dto = PostDTO.builder()
                .id(entity.getId())
                .description(entity.getDescription())
                .imagesPaths(entity.getImagesPaths())
                .publicsIds(entity.getPublicsIds())
                .createAt(entity.getCreateAt())
                .userId(entity.getUserId())
                .userProfileId(entity.getUserProfileId()).build();

        // Complete a UserDTO object
        Long userProfileId = entity.getUserProfileId();
        String token = currentUser.getToken();
        UserDTO userDTO = this.userClient.getUserById(token, userProfileId).block();
        dto.setUserDTO(userDTO);
        return dto;
    }

    @Override
    public Post dtoToEntity(PostDTO dto) {
        log.info("Enter to dtoToEntity()");
        Post entity = Post.builder()
                .id(dto.getId())
                .description(dto.getDescription())
                .imagesPaths(dto.getImagesPaths())
                .publicsIds(dto.getPublicsIds())
                .createAt(dto.getCreateAt())
                .userId(dto.getUserId())
                .userProfileId(dto.getUserProfileId()).build();
        return entity;
    }

    @Override
    public void setCurrentUser(CommonUserDetailsDTO userDetailsDTO) {
        this.currentUser = userDetailsDTO;
    }

    @Override
    public Mono<Page<PostDTO>> search(PostFilterDTO filterDTO, Pageable pageable) {
        log.info("Enter to search()");
        Query query = new Query().with(pageable);
        if (filterDTO != null) {
            if (filterDTO.getUserProfileId() != null) {
                query.addCriteria(Criteria.where("userProfileId").is(filterDTO.getUserProfileId()));
            }

            if (!filterDTO.getUserProfileIds().isEmpty()) {
                query.addCriteria(Criteria.where("userProfileId").in(filterDTO.getUserProfileIds()));
            }

            if (filterDTO.getDateFrom() != null && filterDTO.getDateTo() != null) {
                query.addCriteria(Criteria.where("createAt")
                        .gte(filterDTO.getDateFrom())
                        .lte(filterDTO.getDateTo()));
            }

            query.with(Sort.by(Sort.Direction.ASC, "createAt"));
        }
        Flux<Post> postFlux = reactiveMongoTemplate.find(query, Post.class, "posts");
        Mono<Long> countMono = reactiveMongoTemplate.count(Query.of(query).limit(-1).skip(-1), Post.class);
        Mono<List<PostDTO>> postDTOList = postFlux.map(p -> this.entityToDto(p)).collectList();
        return Mono.zip(postDTOList, countMono).map(tuple2 -> {
            return PageableExecutionUtils.getPage(
                    tuple2.getT1(),
                    pageable,
                    () -> tuple2.getT2());
        });
    }

    @Override
    public Mono<PostDTO> savePost(PostDTO postDTO, Flux<FilePart> files) {
        log.info("Enter to save Post");

        return files.collectList().flatMap(filesList -> {
            Mono<List<String>> imagesPath = Mono.just(List.of());

            if (filesList.size() > 0 && !filesList.get(0).filename().isEmpty()) {
                if (cloudinaryActive) {
                    log.info("CLOUDINARY ACTIVE");
                    log.info("Save files in cloudinary CDN");
                    imagesPath = saveFileInCloudinary(files, postDTO);
                }

                if (!cloudinaryActive) {
                    log.info("CLOUDINARY NOT ACTIVE");
                    log.info("Save files in directory -> " + directoryPath);
                    imagesPath = saveFiles(files);
                }
            }

            return imagesPath.flatMap(imagesPathList -> {
                postDTO.setImagesPaths(imagesPathList);
                postDTO.setCreateAt(LocalDateTime.now());
                Post post = this.dtoToEntity(postDTO);
                return this.postRepository.save(post)
                        .map(this::entityToDto);
            });
        });
    }

    @Override
    public Flux<PostDTO> findAll() {
        log.info("Enter to findAll()");
        return this.postRepository.findAll().map(this::entityToDto);
    }

    @Override
    public Mono<PostDTO> findById(String id) {
        log.info("Enter to findById()");
        return this.postRepository.findById(id).map(this::entityToDto)
                .doOnError(e -> new CustomException(ErrorCode.POST_NOT_FOUND));
    }

    @Override
    public Mono<Void> deleteById(String id) {
        log.info("Enter to deleteById()");
        return this.postRepository.findById(id).map(p -> {
            if (p.getImagesPaths().size() > 0) {
                for (String path : p.getImagesPaths()) {
                    if (path.contains("https")) {
                        // Delete file from cloudinary
                        this.fileService.deleteImageFromCloudinary(path);
                    } else {
                        // Delete file from filesystems
                        this.fileService.deleteFileByPath(path);
                    }
                }
            }
            return p;
        }).flatMap(p -> this.postRepository.deleteById(id)).then()
                .doOnError(e -> new CustomException(ErrorCode.POST_NOT_FOUND));
    }

    @Override
    public Mono<Post> getOne(String id) {
        return this.postRepository.findById(id);
    }

    private Mono<List<String>> saveFileInCloudinary(Flux<FilePart> files, PostDTO postDTO) {
        log.info("Enter to saveFileInCloudinary()");
        return files.flatMap(filePart -> this.fileService.uploadFile(filePart))
                .collectList();
    }

    private Mono<List<String>> saveFiles(Flux<FilePart> files) {
        return files.flatMapSequential(file -> this.fileService.saveImageInDirectory(file, directoryPath))
                .collectList();
    }

}

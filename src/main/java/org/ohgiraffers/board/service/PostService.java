package org.ohgiraffers.board.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.hibernate.sql.Delete;
import org.ohgiraffers.board.domain.dto.*;
import org.ohgiraffers.board.domain.entity.Post;
import org.ohgiraffers.board.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** @Service는 Spring Framework에서 제공하는 어노테이션 중 하나로, 주로 서비스 계층(Service Layer)의 클래스에 사용됩니다.
이 어노테이션의 주요 기능은 다음과 같습니다:
Component Scanning: @Service 어노테이션은 Spring의 Component Scan에 의해 자동으로 빈(Bean)으로 등록됩니다.
즉, Spring이 시작될 때 @Service가 붙은 클래스를 찾아서 ApplicationContext에 빈으로 등록하고, 이 빈은 어플리케이션 내에서 공유되어 사용됩니다.
Business Logic Processing: @Service 어노테이션이 붙은 클래스는 주로 비즈니스 로직을 처리하는 역할을 합니다.
이는 Controller와 Repository 사이의 중간 계층 역할을 하며, 트랜잭션 관리 등의 역할도 수행합니다.
Stereotype Annotation: @Service는 스테레오타입(Stereotype) 어노테이션 중 하나입니다.
이는 코드의 역할을 명시적으로 나타내는데 도움을 주며, @Component, @Repository, @Controller 등과 함께 사용됩니다.
이 어노테이션들은 모두 빈으로 등록되지만, 해당 클래스가 어느 계층에 속하는지를 명확히 나타냅니다.
즉, @Service는 Spring Framework에서 서비스 계층을 나타내고, 비즈니스 로직을 담당하는 클래스에 사용되며, 이를 Spring 컨테이너에 빈으로 등록하는 역할을 합니다.
*/

/** Service를 인터페이스와 구현채로 나누는 이유
 * 1.다형성과 OCP 원칙을 지키기 위해
 * 인터페이스와 구현체가 나누어지면, 구현체는 외부로부터 독립되어, 구현체의 수정이나 확장이 자유로워진다.
 * 2.관습적인 추상화 방식
 * 과거, Spring에서 AOP를 구현 할때 JDK Dynamic Proxy를 사용했는데, 이때 인터페이스가 필수였다.
 * 지금은, CGLB를 기본적으로 포함하여 클래스 기반을 프록시 객체를 생성 할 수 있게 되었다.
 */
/** @Transactional
 * 선언적으로 트렌젝션 관리를 가능하게 해준다.
 * 메서드가 실행되는 동안 모든 데이터베이스 연산을 하나의 트랜잭션으로 묶어 처리한다.
 * 이를통해, 메서드 내에서 데이터베이스 상태를 변경하는 작업들이 모두 성공적으로 완료되면 그 변경사항을 commit하고 하나라도 실패하면 모든 변경사항을 rollback 시켜 관리한다.
 *
 * Transaction
 * 데이터베이스의 상태를 변환시키기 위해 수행하는 작업의 단위
 */

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    @Transactional
    public CreatePostResponse createPost(CreatePostRequest request){

        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .build();

        Post savePost = postRepository.save(post);

        return new CreatePostResponse(savePost.getPostId(), savePost.getTitle(), savePost.getContent());
    }

    public ReadPostResponse readPostById(Long postId) {

        Post foundPost = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("해당 postId로 조회된 게시글이 없습니다."));

        return new ReadPostResponse(foundPost.getPostId(), foundPost.getTitle(), foundPost.getContent());
    }

    @Transactional
    public UpdatePostResponse updatePost(Long postId, UpdatePostRequest request) {

        Post foundPost = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("해당 postId로 조회된 게시글이 없습니다."));

        //Dirty Checking
        //jpa가 알아서 감지하고 수정, 저장하는 곳은 없다.
        foundPost.update(request.getTitle(), request.getContent());

        return new UpdatePostResponse(foundPost.getPostId(), foundPost.getTitle(), foundPost.getContent());
    }

    @Transactional
    public DeletePostResponse deletePost(Long postId){

        Post foundPost = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("해당 postId로 조회된 게시글이 없습니다."));

        postRepository.delete(foundPost);

        return new DeletePostResponse(foundPost.getPostId());
    }

    public Page<ReadPostResponse> readAllPost(Pageable pageable) {

        Page<Post> postsPage = postRepository.findAll(pageable);

        return postsPage.map(post -> new ReadPostResponse(post.getPostId(), post.getTitle(), post.getContent()));
    }
}

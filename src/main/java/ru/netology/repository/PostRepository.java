package ru.netology.repository;

import ru.netology.model.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


// Stub
public class PostRepository {
  private final ConcurrentHashMap<Long, Post> posts = new ConcurrentHashMap<>();
  private final AtomicLong postsCnt = new AtomicLong(0);

  // получить id нового поста
  private long getNewPostId(long id) {
    long rez = id;
    while(posts.containsKey(rez) || rez == 0) {
      rez = postsCnt.incrementAndGet();
    }
    return rez;
  }

  private Post addPost(Post post) {
    // новый id
    long id = getNewPostId(post.getId());
    post.setId(id);
    // добавляем в мар
    posts.put(id, post);
    return post;
  }

  public List<Post> all() {
    return new ArrayList<>(posts.values());
  }

  public Optional<Post> getById(long id) {
    return Optional.ofNullable(posts.get(id));
  }

  public Post save(Post post) {
    long id = post.getId();
    // пост уже присутствует в мар
    if(posts.containsKey(id)) {
      // обновляем
      posts.replace(id, post);
    } else {
      // добавляем
      return addPost(post);
    }
    return post;
  }

  public void removeById(long id) {
    // удаляем
    posts.remove(id);
  }
}

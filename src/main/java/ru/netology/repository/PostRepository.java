package ru.netology.repository;

import ru.netology.model.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;


// Stub
public class PostRepository {
  private final ConcurrentHashMap<Long, Post> posts = new ConcurrentHashMap<>();
  private volatile long postsCnt = 0;

  private synchronized void increaseCnt() {
    postsCnt++;
  }

  private Post addPost(Post post) {
    // новый id
    increaseCnt();
    post.setId(postsCnt);
    // добавляем в мар
    posts.put(postsCnt, post);
    return post;
  }

  public List<Post> all() {
    return new ArrayList<>(posts.values());
  }

  public Optional<Post> getById(long id) {
    return Optional.of(posts.get(id));
  }

  public Post save(Post post) {
    long id = post.getId();
    // пост присутствует в мар
    if(posts.containsKey(id)) {
      // заменяем
      posts.replace(id, post);
      // иначе просто добавляем с новым id
    } else {
      return addPost(post);
    }
    return post;
  }

  public void removeById(long id) {
    // удаляем
    posts.remove(id);
  }
}

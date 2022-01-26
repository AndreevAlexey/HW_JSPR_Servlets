package ru.netology.servlet;

import ru.netology.controller.PostController;
import ru.netology.exception.NotFoundException;
import ru.netology.repository.PostRepository;
import ru.netology.service.PostService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

public class MainServlet extends HttpServlet {
  private PostController controller;
  private final String GET = "GET";
  private final String POST = "POST";
  private final String DELETE = "DELETE";
  private final List<String> validReqTypes = Arrays.asList(GET, POST, DELETE);

  @Override
  public void init() {
    final var repository = new PostRepository();
    final var service = new PostService(repository);
    controller = new PostController(service);
  }

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp) {

    // если деплоились в root context, то достаточно этого
    try {
      final String path = req.getRequestURI();
      final String method = req.getMethod();

      long id = 0L;
      // проверка типа запроса
      if(method == null || !validReqTypes.contains(method) || !path.contains("/api/posts")) {
        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        return;
      }

      // проверка на id в запросе
      if(path.matches("/api/posts/\\d+")) {
        id = Long.parseLong(path.substring(path.lastIndexOf("/") + 1));
      }

      switch (method) {
        // GET
        case GET:
          // есть id
          if (id != 0) controller.getById(id, resp); // получить пост по id
          else controller.all(resp); // получить список всех постов
          break;
        // POST
        case POST:
          controller.save(req.getReader(), resp); // сохранить пост
          break;
        // DELETE
        case DELETE:
          // есть id
          if (id != 0) controller.removeById(id, resp); // удалить пост
          else resp.setStatus(HttpServletResponse.SC_NOT_FOUND); // ошибочный запрос
          break;
      }


    } catch (NotFoundException exp) {
      // ошибочный запрос
      exp.printStackTrace();
      resp.setStatus(HttpServletResponse.SC_NOT_FOUND);

    } catch (Exception e) {
      e.printStackTrace();
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }
}


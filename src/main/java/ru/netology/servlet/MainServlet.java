package ru.netology.servlet;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.netology.constants.ReqType;
import ru.netology.controller.PostController;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

public class MainServlet extends HttpServlet {
  private PostController controller;
  private final List<ReqType> validReqTypes = Arrays.asList(ReqType.GET, ReqType.POST, ReqType.DELETE);

  @Override
  public void init() {
    final var context = new AnnotationConfigApplicationContext("ru.netology");
    controller = context.getBean(PostController.class);
  }

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp) {
    // если деплоились в root context, то достаточно этого
    try {
      final String path = req.getRequestURI();
      final String method = req.getMethod();
      final ReqType reqType = ReqType.getByName(method);
      long id = 0L;
      // проверка типа запроса
      if(reqType == null || !validReqTypes.contains(reqType) || !path.contains("/api/posts")) {
        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        return;
      }
      // проверка на id в запросе
      if(path.matches("/api/posts/\\d+")) {
        id = Long.parseLong(path.substring(path.lastIndexOf("/") + 1));
      }
      // маршрутизатор
      switch (reqType) {
        // GET запрос
        case GET:
          // есть id
          if (id != 0) {
            // получить пост по id
            controller.getById(id, resp);
          } else {
            // получить список всех постов
            controller.all(resp);
          }
          break;
        // POST запрос
        case POST:
          // сохранить пост
          controller.save(req.getReader(), resp);
          break;
        // DELETE запрос
        case DELETE:
          // есть id
          if (id != 0)
            // удалить пост
            controller.removeById(id, resp);
          else
            // ошибочный запрос
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
          break;
        // нет обработчика
        default:
          resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
      }

    } catch (Exception e) {
      e.printStackTrace();
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }
}


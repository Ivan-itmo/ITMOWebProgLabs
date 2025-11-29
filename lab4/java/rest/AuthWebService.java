package rest;

import beans.UserBean;
import entities.UserTable;
import jakarta.ejb.EJB;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Path("/auth")
public class AuthWebService {
    @EJB
    private UserBean userService;
    public static class AuthRequest {
        public String login;
        public String password;
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(AuthRequest req) {
        if (req.login == null || req.password == null || req.login.trim().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Логин и пароль обязательны");
            return Response.status(400).entity(error).build();
        }
        if (userService.loginExists(req.login)) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Пользователь уже существует");
            return Response.status(409).entity(error).build();
        }
        userService.registerUser(req.login, req.password);
        Map<String, String> success = new HashMap<>();
        success.put("message", "Успешная регистрация");
        return Response.ok(success).build();
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@Context HttpServletRequest request, AuthRequest req) {
        if (req.login == null || req.password == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Логин и пароль обязательны");
            return Response.status(400).entity(error).build();
        }
        UserTable user = userService.authenticate(req.login, req.password);
        if (user != null) {
            HttpSession session = request.getSession(true);
            session.setAttribute("userLogin", user.getLogin());

            Map<String, String> success = new HashMap<>();
            success.put("message", "Успешный вход");
            return Response.ok(success).build();
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Неверный логин или пароль");
            return Response.status(401).entity(error).build();
        }
    }

    @GET
    @Path("/status")
    @Produces(MediaType.APPLICATION_JSON)
    public Response status(@Context HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String login = (session != null) ? (String) session.getAttribute("userLogin") : null;
        Map<String, Object> status = new HashMap<>();
        status.put("loggedIn", login != null);
        status.put("login", login);
        return Response.ok(status).build();
    }
}
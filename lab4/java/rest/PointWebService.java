package rest;

import beans.PointBean;
import entities.PointTable;
import jakarta.ejb.EJB;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/check")
public class PointWebService {

    @EJB
    private PointBean pointService;
    public static class PointRequest {
        public double x;
        public double y;
        public double r;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkPoint(@Context HttpServletRequest request, PointRequest req) {
        HttpSession session = request.getSession(false);
        String login = (session != null) ? (String) session.getAttribute("userLogin") : null;
        if (login == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Требуется авторизация");
            return Response.status(401).entity(error).build();
        }
        if (!isValidX(req.x)) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "X должен быть целым числом от -5 до 3");
            return Response.status(400).entity(error).build();
        }
        if (!isValidY(req.y)) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Y должен быть числом от -3 до 3");
            return Response.status(400).entity(error).build();
        }
        if (!isValidR(req.r)) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "R должен быть целым числом от -5 до 3");
            return Response.status(400).entity(error).build();
        }

        boolean hit = checkHit(req.x, req.y, req.r);
        PointTable pointTable = pointService.createPoint(login, req.x, req.y, req.r, hit, LocalDateTime.now());
        Map<String, Object> result = new HashMap<>();
        result.put("hit", hit);
        result.put("pointId", pointTable.getId());
        result.put("timestamp", pointTable.getTimestamp());

        return Response.ok(result).build();
    }

    @GET
    @Path("/history")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHistory(@Context HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String login = (session != null) ? (String) session.getAttribute("userLogin") : null;

        if (login == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Требуется авторизация");
            return Response.status(401).entity(error).build();
        }
        List<PointTable> pointTables = pointService.getUserPoints(login);
        List<Map<String, Object>> history = pointTables.stream()
                .map(pointTable -> {
                    Map<String, Object> pointData = new HashMap<>();
                    pointData.put("x", pointTable.getX());
                    pointData.put("y", pointTable.getY());
                    pointData.put("r", pointTable.getR());
                    pointData.put("hit", pointTable.isHit());
                    pointData.put("timestamp", pointTable.getTimestamp());
                    return pointData;
                })
                .toList();

        return Response.ok(history).build();
    }

    private boolean isValidX(double x) {
        return x == (int) x && x >= -5 && x <= 3;
    }

    private boolean isValidY(double y) {
        return y > -3 && y < 3;
    }

    private boolean isValidR(double r) {
        return r == (int) r && r >= 1 && r <= 3;
    }

    private boolean checkHit(double x, double y, double r) {
        if (x <= 0 && y <= 0 && x >= -r/2 && y >= -r) {
            return true;
        }
        if (x >= 0 && y >= 0 && (x*x + y*y) <= r*r) {
            return true;
        }
        if (x >= 0 && y <= 0 && y >= 0.5 * x - 0.5 * r) {
            return true;
        }

        return false;
    }
}
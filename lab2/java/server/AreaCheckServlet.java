package server;

import result.Result;
import result.Error;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AreaCheckServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String xParam = req.getParameter("x");
        String yParam = req.getParameter("y");
        String rParam = req.getParameter("r");

        req.setAttribute("formX", xParam);
        req.setAttribute("formY", yParam);
        req.setAttribute("formR", rParam);

        if (xParam == null || yParam == null || rParam == null) {
            req.setAttribute("error", new Error("Какой-то из данных пропущен: x, y, r"));
            req.getRequestDispatcher("/page.jsp").forward(req, resp);
            return;
        }

        if (xParam.trim().isEmpty() || yParam.trim().isEmpty() || rParam.trim().isEmpty()) {
            req.setAttribute("error", new Error("Параметры не должны быть пустыми"));
            req.getRequestDispatcher("/page.jsp").forward(req, resp);
            return;
        }

        if (xParam.length() > 8) {
            req.setAttribute("error", new Error("X не длинне 8 символов"));
            req.getRequestDispatcher("/page.jsp").forward(req, resp);
            return;
        }

        double x, y, r;
        try {
            x = Double.parseDouble(xParam);
            y = Double.parseDouble(yParam);
            r = Double.parseDouble(rParam);
        } catch (NumberFormatException e) {
            req.setAttribute("error", new Error("Все параметры должны быть числами"));
            req.getRequestDispatcher("/page.jsp").forward(req, resp);
            return;
        }

        if (Double.isNaN(x) || Double.isInfinite(x) ||
                Double.isNaN(y) || Double.isInfinite(y) ||
                Double.isNaN(r) || Double.isInfinite(r)) {
            req.setAttribute("error", new Error("Parameters must be finite numbers"));
            req.getRequestDispatcher("/page.jsp").forward(req, resp);
            return;
        }

        if (x < -5 || x > 5) {
            req.setAttribute("error", new Error("X должен быть между -5 и 5"));
            req.getRequestDispatcher("/page.jsp").forward(req, resp);
            return;
        }

        if (r != 1 && r != 2 && r != 3 && r != 4 && r != 5) {
            req.setAttribute("error", new Error("R должен быть 1, 2, 3, 4 или 5"));
            req.getRequestDispatcher("/page.jsp").forward(req, resp);
            return;
        }

        boolean hit = checkHit(x, y, r);

        @SuppressWarnings("unchecked")
        List<Result> results = (List<Result>) getServletContext().getAttribute("results");
        if (results == null) {
            results = new ArrayList<>();
            getServletContext().setAttribute("results", results);
        }
        results.add(0, new Result(x, y, r, hit, System.currentTimeMillis()));

        req.setAttribute("lastR", r);
        req.getRequestDispatcher("/page.jsp").forward(req, resp);
    }

    private boolean checkHit(double x, double y, double r) {
        boolean inRectangle = (x >= 0 && x <= r / 2.0 && y >= 0 && y <= r);
        boolean inCircle = (x <= 0 && y <= 0 && (x * x + y * y) <= r * r);
        boolean inTriangle = (x >= 0 && y <= 0 && y - x >= -r / 2.0);
        return inRectangle || inCircle || inTriangle;
    }
}
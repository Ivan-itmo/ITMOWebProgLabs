<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="result.Result" %>
<%@ page import="result.Error" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%
    @SuppressWarnings("unchecked")
    List<Result> results = (List<Result>) application.getAttribute("results");
    boolean hasResults = results != null && !results.isEmpty();
%>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Point Area Check</title>
    <link rel="stylesheet" href="css/style.css">

    <% if (hasResults) { %>
    <script>
        const lastR = <%= request.getAttribute("lastR") != null ? request.getAttribute("lastR") : "1" %>;
        const allResults = [
            <%
            for (Result res : results) {
            %>
            {
                x: <%= res.getX() %>,
                y: <%= res.getY() %>,
                r: <%= res.getR() %>,
                hit: <%= res.isHit() ? "true" : "false" %>
            },
            <%
            }
            %>
        ];
    </script>
    <script src="js/graph-with-points.js"></script>
    <% } else { %>
    <script src="js/graph.js"></script>
    <% } %>

    <script src="js/graphclick.js"></script>
    <script src="js/validator.js"></script>
</head>
<body>
<div class="container">
    <h1>Гладышев Иван Станиславович, P3213, 465544</h1>

    <table class="layout-table">
        <tr>
            <td class="form-column">
                <h2>Форма</h2>
                <form id="checkForm" action="." method="GET">
                    <div class="form-group">
                        <label for="xInput">X:</label>
                        <input type="text" name="x" id="xInput" placeholder="от -5 до 5" value="${param.x}" required maxlength="8">
                    </div>

                    <div class="form-group">
                        <label for="ySelect">Y:</label>
                        <select name="y" id="ySelect" required>
                            <option value="" disabled ${param.y == null ? "selected" : ""}>Выберите Y</option>
                            <option value="-2" ${param.y == "-2" ? "selected" : ""}>-2</option>
                            <option value="-1.5" ${param.y == "-1.5" ? "selected" : ""}>-1.5</option>
                            <option value="-1" ${param.y == "-1" ? "selected" : ""}>-1</option>
                            <option value="-0.5" ${param.y == "-0.5" ? "selected" : ""}>-0.5</option>
                            <option value="0" ${param.y == "0" ? "selected" : ""}>0</option>
                            <option value="0.5" ${param.y == "0.5" ? "selected" : ""}>0.5</option>
                            <option value="1" ${param.y == "1" ? "selected" : ""}>1</option>
                            <option value="1.5" ${param.y == "1.5" ? "selected" : ""}>1.5</option>
                            <option value="2" ${param.y == "2" ? "selected" : ""}>2</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label>R:</label>
                        <div class="checkbox-group">
                            <label class="checkbox-label">
                                <input type="radio" name="r" value="1" id="r1" ${param.r == "1" ? "checked" : ""}>
                                <span>1</span>
                            </label>
                            <label class="checkbox-label">
                                <input type="radio" name="r" value="2" id="r2" ${param.r == "2" ? "checked" : ""}>
                                <span>2</span>
                            </label>
                            <label class="checkbox-label">
                                <input type="radio" name="r" value="3" id="r3" ${param.r == "3" ? "checked" : ""}>
                                <span>3</span>
                            </label>
                            <label class="checkbox-label">
                                <input type="radio" name="r" value="4" id="r4" ${param.r == "4" ? "checked" : ""}>
                                <span>4</span>
                            </label>
                            <label class="checkbox-label">
                                <input type="radio" name="r" value="5" id="r5" ${param.r == "5" ? "checked" : ""}>
                                <span>5</span>
                            </label>
                        </div>
                    </div>

                    <button type="submit">Проверить</button>
                </form>
            </td>

            <td class="graph-column">
                <h2>Область попадания</h2>
                <div class="graph">
                    <canvas id="areaGraph" width="450" height="450"></canvas>
                </div>
            </td>
        </tr>
    </table>

    <%
        Error error = (Error) request.getAttribute("error");
        if (error != null) {
    %>
    <div id="errorMessage" class="error" style="display: block;">
        <%= error.getMessage() %>
    </div>
    <%
    } else {
    %>
    <div id="errorMessage" class="error" style="display: none;"></div>
    <%
        }
    %>

    <div id="results">
        <h2>Результаты</h2>
        <table id="resultsTable">
            <thead>
            <tr>
                <th>X</th>
                <th>Y</th>
                <th>R</th>
                <th>Результат</th>
                <th>Время запроса</th>
            </tr>
            </thead>
            <tbody>
            <%
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
                if (results != null) {
                    for (Result res : results) {
            %>
            <tr class="<%= res.isHit() ? "hit" : "miss" %>">
                <td><%= res.getX() %></td>
                <td><%= res.getY() %></td>
                <td><%= res.getR() %></td>
                <td><%= res.isHit() ? "HIT" : "MISS" %></td>
                <td><%= sdf.format(new Date(res.getTime())) %></td>
            </tr>
            <%
                }
            } %>
            </tbody>
        </table>
    </div>

</div>


</body>
</html>
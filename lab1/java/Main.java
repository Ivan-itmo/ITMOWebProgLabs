package mains;

import com.fastcgi.*;

import java.util.LinkedHashMap;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Main {
    public static void main(String[] args) {
        FCGIInterface fcgi = new FCGIInterface();
        while (true) {
            try {
                int result = fcgi.FCGIaccept();
                if (result < 0) {
                    Thread.sleep(100);
                    continue;
                }

                if (FCGIInterface.request == null) {
                    continue;
                }

                //String method = FCGIInterface.request.params.getProperty("REQUEST_METHOD");
                handleGetRequest();
            } catch (Exception e) {
                sendJsonError("Error");
            }
        }
    }

    private static void handleGetRequest() {
        String query = FCGIInterface.request.params.getProperty("QUERY_STRING");
        LinkedHashMap<String, String> params = parseQuery(query);

        if (!params.containsKey("x") || !params.containsKey("y") || !params.containsKey("r")) {
            sendJsonError("Missing x, y or r");
            return;
        }

        String xStr = params.get("x");
        String yStr = params.get("y");
        String rStr = params.get("r");

        // Валидация x
        int x;
        try {
            x = Integer.parseInt(xStr);
        } catch (NumberFormatException e) {
            sendJsonError("Invalid x value: must be integer");
            return;
        }
        if (x < -3 || x > 5) {
            sendJsonError("x must be in range [-3, 5]");
            return;
        }
        // Дополнительно: если нужно строго по списку, раскомментируй:
    /*
    if (!(x == -3 || x == -2 || x == -1 || x == 0 || x == 1 || x == 2 || x == 3 || x == 4 || x == 5)) {
        sendJsonError("x must be one of: -3, -2, -1, 0, 1, 2, 3, 4, 5");
        return;
    }
    */

        // Валидация y
        BigDecimal yDecimal;
        if (yStr == null || yStr.length() > 8) {
            sendJsonError("Y не длиннеее 8 символов");
            return;
        }

        try {
            yDecimal = new BigDecimal(yStr);
        } catch (NumberFormatException e) {
            sendJsonError("Invalid y value: must be a number");
            return;
        }
        if (yDecimal.compareTo(BigDecimal.valueOf(-3)) < 0 || yDecimal.compareTo(BigDecimal.valueOf(3)) > 0) {
            sendJsonError("y must be between -3 and 3 (inclusive)");
            return;
        }

        // Валидация r
        BigDecimal rDecimal;
        try {
            rDecimal = new BigDecimal(rStr);
        } catch (NumberFormatException e) {
            sendJsonError("Invalid r value: must be a number");
            return;
        }
        BigDecimal[] allowedR = {
                BigDecimal.valueOf(1),
                BigDecimal.valueOf(1.5),
                BigDecimal.valueOf(2),
                BigDecimal.valueOf(2.5),
                BigDecimal.valueOf(3)
        };
        boolean validR = false;
        for (BigDecimal allowed : allowedR) {
            if (rDecimal.compareTo(allowed) == 0) {
                validR = true;
                break;
            }
        }
        if (!validR) {
            sendJsonError("r must be one of: 1, 1.5, 2, 2.5, 3");
            return;
        }

        // Приведение точности
        yDecimal = yDecimal.setScale(30, RoundingMode.HALF_UP);
        rDecimal = rDecimal.setScale(30, RoundingMode.HALF_UP);

        // Проверка попадания
        boolean hit = checkHit(x, yDecimal, rDecimal);

        // Ответ
        System.out.println("Content-Type: application/json");
        System.out.println();
        System.out.println(resp(hit ? "HIT" : "MISS", xStr, yStr, rStr));
        System.out.flush();
    }

    private static LinkedHashMap<String, String> parseQuery(String query) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        for (String pair : query.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                map.put(kv[0], kv[1]);
            }
        }
        return map;
    }

    public static boolean checkHit(int x, BigDecimal y, BigDecimal r) {
        BigDecimal xDec = BigDecimal.valueOf(x);

        boolean inCircle = false;
        if (x <= 0 && y.compareTo(BigDecimal.ZERO) >= 0) {
            BigDecimal xSq = xDec.multiply(xDec);
            BigDecimal ySq = y.multiply(y);
            BigDecimal rSq = r.multiply(r);
            inCircle = xSq.add(ySq).compareTo(rSq) <= 0;
        }

        boolean inTriangle = false;
        if (x >= 0 && y.compareTo(BigDecimal.ZERO) >= 0) {
            inTriangle = xDec.add(y).compareTo(r) <= 0;
        }

        boolean inRectangle = false;
        if (x >= 0 && y.compareTo(BigDecimal.ZERO) <= 0) {
            BigDecimal halfR = r.divide(BigDecimal.valueOf(2), 10, RoundingMode.HALF_UP);
            BigDecimal negativeHalfR = halfR.negate();
            inRectangle = xDec.compareTo(r) <= 0 && y.compareTo(negativeHalfR) >= 0;
        }

        return inCircle || inTriangle || inRectangle;
    }

    private static String resp(String result, String x, String y, String r) {
        return String.format("{\"isShoot\": \"%s\", \"x\": %s, \"y\": %s, \"r\": %s}", result, x, y, r);
    }

    private static void sendJsonError(String message) {
        System.out.println("Content-Type: application/json");
        System.out.println();
        System.out.println(String.format("{\"error\": \"%s\"}", message));
        System.out.flush();
    }
}
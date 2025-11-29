package beans;

import entities.PointTable;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class PointBean {

    @PersistenceContext(unitName = "derby-unit")
    private EntityManager em;

    public PointTable createPoint(String login, double x, double y, double r, boolean hit, LocalDateTime timestamp) {
        PointTable pointTable = new PointTable();
        pointTable.setLogin(login);
        pointTable.setX(x);
        pointTable.setY(y);
        pointTable.setR(r);
        pointTable.setHit(hit);
        pointTable.setTimestamp(timestamp);
        em.persist(pointTable);
        return pointTable;
    }

    public List<PointTable> getUserPoints(String login) {
        return em.createQuery("SELECT p FROM PointTable p WHERE p.login = :login ORDER BY p.timestamp DESC", PointTable.class)
                .setParameter("login", login)
                .getResultList();
    }
}
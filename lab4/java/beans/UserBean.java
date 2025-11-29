package beans;

import entities.UserTable;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.NoResultException;
import org.mindrot.jbcrypt.BCrypt;

@Stateless
public class UserBean {

    @PersistenceContext(unitName = "derby-unit")
    private EntityManager em;

    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public boolean checkPassword(String password, String storedHash) {
        return BCrypt.checkpw(password, storedHash);
    }
    public boolean loginExists(String login) {
        try {
            UserTable user = em.createQuery("SELECT u FROM UserTable u WHERE u.login = :login", UserTable.class)
                    .setParameter("login", login)
                    .getSingleResult();
            return true;
        } catch (NoResultException e) {
            return false;
        }
    }

    public void registerUser(String login, String plainPassword) {
        UserTable user = new UserTable();
        user.setLogin(login);
        user.setPasswordHash(hashPassword(plainPassword));
        em.persist(user);
        em.flush();
    }

    public UserTable authenticate(String login, String plainPassword) {
        try {
            UserTable user = em.createQuery("SELECT u FROM UserTable u WHERE u.login = :login", UserTable.class)
                    .setParameter("login", login)
                    .getSingleResult();
            if (checkPassword(plainPassword, user.getPasswordHash())) {
                return user;
            }
            return null;
        } catch (NoResultException e) {
            return null;
        }
    }
}
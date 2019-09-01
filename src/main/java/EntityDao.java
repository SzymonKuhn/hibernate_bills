import org.hibernate.*;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EntityDao {
    public <T extends EntityInterface> boolean insertOrUpdate(T entity) {
        SessionFactory sessionFactory = HibernateUtils.getSessionFactory();
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            session.saveOrUpdate(entity);

            transaction.commit();
        } catch (HibernateException he) {
            if (transaction != null) {
                transaction.rollback();
                return false;
            }
        }
        return true;
    }

    public List<Invoice> getUnpaidInvoices() {
        List<Invoice> unpaidInvoices = new ArrayList<>();

        SessionFactory sessionFactory = HibernateUtils.getSessionFactory();
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder(); //kreowanie zapytania i klauzuli where
            CriteriaQuery<Invoice> query = criteriaBuilder.createQuery(Invoice.class); //obiekt reprezentujący zapytanie
            Root<Invoice> invoiceRoot = query.from(Invoice.class); // klasa preprezentująca tabelę

            query.select(invoiceRoot).where(criteriaBuilder.isFalse(invoiceRoot.get("isPaid"))); // wykonanie selecta z klauzulą where

            unpaidInvoices.addAll(session.createQuery(query).list());
        }
        return unpaidInvoices;
    }

    public List<Invoice> getInvoicesFromLastDay() {
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime yesterday = today.minusDays(1);

        List<Invoice> yesterdayInvoices = new ArrayList<>();
        SessionFactory sessionFactory = HibernateUtils.getSessionFactory();
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Invoice> query = criteriaBuilder.createQuery(Invoice.class);
            Root<Invoice> root = query.from(Invoice.class);

            query.select(root).where(criteriaBuilder.between(root.get("dateTimeCreated"), yesterday, today));

            yesterdayInvoices.addAll(session.createQuery(query).list());
        }
        return yesterdayInvoices;
    }

    public List<Double> getSumFromTodaysInvoices() {
        List<Double> sumList = new ArrayList<>();
        LocalDateTime today = LocalDateTime.now().withNano(0).withSecond(0).withMinute(0).withHour(0);
        LocalDateTime tommorow = today.plusDays(1);

        SessionFactory sessionFactory = HibernateUtils.getSessionFactory();
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Double> query = criteriaBuilder.createQuery(Double.class);
            Root<Invoice> root = query.from(Invoice.class);

            query.select(criteriaBuilder.sum(root.get("sum"))).where((criteriaBuilder.between(root.get("dateTimeCreated"), today, tommorow)));

            sumList.addAll(session.createQuery(query).getResultList()); //czy musi być zwracana lista, nie można otzymać samego wyniku???
        }
        return sumList;
    }


    public <T extends EntityInterface> List<T> getAll(Class<T> classT) {
        List<T> entities = new ArrayList<>();

        SessionFactory sessionFactory = HibernateUtils.getSessionFactory();
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(classT);
            Root<T> entityRoot = criteriaQuery.from(classT);
            CriteriaQuery<T> all = criteriaQuery.select(entityRoot);

            TypedQuery<T> allQuery = session.createQuery(all);
            entities.addAll(allQuery.getResultList());

        } catch (HibernateException he) {
            System.err.println(he.getMessage());
            he.printStackTrace();
        }
        return entities;
    }

    public <T extends EntityInterface> Optional<T> getEntityById(Class<T> classT, long id) {
        SessionFactory sessionFactory = HibernateUtils.getSessionFactory();
        Optional<T> optionalT;
        try (Session session = sessionFactory.openSession()) {
            optionalT = Optional.ofNullable(session.get(classT, id));
        }
        return optionalT;
    }
}

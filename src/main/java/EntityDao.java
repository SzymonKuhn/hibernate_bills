import org.hibernate.*;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EntityDao {
    public <T extends EntityInterface> boolean insertOrUpdate(T entity) {
        SessionFactory sessionFactory = HibernateUtils.getSessionFactory();
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            session.update(entity);
//            session.saveOrUpdate(entity);

            transaction.commit();
        } catch (HibernateException he) {
            if (transaction != null) {
                transaction.rollback();
                return false;
            }
        }
        return true;
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

    public <T extends EntityInterface> Optional<T> getEntityById (Class<T> classT, long id) {
        SessionFactory sessionFactory = HibernateUtils.getSessionFactory();
        Optional<T> optionalT;
        try (Session session = sessionFactory.openSession()) {
             optionalT = Optional.ofNullable(session.get(classT, id));
        }
        return optionalT;
    }

//    public Double getSumById (long id) {
//        Optional<Invoice> invoiceOptional = getEntityById(Invoice.class, id);
//        if (invoiceOptional.isPresent()) {
//            return  invoiceOptional.get().getSum();
//        } else {
//            return null;
//        }
//    }

    public List<Product> getProductsFromInvoice (long id) {
        List<Product> products = new ArrayList<>();
        Optional<Invoice> invoiceOptional = getEntityById(Invoice.class, id);
        if (invoiceOptional.isPresent()) {
            products.addAll(invoiceOptional.get().getProducts());
        }
        return products;
    }
}

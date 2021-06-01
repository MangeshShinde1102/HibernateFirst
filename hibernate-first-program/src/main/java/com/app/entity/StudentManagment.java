package com.app.entity;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.ResultTransformer;

public class StudentManagment {

	static SessionFactory sessionFactory = null;

	static {
		Configuration cfg = new Configuration(); // parse the hibernate.cfg.xml
		cfg.configure(); // "mysql.cfg.xml"

		StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
		builder.applySettings(cfg.getProperties());

		StandardServiceRegistry serviceRegistry = builder.build();

		sessionFactory = cfg.buildSessionFactory(serviceRegistry); // singleton
	}

	public void saveStudent() {

		// transient state
		Student student = new Student();
		student.setName("Rahul");
		student.setAddress("Kharadi");
		student.setEmail("rahul@gmail.com");

		// persistent state
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Integer id = (Integer) session.save(student);
		System.out.println("primary key : " + id);
		tx.commit();
		Boolean result = tx.wasCommitted();

		if (result)
			System.out.println("saved");
		else
			System.out.println("failed");
		// detached state
		session.close();

	}

	public void delete() {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		// persistent state
		Student student = (Student) session.get(Student.class, 2);
		session.delete(student);
		tx.commit();
		Boolean result = tx.wasCommitted();

		if (result)
			System.out.println("succes");
		else
			System.out.println("failed");
		// detached state
		session.close();
	}

	public void update() {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		// persistent state
		Student student = (Student) session.get(Student.class, 1);
		student.setName("Rahul Moundekar");
		session.update(student);
		tx.commit();
		Boolean result = tx.wasCommitted();

		if (result)
			System.out.println("updated");
		else
			System.out.println("update failed");
		// detached state
		session.close();

	}

	public void persistStudent() {
		// transient state
		Student student = new Student();
		student.setName("Mayur");
		student.setAddress("Karvenagar");
		student.setEmail("mayur@gmail.com");

		// persistent state
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		session.persist(student);
		tx.commit();
		Boolean result = tx.wasCommitted();

		if (result)
			System.out.println("persist");
		else
			System.out.println("failed");
		// detached state
		session.close();
	}

	public void saveOrUpdateStudent() {
		// transient state
		Student student = new Student();
		student.setId(3);
		student.setName("Sarika");
		student.setAddress("Karvenagar");
		student.setEmail("Sarika@gmail.com");

		// persistent state
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		// if id is null then save or if id is present in persistent object the it will
		// update the records
		session.saveOrUpdate(student);

		tx.commit();
		Boolean result = tx.wasCommitted();

		if (result)
			System.out.println("save or update");
		else
			System.out.println("failed");
		// detached state
		session.close();

	}

	public void getStudent() {
		Session session = sessionFactory.openSession();
		Student student = (Student) session.get(Student.class, 1); // immediately hit db and return object

		System.out.println("first " + student);
		
		Student student1 = (Student) session.get(Student.class, 1); // immediately hit db and return object

		System.out.println("second " + student1);

	}

	public void loadStudent() {
		Session session = sessionFactory.openSession();
		Student student = (Student) session.load(Student.class, 1); // immediately hit db and return object
		System.out.println("student id : " + student.getId());
		System.out.println("student id : " + student.getName());
		session.close();
		Student student1 = (Student) session.load(Student.class, 1); // immediately hit db and return object
		System.out.println("student id : " + student1.getId());
		System.out.println("student id : " + student1.getName());
	}

	@SuppressWarnings("unchecked")
	public void selectAll() {
		Session session = sessionFactory.openSession();
		Criteria cr = session.createCriteria(Student.class);
		List<Student> list=cr.list();
	}
	
	
	@SuppressWarnings("unchecked")
	public void restrictionStudent() {
		Session session = sessionFactory.openSession();
		Criteria cr = session.createCriteria(Student.class);
		
		//cr.add(Restrictions.eq("name", "Rahul")); //where name="rahul";
		cr.add(Restrictions.lt("id", 1));
		
		//cr.add(Restrictions.eq("email", "abc@gmail.com")); 
		cr.list().forEach(System.out::println);
		//Student student=(Student)cr.uniqueResult();
			// org.hibernate.NonUniqueResultException: query did not return a unique result: 2
		
		//System.out.println(student);
	}
	
	@SuppressWarnings("serial")
	public void projectionStudent() {
		Session session = sessionFactory.openSession();
		Criteria cr = session.createCriteria(Student.class);
		
		Projection p1=Projections.property("name");
		Projection p2=Projections.property("id");
		
		ProjectionList projecttionList=Projections.projectionList();
		projecttionList.add(p1);
		projecttionList.add(p2);
		
		cr.setProjection(projecttionList);
		
		cr.setResultTransformer(new ResultTransformer() {
			
			@Override
			public Object transformTuple(Object[] tuple, String[] aliases) {
				Student s=new Student();
				
				s.setName((String)tuple[0]);
				
				s.setId((Integer)tuple[1]);
				
				
				return s;
			}
			
			@Override
			public List transformList(List collection) {
				return collection;
			}
		});
		
		cr.list().forEach(System.out::println);
		
	}
	
	public void projectionAggregation() {
		Session session = sessionFactory.openSession();
		Criteria cr = session.createCriteria(Student.class);
		
		cr.setProjection(Projections.max("id"));
		Long sum=(Long)cr.uniqueResult();
		System.out.println(sum);
		
	}
	
	@SuppressWarnings("unchecked")
	public void orderStudent() {
		Session session = sessionFactory.openSession();
		Criteria cr = session.createCriteria(Student.class);
		
		cr.addOrder(Order.desc("id"));
		cr.list().forEach(System.out::println);
		
	}
	
	@SuppressWarnings("unchecked")
	public void pagination() {
		Session session = sessionFactory.openSession();
		Criteria cr = session.createCriteria(Student.class);
		
		cr.setFirstResult(0); //starting with 
		cr.setMaxResults(3); //max records 3 limit 3
		cr.list().forEach(System.out::println);
	}
	
	public void isNull() {
		Session session = sessionFactory.openSession();
		Criteria cr = session.createCriteria(Student.class);
		
		cr.add(Restrictions.isNull("address"));
		cr.list().forEach(System.out::println);
		
	}
	public void isEmtpy() {
		Session session = sessionFactory.openSession();
		Criteria cr = session.createCriteria(Student.class);
		
		cr.add(Restrictions.isEmpty(""));
		cr.list().forEach(System.out::println);
	}
	
	
	public void hql() {
		Session session = sessionFactory.openSession();
		Query query=session.createQuery("select name, email from Student where id=:sid");
		query.setParameter("sid", 1);
		query.setResultTransformer(new ResultTransformer() {
			@Override
			public Student transformTuple(Object[] tuple, String[] aliases) {
				Student student=new Student();
				student.setName((String)tuple[0]);
				student.setEmail((String)tuple[1]);
				return student;
			}
			
			@Override
			public List<Student> transformList(List list) {
				return list;
			}
		});
		query.list().forEach(System.out::println);
		session.close();
	}
	
	public void paginationHQL() {
		Session session = sessionFactory.openSession();
		Query query=session.createQuery("from Student");
		query.setFirstResult(0);
		query.setMaxResults(3);
		query.list().forEach(System.out::println);
		session.close();
	}
	
	public void sql() {
		Session session = sessionFactory.openSession();
		//SQLQuery query=session.createSQLQuery("select * from Student").addEntity(Student.class);
		SQLQuery query=session.createSQLQuery("select name, email from Student where id=:sid");
		query.setParameter("sid", 1);
		query.setResultTransformer(new ResultTransformer() {
			@Override
			public Student transformTuple(Object[] tuple, String[] aliases) {
				Student student=new Student();
				student.setName((String)tuple[0]);
				student.setEmail((String)tuple[1]);
				return student;
			}
			
			@Override
			public List<Student> transformList(List list) {
				return list;
			}
		});
		query.list().forEach(System.out::println);
		
	}
	
	
	
	
	

	public static void main(String[] args) {
		StudentManagment studentManagment = new StudentManagment();
		// studentManagment.saveStudent();
		// studentManagment.delete();
		// studentManagment.update();
		// studentManagment.persistStudent();
		// studentManagment.saveOrUpdateStudent();
		//studentManagment.loadStudent();
		//studentManagment.selectAll();
		studentManagment.sql();
	}

}

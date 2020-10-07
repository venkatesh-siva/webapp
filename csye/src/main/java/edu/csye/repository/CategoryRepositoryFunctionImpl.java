package edu.csye.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import edu.csye.model.Category;

public class CategoryRepositoryFunctionImpl implements CategoryRepositoryFunction {

	@PersistenceContext
    EntityManager entityManager;

public Category getCategoryByName(String category) {
	Query query = entityManager.createNativeQuery("SELECT * FROM category  WHERE category = :category", Category.class);

    query.setParameter("category", category);
    //System.out.println(query.getSingleResult());
    Category categoryData = (Category) query.getResultList().stream().findFirst().orElse(null);
    return categoryData;
}
}

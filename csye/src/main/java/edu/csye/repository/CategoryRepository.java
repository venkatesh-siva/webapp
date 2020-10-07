package edu.csye.repository;

import org.springframework.data.repository.CrudRepository;

import edu.csye.model.Category;

public interface CategoryRepository extends CrudRepository<Category, String>  {
	
	Category findByCategory(String category);

}

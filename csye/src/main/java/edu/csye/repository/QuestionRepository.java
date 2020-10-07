package edu.csye.repository;

import org.springframework.data.repository.CrudRepository;

import edu.csye.model.Question;

public interface QuestionRepository extends CrudRepository<Question, String>  {

}

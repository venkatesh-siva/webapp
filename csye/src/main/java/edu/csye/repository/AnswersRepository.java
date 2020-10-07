package edu.csye.repository;

import org.springframework.data.repository.CrudRepository;

import edu.csye.model.Answer;

public interface AnswersRepository extends CrudRepository<Answer, String> {

}
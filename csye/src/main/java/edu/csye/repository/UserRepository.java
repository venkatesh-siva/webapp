package edu.csye.repository;

import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import edu.csye.model.User;
@Primary
public interface UserRepository extends JpaRepository<User, Integer>, UserRepositoryFunctions {
	@Transactional
    @Modifying
    @Query(value = "UPDATE user u set first_name =?1 , last_name =?2 where u.email_address = ?3", nativeQuery = true)
    int updateUser(String firstName, String lastName, String email);
	
}

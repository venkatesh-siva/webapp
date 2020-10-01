package edu.csye.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import edu.csye.model.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UserRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepositoryFunctions userRepository;

    @Test
    public void getUserByEmailTest() {
        User user = new User();
        user.setUsername("test@test.com");
        entityManager.persist(user);
        entityManager.flush();

        User userDetails = userRepository.getUserByEmail(user.getUsername());

        assertThat(userDetails.getUsername())
                .isEqualTo(user.getUsername());
    }
}

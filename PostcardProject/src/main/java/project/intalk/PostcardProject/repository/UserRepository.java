package project.intalk.PostcardProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.intalk.PostcardProject.personas.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);
}

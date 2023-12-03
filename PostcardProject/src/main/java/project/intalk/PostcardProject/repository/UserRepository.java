package project.intalk.PostcardProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.intalk.PostcardProject.personas.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
}

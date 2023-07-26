package snack.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;

import snack.domain.user.User;

public interface UserRepository extends JpaRepository<User, String> {

}

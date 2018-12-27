package me.linx.vchat.repository;

import me.linx.vchat.bean.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    User findByEmail(@NotNull String email);

    int countByEmail(@NotNull String email);
}

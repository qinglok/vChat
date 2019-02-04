package me.linx.vchat.repository;

import me.linx.vchat.bean.TokenRecord;
import me.linx.vchat.bean.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;

@Repository
public interface TokenRecordRepository extends CrudRepository<TokenRecord, Long> {

    int countByToken(@NotNull String token);

    int countByUser(@NotNull User user);

    void deleteByUser(@NotNull User user);

    void deleteByUser_Id(@NotNull Long userId);
}

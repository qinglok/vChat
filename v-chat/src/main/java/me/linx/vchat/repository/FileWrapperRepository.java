package me.linx.vchat.repository;

import me.linx.vchat.bean.FileWrapper;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;

@Repository
public interface FileWrapperRepository extends CrudRepository<FileWrapper, Long> {

    FileWrapper findByMd5(@NotNull String md5);

    FileWrapper findByName(@NotNull String  name);
}

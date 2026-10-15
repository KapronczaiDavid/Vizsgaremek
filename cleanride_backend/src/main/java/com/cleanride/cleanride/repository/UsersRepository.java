package com.cleanride.cleanride.repository;

import com.cleanride.cleanride.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Integer> {

    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);

    @Query(value = "CALL create_user(:name, :email, :phone, :hash)", nativeQuery = true)
    Users createUserProc(@Param("name") String name, @Param("email") String email,
                         @Param("phone") String phone, @Param("hash") String hash);

    @Query(value = "CALL get_all_users()", nativeQuery = true)
    List<Users> getAllUsersProc();

    @Query(value = "CALL get_user_by_id(:id)", nativeQuery = true)
    Optional<Users> getUserByIdProc(@Param("id") Integer id);

    @Query(value = "CALL get_user_by_email(:email)", nativeQuery = true)
    Optional<Users> getUserByEmailProc(@Param("email") String email);

    @Modifying(clearAutomatically = true)
    @Query(value = "CALL update_user(:id, :name, :email, :phone)", nativeQuery = true)
    void updateUserProc(@Param("id") Integer id, @Param("name") String name,
                        @Param("email") String email, @Param("phone") String phone);

    @Modifying(clearAutomatically = true)
    @Query(value = "CALL delete_user(:id)", nativeQuery = true)
    void deleteUserProc(@Param("id") Integer id);
}
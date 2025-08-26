package com.nayan.appointment.repository;

import com.nayan.appointment.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>
{
	@Query("""
	    SELECT u FROM User u
	    WHERE u.companyId = :companyId
	      AND (u.emailId = :email OR u.contactNumber = :contactNumber)
	    ORDER BY CASE WHEN u.emailId = :email THEN 0 ELSE 1 END
	    LIMIT 1
	""")
	Optional<User> findUserForCompany(@Param("companyId") Long companyId,
	                                  @Param("email") String email,
	                                  @Param("contactNumber") String contactNumber);
}

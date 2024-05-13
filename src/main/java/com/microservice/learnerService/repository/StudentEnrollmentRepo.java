    package com.microservice.learnerService.repository;

    import com.microservice.learnerService.model.data.StudentEnrollment;
    import com.microservice.learnerService.model.data.StudentEnrollmentId;

    import org.springframework.data.mongodb.repository.MongoRepository;	
    import org.springframework.data.mongodb.repository.Query;
    import org.springframework.stereotype.Repository;

    import java.util.List;
    import java.util.Optional;

    @Repository
    public interface StudentEnrollmentRepo extends MongoRepository<StudentEnrollment, StudentEnrollmentId> {


        @Query("{'_id.code':?0}")
        Optional<StudentEnrollment> findByCode(String code);

        @Query("{'_id.studentUserName':?0}")
        Optional <StudentEnrollment> findByStudentId(String studentId);

        @Query("{'_id.studentUserName':?0}")
        List<StudentEnrollment> findByCurrentUsereId(String studentId);
        @Query("{'_id.code':?0, '_id.studentUserName': ?1}")
        Optional<StudentEnrollment> findByCodeAndStudentUserName(String code, String studentUserName);

        @Query("{'_id.code':?0,'_id.studentPeriod':?1}")
        List<StudentEnrollment> findByCodeStudents(String code);

        @Query("{'_id.code':?0, '_id.studentUserName': ?1}")
        Optional <StudentEnrollment> findByStudentIdCourse(String code, String studentUserName);

        @Query("{'_id.studentPeriod':?0}")
        Optional <StudentEnrollment> findByStudentPeriod(String studentPeriod);
        
        
        @Query("{'courseId': ?0, '_id.studentUserName': ?1}")
        Optional<StudentEnrollment> findByStudentIdAndCourseId(String courseId, String studentId);

    }

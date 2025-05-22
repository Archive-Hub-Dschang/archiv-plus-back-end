package
        com.lde.academicservice.repositories;

import com.lde.academicservice.models.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

}

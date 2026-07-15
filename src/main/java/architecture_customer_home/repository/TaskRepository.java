package architecture_customer_home.repository;

import architecture_customer_home.model.Tasks;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Tasks, Integer> {
}

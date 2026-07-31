package architecture_customer_home;

import architecture_customer_home.dto.TaskDto;
import architecture_customer_home.enums.Priority;
import architecture_customer_home.enums.TaskStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ArchitectureCustomerDemoApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void contextLoads() {
	}

	@Test
	void taskCrudFlow() throws Exception {
		TaskDto request = new TaskDto(null, "Study Spring Boot CRUD", false, Priority.MEDIUM, TaskStatus.PENDING, LocalDate.now().plusDays(7), "unnasigned");


		String createdResponse = mockMvc.perform(post("/api/tasks/save")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.description").value("Study Spring Boot CRUD"))
				.andExpect(jsonPath("$.completed").value(false))
				.andReturn()
				.getResponse()
				.getContentAsString();

		TaskDto createdTask = objectMapper.readValue(createdResponse, TaskDto.class);

		mockMvc.perform(get("/api/tasks/getAll"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)));

		//TaskDto updateRequest = new TaskDto(null,"Study Spring Boot CRUD", false, Priority.MEDIUM, TaskStatus.PENDING, LocalDate.now().plusDays(7),"unnasigned");
		TaskDto updateRequest = new TaskDto(null,"Practice DTO and service layers", true, Priority.MEDIUM, TaskStatus.PENDING, LocalDate.now().plusDays(7),"unnasigned");

		mockMvc.perform(put("/api/tasks/{id}", createdTask.id())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(updateRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.description").value("Practice DTO and service layers"))
				.andExpect(jsonPath("$.completed").value(true));

		mockMvc.perform(delete("/api/tasks/{id}", createdTask.id()))
				.andExpect(status().isNoContent());

		mockMvc.perform(get("/api/tasks/{id}", createdTask.id()))
				.andExpect(status().isNotFound());
	}
}

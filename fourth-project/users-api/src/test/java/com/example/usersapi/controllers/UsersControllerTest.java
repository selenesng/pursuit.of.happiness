package com.example.usersapi.controllers;

import com.example.usersapi.models.User;
import com.example.usersapi.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(UsersController.class)
public class UsersControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserRepository mockUserRepository;

	@Autowired
	private ObjectMapper jsonObjectMapper;

	private User newUser;
	private User updatedSecondUser;

	@Before
	public void setUp() {

		User firstUser = new User(
			"someone",
			"Ima",
			"Person"
		);

		User secondUser = new User(
			"someone_else",
			"Someone",
			"Else"
		);

		List<User> mockUsers =
			Stream.of(firstUser, secondUser).collect(Collectors.toList());

		given(mockUserRepository.findAll()).willReturn(mockUsers);

		given(mockUserRepository.findOne(1L)).willReturn(firstUser);
		given(mockUserRepository.findOne(4L)).willReturn(null);

		newUser = new User(
			"new_user_for_create",
			"New",
			"User"
		);
		given(mockUserRepository.save(newUser)).willReturn(newUser);

		updatedSecondUser = new User(
			"updated_username",
			"Updated",
			"Info"
		);
		given(mockUserRepository.save(updatedSecondUser)).willReturn(updatedSecondUser);

		// Mock out Delete to return EmptyResultDataAccessException for missing user with ID of 4
		doAnswer(methodCall -> {
			throw new EmptyResultDataAccessException("ERROR MESSAGE FROM MOCK!!!", 1234);
		}).when(mockUserRepository).delete(4L);

	}

	@Test
	public void findAllUsers_success_returnsStatusOK() throws Exception {

		this.mockMvc
			.perform(get("/"))
			.andExpect(status().isOk());
	}

	@Test
	public void findAllUsers_success_returnAllUsersAsJSON() throws Exception {

		this.mockMvc
			.perform(get("/"))
			.andExpect(jsonPath("$", hasSize(2)));
	}

	@Test
	public void findAllUsers_success_returnUserNameForEachUser() throws Exception {

		this.mockMvc
			.perform(get("/"))
			.andExpect(jsonPath("$[0].userName", is("someone")));
	}

	@Test
	public void findAllUsers_success_returnFirstNameForEachUser() throws Exception {

		this.mockMvc
			.perform(get("/"))
			.andExpect(jsonPath("$[0].firstName", is("Ima")));
	}

	@Test
	public void findAllUsers_success_returnLastNameForEachUser() throws Exception {

		this.mockMvc
			.perform(get("/"))
			.andExpect(jsonPath("$[0].lastName", is("Person")));
	}

	@Test
	public void findUserById_success_returnsStatusOK() throws Exception {

		this.mockMvc
			.perform(get("/1"))
			.andExpect(status().isOk());
	}

	@Test
	public void findUserById_success_returnUserName() throws Exception {

		this.mockMvc
			.perform(get("/1"))
			.andExpect(jsonPath("$.userName", is("someone")));
	}

	@Test
	public void findUserById_success_returnFirstName() throws Exception {

		this.mockMvc
			.perform(get("/1"))
			.andExpect(jsonPath("$.firstName", is("Ima")));
	}

	@Test
	public void findUserById_success_returnLastName() throws Exception {

		this.mockMvc
			.perform(get("/1"))
			.andExpect(jsonPath("$.lastName", is("Person")));
	}

	@Test
	public void findUserById_failure_userNotFoundReturns404() throws Exception {

		this.mockMvc
			.perform(get("/4"))
			.andExpect(status().isNotFound());
	}

	@Test
	public void findUserById_failure_userNotFoundReturnsNotFoundErrorMessage() throws Exception {

		this.mockMvc
			.perform(get("/4"))
			.andExpect(status().reason(containsString("User with ID of 4 was not found!")));
	}

	@Test
	public void deleteUserById_success_returnsStatusOk() throws Exception {

		this.mockMvc
			.perform(delete("/1"))
			.andExpect(status().isOk());
	}

	@Test
	public void deleteUserById_success_deletesViaRepository() throws Exception {

		this.mockMvc.perform(delete("/1"));

		verify(mockUserRepository, times(1)).delete(1L);
	}

	@Test
	public void deleteUserById_failure_userNotFoundReturns404() throws Exception {

		this.mockMvc
			.perform(delete("/4"))
			.andExpect(status().isNotFound());
	}

	@Test
	public void createUser_success_returnsStatusOk() throws Exception {

		this.mockMvc
			.perform(
				post("/")
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonObjectMapper.writeValueAsString(newUser))
			)
			.andExpect(status().isOk());
	}

	@Test
	public void createUser_success_returnsUserName() throws Exception {

		this.mockMvc
			.perform(
				post("/")
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonObjectMapper.writeValueAsString(newUser))
			)
			.andExpect(jsonPath("$.userName", is("new_user_for_create")));
	}

	@Test
	public void createUser_success_returnsFirstName() throws Exception {

		this.mockMvc
			.perform(
				post("/")
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonObjectMapper.writeValueAsString(newUser))
			)
			.andExpect(jsonPath("$.firstName", is("New")));
	}

	@Test
	public void createUser_success_returnsLastName() throws Exception {

		this.mockMvc
			.perform(
				post("/")
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonObjectMapper.writeValueAsString(newUser))
			)
			.andExpect(jsonPath("$.lastName", is("User")));
	}


	@Test
	public void updateUserById_success_returnsStatusOk() throws Exception {

		this.mockMvc
			.perform(
				patch("/1")
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonObjectMapper.writeValueAsString(updatedSecondUser))
			)
			.andExpect(status().isOk());
	}

	@Test
	public void updateUserById_success_returnsUpdatedUserName() throws Exception {

		this.mockMvc
			.perform(
				patch("/1")
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonObjectMapper.writeValueAsString(updatedSecondUser))
			)
			.andExpect(jsonPath("$.userName", is("updated_username")));
	}

	@Test
	public void updateUserById_success_returnsUpdatedFirstName() throws Exception {

		this.mockMvc
			.perform(
				patch("/1")
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonObjectMapper.writeValueAsString(updatedSecondUser))
			)
			.andExpect(jsonPath("$.firstName", is("Updated")));
	}

	@Test
	public void updateUserById_success_returnsUpdatedLastName() throws Exception {

		this.mockMvc
			.perform(
				patch("/1")
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonObjectMapper.writeValueAsString(updatedSecondUser))
			)
			.andExpect(jsonPath("$.lastName", is("Info")));
	}

	@Test
	public void updateUserById_failure_userNotFoundReturns404() throws Exception {

		this.mockMvc
			.perform(
				patch("/4")
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonObjectMapper.writeValueAsString(updatedSecondUser))
			)
			.andExpect(status().isNotFound());
	}

	@Test
	public void updateUserById_failure_userNotFoundReturnsNotFoundErrorMessage() throws Exception {

		this.mockMvc
			.perform(
				patch("/4")
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonObjectMapper.writeValueAsString(updatedSecondUser))
			)
			.andExpect(status().reason(containsString("User with ID of 4 was not found!")));
	}

}

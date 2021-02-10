package com.cos.book.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import com.cos.book.domain.Book;
import com.cos.book.domain.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

// 통합 테스트 (모든 bean들을 똑같이 ioc올리고 테스트 하는 것)
// WebEnvironment.MOCK : 실제 톰켓을 올리는게 아니라 다른 톰켓으로 테스트
// WebEnvironment.RANDOM_PORT : 실제 톰켓으로 테스트
// @AutoConfigureMockMvc : MockMvc를 ioc에 등록해줌
// Transactional : 각각의 테스트함수가 종료될때마다 트랜잭션을 rollback 해주는 어노테이션

@Slf4j
@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.MOCK) 
public class BookControllerIntegreTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private BookRepository bookRepository;
	
	@Autowired
	private EntityManager entityManager;
	
	@BeforeEach	// 모든 함수가 실행되기전에 실행되는 어노테이션
	public void init() {
		// 실행되기전에 항상 1로 초기화 시켜준다.
		// 독립성으로 실행할려면 필요하다.
		entityManager.createNativeQuery("ALTER TABLE book AUTO_INCREMENT = 1").executeUpdate();	// mysql
		//entityManager.createNativeQuery("ALTER TABLE book ALTER COLUMN id RESTART WITH 1").executeUpdate();	// h2 db
		
	}
	
	
	@Test // BDDMockito 패턴 given, when, then
	public void save_테스트() throws Exception {
		
		// given (테스트를 하기 위한 준비)
		Book book = new Book(null,"스프링 따라하기", "코스");
		String content = new ObjectMapper().writeValueAsString(book);
		
		
		// when 테스트 실행
		ResultActions resultActions = mockMvc.perform(post("/book")	// 이것을 하면 세이브함수가 실행된다.
			.contentType(MediaType.APPLICATION_JSON_UTF8)	// 내가 던지는 데이트 타입
			.content(content) // 내가 실제로 던지는 데이트
			.accept(MediaType.APPLICATION_JSON_UTF8)	// 응답은 뭘로
				);
		
		// then (검증) 통과하면 초록색, 실패하면 빨간색
		resultActions
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.title").value("스프링 따라하기"))
			.andDo(MockMvcResultHandlers.print());

	}
	
	@Test
	public void findAll_테스트() throws Exception {
		// given
		List<Book> books = new ArrayList<>();	// 번호 초기화가 안되어있다.
		books.add(new Book(null,"스프링부트 따라하기","코스"));
		books.add(new Book(null,"리엑트 따라하기","코스"));
		books.add(new Book(null,"JUnit 따라하기","코스"));
		bookRepository.saveAll(books);
			
		// when
		ResultActions resultAction = mockMvc.perform(get("/book")
				.accept(MediaType.APPLICATION_JSON_UTF8));
			
		// then
		resultAction
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.[0].id").value(2L))
			.andExpect(jsonPath("$", Matchers.hasSize(6)))
			.andExpect(jsonPath("$.[5].title").value("JUnit 따라하기"))
			.andDo(MockMvcResultHandlers.print());
	}
	
	@Test
	public void findById_테스트() throws Exception {
		// given
		Long id = 6L;
		List<Book> books = new ArrayList<>();	// 번호 초기화가 안되어있다.
		books.add(new Book(null,"스프링부트 따라하기","코스"));
		books.add(new Book(null,"리엑트 따라하기","코스"));
		books.add(new Book(null,"JUnit 따라하기","코스"));
		bookRepository.saveAll(books);
			
		// when
		ResultActions resultAction = mockMvc.perform(get("/book/{id}", id)
				.accept(MediaType.APPLICATION_JSON_UTF8));

		// then
		resultAction
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.title").value("리엑트 따라하기"))
			.andDo(MockMvcResultHandlers.print());
	}

	@Test
	public void update_테스트() throws Exception {
		// given
		Long id = 6L;
		List<Book> books = new ArrayList<>();	// 번호 초기화가 안되어있다.
		books.add(new Book(null,"스프링부트 따라하기","코스"));
		books.add(new Book(null,"리엑트 따라하기","코스"));
		books.add(new Book(null,"JUnit 따라하기","코스"));
		bookRepository.saveAll(books);
		
		Book book = new Book(null,"c++따라하기","코스");
		String content = new ObjectMapper().writeValueAsString(book);	// 날릴 데이터 만든 것
	
		
		// when
		ResultActions resultActions = mockMvc.perform(put("/book/{id}", id)	// 이것을 하면 put함수 실행된다.
				.contentType(MediaType.APPLICATION_JSON_UTF8)	// 내가 던지는 데이트 타입
				.content(content)
				.accept(MediaType.APPLICATION_JSON_UTF8)	// 응답은 뭘로
					);
			
		// then (검증) 통과하면 초록색, 실패하면 빨간색
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.title").value("c++따라하기"))
			.andDo(MockMvcResultHandlers.print());
	}
	
	@Test
	public void delete_테스트() throws Exception {
		// given
		Long id = 6L;
		List<Book> books = new ArrayList<>();	// 번호 초기화가 안되어있다.
		books.add(new Book(null,"스프링부트 따라하기","코스"));
		books.add(new Book(null,"리엑트 따라하기","코스"));
		books.add(new Book(null,"JUnit 따라하기","코스"));
		bookRepository.saveAll(books);
		
		// when
		ResultActions resultActions = mockMvc.perform(delete("/book/{id}", id)	
				.accept(MediaType.TEXT_PLAIN)	// 응답 받을 데이터
					);
			
		// then (검증) 통과하면 초록색, 실패하면 빨간색
		resultActions
			.andExpect(status().isOk())
			.andDo(MockMvcResultHandlers.print());
		
		MvcResult requestResult = resultActions.andReturn();
		String result = requestResult.getResponse().getContentAsString();
		assertEquals("ok", result);
	}
}

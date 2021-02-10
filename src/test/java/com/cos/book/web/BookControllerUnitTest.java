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

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.cos.book.domain.Book;
import com.cos.book.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

// 단위테스트 (Controller 관련 로직만 띄우기) filter, ControllerAdvice / 최소한의 로직만 이용해서 테스트 하는 것

// @ExtendWith // 스프링으로 junit테스트 할 때 꼭 필요하다. WebMvcTest 안에 없으면 해줄것, junit4에는 꼭 적어둘 것 junit5 에는 안적어도 된다. // 스프링으로 확장하는 것
@Slf4j
@WebMvcTest	// (Controller, Filter, ControllerAdvice)가 메모리에 뜬다
public class BookControllerUnitTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private BookService bookService;
	
	@Test // BDDMockito 패턴 given, when, then
	void save_테스트() throws Exception {
		
		// given (테스트를 하기 위한 준비)
		log.info("save_테스트() 시작=========================================");
		Book book = new Book(null,"스프링 따라하기", "코스");
		String content = new ObjectMapper().writeValueAsString(book);
		when(bookService.저장하기(book)).thenReturn(new Book(1L,"스프링 따라하기","코스"));
		log.info(content);
		
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
		List<Book> books = new ArrayList<>();
		books.add(new Book(1L,"스프링부트 따라하기","코스"));
		books.add(new Book(2L,"리엑트 따라하기","코스"));
		
		when(bookService.모두가져오기()).thenReturn(books);
		
		// when
		ResultActions resultAction = mockMvc.perform(get("/book")
				.accept(MediaType.APPLICATION_JSON_UTF8));
			
		// then
		resultAction
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", Matchers.hasSize(2)))
			.andExpect(jsonPath("$.[0].title").value("스프링부트 따라하기"))
			.andDo(MockMvcResultHandlers.print());
	}
	
	@Test
	public void findById_테스트() throws Exception {
		// given
		Long id = 1L;
		when(bookService.한건가져오기(id)).thenReturn(new Book(1L,"자바 공부하기","쌀"));

		// when
		ResultActions resultAction = mockMvc.perform(get("/book/{id}", id).accept(MediaType.APPLICATION_JSON_UTF8));

		// then
		resultAction
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.title").value("자바 공부하기"))
			.andDo(MockMvcResultHandlers.print());
	}

	@Test
	public void update_테스트() throws Exception {
		// given
		Long id = 1L;
		Book book = new Book(null,"c++따라하기","코스");
		String content = new ObjectMapper().writeValueAsString(book);	// 날릴 데이터 만든 것

		ResultActions resultActions = mockMvc.perform(put("/book/{id}", id)	// 이것을 하면 put함수 실행된다.
				.contentType(MediaType.APPLICATION_JSON_UTF8)	// 내가 던지는 데이트 타입
				.content(content) // 내가 실제로 던지는 데이트
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
		Long id = 1L;
		
		when(bookService.삭제하기(id)).thenReturn("ok");	// 자기 자신을 return 못한다 삭제를 했기 때문에

		// when
		ResultActions resultActions = mockMvc.perform(delete("/book/{id}", id)	
				.accept(MediaType.TEXT_PLAIN)	// 응답은 뭘로
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

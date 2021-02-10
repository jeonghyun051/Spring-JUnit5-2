package com.cos.book.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cos.book.domain.Book;
import com.cos.book.domain.BookRepository;

import lombok.extern.slf4j.Slf4j;

// 단위 테스트
// service와 관련된 애들만 메모리에 띄우면 됨
// BookRepository => 가짜 객체로 만들 수 있음

@Slf4j
@ExtendWith(MockitoExtension.class) // mockito 환경
public class BookServiceUnitTest {
	
	@InjectMocks // BookService 객체가 만들어질 때 해당 파일에 @Mock로 등록된 모든 애들을 주입받는다. mockito 환경에 뜬다. 그러면 BookRepository가 가짜객체로 뜬다.
	private BookService bookService; // 얘를 메모리에 올리는 가장 간단한 방법은 뭐냐면
	
	@Mock
	private BookRepository bookRepository;

	@Test
	public void 저장하기_테스트() {

		// BODMocikto 방식
		// given
		Book book = new Book();
		book.setTitle("책제목1");
		book.setAuthor("책저자1");
	
		// stub - 동작 지정
		when(bookRepository.save(book)).thenReturn(book);
		
		// test execute
		Book bookEntity = bookService.저장하기(book);
		
		// then
		assertEquals(bookEntity, book);
	}
}

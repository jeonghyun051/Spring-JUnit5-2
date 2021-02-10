package com.cos.book.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

// 단위테스트  
// db관련된 bean이 ioc에 등록되면 됨

@Slf4j
@Transactional // 하나하나 실행될 때마다 롤백
@AutoConfigureTestDatabase(replace = Replace.ANY) //   
@DataJpaTest // jpa 관련된 애들만 메모리에 뜬다.
public class BookRepositoryUnitTest {

	// @Mock로 안띄워도된다. 이미 띄어져있다. 왜냐하면 13 @DataJpaTest가 BookRepository들을 다 ioc에 등록해둠
	@Autowired
	private BookRepository bookRepository;
	
	@Test
	public void save_테스트() {
		// given
		Book book = new Book(null, "책제목1", "책저자1");
		
		// when
		Book bookEntity = bookRepository.save(book);
	
		// then
		assertEquals("책제목1", bookEntity.getTitle());
	}
	
	@Test
	public void findAll_테스트() {
		// given
		bookRepository.saveAll(
				Arrays.asList(
						new Book(null, "스프링부트 따라하기", "코스"),
						new Book(null, "리엑트 따라하기", "코스")
				)
			);
		
		// when
		List<Book> bookEntitys = bookRepository.findAll();
		
		// then
		log.info("bookEntitys : "+bookEntitys.size() );
		assertNotEquals(0, bookEntitys.size());
		assertEquals(2, bookEntitys.size());
	}
}

package com.cos.book.service;

import java.util.List;
import java.util.function.Supplier;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cos.book.domain.Book;
import com.cos.book.domain.BookRepository;
import lombok.RequiredArgsConstructor;

// 기능을 정의할 수 있고, 트랙잭션을 관리할 수 있음
// 여러개 데이터베이스 관련 서비스를 관리할수있다.

@RequiredArgsConstructor	// final이 붙어있는 생성자를 만들어준다. 그러면 자동으로 di 완성
@Service
public class BookService {
	
	private final BookRepository bookRepository; // di를 해야하는데 생성자를 만들어주는 어노테이션을 12라인에서 등록

					// 서비스 함수가 종료될 때 commit할지 rollback할지 트랙잭션 관리하겠다.
	@Transactional // 여기서 함수가 3번이 실행됐을 때 2번째가 실패하면 롤백된다. 실패안하면 커밋. 트랜잭션을 걸어야 관리가 된다.
	public Book 저장하기(Book book) {
		return bookRepository.save(book);
				
	}
	
	@Transactional(readOnly = true)	// jpa 변경감지라는 내부 기능 활성화 x, 트랜잭션이 걸려있으니까 update시 정합성을 유지해줌. insert의 유령데이터현상을 못막음
	public Book 한건가져오기(Long id) {
		return bookRepository.findById(id) // 오류가 날 수 있다 못찾을수 있기 때문에. 
			.orElseThrow(new Supplier<IllegalArgumentException>() {	// 못찾으면 내부적으로 인셉션이 발동된다. 
				@Override
				public IllegalArgumentException get() {			 
					return new IllegalArgumentException("id를 확인해주세요!!");	// 그래서 얘가 실행된다.
				}	
			});		
	}
	
	@Transactional(readOnly = true)
	public List<Book> 모두가져오기(){
		return bookRepository.findAll();
		
	}

	@Transactional
	public Book 수정하기(Long id, Book book) {
		//더티체킹 update치기
		Book bookEntity = bookRepository.findById(id)
			.orElseThrow(()->new IllegalArgumentException("id를 확인해주세요!!")); // 영속화 (book 오브젝트)	// 32: 코드를 이렇게 짧게 바꿀수 있음
		bookEntity.setTitle(book.getTitle());
		bookEntity.setAuthor(book.getAuthor());
		
		return bookEntity;
		
	}// 함수 종료 => 트랜잭션 종료 => 영속화 되어있는 데이터를 db로 갱신(flush) => commit 이것을 더티체킹
	
	@Transactional
	public String 삭제하기(Long id) {
		bookRepository.deleteById(id); // deleteById는 리턴값이 없음 오류가 터지면 인셉션을 타니까 신경쓰지말고
		return "ok";	// ok가 떨어지면 잘된거 잘안되거면 인셉션으로 관리하면 되니까 나중에
		
	}
	
}

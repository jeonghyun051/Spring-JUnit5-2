package com.cos.book.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity // 서버 실행시에 object Relation Mapping 이 된다. 서버 실행시에 테이블이 h2에 생성된다는 뜻
public class Book {

	@Id	// pk를 해당 변수로 하겟다는 뜻.
	@GeneratedValue(strategy = GenerationType.IDENTITY)	//해당 데이터베이스 번호증가 전략을 따라가겠다는 뜻.
	private Long id;	// 나중에 null을 넣을 수 있는데 관리하기 편하다.
	
	private String title;
	private String author;
	 
}

package com.wadehunt.mystorytime.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.wadehunt.mystorytime.models.Story;
import com.wadehunt.mystorytime.models.User;

public interface SortingRepository extends PagingAndSortingRepository<Story, Long>{
	
	@Query(value="SELECT * FROM a_good_book.story WHERE story_genre = ?1 ORDER BY created_at DESC", nativeQuery=true)
	List<Story> findStoriesByGenre(String storyGenre);
	
	@Query(value="SELECT * FROM a_good_book.story WHERE user_id = ?1 ORDER BY created_at DESC", nativeQuery=true)
	Page<Story> findStoriesByStoryAuthor(PageRequest pageRequest, Long userId);
}

package com.lh.blog.dao;

import com.lh.blog.bean.Carousel;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarouselDAO extends JpaRepository<Carousel,Integer> {

}

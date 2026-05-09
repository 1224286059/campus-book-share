package com.campus.bookshare.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.bookshare.dto.CategorySaveDTO;
import com.campus.bookshare.entity.BookCategory;
import com.campus.bookshare.vo.CategoryVO;

import java.util.List;

public interface BookCategoryService extends IService<BookCategory> {

    List<CategoryVO> listAll();

    CategoryVO create(CategorySaveDTO dto);

    CategoryVO update(Long id, CategorySaveDTO dto);

    void delete(Long id);
}

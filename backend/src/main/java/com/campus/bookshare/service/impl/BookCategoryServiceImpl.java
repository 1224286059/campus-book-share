package com.campus.bookshare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.bookshare.common.BusinessException;
import com.campus.bookshare.dto.CategorySaveDTO;
import com.campus.bookshare.entity.Book;
import com.campus.bookshare.entity.BookCategory;
import com.campus.bookshare.mapper.BookCategoryMapper;
import com.campus.bookshare.mapper.BookMapper;
import com.campus.bookshare.service.BookCategoryService;
import com.campus.bookshare.utils.BeanCopyUtils;
import com.campus.bookshare.vo.CategoryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookCategoryServiceImpl extends ServiceImpl<BookCategoryMapper, BookCategory> implements BookCategoryService {

    @Autowired
    private BookMapper bookMapper;

    @Override
    public List<CategoryVO> listAll() {
        return BeanCopyUtils.copyList(list(new LambdaQueryWrapper<BookCategory>().orderByAsc(BookCategory::getId)), CategoryVO.class);
    }

    @Override
    public CategoryVO create(CategorySaveDTO dto) {
        checkName(dto.getName(), null);
        BookCategory category = new BookCategory();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        save(category);
        return BeanCopyUtils.copy(category, CategoryVO.class);
    }

    @Override
    public CategoryVO update(Long id, CategorySaveDTO dto) {
        BookCategory category = getById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        checkName(dto.getName(), id);
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        updateById(category);
        return BeanCopyUtils.copy(category, CategoryVO.class);
    }

    @Override
    public void delete(Long id) {
        Long count = bookMapper.selectCount(new LambdaQueryWrapper<Book>().eq(Book::getCategoryId, id));
        if (count != null && count.longValue() > 0L) {
            throw new BusinessException("该分类下存在书籍，不能删除");
        }
        removeById(id);
    }

    private void checkName(String name, Long excludeId) {
        LambdaQueryWrapper<BookCategory> wrapper = new LambdaQueryWrapper<BookCategory>().eq(BookCategory::getName, name);
        if (excludeId != null) {
            wrapper.ne(BookCategory::getId, excludeId);
        }
        if (count(wrapper) > 0) {
            throw new BusinessException("分类名称已存在");
        }
    }
}

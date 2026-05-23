package com.campus.bookshare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.bookshare.common.BusinessException;
import com.campus.bookshare.context.UserContext;
import com.campus.bookshare.dto.BookCreateDTO;
import com.campus.bookshare.dto.BookQueryDTO;
import com.campus.bookshare.dto.BookReshareDTO;
import com.campus.bookshare.entity.Book;
import com.campus.bookshare.entity.BookCategory;
import com.campus.bookshare.entity.User;
import com.campus.bookshare.enums.BookStatusEnum;
import com.campus.bookshare.enums.CirculationTypeEnum;
import com.campus.bookshare.enums.PointsSourceTypeEnum;
import com.campus.bookshare.enums.ShareTypeEnum;
import com.campus.bookshare.mapper.BookMapper;
import com.campus.bookshare.service.BookCategoryService;
import com.campus.bookshare.service.BookService;
import com.campus.bookshare.service.CirculationRecordService;
import com.campus.bookshare.service.UserService;
import com.campus.bookshare.vo.BookVO;
import com.campus.bookshare.vo.PageVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements BookService {

    @Autowired
    private BookCategoryService categoryService;
    @Autowired
    private UserService userService;
    @Autowired
    private CirculationRecordService circulationRecordService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BookVO create(BookCreateDTO dto) {
        validateShareType(dto.getShareType());
        checkCategory(dto.getCategoryId());
        Book book = new Book();
        BeanUtils.copyProperties(dto, book);
        Long currentUserId = UserContext.getCurrentUserId();
        book.setOwnerId(currentUserId);
        book.setOriginalOwnerId(currentUserId);
        book.setStatus(BookStatusEnum.PENDING.name());
        book.setCirculationCount(0);
        if (book.getPrice() == null) {
            book.setPrice(java.math.BigDecimal.ZERO);
        }
        save(book);
        return toBookVO(book);
    }

    @Override
    public PageVO<BookVO> listBooks(BookQueryDTO dto, boolean onlyOnShelf) {
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<Book>();
        if (onlyOnShelf) {
            wrapper.eq(Book::getStatus, BookStatusEnum.ON_SHELF.name());
        }
        if (StringUtils.hasText(dto.getKeyword())) {
            wrapper.and(function -> function.like(Book::getTitle, dto.getKeyword())
                    .or().like(Book::getAuthor, dto.getKeyword())
                    .or().like(Book::getDescription, dto.getKeyword()));
        }
        if (dto.getCategoryId() != null) {
            wrapper.eq(Book::getCategoryId, dto.getCategoryId());
        }
        if (StringUtils.hasText(dto.getMajor())) {
            wrapper.like(Book::getMajor, dto.getMajor());
        }
        if (StringUtils.hasText(dto.getCourseName())) {
            wrapper.like(Book::getCourseName, dto.getCourseName());
        }
        if (StringUtils.hasText(dto.getShareType())) {
            validateShareType(dto.getShareType());
            wrapper.eq(Book::getShareType, dto.getShareType());
        }
        wrapper.orderByDesc(Book::getCreateTime);
        Page<Book> page = new Page<Book>(dto.getPage(), dto.getSize());
        Page<Book> bookPage = page(page, wrapper);
        List<BookVO> records = new ArrayList<BookVO>();
        for (Book book : bookPage.getRecords()) {
            records.add(toBookVO(book));
        }
        PageVO<BookVO> result = new PageVO<BookVO>();
        result.setCurrent(bookPage.getCurrent());
        result.setSize(bookPage.getSize());
        result.setTotal(bookPage.getTotal());
        result.setRecords(records);
        return result;
    }

    @Override
    public BookVO detail(Long id) {
        Book book = getById(id);
        if (book == null) {
            throw new BusinessException("书籍不存在");
        }
        return toBookVO(book);
    }

    @Override
    public List<BookVO> myPublished() {
        List<Book> books = list(new LambdaQueryWrapper<Book>()
                .eq(Book::getOriginalOwnerId, UserContext.getCurrentUserId())
                .orderByDesc(Book::getCreateTime));
        return convertList(books);
    }

    @Override
    public List<BookVO> myOwned() {
        List<Book> books = list(new LambdaQueryWrapper<Book>()
                .eq(Book::getOwnerId, UserContext.getCurrentUserId())
                .orderByDesc(Book::getUpdateTime));
        return convertList(books);
    }

    @Override
    public void offShelfByOwner(Long id) {
        Book book = getById(id);
        if (book == null) {
            throw new BusinessException("书籍不存在");
        }
        Long currentUserId = UserContext.getCurrentUserId();
        if (!currentUserId.equals(book.getOwnerId()) && !currentUserId.equals(book.getOriginalOwnerId())) {
            throw new BusinessException("只能下架自己发布或当前持有的书籍");
        }
        book.setStatus(BookStatusEnum.OFF_SHELF.name());
        updateById(book);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BookVO reshare(Long id, BookReshareDTO dto) {
        validateShareType(dto.getShareType());
        Book book = getById(id);
        if (book == null) {
            throw new BusinessException("书籍不存在");
        }
        Long currentUserId = UserContext.getCurrentUserId();
        if (!currentUserId.equals(book.getOwnerId())) {
            throw new BusinessException("只有当前持有人才能再次共享");
        }
        book.setShareType(dto.getShareType());
        if (dto.getPrice() != null) {
            book.setPrice(dto.getPrice());
        }
        book.setDescription(dto.getDescription());
        book.setConditionLevel(dto.getConditionLevel());
        book.setBookLocation(dto.getBookLocation());
        book.setCoverUrl(dto.getCoverUrl());
        book.setStatus(BookStatusEnum.PENDING.name());
        updateById(book);
        circulationRecordService.addRecord(book.getId(), currentUserId, currentUserId,
                CirculationTypeEnum.RESHARE, null, "再次共享发布");
        return toBookVO(book);
    }

    @Override
    public List<BookVO> listPending() {
        return convertList(list(new LambdaQueryWrapper<Book>()
                .eq(Book::getStatus, BookStatusEnum.PENDING.name())
                .orderByDesc(Book::getCreateTime)));
    }

    @Override
    public List<BookVO> adminList(BookQueryDTO dto, String status) {
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<Book>();
        if (dto != null) {
            if (StringUtils.hasText(dto.getKeyword())) {
                wrapper.and(function -> function.like(Book::getTitle, dto.getKeyword())
                        .or().like(Book::getAuthor, dto.getKeyword())
                        .or().like(Book::getDescription, dto.getKeyword()));
            }
            if (dto.getCategoryId() != null) {
                wrapper.eq(Book::getCategoryId, dto.getCategoryId());
            }
            if (StringUtils.hasText(dto.getMajor())) {
                wrapper.like(Book::getMajor, dto.getMajor());
            }
            if (StringUtils.hasText(dto.getCourseName())) {
                wrapper.like(Book::getCourseName, dto.getCourseName());
            }
            if (StringUtils.hasText(dto.getShareType())) {
                validateShareType(dto.getShareType());
                wrapper.eq(Book::getShareType, dto.getShareType());
            }
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(Book::getStatus, status);
        }
        wrapper.orderByDesc(Book::getCreateTime);
        return convertList(list(wrapper));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(Long id) {
        Book book = getById(id);
        if (book == null) {
            throw new BusinessException("书籍不存在");
        }
        if (!BookStatusEnum.PENDING.name().equals(book.getStatus())) {
            throw new BusinessException("只有待审核书籍才能通过");
        }
        book.setStatus(BookStatusEnum.ON_SHELF.name());
        updateById(book);
        userService.changePoints(book.getOwnerId(), 2, PointsSourceTypeEnum.BOOK_APPROVED.name(), "书籍审核通过，积分+2");
    }

    @Override
    public void reject(Long id) {
        Book book = getById(id);
        if (book == null) {
            throw new BusinessException("书籍不存在");
        }
        if (!BookStatusEnum.PENDING.name().equals(book.getStatus())) {
            throw new BusinessException("只有待审核书籍才能驳回");
        }
        book.setStatus(BookStatusEnum.REJECTED.name());
        updateById(book);
    }

    @Override
    public void adminOffShelf(Long id) {
        Book book = getById(id);
        if (book == null) {
            throw new BusinessException("书籍不存在");
        }
        book.setStatus(BookStatusEnum.OFF_SHELF.name());
        updateById(book);
    }

    @Override
    public Book getAvailableBook(Long id) {
        Book book = getById(id);
        if (book == null) {
            throw new BusinessException("书籍不存在");
        }
        return book;
    }

    private void validateShareType(String shareType) {
        try {
            ShareTypeEnum.valueOf(shareType);
        } catch (Exception e) {
            throw new BusinessException("共享方式不合法");
        }
    }

    private void checkCategory(Long categoryId) {
        if (categoryService.getById(categoryId) == null) {
            throw new BusinessException("分类不存在");
        }
    }

    private List<BookVO> convertList(List<Book> books) {
        List<BookVO> list = new ArrayList<BookVO>();
        for (Book book : books) {
            list.add(toBookVO(book));
        }
        return list;
    }

    private BookVO toBookVO(Book book) {
        BookVO vo = new BookVO();
        BeanUtils.copyProperties(book, vo);
        BookCategory category = categoryService.getById(book.getCategoryId());
        User owner = userService.getById(book.getOwnerId());
        vo.setCategoryName(category == null ? null : category.getName());
        vo.setOwnerUsername(owner == null ? null : owner.getUsername());
        return vo;
    }
}

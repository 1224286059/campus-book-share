package com.campus.bookshare.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.bookshare.dto.BookCreateDTO;
import com.campus.bookshare.dto.BookQueryDTO;
import com.campus.bookshare.dto.BookReshareDTO;
import com.campus.bookshare.entity.Book;
import com.campus.bookshare.vo.BookVO;
import com.campus.bookshare.vo.PageVO;

import java.util.List;

public interface BookService extends IService<Book> {

    BookVO create(BookCreateDTO dto);

    PageVO<BookVO> listBooks(BookQueryDTO dto, boolean onlyOnShelf);

    BookVO detail(Long id);

    List<BookVO> myPublished();

    List<BookVO> myOwned();

    void offShelfByOwner(Long id);

    BookVO reshare(Long id, BookReshareDTO dto);

    List<BookVO> listPending();

    List<BookVO> adminList(BookQueryDTO dto, String status);

    void approve(Long id);

    void reject(Long id);

    void adminOffShelf(Long id);

    Book getAvailableBook(Long id);
}

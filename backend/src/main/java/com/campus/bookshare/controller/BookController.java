package com.campus.bookshare.controller;

import com.campus.bookshare.common.Result;
import com.campus.bookshare.dto.BookCreateDTO;
import com.campus.bookshare.dto.BookQueryDTO;
import com.campus.bookshare.dto.BookReshareDTO;
import com.campus.bookshare.service.BookService;
import com.campus.bookshare.vo.BookVO;
import com.campus.bookshare.vo.PageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @PostMapping
    public Result<BookVO> create(@Valid @RequestBody BookCreateDTO dto) {
        return Result.success(bookService.create(dto));
    }

    @GetMapping
    public Result<PageVO<BookVO>> list(BookQueryDTO dto) {
        return Result.success(bookService.listBooks(dto, true));
    }

    @GetMapping("/{id}")
    public Result<BookVO> detail(@PathVariable Long id) {
        return Result.success(bookService.detail(id));
    }

    @GetMapping("/my-published")
    public Result<List<BookVO>> myPublished() {
        return Result.success(bookService.myPublished());
    }

    @GetMapping("/my-owned")
    public Result<List<BookVO>> myOwned() {
        return Result.success(bookService.myOwned());
    }

    @PutMapping("/{id}/off-shelf")
    public Result<Void> offShelf(@PathVariable Long id) {
        bookService.offShelfByOwner(id);
        return Result.success("下架成功", null);
    }

    @PostMapping("/{id}/reshare")
    public Result<BookVO> reshare(@PathVariable Long id, @Valid @RequestBody BookReshareDTO dto) {
        return Result.success(bookService.reshare(id, dto));
    }
}

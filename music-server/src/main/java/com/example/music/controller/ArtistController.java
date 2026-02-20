package com.example.music.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.music.common.result.Result;
import com.example.music.domain.dto.ArtistCreateReq;
import com.example.music.domain.dto.ArtistUpdateReq;
import com.example.music.domain.vo.ArtistVO;
import com.example.music.domain.vo.SongVO;
import com.example.music.domain.vo.UploadVO;
import com.example.music.service.ArtistService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Artist", description = "歌手管理接口")
@RestController
@RequestMapping("/artist")
public class ArtistController {

    private final ArtistService artistService;

    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @PostMapping("/upload/avatar")
    public Result<UploadVO> uploadAvatar(@RequestParam("file") MultipartFile file) {
        return Result.ok(artistService.uploadAvatar(file));
    }

    @PostMapping("/create")
    public Result<Long> create(@Valid @RequestBody ArtistCreateReq req) {
        return Result.ok(artistService.create(req));
    }

    @GetMapping("/{id}")
    public Result<ArtistVO> detail(@PathVariable Long id) {
        return Result.ok(artistService.detail(id));
    }

    @GetMapping("/page")
    public Result<IPage<ArtistVO>> page(@RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        @RequestParam(required = false) String keyword) {
        return Result.ok(artistService.page(page, size, keyword));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody ArtistUpdateReq req) {
        artistService.update(id, req);
        return Result.ok(null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        artistService.remove(id);
        return Result.ok(null);
    }

    @GetMapping("/{id}/songs")
    public Result<IPage<SongVO>> songs(@PathVariable Long id,
                                       @RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        return Result.ok(artistService.songs(id, page, size));
    }
}
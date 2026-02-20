package com.example.music.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.music.common.result.Result;
import com.example.music.domain.dto.SongCreateReq;
import com.example.music.domain.dto.SongUpdateReq;
import com.example.music.domain.vo.SongHotVO;
import com.example.music.domain.vo.SongVO;
import com.example.music.domain.vo.UploadVO;
import com.example.music.service.SongService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Song", description = "歌曲相关接口")
@RestController
@RequestMapping("/song")
public class SongController {

    private final SongService songService;

    public SongController(SongService songService) {
        this.songService = songService;
    }

    // 上传音频
    @PostMapping("/upload/audio")
    public Result<UploadVO> uploadAudio(@RequestParam("file") MultipartFile file) {
        return Result.ok(songService.uploadAudio(file));
    }

    // 上传封面
    @PostMapping("/upload/cover")
    public Result<UploadVO> uploadCover(@RequestParam("file") MultipartFile file) {
        return Result.ok(songService.uploadCover(file));
    }

    @PostMapping("/create")
    public Result<Long> create(@Valid @RequestBody SongCreateReq req) {
        return Result.ok(songService.create(req));
    }

    @GetMapping("/{id}")
    public Result<SongVO> detail(@PathVariable Long id) {
        return Result.ok(songService.detail(id));
    }

    @GetMapping("/page")
    public Result<IPage<SongVO>> page(@RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "10") int size,
                                      @RequestParam(required = false) String keyword) {
        return Result.ok(songService.page(page, size, keyword));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody SongUpdateReq req) {
        songService.update(id, req);
        return Result.ok(null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        songService.remove(id);
        return Result.ok(null);
    }

    @GetMapping("/{id}/play-url")
    public Result<?> playUrl(@PathVariable Long id) {
        String url = songService.getPlayUrl(id);
        return Result.ok(java.util.Map.of("url", url));
    }

    @GetMapping("/{id}/download-url")
    public Result<?> downloadUrl(@PathVariable Long id) {
        String url = songService.getDownloadUrl(id);
        return Result.ok(java.util.Map.of("url", url));
    }

    @PostMapping("/{id}/play")
    public Result<Void> play(@PathVariable Long id) {
        songService.play(id);
        return Result.ok(null);
    }

    @PostMapping("/{id}/favorite")
    public Result<Void> favorite(@PathVariable Long id) {
        songService.favorite(id);
        return Result.ok(null);
    }

    @DeleteMapping("/{id}/favorite")
    public Result<Void> unfavorite(@PathVariable Long id) {
        songService.unfavorite(id);
        return Result.ok(null);
    }

    @GetMapping("/favorite/page")
    public Result<IPage<SongVO>> favoritePage(@RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "10") int size) {
        return Result.ok(songService.favoritePage(page, size));
    }

    @GetMapping("/hot")
    public Result<List<SongHotVO>> hot(@RequestParam(name="size", defaultValue="10") Integer size) {
        if (size == null || size <= 0) size = 10;
        if (size > 50) size = 50;
        return Result.ok(songService.hot(size));
    }

}
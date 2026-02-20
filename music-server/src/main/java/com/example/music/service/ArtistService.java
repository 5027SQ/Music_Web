package com.example.music.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.music.domain.dto.ArtistCreateReq;
import com.example.music.domain.dto.ArtistUpdateReq;
import com.example.music.domain.vo.ArtistVO;
import com.example.music.domain.vo.SongVO;
import com.example.music.domain.vo.UploadVO;
import org.springframework.web.multipart.MultipartFile;

public interface ArtistService {
    UploadVO uploadAvatar(MultipartFile file);

    Long create(ArtistCreateReq req);
    ArtistVO detail(Long id);
    IPage<ArtistVO> page(int page, int size, String keyword);
    void update(Long id, ArtistUpdateReq req);
    void remove(Long id);

    IPage<SongVO> songs(Long artistId, int page, int size);
}
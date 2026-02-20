package com.example.music.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.music.domain.dto.SongCreateReq;
import com.example.music.domain.dto.SongUpdateReq;
import com.example.music.domain.vo.SongVO;
import com.example.music.domain.vo.UploadVO;
import org.springframework.web.multipart.MultipartFile;
import com.example.music.domain.vo.SongHotVO;
import java.util.List;

public interface SongService {
    UploadVO uploadAudio(MultipartFile file);
    UploadVO uploadCover(MultipartFile file);

    Long create(SongCreateReq req);
    SongVO detail(Long id);
    IPage<SongVO> page(int page, int size, String keyword);

    void update(Long id, SongUpdateReq req);
    void remove(Long id);

    // 播放相关（私有桶用 presigned url）
    String getPlayUrl(Long songId);
    void play(Long songId);

    // 下载链接（登录后）
    String getDownloadUrl(Long songId);

    // 收藏相关
    void favorite(Long songId);
    void unfavorite(Long songId);
    IPage<SongVO> favoritePage(int page, int size);

    //热歌
    List<SongHotVO> hot(Integer size);
}
package com.example.music.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.music.common.exception.BizException;
import com.example.music.config.MinioProperties;
import com.example.music.domain.dto.ArtistCreateReq;
import com.example.music.domain.dto.ArtistUpdateReq;
import com.example.music.domain.entity.Artist;
import com.example.music.domain.entity.Song;
import com.example.music.domain.vo.ArtistVO;
import com.example.music.domain.vo.SongVO;
import com.example.music.domain.vo.UploadVO;
import com.example.music.mapper.ArtistMapper;
import com.example.music.mapper.SongMapper;
import com.example.music.security.AuthContext;
import com.example.music.service.ArtistService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ArtistServiceImpl implements ArtistService {

    private final ArtistMapper artistMapper;
    private final SongMapper songMapper;
    private final MinioClient minioClient;
    private final MinioProperties minioProps;

    public ArtistServiceImpl(ArtistMapper artistMapper,
                             SongMapper songMapper,
                             MinioClient minioClient,
                             MinioProperties minioProps) {
        this.artistMapper = artistMapper;
        this.songMapper = songMapper;
        this.minioClient = minioClient;
        this.minioProps = minioProps;
    }

    private void requireLogin() {
        if (AuthContext.getUserId() == null) {
            throw new BizException("not logged in");
        }
    }

    private UploadVO upload(MultipartFile file, String folder, String[] allowExt) {
        if (file == null || file.isEmpty()) throw new BizException("file is empty");

        String original = file.getOriginalFilename();
        String ext = "";
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf(".")).toLowerCase();
        }

        boolean ok = false;
        for (String a : allowExt) {
            if (a.equals(ext)) { ok = true; break; }
        }
        if (!ok) throw new BizException("unsupported file type: " + ext);

        String key = folder + "/" + UUID.randomUUID().toString().replace("-", "") + ext;

        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioProps.getBucket())
                            .object(key)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        } catch (Exception e) {
            throw new BizException("minio upload failed: " + e.getMessage());
        }

        String url = minioProps.getEndpoint() + "/" + minioProps.getBucket() + "/" + key;
        return new UploadVO(url, key);
    }

    @Override
    public UploadVO uploadAvatar(MultipartFile file) {
        requireLogin();
        return upload(file, "artist-avatar", new String[]{".jpg", ".jpeg", ".png", ".webp"});
    }

    @Override
    public Long create(ArtistCreateReq req) {
        requireLogin();

        Artist a = new Artist();
        a.setName(req.getName());
        a.setAvatarUrl(req.getAvatarUrl());
        a.setIntro(req.getIntro());
        a.setStatus(1);
        a.setCreatedAt(LocalDateTime.now());
        a.setUpdatedAt(LocalDateTime.now());

        artistMapper.insert(a);
        return a.getId();
    }

    @Override
    public ArtistVO detail(Long id) {
        Artist a = artistMapper.selectById(id);
        if (a == null) throw new BizException("artist not found");

        ArtistVO vo = new ArtistVO();
        vo.setId(a.getId());
        vo.setName(a.getName());
        vo.setAvatarUrl(a.getAvatarUrl());
        vo.setIntro(a.getIntro());
        vo.setStatus(a.getStatus());
        vo.setCreatedAt(a.getCreatedAt());
        return vo;
    }

    @Override
    public IPage<ArtistVO> page(int page, int size, String keyword) {
        Page<Artist> p = new Page<>(page, size);

        LambdaQueryWrapper<Artist> qw = new LambdaQueryWrapper<Artist>()
                .eq(Artist::getStatus, 1)
                .orderByDesc(Artist::getCreatedAt);

        if (keyword != null && !keyword.isBlank()) {
            qw.like(Artist::getName, keyword);
        }

        IPage<Artist> res = artistMapper.selectPage(p, qw);
        return res.convert(a -> {
            ArtistVO vo = new ArtistVO();
            vo.setId(a.getId());
            vo.setName(a.getName());
            vo.setAvatarUrl(a.getAvatarUrl());
            vo.setIntro(a.getIntro());
            vo.setStatus(a.getStatus());
            vo.setCreatedAt(a.getCreatedAt());
            return vo;
        });
    }

    @Override
    public void update(Long id, ArtistUpdateReq req) {
        requireLogin();

        Artist a = artistMapper.selectById(id);
        if (a == null) throw new BizException("artist not found");

        if (req.getName() != null) a.setName(req.getName());
        if (req.getAvatarUrl() != null) a.setAvatarUrl(req.getAvatarUrl());
        if (req.getIntro() != null) a.setIntro(req.getIntro());
        if (req.getStatus() != null) a.setStatus(req.getStatus());

        a.setUpdatedAt(LocalDateTime.now());
        artistMapper.updateById(a);
    }

    @Override
    public void remove(Long id) {
        requireLogin();
        artistMapper.deleteById(id);
    }

    @Override
    public IPage<SongVO> songs(Long artistId, int page, int size) {
        Artist a = artistMapper.selectById(artistId);
        if (a == null) throw new BizException("artist not found");

        Page<Song> p = new Page<>(page, size);
        LambdaQueryWrapper<Song> qw = new LambdaQueryWrapper<Song>()
                .eq(Song::getArtistId, artistId)
                .eq(Song::getStatus, 1)
                .orderByDesc(Song::getCreatedAt);

        IPage<Song> res = songMapper.selectPage(p, qw);
        return res.convert(s -> {
            SongVO vo = new SongVO();
            vo.setId(s.getId());
            vo.setTitle(s.getTitle());
            vo.setArtist(s.getArtist());
            vo.setArtistId(s.getArtistId());
            vo.setAlbum(s.getAlbum());
            vo.setCoverUrl(s.getCoverUrl());
            vo.setAudioUrl(s.getAudioUrl());
            vo.setDurationSec(s.getDurationSec());
            vo.setUploaderId(s.getUploaderId());
            vo.setPlayCount(s.getPlayCount());
            vo.setStatus(s.getStatus());
            vo.setCreatedAt(s.getCreatedAt());
            return vo;
        });
    }
}
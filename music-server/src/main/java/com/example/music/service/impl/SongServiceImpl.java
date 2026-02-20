package com.example.music.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.music.common.exception.BizException;
import com.example.music.common.minio.MinioPresignUtil;
import com.example.music.config.MinioProperties;
import com.example.music.domain.dto.SongCreateReq;
import com.example.music.domain.dto.SongUpdateReq;
import com.example.music.domain.entity.Song;
import com.example.music.domain.entity.SongFavorite;
import com.example.music.domain.vo.SongHotVO;
import com.example.music.domain.vo.SongVO;
import com.example.music.domain.vo.UploadVO;
import com.example.music.mapper.SongFavoriteMapper;
import com.example.music.mapper.SongMapper;
import com.example.music.security.AuthContext;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class SongServiceImpl implements com.example.music.service.SongService {

    private static final String HOT_KEY = "music:hot:song";

    private final SongMapper songMapper;
    private final SongFavoriteMapper songFavoriteMapper;
    private final MinioClient minioClient;
    private final MinioProperties minioProps;
    private final StringRedisTemplate stringRedisTemplate;

    // ✅ 只保留这一个构造函数，杜绝“final 字段未初始化”
    public SongServiceImpl(SongMapper songMapper,
                           SongFavoriteMapper songFavoriteMapper,
                           MinioClient minioClient,
                           MinioProperties minioProps,
                           StringRedisTemplate stringRedisTemplate) {
        this.songMapper = songMapper;
        this.songFavoriteMapper = songFavoriteMapper;
        this.minioClient = minioClient;
        this.minioProps = minioProps;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    private boolean isAdmin() {
        String role = AuthContext.getRole();
        return role != null && role.equalsIgnoreCase("ROLE_ADMIN");
    }

    private void requireLogin() {
        if (AuthContext.getUserId() == null) {
            throw new BizException("not logged in");
        }
    }

    private void checkOwnerOrAdmin(Song song) {
        Long uid = AuthContext.getUserId();
        if (uid == null) throw new BizException("not logged in");
        if (song == null) throw new BizException("song not found");
        if (!isAdmin() && !uid.equals(song.getUploaderId())) {
            throw new BizException("no permission");
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

        // 注意：这里拼的是对象访问 URL（桶私有时会 403，这是正常的）
        String url = minioProps.getEndpoint() + "/" + minioProps.getBucket() + "/" + key;
        return new UploadVO(url, key);
    }

    private SongVO toVo(Song s) {
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
    }

    @Override
    public UploadVO uploadAudio(MultipartFile file) {
        return upload(file, "audio", new String[]{".mp3", ".wav", ".flac", ".m4a"});
    }

    @Override
    public UploadVO uploadCover(MultipartFile file) {
        return upload(file, "cover", new String[]{".jpg", ".jpeg", ".png", ".webp"});
    }

    @Override
    public Long create(SongCreateReq req) {
        Long uid = AuthContext.getUserId();
        if (uid == null) throw new BizException("not logged in");

        Song s = new Song();
        s.setTitle(req.getTitle());
        s.setArtist(req.getArtist());
        s.setArtistId(req.getArtistId());
        s.setAlbum(req.getAlbum());
        s.setCoverUrl(req.getCoverUrl());
        s.setAudioUrl(req.getAudioUrl());
        s.setDurationSec(req.getDurationSec());
        s.setUploaderId(uid);
        s.setPlayCount(0L);
        s.setStatus(1);
        s.setCreatedAt(LocalDateTime.now());
        s.setUpdatedAt(LocalDateTime.now());

        songMapper.insert(s);
        return s.getId();
    }

    @Override
    public SongVO detail(Long id) {
        Song s = songMapper.selectById(id);
        if (s == null) throw new BizException("song not found");
        return toVo(s);
    }

    @Override
    public IPage<SongVO> page(int page, int size, String keyword) {
        Page<Song> p = new Page<>(page, size);

        LambdaQueryWrapper<Song> qw = new LambdaQueryWrapper<Song>()
                .eq(Song::getStatus, 1)
                .orderByDesc(Song::getCreatedAt);

        if (keyword != null && !keyword.isBlank()) {
            qw.like(Song::getTitle, keyword);
        }

        IPage<Song> res = songMapper.selectPage(p, qw);
        return res.convert(this::toVo);
    }

    @Override
    public void update(Long id, SongUpdateReq req) {
        Song s = songMapper.selectById(id);
        checkOwnerOrAdmin(s);

        if (req.getTitle() != null) s.setTitle(req.getTitle());
        if (req.getArtist() != null) s.setArtist(req.getArtist());
        if (req.getArtistId() != null) s.setArtistId(req.getArtistId());
        if (req.getAlbum() != null) s.setAlbum(req.getAlbum());
        if (req.getCoverUrl() != null) s.setCoverUrl(req.getCoverUrl());
        if (req.getAudioUrl() != null) s.setAudioUrl(req.getAudioUrl());
        if (req.getDurationSec() != null) s.setDurationSec(req.getDurationSec());
        if (req.getStatus() != null) s.setStatus(req.getStatus());

        s.setUpdatedAt(LocalDateTime.now());
        songMapper.updateById(s);
    }

    @Override
    public void remove(Long id) {
        Song s = songMapper.selectById(id);
        checkOwnerOrAdmin(s);

        try {
            String prefix = minioProps.getEndpoint() + "/" + minioProps.getBucket() + "/";
            if (s.getAudioUrl() != null && s.getAudioUrl().startsWith(prefix)) {
                String key = s.getAudioUrl().substring(prefix.length());
                minioClient.removeObject(RemoveObjectArgs.builder()
                        .bucket(minioProps.getBucket())
                        .object(key)
                        .build());
            }
        } catch (Exception ignore) {}

        songMapper.deleteById(id);
    }

    @Override
    public void play(Long songId) {
        // 热度 +1
        stringRedisTemplate.opsForZSet()
                .incrementScore(HOT_KEY, String.valueOf(songId), 1);
    }

    @Override
    public String getPlayUrl(Long songId) {
        Song song = songMapper.selectById(songId);
        if (song == null || song.getStatus() == null || song.getStatus() != 1) {
            throw new BizException("song not found");
        }

        String bucket = minioProps.getBucket();

        // 从 audioUrl 解析出 objectKey
        String objectKey = MinioPresignUtil.extractObjectKeyFromUrl(song.getAudioUrl(), bucket);
        if (objectKey == null || objectKey.isBlank()) {
            throw new BizException("invalid audioUrl, cannot extract objectKey");
        }

        // 记一次播放
        play(songId);

        // 返回 10 分钟有效的可播放链接
        return MinioPresignUtil.presignGet(minioClient, bucket, objectKey, 600);
    }

    @Override
    public String getDownloadUrl(Long songId) {
        requireLogin();
        Song song = songMapper.selectById(songId);
        if (song == null || song.getStatus() == null || song.getStatus() != 1) {
            throw new BizException("song not found");
        }

        String bucket = minioProps.getBucket();
        String objectKey = MinioPresignUtil.extractObjectKeyFromUrl(song.getAudioUrl(), bucket);
        if (objectKey == null || objectKey.isBlank()) {
            throw new BizException("invalid audioUrl, cannot extract objectKey");
        }

        // 返回 10 分钟有效的下载链接
        return MinioPresignUtil.presignGet(minioClient, bucket, objectKey, 600);
    }

    @Override
    public void favorite(Long songId) {
        requireLogin();

        Song s = songMapper.selectById(songId);
        if (s == null || s.getStatus() == null || s.getStatus() != 1) {
            throw new BizException("song not found");
        }

        Long uid = AuthContext.getUserId();
        SongFavorite exist = songFavoriteMapper.selectOne(new LambdaQueryWrapper<SongFavorite>()
                .eq(SongFavorite::getUserId, uid)
                .eq(SongFavorite::getSongId, songId)
                .last("limit 1"));
        if (exist != null) return;

        SongFavorite f = new SongFavorite();
        f.setUserId(uid);
        f.setSongId(songId);
        f.setCreatedAt(LocalDateTime.now());
        songFavoriteMapper.insert(f);
    }

    @Override
    public void unfavorite(Long songId) {
        requireLogin();
        Long uid = AuthContext.getUserId();
        songFavoriteMapper.delete(new LambdaQueryWrapper<SongFavorite>()
                .eq(SongFavorite::getUserId, uid)
                .eq(SongFavorite::getSongId, songId));
    }

    @Override
    public IPage<SongVO> favoritePage(int page, int size) {
        requireLogin();
        Long uid = AuthContext.getUserId();

        Page<SongFavorite> p = new Page<>(page, size);
        LambdaQueryWrapper<SongFavorite> qw = new LambdaQueryWrapper<SongFavorite>()
                .eq(SongFavorite::getUserId, uid)
                .orderByDesc(SongFavorite::getCreatedAt);

        IPage<SongFavorite> favPage = songFavoriteMapper.selectPage(p, qw);
        List<Long> ids = favPage.getRecords().stream().map(SongFavorite::getSongId).toList();

        Page<SongVO> res = new Page<>(page, size);
        res.setTotal(favPage.getTotal());

        if (ids.isEmpty()) {
            res.setRecords(List.of());
            return res;
        }

        List<Song> songs = songMapper.selectBatchIds(ids);
        Map<Long, Song> map = songs.stream().collect(Collectors.toMap(Song::getId, s -> s, (a, b) -> a));

        List<SongVO> records = new ArrayList<>();
        for (Long id : ids) {
            Song s = map.get(id);
            if (s == null) continue;
            records.add(toVo(s));
        }
        res.setRecords(records);
        return res;
    }

    @Override
    public List<SongHotVO> hot(Integer size) {
        int n = (size == null ? 10 : size);
        if (n <= 0) n = 10;
        if (n > 50) n = 50;

        // ✅ 用同一个 ZSET key：HOT_KEY = "music:hot:song"
        Set<ZSetOperations.TypedTuple<String>> tuples =
                stringRedisTemplate.opsForZSet().reverseRangeWithScores(HOT_KEY, 0, n - 1);

        if (tuples == null || tuples.isEmpty()) return List.of();

        List<Long> ids = tuples.stream()
                .map(t -> Long.valueOf(Objects.requireNonNull(t.getValue())))
                .toList();

        // ✅ 你不是 ServiceImpl，所以用 mapper 批量查
        List<Song> songs = songMapper.selectBatchIds(ids);

        // ✅ 只返回上架 status==1
        Map<Long, Song> map = songs.stream()
                .filter(s -> s.getStatus() != null && s.getStatus() == 1)
                .collect(Collectors.toMap(Song::getId, s -> s, (a, b) -> a));

        // ✅ 按 redis 的 score 顺序组装返回
        List<SongHotVO> res = new ArrayList<>();
        for (ZSetOperations.TypedTuple<String> t : tuples) {
            Long id = Long.valueOf(t.getValue());
            Song s = map.get(id);
            if (s == null) continue;

            SongHotVO vo = new SongHotVO();
            BeanUtils.copyProperties(s, vo);
            vo.setHotScore(t.getScore());
            res.add(vo);
        }
        return res;
    }

}
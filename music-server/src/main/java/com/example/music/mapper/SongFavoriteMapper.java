package com.example.music.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.music.domain.entity.SongFavorite;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SongFavoriteMapper extends BaseMapper<SongFavorite> {
}
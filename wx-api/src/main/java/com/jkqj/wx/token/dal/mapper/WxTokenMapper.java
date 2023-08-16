package com.jkqj.wx.token.dal.mapper;

import com.jkqj.wx.token.dal.po.WxToken;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WxTokenMapper {
    List<WxToken> selectToken(@Param("appId") String appId);
    List<WxToken> selectTokenLock(@Param("appId") String appId);
    int update(WxToken wxToken);
}

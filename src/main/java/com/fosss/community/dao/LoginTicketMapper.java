package com.fosss.community.dao;

import com.fosss.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

/**
 * @author: fosss
 * Date: 2023/9/1
 * Time: 13:18
 * Description:
 */
@Mapper
public interface LoginTicketMapper {

    /**
     * 添加登录信息
     */
    int insertLoginTicket(LoginTicket loginTicket);

    /**
     * 根据凭证查询登录信息
     */
    LoginTicket selectByTicket(@Param("ticket") String ticket);

    /**
     * 更新登录状态
     */
    int updateStatus(@Param("ticket") String ticket, @Param("status") int status);
}

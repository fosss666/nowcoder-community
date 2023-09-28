package com.fosss.community.controller;

import com.fosss.community.annotation.LoginRequired;
import com.fosss.community.constant.ExceptionConstant;
import com.fosss.community.constant.LikeConstant;
import com.fosss.community.entity.Comment;
import com.fosss.community.entity.DiscussPost;
import com.fosss.community.entity.Page;
import com.fosss.community.entity.User;
import com.fosss.community.exception.BusinessException;
import com.fosss.community.properties.ApplicationProperty;
import com.fosss.community.service.*;
import com.fosss.community.utils.CommunityUtil;
import com.fosss.community.utils.ThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fosss.community.constant.LikeConstant.ENTITY_TYPE_POST;
import static com.fosss.community.constant.LikeConstant.ENTITY_TYPE_USER;

/**
 * @author: fosss
 * Date: 2023/9/5
 * Time: 12:02
 * Description:
 */
@Controller
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;
    @Resource
    private ApplicationProperty applicationProperty;
    @Resource
    private ThreadLocalUtil threadLocalUtil;
    @Resource
    private LikeService likeService;
    @Resource
    private FollowService followService;
    @Resource
    private DiscussPostService discussPostService;
    @Resource
    private CommentService commentService;

    /**
     * 跳转账号设置页面
     */
    @LoginRequired
    @GetMapping("/setting")
    public String setting() {
        return "/site/setting";
    }

    /**
     * 上传头像
     */
    @LoginRequired
    @PostMapping("/upload")
    public String upload(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", ExceptionConstant.FILE_NOT_UPLOAD_ERROR);
            return "/site/setting";
        }

        String fileName = headerImage.getOriginalFilename();
        if (StringUtils.isBlank(fileName)) {
            model.addAttribute("error", ExceptionConstant.FILE_NOT_UPLOAD_ERROR);
            return "/site/setting";
        }
        //获取文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", ExceptionConstant.FILE_FORMAT_ERROR);
            return "/site/setting";
        }


        //创建文件名
        fileName = CommunityUtil.generateUUID() + suffix;
        //创建文件
        File file = new File(applicationProperty.getUploadPath() + "/" + fileName);
        //存储文件
        try {
            headerImage.transferTo(file);
        } catch (IOException e) {
            log.error(ExceptionConstant.FILE_UPLOAD_ERROR + ":" + e.getMessage());
        }
        //更新数据库中头像地址
        //拼接地址 http://localhost:8081/community/user/header/xxx.png
        String domain = applicationProperty.getDomain();
        String contextPath = applicationProperty.getContextPath();
        int port = applicationProperty.getPort();
        String headerImagePath = domain + ":" + port + contextPath + "/" + "user/header/" + fileName;

        //获取用户id
        User user = threadLocalUtil.get();
        userService.upload(user.getId(), headerImagePath);

        return "redirect:/index";
    }

    /**
     * 获取头像
     */
    @GetMapping("/header/{fileName}")
    public void getHeaderImage(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        //拼接服务器存放地址
        fileName = applicationProperty.getUploadPath() + "/" + fileName;

        //设置相应类型
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);//+1是去掉图片类型前边的点
        response.setContentType("image/" + suffix);
        //读取文件，写入到输出流中
        try (
                FileInputStream fis = new FileInputStream(fileName);
                ServletOutputStream os = response.getOutputStream();
        ) {
            byte[] bytes = new byte[1024];
            int length = 0;
            while ((length = fis.read(bytes)) != -1) {
                os.write(bytes, 0, length);
            }
        } catch (Exception e) {
            log.error(ExceptionConstant.FILE_READ_ERROR + ":" + e.getMessage());
        }
    }

    /**
     * 修改密码
     */
    @LoginRequired
    @PostMapping(path = "/updatePassword")
    public String updatePassword(String oldPassword, String newPassword, Model model) {
        User user = threadLocalUtil.get();
        Map<String, Object> map = userService.updatePassword(user.getId(), oldPassword, newPassword);
        if (map == null || map.isEmpty()) {
            return "redirect:/logout";
        } else {
            model.addAttribute("oldPasswordMsg", map.get("oldPasswordMsg"));
            model.addAttribute("newPasswordMsg", map.get("newPasswordMsg"));
            return "/site/setting";
        }
    }

    /**
     * 个人主页
     */
    @GetMapping("/profile/{userId}")
    public String profile(@PathVariable("userId") int userId, Model model) {
        //查询用户信息
        User user = userService.findUserById(userId);
        //判断用户是否存在
        if (user == null) throw new BusinessException(ExceptionConstant.USER_NOT_FOUND);
        model.addAttribute("user", user);
        //点赞数量
        int likeCount = likeService.getUserLikeCount(userId);
        model.addAttribute("likeCount", likeCount);

        //关注数量
        long followeeCount = followService.getFolloweeCount(userId, LikeConstant.ENTITY_TYPE_USER);
        model.addAttribute("followeeCount", followeeCount);
        //粉丝数量
        long followerCount = followService.getFollowerCount(userId, LikeConstant.ENTITY_TYPE_USER);
        model.addAttribute("followerCount", followerCount);
        //当前用户是否已关注目前访问的用户
        boolean hasFollowed = false;
        if (threadLocalUtil.get() != null) {
            hasFollowed = followService.hasFollowed(threadLocalUtil.get().getId(), userId, LikeConstant.ENTITY_TYPE_USER);
        }
        model.addAttribute("hasFollowed", hasFollowed);
        //跳转到个人主页
        return "/site/profile";
    }

    /**
     * 我的帖子
     */
    @GetMapping("/mypost/{userId}")
    public String getMyPost(@PathVariable("userId") int userId, Page page, Model model) {
        //查询用户信息
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new BusinessException(ExceptionConstant.USER_NOT_FOUND);
        }
        model.addAttribute("user", user);

        // 分页信息
        page.setPath("/user/mypost/" + userId);
        page.setRows(discussPostService.findDiscussPostRows(userId));

        // 帖子列表
        List<DiscussPost> discussList = discussPostService
                .findDiscussPosts(userId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> discussVOList = new ArrayList<>();
        if (discussList != null) {
            for (DiscussPost post : discussList) {
                Map<String, Object> map = new HashMap<>();
                map.put("discussPost", post);
                //点赞数
                map.put("likeCount", likeService.getEntityLikeCount(ENTITY_TYPE_POST, post.getId()));
                discussVOList.add(map);
            }
        }
        model.addAttribute("discussPosts", discussVOList);

        return "/site/my-post";
    }

    /**
     * 我的回复
     */
    @GetMapping("/myreply/{userId}")
    public String getMyreply(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) throw new BusinessException(ExceptionConstant.USER_NOT_FOUND);
        model.addAttribute("user", user);

        //设置分页信息
        page.setPath("/user/myreply/" + userId);
        page.setRows(commentService.findUserCount(userId));

        //查询用户的回复
        List<Comment> commentList = commentService.findUserComments(userId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> commentVOList = new ArrayList<>();
        if (commentList != null) {
            for (Comment comment : commentList) {
                Map<String, Object> map = new HashMap<>();
                map.put("comment", comment);
                DiscussPost post = discussPostService.selectById(comment.getEntityId());
                map.put("discussPost", post);
                commentVOList.add(map);
            }
        }
        model.addAttribute("comments", commentVOList);

        return "/site/my-reply";
    }
}

package com.zxk.xxxt.controller;

import com.alibaba.fastjson.JSONObject;
import com.zxk.xxxt.DTO.*;
import com.zxk.xxxt.Utils.JsonUtils;
import com.zxk.xxxt.VO.CommentVO;
import com.zxk.xxxt.anno.NotLogin;
import com.zxk.xxxt.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 创建评论
     */
    @PostMapping("/create")
    public JSONObject createComment(@Valid @RequestBody CommentCreateDTO dto, HttpServletRequest request) {
        try {
            // 获取当前登录用户ID
            @SuppressWarnings("unchecked")
            Map<String, String> currentUser = (Map<String, String>) request.getAttribute("currentUser");
            if (currentUser == null) {
                return JsonUtils.fail("未登录");
            }

            String userId = currentUser.get("userId");
            if (userId == null || userId.isEmpty()) {
                return JsonUtils.fail("用户ID不能为空");
            }

            boolean success = commentService.createComment(dto, Integer.parseInt(userId));
            if (success) {
                return JsonUtils.success("评论成功");
            } else {
                return JsonUtils.fail("评论失败");
            }
        } catch (NumberFormatException e) {
            return JsonUtils.fail("用户ID格式错误");
        } catch (Exception e) {
            return JsonUtils.fail("评论失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID查询评论
     */
    @PostMapping("/getbyid")
    @NotLogin
    public JSONObject getById(@Valid @RequestBody IdDTO dto) {
        try {
            CommentVO comment = commentService.getById(dto.getId());
            if (comment == null) {
                return JsonUtils.fail("评论不存在");
            }
            return JsonUtils.success(comment);
        } catch (Exception e) {
            return JsonUtils.fail(e.getMessage());
        }
    }

    /**
     * 根据帖子ID查询评论列表
     */
    @PostMapping("/getbypost")
    @NotLogin
    public JSONObject getByPostId(@Valid @RequestBody CommentQueryDTO dto) {
        try {
            if (dto.getPostId() == null) {
                return JsonUtils.fail("帖子ID不能为空");
            }
            List<CommentVO> comments = commentService.getByPostId(dto.getPostId());
            return JsonUtils.success(comments);
        } catch (Exception e) {
            return JsonUtils.fail(e.getMessage());
        }
    }

    /**
     * 根据父评论ID查询子评论列表
     */
    @PostMapping("/getbyparent")
    @NotLogin
    public JSONObject getByParentId(@Valid @RequestBody CommentQueryDTO dto) {
        try {
            if (dto.getParentId() == null) {
                return JsonUtils.fail("父评论ID不能为空");
            }
            List<CommentVO> comments = commentService.getByParentId(dto.getParentId());
            return JsonUtils.success(comments);
        } catch (Exception e) {
            return JsonUtils.fail(e.getMessage());
        }
    }

    /**
     * 更新评论
     */
    @PutMapping("/update")
    public JSONObject updateComment(@Valid @RequestBody CommentUpdateDTO dto, HttpServletRequest request) {
        try {
            // 获取当前登录用户ID
            @SuppressWarnings("unchecked")
            Map<String, String> currentUser = (Map<String, String>) request.getAttribute("currentUser");
            if (currentUser == null) {
                return JsonUtils.fail("未登录");
            }

            String userId = currentUser.get("userId");
            if (userId == null || userId.isEmpty()) {
                return JsonUtils.fail("用户ID不能为空");
            }

            // 验证是否为评论作者
            CommentVO existingComment = commentService.getById(dto.getId());
            if (existingComment == null) {
                return JsonUtils.fail("评论不存在");
            }
            if (!existingComment.getUserId().toString().equals(userId)) {
                return JsonUtils.fail("无权修改此评论");
            }

            boolean success = commentService.updateComment(dto);
            if (success) {
                return JsonUtils.success("更新成功");
            } else {
                return JsonUtils.fail("更新失败");
            }
        } catch (Exception e) {
            return JsonUtils.fail("更新失败: " + e.getMessage());
        }
    }

    /**
     * 删除评论（逻辑删除）
     */
    @PutMapping("/delete")
    public JSONObject deleteComment(@Valid @RequestBody IdDTO dto, HttpServletRequest httpRequest) {
        try {
            // 获取当前登录用户ID
            @SuppressWarnings("unchecked")
            Map<String, String> currentUser = (Map<String, String>) httpRequest.getAttribute("currentUser");
            if (currentUser == null) {
                return JsonUtils.fail("未登录");
            }

            String userId = currentUser.get("userId");
            if (userId == null || userId.isEmpty()) {
                return JsonUtils.fail("用户ID不能为空");
            }

            // 验证是否为评论作者
            CommentVO comment = commentService.getById(dto.getId());
            if (comment == null) {
                return JsonUtils.fail("评论不存在");
            }
            if (!comment.getUserId().toString().equals(userId)) {
                return JsonUtils.fail("无权删除此评论");
            }

            boolean success = commentService.deleteComment(dto.getId());
            if (success) {
                return JsonUtils.success("删除成功");
            } else {
                return JsonUtils.fail("删除失败");
            }
        } catch (Exception e) {
            return JsonUtils.fail("删除失败: " + e.getMessage());
        }
    }
}

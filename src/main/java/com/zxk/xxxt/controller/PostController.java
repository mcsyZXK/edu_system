package com.zxk.xxxt.controller;

import com.alibaba.fastjson.JSONObject;
import com.zxk.xxxt.DTO.*;
import com.zxk.xxxt.Utils.JsonUtils;
import com.zxk.xxxt.VO.PostVO;
import com.zxk.xxxt.anno.NotLogin;
import com.zxk.xxxt.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;

    /**
     * 创建帖子
     */
    @PostMapping("/create")
    public JSONObject createPost(@Valid @RequestBody PostCreateDTO dto, HttpServletRequest request) {
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

            boolean success = postService.createPost(dto, Integer.parseInt(userId));
            if (success) {
                return JsonUtils.success("发布成功");
            } else {
                return JsonUtils.fail("发布失败");
            }
        } catch (NumberFormatException e) {
            return JsonUtils.fail("用户ID格式错误");
        } catch (Exception e) {
            return JsonUtils.fail("发布失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID查询帖子
     */
    @PostMapping("/getbyid")
    @NotLogin
    public JSONObject getById(@Valid @RequestBody IdDTO dto) {
        try {
            PostVO post = postService.getById(dto.getId());
            if (post == null) {
                return JsonUtils.fail("帖子不存在");
            }
            return JsonUtils.success(post);
        } catch (Exception e) {
            return JsonUtils.fail(e.getMessage());
        }
    }

    /**
     * 根据用户ID查询帖子列表
     */
    @PostMapping("/getbyuser")
    @NotLogin
    public JSONObject getByUserId(@Valid @RequestBody PostQueryDTO dto) {
        try {
            if (dto.getUserId() == null) {
                return JsonUtils.fail("用户ID不能为空");
            }
            List<PostVO> posts = postService.getByUserId(dto.getUserId());
            return JsonUtils.success(posts);
        } catch (Exception e) {
            return JsonUtils.fail(e.getMessage());
        }
    }

    /**
     * 根据分类查询帖子列表
     */
    @PostMapping("/getbycategory")
    @NotLogin
    public JSONObject getByCategory(@Valid @RequestBody PostQueryDTO dto) {
        try {
            if (dto.getCategory() == null) {
                return JsonUtils.fail("分类不能为空");
            }
            List<PostVO> posts = postService.getByCategory(dto.getCategory());
            return JsonUtils.success(posts);
        } catch (Exception e) {
            return JsonUtils.fail(e.getMessage());
        }
    }

    /**
     * 查询所有已发布的帖子
     */
    @GetMapping("/list")
    @NotLogin
    public JSONObject getAllPublished() {
        try {
            List<PostVO> posts = postService.getAllPublished();
            return JsonUtils.success(posts);
        } catch (Exception e) {
            return JsonUtils.fail(e.getMessage());
        }
    }

    /**
     * 更新帖子
     */
    @PutMapping("/update")
    public JSONObject updatePost(@Valid @RequestBody PostUpdateDTO dto, HttpServletRequest request) {
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
            if (dto.getId() == null) {
                return JsonUtils.fail("帖子ID不能为空");
            }

            // 验证是否为帖子作者（需要查询原帖子）
            PostVO existingPost = postService.getById(dto.getId());
            if (existingPost == null) {
                return JsonUtils.fail("帖子不存在");
            }
            if (!existingPost.getUserId().toString().equals(userId)) {
                return JsonUtils.fail("无权修改此帖子");
            }

            boolean success = postService.updatePost(dto);
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
     * 删除帖子（逻辑删除）
     */
    @PutMapping("/delete")
    public JSONObject deletePost(@Valid @RequestBody IdDTO dto, HttpServletRequest httpRequest) {
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

            // 验证是否为帖子作者
            PostVO post = postService.getById(dto.getId());
            if (post == null) {
                return JsonUtils.fail("帖子不存在");
            }
            if (!post.getUserId().toString().equals(userId)) {
                return JsonUtils.fail("无权删除此帖子");
            }

            boolean success = postService.deletePost(dto.getId());
            if (success) {
                return JsonUtils.success("删除成功");
            } else {
                return JsonUtils.fail("删除失败");
            }
        } catch (Exception e) {
            return JsonUtils.fail("删除失败: " + e.getMessage());
        }
    }

    /**
     * 更新帖子状态（管理员功能）
     */
    @PutMapping("/status")
    public JSONObject updateStatus(@Valid @RequestBody PostStatusUpdateDTO dto) {
        try {
            boolean success = postService.updateStatus(dto);
            if (success) {
                return JsonUtils.success("状态更新成功");
            } else {
                return JsonUtils.fail("状态更新失败");
            }
        } catch (Exception e) {
            return JsonUtils.fail(e.getMessage());
        }
    }

    /**
     * 置顶/取消置顶（管理员功能）
     */
    @PutMapping("/top")
    public JSONObject updateTopStatus(@Valid @RequestBody PostTopUpdateDTO dto) {
        try {
            boolean success = postService.updateTopStatus(dto);
            if (success) {
                return JsonUtils.success("置顶状态更新成功");
            } else {
                return JsonUtils.fail("置顶状态更新失败");
            }
        } catch (Exception e) {
            return JsonUtils.fail(e.getMessage());
        }
    }

    /**
     * 点赞/取消点赞
     */
    @PutMapping("/like")
    public JSONObject likePost(@Valid @RequestBody PostLikeDTO dto) {
        try {
            boolean success = postService.updateLikeCount(dto);
            if (success) {
                return JsonUtils.success("操作成功");
            } else {
                return JsonUtils.fail("操作失败");
            }
        } catch (Exception e) {
            return JsonUtils.fail(e.getMessage());
        }
    }
}

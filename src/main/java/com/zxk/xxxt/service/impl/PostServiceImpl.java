package com.zxk.xxxt.service.impl;

import com.zxk.xxxt.DTO.*;
import com.zxk.xxxt.POJO.Post;
import com.zxk.xxxt.POJO.Student;
import com.zxk.xxxt.VO.PostVO;
import com.zxk.xxxt.mapper.PostMapper;
import com.zxk.xxxt.mapper.StudentMapper;
import com.zxk.xxxt.service.PostService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {
    @Autowired
    private PostMapper postMapper;
    @Autowired
    private StudentMapper studentMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createPost(PostCreateDTO dto, Integer userId) {
        Post post = new Post();
        BeanUtils.copyProperties(dto, post);
        post.setUserId(userId);
        
        // 设置创建时间和更新时间（Unix时间戳，单位：秒）
        int currentTime = (int) (System.currentTimeMillis() / 1000);
        post.setCreateTime(currentTime);
        post.setUpdateTime(currentTime);
        post.setDeleteTime(0); // 默认未删除
        
        // 设置默认值
        post.setStatus(0); // 默认待审核（0）
        post.setIsTop(false); // 默认不置顶
        post.setLikeCount(0); // 默认点赞数为0

        int result = postMapper.insertSelective(post);
        return result > 0;
    }

    @Override
    public PostVO getById(Integer id) {
        Post post = postMapper.selectByPrimaryKey(id);
        if (post == null || (post.getDeleteTime() != null && post.getDeleteTime() != 0)) {
            return null;
        }
        return convertToVO(post);
    }

    @Override
    public List<PostVO> getByUserId(Integer userId) {
        List<Post> posts = postMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Post>()
                .eq("user_id", userId)
                .eq("delete_time", 0)
                .orderByDesc("create_time")
        );
        return convertToVOList(posts);
    }

    @Override
    public List<PostVO> getByCategory(Integer category) {
        List<Post> posts = postMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Post>()
                .eq("category", category)
                .eq("status", 1) // 只查询已发布的
                .eq("delete_time", 0)
                .orderByDesc("is_top", "create_time")
        );
        return convertToVOList(posts);
    }

    @Override
    public List<PostVO> getAllPublished() {
        List<Post> posts = postMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Post>()
                .eq("status", 1) // 已发布
                .eq("delete_time", 0)
                .orderByDesc("is_top", "create_time")
        );
        return convertToVOList(posts);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePost(PostUpdateDTO dto) {
        Post post = new Post();
        BeanUtils.copyProperties(dto, post);
        // 更新更新时间
        int updateTime = (int) (System.currentTimeMillis() / 1000);
        post.setUpdateTime(updateTime);
        
        int result = postMapper.updateByPrimaryKeySelective(post);
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deletePost(Integer id) {
        // 逻辑删除，设置删除时间
        Post post = new Post();
        post.setId(id);
        int deleteTime = (int) (System.currentTimeMillis() / 1000);
        post.setDeleteTime(deleteTime);
        post.setUpdateTime(deleteTime);
        
        int result = postMapper.updateByPrimaryKeySelective(post);
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(PostStatusUpdateDTO dto) {
        Post post = new Post();
        post.setId(dto.getId());
        post.setStatus(dto.getStatus());
        int updateTime = (int) (System.currentTimeMillis() / 1000);
        post.setUpdateTime(updateTime);
        
        int result = postMapper.updateByPrimaryKeySelective(post);
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTopStatus(PostTopUpdateDTO dto) {
        Post post = new Post();
        post.setId(dto.getId());
        post.setIsTop(dto.getIsTop());
        int updateTime = (int) (System.currentTimeMillis() / 1000);
        post.setUpdateTime(updateTime);
        
        int result = postMapper.updateByPrimaryKeySelective(post);
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateLikeCount(PostLikeDTO dto) {
        Post post = postMapper.selectByPrimaryKey(dto.getId());
        if (post == null) {
            return false;
        }
        
        int currentLikeCount = post.getLikeCount() != null ? post.getLikeCount() : 0;
        // 如果 increment 为 null，使用默认值 1
        int increment = dto.getIncrement() != null ? dto.getIncrement() : 1;
        int newLikeCount = Math.max(0, currentLikeCount + increment); // 确保不为负数
        
        post.setLikeCount(newLikeCount);
        int updateTime = (int) (System.currentTimeMillis() / 1000);
        post.setUpdateTime(updateTime);
        
        int result = postMapper.updateByPrimaryKeySelective(post);
        return result > 0;
    }

    /**
     * 将 Post POJO 转换为 PostVO
     */
    private PostVO convertToVO(Post post) {
        if (post == null) {
            return null;
        }
        PostVO vo = new PostVO();
        BeanUtils.copyProperties(post, vo);
        
        // 填充用户信息
        if (post.getUserId() != null) {
            Student student = studentMapper.selectByPrimaryKey(post.getUserId());
            if (student != null) {
                vo.setUsername(student.getUsername());
                vo.setNickname(student.getNickname());
                vo.setAvatar(student.getAvatar());
            }
        }
        
        // 填充分类名称
        if (post.getCategory() != null) {
            switch (post.getCategory()) {
                case 0:
                    vo.setCategoryName("学习资料");
                    break;
                case 1:
                    vo.setCategoryName("生活资讯");
                    break;
                case 2:
                    vo.setCategoryName("日常分享");
                    break;
                default:
                    vo.setCategoryName("未知分类");
                    break;
            }
        }
        
        // 填充状态名称
        if (post.getStatus() != null) {
            switch (post.getStatus()) {
                case 0:
                    vo.setStatusName("待审核");
                    break;
                case 1:
                    vo.setStatusName("已发布");
                    break;
                case 2:
                    vo.setStatusName("已驳回");
                    break;
                default:
                    vo.setStatusName("未知状态");
                    break;
            }
        }
        
        // TODO: 填充评论数（需要统计）
        vo.setCommentCount(0);
        
        return vo;
    }

    /**
     * 将 Post 列表转换为 PostVO 列表
     */
    private List<PostVO> convertToVOList(List<Post> posts) {
        if (posts == null || posts.isEmpty()) {
            return new ArrayList<>();
        }
        return posts.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }
}


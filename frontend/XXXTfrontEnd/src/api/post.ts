import request from '@/utils/request'

// 帖子相关接口类型定义
export interface PostCreateDTO {
  title: string
  content: string
  category: number // 0-学习资料，1-生活资讯，2-日常分享
  images?: string[]
}

export interface PostUpdateDTO {
  id: number
  title?: string
  content?: string
  category?: number
  images?: string[]
}

export interface PostQueryDTO {
  userId?: number
  category?: number
  status?: number
}

export interface PostVO {
  id: number
  userId: number
  username: string
  nickname?: string
  avatar?: string
  title: string
  content: string
  category: number
  categoryName: string
  status: number
  statusName: string
  likeCount: number
  commentCount: number
  isTop: boolean
  images?: string[]
  createTime: number
  updateTime: number
}

export interface PostLikeDTO {
  id: number
  increment: number // 1-点赞，-1-取消点赞
}

// 帖子API
export const postApi = {
  // 创建帖子
  createPost(data: PostCreateDTO) {
    return request.post<string>('/post/create', data)
  },

  // 根据ID查询帖子
  getById(id: number) {
    return request.post<PostVO>('/post/getbyid', { id })
  },

  // 根据用户ID查询帖子列表
  getByUserId(userId: number) {
    return request.post<PostVO[]>('/post/getbyuser', { userId })
  },

  // 根据分类查询帖子列表
  getByCategory(category: number) {
    return request.post<PostVO[]>('/post/getbycategory', { category })
  },

  // 获取所有已发布的帖子
  getAllPublished() {
    return request.get<PostVO[]>('/post/list')
  },

  // 更新帖子
  updatePost(data: PostUpdateDTO) {
    return request.put<string>('/post/update', data)
  },

  // 删除帖子
  deletePost(id: number) {
    return request.put<string>('/post/delete', { id })
  },

  // 更新帖子状态（管理员）
  updateStatus(id: number, status: number) {
    return request.put<string>('/post/status', { id, status })
  },

  // 置顶/取消置顶（管理员）
  updateTopStatus(id: number, isTop: boolean) {
    return request.put<string>('/post/top', { id, isTop })
  },

  // 点赞/取消点赞
  likePost(id: number, increment: number) {
    return request.put<string>('/post/like', { id, increment })
  }
}


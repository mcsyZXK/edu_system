import request from '@/utils/request'

// 评论相关接口类型定义
export interface CommentCreateDTO {
  postId: number
  parentId?: number
  content: string
}

export interface CommentUpdateDTO {
  id: number
  content: string
}

export interface CommentQueryDTO {
  postId?: number
  parentId?: number
}

export interface CommentVO {
  id: number
  postId: number
  userId: number
  username: string
  nickname?: string
  avatar?: string
  parentId?: number
  content: string
  likeCount: number
  createTime: number
  updateTime: number
  children?: CommentVO[] // 子评论
}

// 评论API
export const commentApi = {
  // 创建评论
  createComment(data: CommentCreateDTO) {
    return request.post<string>('/comment/create', data)
  },

  // 根据ID查询评论
  getById(id: number) {
    return request.post<CommentVO>('/comment/getbyid', { id })
  },

  // 根据帖子ID查询评论列表
  getByPostId(postId: number) {
    return request.post<CommentVO[]>('/comment/getbypost', { postId })
  },

  // 根据父评论ID查询子评论列表
  getByParentId(parentId: number) {
    return request.post<CommentVO[]>('/comment/getbyparent', { parentId })
  },

  // 更新评论
  updateComment(data: CommentUpdateDTO) {
    return request.put<string>('/comment/update', data)
  },

  // 删除评论
  deleteComment(id: number) {
    return request.put<string>('/comment/delete', { id })
  }
}


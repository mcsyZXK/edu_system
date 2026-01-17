import request from '@/utils/request'

// 文件相关接口类型定义
export interface FileVO {
  fileName: string
  originalFileName: string
  fileSize: number
  fileUrl: string
  contentType: string
  fileExtension: string
}

// 文件API
export const fileApi = {
  // 上传文件
  uploadFile(file: File) {
    const formData = new FormData()
    formData.append('file', file)
    return request.post<FileVO>('/file/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // 获取文件URL（直接访问，不需要API调用）
  getFileUrl(relativePath: string) {
    const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:4096/zxk'
    return `${baseURL}/file/download/${relativePath}`
  },

  // 删除文件
  deleteFile(fileUrl: string) {
    return request.delete<string>('/file/delete', {
      params: { fileUrl }
    })
  }
}


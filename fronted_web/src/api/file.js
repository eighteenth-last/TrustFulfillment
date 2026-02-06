import request from '@/utils/request'

/**
 * 上传单个文件
 * @param {File} file 文件对象
 * @param {string} folder 文件夹 (docs, images)
 */
export function uploadFile(file, folder = 'docs') {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('folder', folder)
  
  return request({
    url: '/file/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 批量上传文件
 * @param {File[]} files 文件数组
 * @param {string} folder 文件夹 (docs, images)
 */
export function uploadFiles(files, folder = 'docs') {
  const formData = new FormData()
  files.forEach(file => {
    formData.append('files', file)
  })
  formData.append('folder', folder)
  
  return request({
    url: '/file/upload/batch',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 删除文件
 * @param {string} url 文件URL
 */
export function deleteFile(url) {
  return request({
    url: '/file/delete',
    method: 'delete',
    params: { url }
  })
}

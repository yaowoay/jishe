import axios from 'axios'

/**
 * 上传图片到后端进行人脸检测，返回检测参数（face_num, face_1, ...）
 * @param {File|Blob} file - 图片文件
 * @returns {Promise<Object>} - 检测结果参数对象
 */
export async function detectFace(file) {
  const formData = new FormData()
  formData.append('file', file)

  // 直接使用后端服务器地址
  const apiUrl = process.env.NODE_ENV === 'development'
    ? 'http://localhost:8089/api/face/detect'
    : '/api/face/detect'

  const resp = await axios.post(apiUrl, formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
  if (resp.data.success) {
    // 后端返回的data是JSON字符串，需要解析为对象
    const faceData = typeof resp.data.data === 'string'
      ? JSON.parse(resp.data.data)
      : resp.data.data

    // 简化控制台输出，只显示关键信息
    console.log('人脸检测完成，检测到人脸数:', faceData.face_num || 0)
    return faceData
  } else {
    throw new Error(resp.data.message || '检测失败')
  }
}
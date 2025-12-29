// 用户相关类型
export interface UserInfo {
  id: string
  username: string
  userType: number
  studentId?: string
  teacherId?: string
}

// 申请状态
export enum ApplicationStatus {
  PENDING = 0,      // 待审批
  IN_PROGRESS = 1,  // 审批中
  APPROVED = 2,     // 已通过
  REJECTED = 3,     // 已拒绝
  CANCELLED = 4,    // 已撤销
}

// 审批结果
export enum ApprovalResult {
  REJECTED = 0,     // 拒绝
  APPROVED = 1,     // 通过
}

// 用户类型
export enum UserType {
  STUDENT = 1,      // 学生
  TEACHER = 2,      // 教师
}

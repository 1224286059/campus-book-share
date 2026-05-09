export var shareTypeMap = {
  SALE: '出售',
  BORROW: '借阅',
  EXCHANGE: '交换',
  DONATE: '捐赠'
}

export var shareTypeTagMap = {
  SALE: 'danger',
  BORROW: 'success',
  EXCHANGE: 'warning',
  DONATE: 'primary'
}

export var bookStatusMap = {
  PENDING: '待审核',
  ON_SHELF: '已上架',
  SHARING: '共享中',
  COMPLETED: '已完成',
  OFF_SHELF: '已下架',
  REJECTED: '审核驳回'
}

export var orderTypeMap = {
  SALE: '购买',
  BORROW: '借阅',
  EXCHANGE: '交换',
  DONATE: '捐赠领取'
}

export var orderStatusMap = {
  PENDING: '待确认',
  ACCEPTED: '进行中',
  COMPLETED: '已完成',
  CANCELLED: '已取消',
  REJECTED: '已拒绝'
}

export var borrowStatusMap = {
  BORROWING: '借阅中',
  RETURNED: '已归还',
  OVERDUE: '已逾期'
}

export var reportTargetTypeMap = {
  BOOK: '书籍',
  USER: '用户',
  ORDER: '订单',
  EVALUATION: '评价'
}

export var shareTypeOptions = [
  { label: '出售', value: 'SALE' },
  { label: '借阅', value: 'BORROW' },
  { label: '交换', value: 'EXCHANGE' },
  { label: '捐赠', value: 'DONATE' }
]

function pad(value) {
  return value < 10 ? '0' + value : String(value)
}

export function formatDateTime(value) {
  if (!value) {
    return '-'
  }
  var date = new Date(value)
  if (Number.isNaN(date.getTime())) {
    return value
  }
  return date.getFullYear() + '-' + pad(date.getMonth() + 1) + '-' + pad(date.getDate()) + ' '
    + pad(date.getHours()) + ':' + pad(date.getMinutes()) + ':' + pad(date.getSeconds())
}

export function toDateTimeString(value) {
  if (!value) {
    return null
  }
  var date = value instanceof Date ? value : new Date(value)
  if (Number.isNaN(date.getTime())) {
    return null
  }
  return date.getFullYear() + '-' + pad(date.getMonth() + 1) + '-' + pad(date.getDate()) + ' '
    + pad(date.getHours()) + ':' + pad(date.getMinutes()) + ':' + pad(date.getSeconds())
}

export function formatPrice(value) {
  if (value === null || value === undefined || value === '') {
    return '面议'
  }
  return '¥' + Number(value).toFixed(2)
}

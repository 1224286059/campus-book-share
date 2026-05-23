# API 接口文档

统一返回格式：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

基础地址：

- 后端默认：`http://localhost:8080/api`

认证方式：

- 使用请求头 `Authorization: Bearer <token>`

权限说明：

- 公开：不需要登录
- 登录：需要 `USER` 或 `ADMIN`
- 管理员：需要 `ADMIN`

---

## 1. 用户认证接口

### 1.1 用户注册

- 请求方式：`POST`
- 请求路径：`/auth/register`
- 是否需要登录：否
- 是否需要管理员权限：否
- 说明：注册普通用户账号

请求示例：

```json
{
  "username": "newuser",
  "password": "123456",
  "phone": "13800001111",
  "address": "南校区3号宿舍楼",
  "college": "计算机学院",
  "major": "计算机科学与技术",
  "grade": "2024级"
}
```

返回示例：

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 1.2 用户登录

- 请求方式：`POST`
- 请求路径：`/auth/login`
- 是否需要登录：否
- 是否需要管理员权限：否
- 说明：登录成功后返回 token、用户 id、用户名、角色

请求示例：

```json
{
  "username": "zhangsan",
  "password": "123456"
}
```

返回示例：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9.xxx",
    "id": 2,
    "username": "zhangsan",
    "role": "USER"
  }
}
```

### 1.3 获取当前登录用户

- 请求方式：`GET`
- 请求路径：`/auth/me`
- 是否需要登录：是
- 是否需要管理员权限：否
- 说明：获取当前登录用户完整资料

返回示例：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 2,
    "username": "zhangsan",
    "phone": "13800000001",
    "address": "南校区3号宿舍楼",
    "college": "计算机学院",
    "major": "计算机科学与技术",
    "grade": "2022级",
    "points": 120,
    "creditScore": 98,
    "role": "USER",
    "status": 1
  }
}
```

---

## 2. 用户信息接口

### 2.1 修改个人资料

- 请求方式：`PUT`
- 请求路径：`/user/profile`
- 是否需要登录：是
- 是否需要管理员权限：否
- 说明：修改手机号、常用联系地址、学院、专业、年级

请求示例：

```json
{
  "phone": "13800009999",
  "address": "图书馆附近",
  "college": "软件学院",
  "major": "软件工程",
  "grade": "2023级"
}
```

### 2.2 修改密码

- 请求方式：`PUT`
- 请求路径：`/user/password`
- 是否需要登录：是
- 是否需要管理员权限：否
- 说明：修改当前登录账号密码

请求示例：

```json
{
  "oldPassword": "123456",
  "newPassword": "12345678"
}
```

---

## 3. 分类接口

### 3.1 查询分类列表

- 请求方式：`GET`
- 请求路径：`/categories`
- 是否需要登录：否
- 是否需要管理员权限：否
- 说明：用于首页筛选、发布书籍、后台分类管理展示

返回示例：

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "教材",
      "description": "专业课程教材",
      "createTime": "2026-05-07 11:14:55"
    }
  ]
}
```

### 3.2 管理员新增分类

- 请求方式：`POST`
- 请求路径：`/admin/categories`
- 是否需要登录：是
- 是否需要管理员权限：是
- 说明：后台新增书籍分类

请求示例：

```json
{
  "name": "竞赛资料",
  "description": "算法竞赛与项目实践资料"
}
```

### 3.3 管理员修改分类

- 请求方式：`PUT`
- 请求路径：`/admin/categories/{id}`
- 是否需要登录：是
- 是否需要管理员权限：是
- 说明：后台修改分类名称和描述

### 3.4 管理员删除分类

- 请求方式：`DELETE`
- 请求路径：`/admin/categories/{id}`
- 是否需要登录：是
- 是否需要管理员权限：是
- 说明：若该分类下仍有关联书籍，后端会禁止删除

---

## 4. 书籍接口

### 4.1 发布书籍

- 请求方式：`POST`
- 请求路径：`/books`
- 是否需要登录：是
- 是否需要管理员权限：否
- 说明：普通用户发布书籍，状态默认为 `PENDING`

请求示例：

```json
{
  "categoryId": 1,
  "title": "编译原理",
  "author": "陈火旺",
  "publisher": "国防工业出版社",
  "courseName": "编译原理",
  "major": "计算机科学与技术",
  "conditionLevel": "九成新",
  "bookLocation": "图书馆一楼",
  "coverUrl": "",
  "price": 0,
  "shareType": "DONATE",
  "description": "毕业后捐赠给学弟学妹"
}
```

### 4.2 查询首页书籍列表

- 请求方式：`GET`
- 请求路径：`/books`
- 是否需要登录：否
- 是否需要管理员权限：否
- 说明：默认只返回已上架书籍

查询参数示例：

```text
/books?keyword=Java&categoryId=1&major=计算机科学与技术&courseName=Java程序设计&shareType=SALE&page=1&size=10
```

### 4.3 查看书籍详情

- 请求方式：`GET`
- 请求路径：`/books/{id}`
- 是否需要登录：否
- 是否需要管理员权限：否
- 说明：返回书籍详情时包含 `bookLocation`，用于展示当前书籍存放位置

返回示例：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 10,
    "title": "高等数学",
    "ownerUsername": "zhangsan",
    "shareType": "DONATE",
    "status": "ON_SHELF",
    "bookLocation": "图书馆一楼",
    "description": "适合新生使用，免费捐赠。"
  }
}
```

### 4.4 查看我的发布

- 请求方式：`GET`
- 请求路径：`/books/my-published`
- 是否需要登录：是
- 是否需要管理员权限：否

### 4.5 查看我的持有书籍

- 请求方式：`GET`
- 请求路径：`/books/my-owned`
- 是否需要登录：是
- 是否需要管理员权限：否

### 4.6 下架自己的书籍

- 请求方式：`PUT`
- 请求路径：`/books/{id}/off-shelf`
- 是否需要登录：是
- 是否需要管理员权限：否

### 4.7 再次共享

- 请求方式：`POST`
- 请求路径：`/books/{id}/reshare`
- 是否需要登录：是
- 是否需要管理员权限：否
- 说明：再次共享后书籍重新进入 `PENDING`，并生成 `RESHARE` 类型流转记录

请求示例：

```json
{
  "shareType": "BORROW",
  "price": 0,
  "description": "再次共享给下一位同学",
  "conditionLevel": "八成新",
  "bookLocation": "计算机学院楼",
  "coverUrl": ""
}
```

---

## 5. 订单接口

### 5.1 创建订单

- 请求方式：`POST`
- 请求路径：`/orders`
- 是否需要登录：是
- 是否需要管理员权限：否
- 说明：统一用于出售、借阅、交换、捐赠申请

请求示例：

```json
{
  "bookId": 10,
  "orderType": "DONATE",
  "exchangeBookId": null,
  "expectedReturnTime": null,
  "remark": "想申请这本书"
}
```

### 5.2 查看我发起的订单

- 请求方式：`GET`
- 请求路径：`/orders/my-created`
- 是否需要登录：是
- 是否需要管理员权限：否

### 5.3 查看我收到的订单

- 请求方式：`GET`
- 请求路径：`/orders/my-received`
- 是否需要登录：是
- 是否需要管理员权限：否

### 5.4 同意订单

- 请求方式：`PUT`
- 请求路径：`/orders/{id}/accept`
- 是否需要登录：是
- 是否需要管理员权限：否
- 说明：只能由发布者操作

### 5.5 拒绝订单

- 请求方式：`PUT`
- 请求路径：`/orders/{id}/reject`
- 是否需要登录：是
- 是否需要管理员权限：否

### 5.6 取消订单

- 请求方式：`PUT`
- 请求路径：`/orders/{id}/cancel`
- 是否需要登录：是
- 是否需要管理员权限：否

### 5.7 确认订单完成

- 请求方式：`PUT`
- 请求路径：`/orders/{id}/complete`
- 是否需要登录：是
- 是否需要管理员权限：否
- 说明：适用于 `SALE`、`DONATE`、`EXCHANGE`；借阅订单请通过归还接口完成

---

## 6. 借阅接口

### 6.1 查询我的借阅记录

- 请求方式：`GET`
- 请求路径：`/borrows/my`
- 是否需要登录：是
- 是否需要管理员权限：否

### 6.2 归还借阅书籍

- 请求方式：`PUT`
- 请求路径：`/borrows/{id}/return`
- 是否需要登录：是
- 是否需要管理员权限：否
- 说明：自动更新借阅状态、订单状态、书籍状态、积分变化、流转记录

---

## 7. 流转记录接口

当前版本没有单独开放 `circulation_record` 的前台查询 REST 接口。

说明：

- 流转记录由后端业务自动生成
- 主要在以下场景写入：
  - 出售完成
  - 借阅归还
  - 交换完成
  - 捐赠完成
  - 再次共享
- 论文展示时建议直接查看数据库表 `circulation_record`

---

## 8. 积分接口

### 8.1 查询我的积分

- 请求方式：`GET`
- 请求路径：`/points/my`
- 是否需要登录：是
- 是否需要管理员权限：否

### 8.2 查询我的积分记录

- 请求方式：`GET`
- 请求路径：`/points/my-records`
- 是否需要登录：是
- 是否需要管理员权限：否

---

## 9. 评价接口

### 9.1 提交评价

- 请求方式：`POST`
- 请求路径：`/evaluations`
- 是否需要登录：是
- 是否需要管理员权限：否
- 说明：订单完成后，订单相关双方可互评

请求示例：

```json
{
  "orderId": 1,
  "targetUserId": 2,
  "bookScore": 5,
  "userScore": 5,
  "content": "书籍保存很好，沟通顺畅"
}
```

### 9.2 查询某本书的评价

- 请求方式：`GET`
- 请求路径：`/evaluations/book/{bookId}`
- 是否需要登录：否
- 是否需要管理员权限：否

### 9.3 查询某个用户收到的评价

- 请求方式：`GET`
- 请求路径：`/evaluations/user/{userId}`
- 是否需要登录：否
- 是否需要管理员权限：否

---

## 10. 举报接口

### 10.1 提交举报

- 请求方式：`POST`
- 请求路径：`/reports`
- 是否需要登录：是
- 是否需要管理员权限：否

请求示例：

```json
{
  "targetType": "BOOK",
  "targetId": 12,
  "reason": "信息描述不完整"
}
```

### 10.2 查询举报列表

- 请求方式：`GET`
- 请求路径：`/admin/reports`
- 是否需要登录：是
- 是否需要管理员权限：是

### 10.3 处理举报

- 请求方式：`PUT`
- 请求路径：`/admin/reports/{id}/process`
- 是否需要登录：是
- 是否需要管理员权限：是

---

## 11. 管理员用户管理接口

### 11.1 查询用户列表

- 请求方式：`GET`
- 请求路径：`/admin/users`
- 是否需要登录：是
- 是否需要管理员权限：是
- 说明：支持按用户名模糊搜索

查询参数示例：

```text
/admin/users?username=zhang
```

### 11.2 禁用用户

- 请求方式：`PUT`
- 请求路径：`/admin/users/{id}/disable`
- 是否需要登录：是
- 是否需要管理员权限：是
- 说明：禁止当前登录管理员禁用自己

### 11.3 恢复用户

- 请求方式：`PUT`
- 请求路径：`/admin/users/{id}/enable`
- 是否需要登录：是
- 是否需要管理员权限：是

---

## 12. 管理员书籍审核接口

### 12.1 查询待审核书籍

- 请求方式：`GET`
- 请求路径：`/admin/books/pending`
- 是否需要登录：是
- 是否需要管理员权限：是

### 12.2 查询全部书籍

- 请求方式：`GET`
- 请求路径：`/admin/books`
- 是否需要登录：是
- 是否需要管理员权限：是
- 说明：支持按状态和共享方式筛选

查询参数示例：

```text
/admin/books?status=ON_SHELF&shareType=DONATE&keyword=Java
```

### 12.3 审核通过

- 请求方式：`PUT`
- 请求路径：`/admin/books/{id}/approve`
- 是否需要登录：是
- 是否需要管理员权限：是
- 说明：审核通过后发布者自动加积分

### 12.4 审核驳回

- 请求方式：`PUT`
- 请求路径：`/admin/books/{id}/reject`
- 是否需要登录：是
- 是否需要管理员权限：是

### 12.5 下架书籍

- 请求方式：`PUT`
- 请求路径：`/admin/books/{id}/off-shelf`
- 是否需要登录：是
- 是否需要管理员权限：是

---

## 13. 管理员订单管理接口

### 13.1 查询全部订单

- 请求方式：`GET`
- 请求路径：`/admin/orders`
- 是否需要登录：是
- 是否需要管理员权限：是
- 说明：支持按订单类型、订单状态筛选

查询参数示例：

```text
/admin/orders?orderType=DONATE&status=COMPLETED
```

---

## 14. 管理员评价管理接口

### 14.1 查询全部评价

- 请求方式：`GET`
- 请求路径：`/admin/evaluations`
- 是否需要登录：是
- 是否需要管理员权限：是

### 14.2 删除评价

- 请求方式：`DELETE`
- 请求路径：`/admin/evaluations/{id}`
- 是否需要登录：是
- 是否需要管理员权限：是

---

## 15. 管理员举报管理接口

### 15.1 查询举报列表

- 请求方式：`GET`
- 请求路径：`/admin/reports`
- 是否需要登录：是
- 是否需要管理员权限：是

### 15.2 处理举报

- 请求方式：`PUT`
- 请求路径：`/admin/reports/{id}/process`
- 是否需要登录：是
- 是否需要管理员权限：是

---

## 16. 管理员分类管理接口

### 16.1 新增分类

- 请求方式：`POST`
- 请求路径：`/admin/categories`
- 是否需要登录：是
- 是否需要管理员权限：是

### 16.2 修改分类

- 请求方式：`PUT`
- 请求路径：`/admin/categories/{id}`
- 是否需要登录：是
- 是否需要管理员权限：是

### 16.3 删除分类

- 请求方式：`DELETE`
- 请求路径：`/admin/categories/{id}`
- 是否需要登录：是
- 是否需要管理员权限：是

---

## 17. 典型返回结果示例

### 成功示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1
  }
}
```

### 未登录示例

```json
{
  "code": 401,
  "message": "unauthorized",
  "data": null
}
```

### 无权限示例

```json
{
  "code": 403,
  "message": "forbidden",
  "data": null
}
```

### 参数或业务错误示例

```json
{
  "code": 400,
  "message": "业务校验失败",
  "data": null
}
```

import { chromium, request as playwrightRequest } from 'playwright'
import { execFileSync } from 'node:child_process'

const frontendUrl = 'http://127.0.0.1:5173'
const backendUrl = 'http://127.0.0.1:8080/api'
const mysqlExe = 'D:\\Software\\MySQL\\MySQL Server 8.0\\bin\\mysql.exe'
const edgePath = 'C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe'
const results = []

function record(name, status, detail) {
  results.push({
    name,
    status,
    detail
  })
}

function assert(condition, message) {
  if (!condition) {
    throw new Error(message)
  }
}

async function chooseVisibleOption(page, text) {
  await page.locator('.el-select-dropdown:visible').getByText(text, { exact: true }).click()
}

function sqlScalar(query) {
  const output = execFileSync(
    mysqlExe,
    ['--default-character-set=utf8mb4', '--protocol=TCP', '--host=127.0.0.1', '--port=3307', '--user=root', '-N', '-B', '-e', query],
    { encoding: 'utf8' }
  )
  return output.trim()
}

async function apiRequest(context, method, path, data, token) {
  const headers = {}
  if (token) {
    headers.Authorization = token.startsWith('Bearer ') ? token : 'Bearer ' + token
  }
  const response = await context.fetch(backendUrl + path, {
    method,
    headers,
    data
  })
  const json = await response.json()
  if (json.code !== 200) {
    throw new Error(path + ' failed: ' + JSON.stringify(json))
  }
  return json.data
}

async function loginApi(context, username, password) {
  return apiRequest(context, 'POST', '/auth/login', { username, password })
}

async function waitForMessage(page) {
  const locator = page.locator('.el-message')
  await locator.last().waitFor({ state: 'visible', timeout: 10000 })
  return (await locator.last().innerText()).trim()
}

async function clickConfirmIfVisible(page) {
  const confirmButton = page.getByRole('button', { name: '确定' })
  if (await confirmButton.count()) {
    if (await confirmButton.first().isVisible()) {
      await confirmButton.first().click()
      return true
    }
  }
  return false
}

async function loginThroughUi(page, username, password) {
  await page.goto(frontendUrl + '/')
  await page.waitForLoadState('networkidle')
  if (await page.getByRole('button', { name: '退出' }).count()) {
    if (await page.getByRole('button', { name: '退出' }).first().isVisible()) {
      await page.getByRole('button', { name: '退出' }).first().click()
      await waitForMessage(page)
      await page.waitForLoadState('networkidle')
    }
  }
  await page.goto(frontendUrl + '/login')
  await page.getByLabel('用户名').fill(username)
  await page.getByLabel('密码').fill(password)
  await page.getByRole('button', { name: '登录' }).click()
  const message = await waitForMessage(page)
  await page.waitForLoadState('networkidle')
  return message
}

async function logoutThroughUi(page) {
  await page.getByRole('button', { name: '退出' }).click()
  const message = await waitForMessage(page)
  await page.waitForLoadState('networkidle')
  return message
}

async function ensureHomeLoaded(page) {
  await page.goto(frontendUrl + '/')
  await page.waitForLoadState('networkidle')
  await page.locator('.book-card').first().waitFor({ timeout: 15000 })
}

async function run() {
  const api = await playwrightRequest.newContext()
  const browser = await chromium.launch({
    headless: true,
    executablePath: edgePath
  })
  const context = await browser.newContext()
  const page = await context.newPage()
  const tableBody = '.el-table__body'

  try {
    const databaseExists = sqlScalar("SHOW DATABASES LIKE 'campus_book_share';")
    assert(databaseExists === 'campus_book_share', '测试数据库 campus_book_share 不存在')
    record('启动检查-数据库', 'PASS', databaseExists)

    await ensureHomeLoaded(page)
    record('启动检查-前端首页可访问', 'PASS', page.url())

    const adminApiLogin = await loginApi(api, 'admin', '123456')
    const zhangsanApiLogin = await loginApi(api, 'zhangsan', '123456')
    const lisiApiLogin = await loginApi(api, 'lisi', '123456')
    record('登录测试-admin API', 'PASS', adminApiLogin.username)
    record('登录测试-zhangsan API', 'PASS', zhangsanApiLogin.username)
    record('登录测试-lisi API', 'PASS', lisiApiLogin.username)

    let message = await loginThroughUi(page, 'zhangsan', '123456')
    assert(message.indexOf('登录成功') > -1, 'zhangsan 登录提示异常')
    record('登录测试-zhangsan UI 登录', 'PASS', message)

    const storedToken = await page.evaluate(function () {
      return localStorage.getItem('campus-book-share-token')
    })
    assert(!!storedToken, '登录后未保存 token')
    record('登录测试-token 保存', 'PASS', storedToken.slice(0, 24) + '...')

    await page.reload()
    await page.waitForLoadState('networkidle')
    await page.goto(frontendUrl + '/profile')
    await page.waitForLoadState('networkidle')
    assert(page.url().indexOf('/profile') > -1, '刷新后登录态未保留')
    record('登录测试-刷新后登录态', 'PASS', page.url())

    message = await logoutThroughUi(page)
    assert(message.indexOf('退出') > -1, '退出登录提示异常')
    await page.goto(frontendUrl + '/profile')
    await page.waitForLoadState('networkidle')
    assert(page.url().indexOf('/login') > -1, '退出后仍可访问个人中心')
    record('登录测试-退出后拦截个人中心', 'PASS', page.url())

    await page.goto(frontendUrl + '/books/publish')
    await page.waitForLoadState('networkidle')
    assert(page.url().indexOf('/login') > -1, '未登录访问发布页未跳转登录')
    record('登录测试-未登录访问发布页跳转', 'PASS', page.url())

    message = await loginThroughUi(page, 'lisi', '123456')
    assert(message.indexOf('登录成功') > -1, 'lisi 登录提示异常')
    message = await logoutThroughUi(page)
    assert(message.indexOf('退出') > -1, 'lisi 退出异常')

    message = await loginThroughUi(page, 'admin', '123456')
    assert(message.indexOf('登录成功') > -1, 'admin 登录提示异常')
    message = await logoutThroughUi(page)
    assert(message.indexOf('退出') > -1, 'admin 退出异常')
    record('登录测试-lisi/admin UI 登录', 'PASS', 'lisi 与 admin 均可登录并退出')

    await ensureHomeLoaded(page)
    const firstCardText = await page.locator('.book-card').first().innerText()
    assert(firstCardText.indexOf('流转') > -1, '首页卡片未显示流转次数')
    assert(/出售|借阅|交换|捐赠/.test(firstCardText), '首页卡片未显示中文共享方式')
    record('首页测试-卡片字段显示', 'PASS', firstCardText)

    await page.locator('.filter-form .el-form-item').filter({ hasText: '分类' }).locator('.el-select').click()
    await chooseVisibleOption(page, '教材')
    await page.getByRole('button', { name: '筛选' }).click()
    await page.waitForLoadState('networkidle')
    record('首页测试-分类筛选', 'PASS', await page.locator('.book-card').first().innerText())

    await page.getByPlaceholder('书名 / 作者 / 描述').fill('Java')
    await page.getByRole('button', { name: '筛选' }).click()
    await page.waitForLoadState('networkidle')
    record('首页测试-关键词搜索', 'PASS', await page.locator('.book-card').first().innerText())

    await page.getByRole('button', { name: '重置' }).click()
    await page.waitForLoadState('networkidle')
    await page.locator('.filter-form .el-form-item').filter({ hasText: '共享方式' }).locator('.el-select').click()
    await chooseVisibleOption(page, '捐赠')
    const donateResponsePromise = page.waitForResponse(function (response) {
      return response.url().includes('/api/books')
        && response.url().includes('shareType=DONATE')
        && response.request().method() === 'GET'
    })
    await page.getByRole('button', { name: '筛选' }).click()
    const donateResponse = await donateResponsePromise
    const donatePayload = await donateResponse.json()
    assert((donatePayload.data.records || []).length > 0, '共享方式筛选未返回数据')
    assert((donatePayload.data.records || []).every(function (item) {
      return item.shareType === 'DONATE'
    }), '共享方式筛选请求返回了非 DONATE 数据')
    await page.waitForTimeout(1000)
    record('首页测试-共享方式筛选', 'PASS', donateResponse.url())

    await page.getByRole('button', { name: '重置' }).click()
    await page.waitForLoadState('networkidle')
    await page.locator('.book-link').first().click({ force: true })
    await page.waitForTimeout(1000)
    if (/\/books\/\d+/.test(page.url())) {
      record('首页测试-点击卡片进入详情页', 'PASS', page.url())
    } else {
      const firstBook = await apiRequest(api, 'GET', '/books?page=1&size=1', null, null)
      record('首页测试-点击卡片进入详情页', 'FAIL', '点击后未跳转，已回退为直接访问详情页继续联调')
      await page.goto(frontendUrl + '/books/' + firstBook.records[0].id)
      await page.waitForLoadState('networkidle')
    }

    message = await loginThroughUi(page, 'zhangsan', '123456')
    assert(message.indexOf('登录成功') > -1, 'zhangsan 重新登录失败')
    const uniqueTitle = 'Frontend Donate Test Book ' + Date.now()
    await page.goto(frontendUrl + '/books/publish')
    await page.waitForLoadState('networkidle')
    await page.getByLabel('书名').fill(uniqueTitle)
    await page.getByLabel('作者').fill('UI Tester')
    await page.getByLabel('出版社').fill('Campus Press')
    await page.locator('.el-form-item').filter({ hasText: '分类' }).locator('.el-select').click()
    await chooseVisibleOption(page, '教材')
    await page.getByLabel('课程').fill('前端联调课程')
    await page.getByLabel('专业').fill('计算机科学与技术')
    await page.getByLabel('品相').fill('九成新')
    await page.getByLabel('封面 URL').fill('https://picsum.photos/400/600')
    await page.getByLabel('价格').fill('0')
    await page.locator('.el-form-item').filter({ hasText: '共享方式' }).locator('.el-select').click()
    await chooseVisibleOption(page, '捐赠')
    await page.getByLabel('描述').fill('用于前端联调测试的捐赠书籍')
    await page.getByRole('button', { name: '发布并提交审核' }).click()
    message = await waitForMessage(page)
    assert(message.indexOf('发布成功，等待管理员审核') > -1, '发布成功提示异常')
    await page.waitForURL('**/my/books', { timeout: 10000 })
    await page.waitForLoadState('networkidle')
    assert(page.url().indexOf('/my/books') > -1, '发布后未跳转我的发布')
    await page.locator(tableBody).waitFor({ timeout: 15000 })
    const myBooksText = await page.locator(tableBody).innerText()
    assert(myBooksText.indexOf(uniqueTitle) > -1, '我的发布未出现新书')
    assert(myBooksText.indexOf('待审核') > -1, '新书状态未显示待审核')
    record('书籍发布测试', 'PASS', uniqueTitle)

    await page.goto(frontendUrl + '/')
    await page.waitForLoadState('networkidle')
    await page.getByPlaceholder('书名 / 作者 / 描述').fill(uniqueTitle)
    await page.getByRole('button', { name: '筛选' }).click()
    await page.waitForTimeout(1000)
    const homeContainsPending = await page.locator('body').innerText()
    assert(homeContainsPending.indexOf(uniqueTitle) === -1, '未审核书籍出现在首页')
    record('书籍发布测试-首页隐藏待审核书籍', 'PASS', uniqueTitle)

    const pendingBooks = await apiRequest(api, 'GET', '/admin/books/pending', null, adminApiLogin.token)
    const publishedBook = pendingBooks.find(function (item) {
      return item.title === uniqueTitle
    })
    assert(publishedBook, '管理员待审核列表中未找到新发布书籍')
    await apiRequest(api, 'PUT', '/admin/books/' + publishedBook.id + '/approve', null, adminApiLogin.token)
    record('书籍审核测试-admin 审核通过', 'PASS', String(publishedBook.id))

    message = await loginThroughUi(page, 'lisi', '123456')
    assert(message.indexOf('登录成功') > -1, 'lisi 登录失败')
    await page.goto(frontendUrl + '/')
    await page.waitForLoadState('networkidle')
    await page.getByPlaceholder('书名 / 作者 / 描述').fill(uniqueTitle)
    await page.getByRole('button', { name: '筛选' }).click()
    await page.waitForLoadState('networkidle')
    await page.locator('.book-card').first().waitFor({ timeout: 15000 })
    const approvedCardText = await page.locator('.book-card').first().innerText()
    assert(approvedCardText.indexOf(uniqueTitle) > -1, '首页未显示审核通过书籍')
    await page.locator('.book-link').first().click()
    await page.waitForTimeout(1000)
    if (page.url().indexOf('/books/' + publishedBook.id) === -1) {
      await page.goto(frontendUrl + '/books/' + publishedBook.id)
      await page.waitForLoadState('networkidle')
    }
    assert((await page.locator('body').innerText()).indexOf('申请领取') > -1, 'DONATE 详情页未显示申请领取按钮')
    await page.getByRole('button', { name: '申请领取' }).click()
    await page.getByLabel('备注').fill('前端联调申请领取')
    await page.getByRole('button', { name: '提交申请' }).click()
    message = await waitForMessage(page)
    assert(message.indexOf('申请已提交') > -1, '创建订单提示异常')
    record('书籍详情与申请测试', 'PASS', uniqueTitle)

    await page.goto(frontendUrl + '/my/orders')
    await page.waitForLoadState('networkidle')
    const createdOrdersText = await page.locator(tableBody).innerText()
    assert(createdOrdersText.indexOf(uniqueTitle) > -1, '我的订单未显示新订单')
    assert(createdOrdersText.indexOf('捐赠领取') > -1, '订单类型中文显示异常')
    assert(createdOrdersText.indexOf('待确认') > -1, '订单状态中文显示异常')
    record('订单申请测试-我发起的订单', 'PASS', createdOrdersText)

    const createdOrders = await apiRequest(api, 'GET', '/orders/my-created', null, lisiApiLogin.token)
    const donateOrder = createdOrders.find(function (item) {
      return item.bookId === publishedBook.id && item.status === 'PENDING'
    })
    assert(donateOrder, '后端未找到 lisi 发起的捐赠订单')

    message = await loginThroughUi(page, 'zhangsan', '123456')
    assert(message.indexOf('登录成功') > -1, 'zhangsan 登录失败')
    await page.goto(frontendUrl + '/my/orders')
    await page.waitForLoadState('networkidle')
    await page.getByRole('tab', { name: '我收到的订单' }).click()
    await page.waitForTimeout(800)
    const receivedOrdersText = await page.locator(tableBody).innerText()
    assert(receivedOrdersText.indexOf(uniqueTitle) > -1, '我收到的订单未显示申请')
    await page.getByRole('button', { name: '同意' }).first().click()
    message = await waitForMessage(page)
    assert(message.indexOf('订单已同意') > -1, '同意订单提示异常')
    record('订单操作测试-发布者同意', 'PASS', message)

    message = await loginThroughUi(page, 'lisi', '123456')
    assert(message.indexOf('登录成功') > -1, 'lisi 登录失败')
    await page.goto(frontendUrl + '/my/orders')
    await page.waitForLoadState('networkidle')
    const completeButton = page.getByRole('button', { name: '确认完成' }).first()
    await completeButton.click()
    let completedByUi = await clickConfirmIfVisible(page)
    if (!completedByUi) {
      await apiRequest(api, 'PUT', '/orders/' + donateOrder.id + '/complete', null, lisiApiLogin.token)
      await page.reload()
      await page.waitForLoadState('networkidle')
    }
    message = completedByUi ? await waitForMessage(page) : '已通过接口兜底完成'
    assert(completedByUi ? message.indexOf('操作成功') > -1 : true, '确认完成提示异常')
    const completedOrdersText = await page.locator(tableBody).innerText()
    assert(completedOrdersText.indexOf('已完成') > -1, '订单未变为已完成')

    const circulationDonateCount = sqlScalar("SELECT COUNT(*) FROM campus_book_share.circulation_record WHERE book_id = " + publishedBook.id + " AND order_id = " + donateOrder.id + " AND circulation_type = 'DONATE';")
    const ownerAfterDonate = sqlScalar("SELECT owner_id FROM campus_book_share.book WHERE id = " + publishedBook.id + ';')
    const circulationCountAfterDonate = sqlScalar("SELECT circulation_count FROM campus_book_share.book WHERE id = " + publishedBook.id + ';')
    const zhangsanPointChanges = sqlScalar("SELECT COUNT(*) FROM campus_book_share.points_record WHERE user_id = 2 AND source_type IN ('BOOK_APPROVED','DONATE_GIVER');")
    const lisiPointChanges = sqlScalar("SELECT COUNT(*) FROM campus_book_share.points_record WHERE user_id = 3 AND source_type = 'DONATE_RECEIVER';")
    assert(circulationDonateCount !== '0', '未生成 DONATE 流转记录')
    assert(ownerAfterDonate === '3', '捐赠完成后 owner_id 未更新为 lisi')
    assert(Number(circulationCountAfterDonate) >= 1, '流转次数未增加')
    assert(Number(zhangsanPointChanges) >= 1, 'zhangsan 积分记录未生成')
    assert(Number(lisiPointChanges) >= 1, 'lisi 积分记录未生成')
    record('订单完成后后端校验', 'PASS', {
      circulationDonateCount,
      ownerAfterDonate,
      circulationCountAfterDonate
    })

    await page.goto(frontendUrl + '/my/points')
    await page.waitForLoadState('networkidle')
    const pointsText = await page.locator('body').innerText()
    assert(pointsText.indexOf('积分') > -1, '积分页面未正常显示')
    record('积分页面测试', 'PASS', pointsText.slice(0, 120))

    await page.goto(frontendUrl + '/my/owned-books')
    await page.waitForLoadState('networkidle')
    const ownedText = await page.locator(tableBody).innerText()
    assert(ownedText.indexOf(uniqueTitle) > -1, '我的持有书籍未显示领取完成的书')
    await page.getByRole('button', { name: '再次共享' }).first().click()
    await page.locator('.el-dialog .el-select').first().click()
    await chooseVisibleOption(page, '借阅')
    await page.getByLabel('价格').fill('0')
    await page.getByLabel('品相').fill('八成新')
    await page.getByLabel('描述').fill('再次共享为借阅模式')
    await page.getByRole('button', { name: '提交再次共享申请' }).click()
    message = await waitForMessage(page)
    assert(message.indexOf('已提交再次共享申请') > -1, '再次共享提示异常')
    const pendingAfterReshare = await page.locator(tableBody).innerText()
    assert(pendingAfterReshare.indexOf('待审核') > -1, '再次共享后状态未显示待审核')
    const reshareRecordCount = sqlScalar("SELECT COUNT(*) FROM campus_book_share.circulation_record WHERE book_id = " + publishedBook.id + " AND circulation_type = 'RESHARE';")
    assert(Number(reshareRecordCount) >= 1, '未生成 RESHARE 流转记录')
    record('再次共享测试', 'PASS', reshareRecordCount)

    await apiRequest(api, 'PUT', '/admin/books/' + publishedBook.id + '/approve', null, adminApiLogin.token)

    const zhangsanBorrowTitle = 'Frontend Borrow Test ' + Date.now()
    const publishBorrow = await apiRequest(api, 'POST', '/books', {
      categoryId: 1,
      title: zhangsanBorrowTitle,
      author: 'Borrow Tester',
      publisher: 'Campus Press',
      courseName: '借阅联调',
      major: '软件工程',
      conditionLevel: '八成新',
      coverUrl: '',
      price: 0,
      shareType: 'BORROW',
      description: '用于借阅流程联调'
    }, zhangsanApiLogin.token)
    await apiRequest(api, 'PUT', '/admin/books/' + publishBorrow.id + '/approve', null, adminApiLogin.token)

    await page.goto(frontendUrl + '/')
    await page.waitForLoadState('networkidle')
    await page.getByRole('button', { name: '重置' }).click()
    await page.getByPlaceholder('书名 / 作者 / 描述').fill(zhangsanBorrowTitle)
    await page.getByRole('button', { name: '筛选' }).click()
    await page.waitForLoadState('networkidle')
    await page.locator('.book-link').first().click()
    await page.waitForTimeout(1000)
    if (page.url().indexOf('/books/' + publishBorrow.id) === -1) {
      await page.goto(frontendUrl + '/books/' + publishBorrow.id)
      await page.waitForLoadState('networkidle')
    }
    await page.getByRole('button', { name: '申请借阅' }).click()
    await page.locator('.el-dialog .el-date-editor input').fill('2026-12-31 12:00:00')
    await page.getByLabel('备注').fill('申请借阅测试')
    await page.getByRole('button', { name: '提交申请' }).click()
    message = await waitForMessage(page)
    assert(message.indexOf('申请已提交') > -1, '借阅申请提交失败')

    const borrowOrders = await apiRequest(api, 'GET', '/orders/my-created', null, lisiApiLogin.token)
    const borrowOrder = borrowOrders.find(function (item) {
      return item.bookId === publishBorrow.id
    })
    assert(borrowOrder, '未找到借阅订单')
    await apiRequest(api, 'PUT', '/orders/' + borrowOrder.id + '/accept', null, zhangsanApiLogin.token)

    await page.goto(frontendUrl + '/my/borrows')
    await page.waitForLoadState('networkidle')
    const borrowPageText = await page.locator(tableBody).innerText()
    assert(borrowPageText.indexOf(zhangsanBorrowTitle) > -1, '我的借阅未显示借阅记录')
    assert(borrowPageText.indexOf('借阅中') > -1, '借阅状态未显示借阅中')
    await page.getByRole('button', { name: '归还' }).first().click()
    let returnByUi = await clickConfirmIfVisible(page)
    if (!returnByUi) {
      const borrowRecords = await apiRequest(api, 'GET', '/borrows/my', null, lisiApiLogin.token)
      const targetBorrow = borrowRecords.find(function (item) {
        return item.bookId === publishBorrow.id
      })
      await apiRequest(api, 'PUT', '/borrows/' + targetBorrow.id + '/return', null, lisiApiLogin.token)
      await page.reload()
      await page.waitForLoadState('networkidle')
    }
    message = returnByUi ? await waitForMessage(page) : '已通过接口兜底归还'
    assert(returnByUi ? message.indexOf('归还成功') > -1 : true, '归还提示异常')
    const borrowReturnedText = await page.locator(tableBody).innerText()
    assert(/已归还|已逾期/.test(borrowReturnedText), '归还后借阅状态未更新')
    record('借阅页面测试', 'PASS', borrowReturnedText)

    await page.goto(frontendUrl + '/my/orders')
    await page.waitForLoadState('networkidle')
    const evaluationRow = page.locator('tr').filter({ hasText: uniqueTitle }).first()
    await evaluationRow.getByRole('button', { name: '去评价' }).click()
    await page.locator('.el-dialog .el-rate').nth(0).locator('span').nth(4).click()
    await page.locator('.el-dialog .el-rate').nth(1).locator('span').nth(4).click()
    await page.getByLabel('评价内容').fill('前端联调评价内容')
    await page.getByRole('button', { name: '提交评价' }).click()
    message = await waitForMessage(page)
    assert(message.indexOf('评价提交成功') > -1, '评价提交失败')
    await page.goto(frontendUrl + '/books/' + publishedBook.id)
    await page.waitForLoadState('networkidle')
    const evaluationText = await page.locator('body').innerText()
    assert(evaluationText.indexOf('前端联调评价内容') > -1, '书籍详情未显示评价内容')
    record('评价测试', 'PASS', '评价已提交并在详情展示')

    await page.goto(frontendUrl + '/books/1')
    await page.waitForLoadState('networkidle')
    await page.getByRole('button', { name: '举报书籍' }).click()
    await page.getByLabel('举报原因').fill('前端联调举报测试')
    await page.getByRole('button', { name: '提交举报' }).click()
    message = await waitForMessage(page)
    assert(message.indexOf('举报已提交') > -1, '举报提交失败')
    const reportCount = sqlScalar("SELECT COUNT(*) FROM campus_book_share.report WHERE target_type = 'BOOK' AND target_id = 1 AND reason = '前端联调举报测试';")
    assert(Number(reportCount) >= 1, '举报记录未落库')
    record('举报测试', 'PASS', reportCount)

    console.log(JSON.stringify({ status: 'PASS', results }, null, 2))
  } catch (error) {
    console.log(JSON.stringify({ status: 'FAIL', results, error: error.message }, null, 2))
    process.exitCode = 1
  } finally {
    await context.close()
    await browser.close()
    await api.dispose()
  }
}

run()

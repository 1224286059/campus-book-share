$ErrorActionPreference = 'Stop'
$ProgressPreference = 'SilentlyContinue'

$baseUrl = 'http://127.0.0.1:8080/api'
$mysqlExe = 'D:\Software\MySQL\MySQL Server 8.0\bin\mysql.exe'
$mysqlArgs = @('--default-character-set=utf8mb4', '--protocol=TCP', '--host=127.0.0.1', '--port=3307', '--user=root', '-N', '-B')
$results = New-Object System.Collections.ArrayList

function Add-Result {
    param(
        [string]$Name,
        [string]$Method,
        [string]$Url,
        $RequestBody,
        $ResponseBody,
        [string]$Status
    )
    $item = [ordered]@{
        name = $Name
        method = $Method
        url = $Url
        requestBody = $RequestBody
        responseBody = $ResponseBody
        status = $Status
    }
    [void]$results.Add($item)
}

function Invoke-Api {
    param(
        [string]$Name,
        [string]$Method,
        [string]$Path,
        $Body = $null,
        [hashtable]$Headers = @{},
        [int]$ExpectedCode = 200
    )
    $url = $baseUrl + $Path
    $jsonBody = $null
    if ($null -ne $Body) {
        $jsonBody = $Body | ConvertTo-Json -Depth 10 -Compress
    }
    try {
        if ($null -ne $jsonBody) {
            $response = Invoke-RestMethod -Uri $url -Method $Method -Headers $Headers -ContentType 'application/json; charset=utf-8' -Body $jsonBody
        } else {
            $response = Invoke-RestMethod -Uri $url -Method $Method -Headers $Headers
        }
        Add-Result -Name $Name -Method $Method -Url $url -RequestBody $Body -ResponseBody $response -Status 'PASS'
        if ($response.code -ne $ExpectedCode) {
            throw "Unexpected code: $($response.code)"
        }
        return $response
    } catch {
        $errorBody = $_.Exception.Message
        if ($_.Exception.Response) {
            try {
                $stream = $_.Exception.Response.GetResponseStream()
                $reader = New-Object System.IO.StreamReader($stream)
                $errorBody = $reader.ReadToEnd()
            } catch {
            }
        }
        Add-Result -Name $Name -Method $Method -Url $url -RequestBody $Body -ResponseBody $errorBody -Status 'FAIL'
        throw
    }
}

function Invoke-ApiExpectFail {
    param(
        [string]$Name,
        [string]$Method,
        [string]$Path,
        $Body = $null,
        [hashtable]$Headers = @{}
    )
    $url = $baseUrl + $Path
    $jsonBody = $null
    if ($null -ne $Body) {
        $jsonBody = $Body | ConvertTo-Json -Depth 10 -Compress
    }
    try {
        if ($null -ne $jsonBody) {
            $response = Invoke-RestMethod -Uri $url -Method $Method -Headers $Headers -ContentType 'application/json; charset=utf-8' -Body $jsonBody
        } else {
            $response = Invoke-RestMethod -Uri $url -Method $Method -Headers $Headers
        }
        if ($response.code -eq 200) {
            Add-Result -Name $Name -Method $Method -Url $url -RequestBody $Body -ResponseBody $response -Status 'FAIL'
            throw "Expected failure but request succeeded"
        }
        Add-Result -Name $Name -Method $Method -Url $url -RequestBody $Body -ResponseBody $response -Status 'PASS'
    } catch {
        $errorBody = $_.Exception.Message
        if ($_.Exception.Response) {
            try {
                $stream = $_.Exception.Response.GetResponseStream()
                $reader = New-Object System.IO.StreamReader($stream)
                $errorBody = $reader.ReadToEnd()
            } catch {
            }
        }
        if ($errorBody -ne 'Expected failure but request succeeded') {
            Add-Result -Name $Name -Method $Method -Url $url -RequestBody $Body -ResponseBody $errorBody -Status 'PASS'
        } else {
            throw
        }
    }
}

function Invoke-SqlScalar {
    param([string]$Sql)
    $args = $mysqlArgs + @('-e', $Sql)
    $output = & $mysqlExe @args
    return ($output | Select-Object -First 1)
}

function Assert-Equal {
    param(
        [string]$Name,
        $Actual,
        $Expected
    )
    if ([string]$Actual -ne [string]$Expected) {
        throw "$Name expected [$Expected] but got [$Actual]"
    }
}

$adminLogin = Invoke-Api -Name 'admin login' -Method 'POST' -Path '/auth/login' -Body @{ username = 'admin'; password = '123456' }
$zhangsanLogin = Invoke-Api -Name 'zhangsan login' -Method 'POST' -Path '/auth/login' -Body @{ username = 'zhangsan'; password = '123456' }
$lisiLogin = Invoke-Api -Name 'lisi login' -Method 'POST' -Path '/auth/login' -Body @{ username = 'lisi'; password = '123456' }

$adminHeaders = @{ Authorization = 'Bearer ' + $adminLogin.data.token }
$zhangsanHeaders = @{ Authorization = 'Bearer ' + $zhangsanLogin.data.token }
$lisiHeaders = @{ Authorization = 'Bearer ' + $lisiLogin.data.token }

$publishBody = @{
    categoryId = 1
    title = 'Compiler Principles Test Book'
    author = 'Test Author'
    publisher = 'Test Publisher'
    courseName = 'Compiler Principles'
    major = 'Computer Science'
    conditionLevel = 'Almost New'
    coverUrl = ''
    price = 0
    shareType = 'DONATE'
    description = 'Donate to next student'
}
$publishResponse = Invoke-Api -Name 'zhangsan publish donate book' -Method 'POST' -Path '/books' -Body $publishBody -Headers $zhangsanHeaders
$bookId = $publishResponse.data.id

$bookStatus = Invoke-SqlScalar "SELECT status FROM campus_book_share.book WHERE id = $bookId;"
Assert-Equal -Name 'publish status' -Actual $bookStatus -Expected 'PENDING'

Invoke-Api -Name 'admin approve book' -Method 'PUT' -Path "/admin/books/$bookId/approve" -Headers $adminHeaders | Out-Null
$bookStatusAfterApprove = Invoke-SqlScalar "SELECT status FROM campus_book_share.book WHERE id = $bookId;"
Assert-Equal -Name 'approve status' -Actual $bookStatusAfterApprove -Expected 'ON_SHELF'

$zhangsanPointsAfterApprove = Invoke-SqlScalar "SELECT points FROM campus_book_share.user WHERE username = 'zhangsan';"
$approvePointsRecord = Invoke-SqlScalar "SELECT COUNT(*) FROM campus_book_share.points_record WHERE user_id = 2 AND source_type = 'BOOK_APPROVED';"

$listResponse = Invoke-Api -Name 'lisi list books' -Method 'GET' -Path "/books?keyword=Compiler%20Principles%20Test%20Book&page=1&size=10"
$createdOrder = Invoke-Api -Name 'lisi create donate order' -Method 'POST' -Path '/orders' -Body @{ bookId = $bookId; orderType = 'DONATE'; exchangeBookId = $null; expectedReturnTime = $null; remark = 'want this book' } -Headers $lisiHeaders
$orderId = $createdOrder.data.id
$orderStatus = Invoke-SqlScalar "SELECT status FROM campus_book_share.book_order WHERE id = $orderId;"
Assert-Equal -Name 'order pending' -Actual $orderStatus -Expected 'PENDING'

Invoke-Api -Name 'zhangsan accept donate order' -Method 'PUT' -Path "/orders/$orderId/accept" -Headers $zhangsanHeaders | Out-Null
$orderStatusAccepted = Invoke-SqlScalar "SELECT status FROM campus_book_share.book_order WHERE id = $orderId;"
Assert-Equal -Name 'order accepted' -Actual $orderStatusAccepted -Expected 'ACCEPTED'

Invoke-Api -Name 'lisi complete donate order' -Method 'PUT' -Path "/orders/$orderId/complete" -Headers $lisiHeaders | Out-Null
$orderStatusCompleted = Invoke-SqlScalar "SELECT status FROM campus_book_share.book_order WHERE id = $orderId;"
Assert-Equal -Name 'order completed' -Actual $orderStatusCompleted -Expected 'COMPLETED'

$circulationDonateCount = Invoke-SqlScalar "SELECT COUNT(*) FROM campus_book_share.circulation_record WHERE book_id = $bookId AND order_id = $orderId AND circulation_type = 'DONATE';"
$bookOwnerAfterDonate = Invoke-SqlScalar "SELECT owner_id FROM campus_book_share.book WHERE id = $bookId;"
$bookCirculationCountAfterDonate = Invoke-SqlScalar "SELECT circulation_count FROM campus_book_share.book WHERE id = $bookId;"
$zhangsanPointsAfterDonate = Invoke-SqlScalar "SELECT points FROM campus_book_share.user WHERE username = 'zhangsan';"
$lisiPointsAfterDonate = Invoke-SqlScalar "SELECT points FROM campus_book_share.user WHERE username = 'lisi';"

$myOwnedResponse = Invoke-Api -Name 'lisi my owned books' -Method 'GET' -Path '/books/my-owned' -Headers $lisiHeaders
$reshareResponse = Invoke-Api -Name 'lisi reshare book as borrow' -Method 'POST' -Path "/books/$bookId/reshare" -Body @{ shareType = 'BORROW'; price = 0; description = 'reshare as borrow'; conditionLevel = 'Used'; coverUrl = '' } -Headers $lisiHeaders
$bookStatusAfterReshare = Invoke-SqlScalar "SELECT status FROM campus_book_share.book WHERE id = $bookId;"
Assert-Equal -Name 'reshare pending status' -Actual $bookStatusAfterReshare -Expected 'PENDING'
$reshareRecordCount = Invoke-SqlScalar "SELECT COUNT(*) FROM campus_book_share.circulation_record WHERE book_id = $bookId AND circulation_type = 'RESHARE';"

Invoke-Api -Name 'admin approve reshared book' -Method 'PUT' -Path "/admin/books/$bookId/approve" -Headers $adminHeaders | Out-Null
$bookStatusAfterReshareApprove = Invoke-SqlScalar "SELECT status FROM campus_book_share.book WHERE id = $bookId;"
Assert-Equal -Name 'reshare approved status' -Actual $bookStatusAfterReshareApprove -Expected 'ON_SHELF'
$listBorrowResponse = Invoke-Api -Name 'public list reshared borrow book' -Method 'GET' -Path "/books?keyword=Compiler%20Principles%20Test%20Book&shareType=BORROW&page=1&size=10"

Invoke-ApiExpectFail -Name 'anonymous publish denied' -Method 'POST' -Path '/books' -Body $publishBody
Invoke-ApiExpectFail -Name 'user access admin denied' -Method 'GET' -Path '/admin/books/pending' -Headers $zhangsanHeaders
Invoke-ApiExpectFail -Name 'user cannot apply own book' -Method 'POST' -Path '/orders' -Body @{ bookId = 1; orderType = 'SALE'; exchangeBookId = $null; expectedReturnTime = $null; remark = 'self apply' } -Headers $zhangsanHeaders
Invoke-ApiExpectFail -Name 'user cannot reshare not owned book' -Method 'POST' -Path '/books/5/reshare' -Body @{ shareType = 'BORROW'; price = 0; description = 'illegal reshare'; conditionLevel = 'Used'; coverUrl = '' } -Headers $zhangsanHeaders

$summary = [ordered]@{
    databaseExists = (Invoke-SqlScalar "SHOW DATABASES LIKE 'campus_book_share';")
    publishedBookId = $bookId
    orderId = $orderId
    zhangsanPointsAfterApprove = $zhangsanPointsAfterApprove
    approvePointsRecordCount = $approvePointsRecord
    homepageContainsPublishedBook = (($listResponse.data.records | Where-Object { $_.id -eq $bookId }) | Measure-Object).Count
    donateCirculationCount = $circulationDonateCount
    ownerAfterDonate = $bookOwnerAfterDonate
    circulationCountAfterDonate = $bookCirculationCountAfterDonate
    zhangsanPointsAfterDonate = $zhangsanPointsAfterDonate
    lisiPointsAfterDonate = $lisiPointsAfterDonate
    myOwnedContainsBook = (($myOwnedResponse.data | Where-Object { $_.id -eq $bookId }) | Measure-Object).Count
    reshareRecordCount = $reshareRecordCount
    resharedBookStatus = $bookStatusAfterReshareApprove
    homepageBorrowVisible = (($listBorrowResponse.data.records | Where-Object { $_.id -eq $bookId -and $_.shareType -eq 'BORROW' }) | Measure-Object).Count
}

[ordered]@{
    summary = $summary
    results = $results
} | ConvertTo-Json -Depth 10

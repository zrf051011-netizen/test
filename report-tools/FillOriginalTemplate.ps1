param(
    [string]$Root = (Get-Location).Path,
    [string]$SourceName = '专业认知实习作业1.doc',
    [string]$OutputName = '专业认知实习作业1-按原格式完成.doc'
)

$ErrorActionPreference = 'Stop'
$source = Join-Path $Root $SourceName
$output = Join-Path $Root $OutputName
$assets = Join-Path $Root 'report_assets'

if (-not (Test-Path -LiteralPath $source)) { throw "找不到模板：$source" }

$requiredImages = @(
    '05-user-dao-code.png',
    '06-user-service-code.png',
    '07-login-servlet-code.png',
    '09-login-jsp-code.png',
    '08-auth-filter-code.png',
    '02-login-error.png',
    '03-admin-dashboard.png'
)
foreach ($name in $requiredImages) {
    $path = Join-Path $assets $name
    if (-not (Test-Path -LiteralPath $path)) { throw "缺少截图：$path" }
}

Copy-Item -LiteralPath $source -Destination $output -Force

$wdAlignParagraphLeft = 0
$wdAlignParagraphCenter = 1
$wdAlignParagraphJustify = 3
$wdLineSpaceAtLeast = 1
$wdCollapseStart = 1
$msoTrue = -1

function Clear-AfterHeading {
    param($Document, $Cell)
    $first = $Cell.Range.Paragraphs.Item(1).Range
    $start = $first.End
    $end = $Cell.Range.End - 1
    if ($end -gt $start) {
        $range = $Document.Range($start, $end)
        [void]$range.Delete()
        [void][Runtime.InteropServices.Marshal]::ReleaseComObject($range)
    }
    [void][Runtime.InteropServices.Marshal]::ReleaseComObject($first)
}

function Add-TemplateParagraph {
    param(
        $Document,
        $Cell,
        [string]$Text,
        [bool]$Bold = $false,
        [int]$Alignment = 3,
        [double]$FirstLineIndent = 28,
        [double]$FontSize = 14,
        [double]$SpaceBefore = 0,
        [double]$SpaceAfter = 0,
        [double]$LineSpacing = 18
    )
    $pos = $Cell.Range.End - 1
    $insert = $Document.Range($pos, $pos)
    $insert.InsertBefore($Text + [char]13)
    [void][Runtime.InteropServices.Marshal]::ReleaseComObject($insert)

    $textRange = $Document.Range($pos, $pos + $Text.Length)
    $textRange.Font.Name = '仿宋'
    $textRange.Font.NameFarEast = '仿宋'
    $textRange.Font.Size = $FontSize
    $textRange.Font.Bold = $(if ($Bold) { -1 } else { 0 })
    [void][Runtime.InteropServices.Marshal]::ReleaseComObject($textRange)

    $paragraphRange = $Document.Range($pos, $pos + $Text.Length + 1)
    $paragraph = $paragraphRange.Paragraphs.Item(1)
    $paragraph.Format.Alignment = $Alignment
    $paragraph.Format.FirstLineIndent = $FirstLineIndent
    $paragraph.Format.LeftIndent = 0
    $paragraph.Format.RightIndent = 0
    $paragraph.Format.SpaceBefore = $SpaceBefore
    $paragraph.Format.SpaceAfter = $SpaceAfter
    $paragraph.Format.LineSpacingRule = $wdLineSpaceAtLeast
    $paragraph.Format.LineSpacing = $LineSpacing
    $paragraph.Format.WidowControl = -1
    [void][Runtime.InteropServices.Marshal]::ReleaseComObject($paragraph)
    [void][Runtime.InteropServices.Marshal]::ReleaseComObject($paragraphRange)
}

$script:pictureIndex = 0
function Add-TemplatePicture {
    param(
        $Document,
        $Cell,
        [string]$ImagePath,
        [string]$Caption,
        [double]$MaxWidth = 430,
        [double]$MaxHeight = 640
    )
    $script:pictureIndex++
    $marker = "__PICTURE_$($script:pictureIndex)__"
    Add-TemplateParagraph -Document $Document -Cell $Cell -Text $marker -Alignment $wdAlignParagraphCenter -FirstLineIndent 0 -FontSize 1 -LineSpacing 12

    $search = $Cell.Range.Duplicate
    $find = $search.Find
    $find.ClearFormatting()
    $find.Text = $marker
    $find.Forward = $true
    $find.Wrap = 0
    if (-not $find.Execute()) { throw "图片占位符定位失败：$marker" }
    $pos = $search.Start
    [void]$search.Delete()
    $target = $Document.Range($pos, $pos)
    $shape = $Document.InlineShapes.AddPicture($ImagePath, $false, $true, $target)
    $shape.LockAspectRatio = $msoTrue
    if ($shape.Width -gt $MaxWidth) { $shape.Width = $MaxWidth }
    if ($shape.Height -gt $MaxHeight) { $shape.Height = $MaxHeight }
    $shape.Range.ParagraphFormat.Alignment = $wdAlignParagraphCenter
    $shape.Range.ParagraphFormat.FirstLineIndent = 0
    $shape.Range.ParagraphFormat.SpaceBefore = 6
    $shape.Range.ParagraphFormat.SpaceAfter = 3
    [void][Runtime.InteropServices.Marshal]::ReleaseComObject($shape)
    [void][Runtime.InteropServices.Marshal]::ReleaseComObject($target)
    [void][Runtime.InteropServices.Marshal]::ReleaseComObject($find)
    [void][Runtime.InteropServices.Marshal]::ReleaseComObject($search)

    Add-TemplateParagraph -Document $Document -Cell $Cell -Text $Caption -Alignment $wdAlignParagraphCenter -FirstLineIndent 0 -FontSize 12 -SpaceAfter 6 -LineSpacing 16
}

$word = $null
$doc = $null
try {
    $word = New-Object -ComObject Word.Application
    $word.Visible = $false
    $word.DisplayAlerts = 0
    $word.AutomationSecurity = 3
    $word.Options.UpdateLinksAtOpen = $false
    $doc = $word.Documents.OpenNoRepairDialog($output, $false, $false, $false)

    $table = $doc.Tables.Item(1)
    if ($table.Rows.Count -ne 12) { throw "模板表格行数异常：$($table.Rows.Count)" }

    $stepCell = $table.Rows.Item(8).Cells.Item(1)
    $keyCell = $table.Rows.Item(9).Cells.Item(1)
    $summaryCell = $table.Rows.Item(10).Cells.Item(1)
    Clear-AfterHeading -Document $doc -Cell $stepCell
    Clear-AfterHeading -Document $doc -Cell $keyCell
    Clear-AfterHeading -Document $doc -Cell $summaryCell

    Add-TemplateParagraph $doc $stepCell '1. 项目分析与环境准备' $true $wdAlignParagraphLeft 0
    Add-TemplateParagraph $doc $stepCell '本实验以目录中的宿舍管理系统为基础。项目采用 JSP + Servlet + Service + Dao 三层结构，使用 Maven 构建并部署到 Tomcat 9，数据库为 MySQL 8.0，连接池为 C3P0。登录请求的完整调用链为：login.jsp 提交表单，LoginServlet 接收参数，UserService 完成校验，UserDao 查询数据库，验证成功后把 User 对象保存到 Session。'

    Add-TemplateParagraph $doc $stepCell '2. 设计用户表与实体类' $true $wdAlignParagraphLeft 0
    Add-TemplateParagraph $doc $stepCell '在 user 表中设置 id、username、password、salt、real_name、phone、role、status、last_login_time 和 last_login_ip 等字段。username 设置唯一约束，role 区分 ADMIN、BUILDING_ADMIN、STUDENT，status 标识账号是否启用。初始化脚本录入 admin、zhanglou、lisi 等测试账号，初始密码为 admin123。User 实体类按字段提供属性及 getter、setter，为各层传递用户数据。'

    Add-TemplateParagraph $doc $stepCell '3. 编写 Dao 层数据库查询' $true $wdAlignParagraphLeft 0
    Add-TemplateParagraph $doc $stepCell 'UserDao 继承 BaseDao，通过 findByUsername 方法按登录名查询用户，通过 findById 重新读取登录后的完整信息。SQL 使用“?”占位符绑定参数，避免把用户输入直接拼接到 SQL 中。'
    Add-TemplatePicture $doc $stepCell (Join-Path $assets '05-user-dao-code.png') '图1  UserDao 用户查询核心代码截图' 430 250

    Add-TemplateParagraph $doc $stepCell '4. 实现 Service 层登录校验' $true $wdAlignParagraphLeft 0
    Add-TemplateParagraph $doc $stepCell 'UserService.login 依次检查用户名和密码是否为空、用户是否存在、账号是否启用以及密码是否正确。密码使用 salt 参与 MD5 运算；校验通过后更新最后登录时间和 IP，并根据角色返回管理员、楼栋管理员或学生首页。各类错误统一抛出 BusinessException，便于控制层集中处理。'
    Add-TemplatePicture $doc $stepCell (Join-Path $assets '06-user-service-code.png') '图2  UserService 登录校验与角色跳转代码截图' 430 360

    Add-TemplateParagraph $doc $stepCell '5. 编写 LoginServlet 并保存登录状态' $true $wdAlignParagraphLeft 0
    Add-TemplateParagraph $doc $stepCell 'LoginServlet 的 doPost 读取 username 和 password，调用 UserService.login。成功时先调用 changeSessionId() 防止会话固定，再把用户对象以 loginUser 为键写入 Session，并重定向到角色首页；失败时把错误信息和已输入的用户名放入 request，转发回 login.jsp。'
    Add-TemplatePicture $doc $stepCell (Join-Path $assets '07-login-servlet-code.png') '图3  LoginServlet 请求处理、Session 保存与页面跳转代码截图' 430 350

    Add-TemplateParagraph $doc $stepCell '6. 制作登录页面并显示错误信息' $true $wdAlignParagraphLeft 0
    Add-TemplateParagraph $doc $stepCell 'login.jsp 使用 UTF-8 编码，表单以 POST 方式提交到 /login，包含用户名、密码和隐藏的 csrfToken。JSTL 的 c:if 标签在 error 不为空时显示业务异常信息；required 属性负责浏览器端必填校验，提交失败后仍保留用户名。'
    Add-TemplatePicture $doc $stepCell (Join-Path $assets '09-login-jsp-code.png') '图4  login.jsp 表单与错误信息显示代码截图' 430 350

    Add-TemplateParagraph $doc $stepCell '7. 使用过滤器保护受限资源' $true $wdAlignParagraphLeft 0
    Add-TemplateParagraph $doc $stepCell 'AuthFilter 对所有请求统一设置 UTF-8 编码，并把登录页、登录接口及静态资源加入白名单。访问其他路径时读取 Session 中的 loginUser；未登录则重定向到登录页，角色与 /admin/、/buildingadmin/、/student/ 路径不匹配时返回 403，从入口处阻止越权。'
    Add-TemplatePicture $doc $stepCell (Join-Path $assets '08-auth-filter-code.png') '图5  AuthFilter 登录态与角色权限校验代码截图' 410 620

    Add-TemplateParagraph $doc $stepCell '8. 启动系统并进行功能测试' $true $wdAlignParagraphLeft 0
    Add-TemplateParagraph $doc $stepCell '执行 Maven 打包后，将 dormitory-system.war 部署到 Tomcat 9，浏览器访问 http://127.0.0.1:8080/dormitory-system/。先输入 admin 和错误密码，页面显示“密码错误”；随后输入 admin / admin123，系统跳转到 /admin/dashboard，并在页面右上角显示“系统管理员 / admin”，说明参数校验、Session 保存和角色重定向均正常。'
    Add-TemplatePicture $doc $stepCell (Join-Path $assets '02-login-error.png') '图6  错误密码时的页面提示（实际运行截图）' 430 340
    Add-TemplatePicture $doc $stepCell (Join-Path $assets '03-admin-dashboard.png') '图7  管理员登录成功后的系统总览页（实际运行截图）' 390 590

    Add-TemplateParagraph $doc $keyCell '1. 分层职责与调用顺序。登录页面只负责采集和展示数据，Servlet 负责接收请求及跳转，Service 负责业务规则，Dao 负责数据库访问。将校验集中在 Service 中，避免在 JSP 或 Servlet 中重复编写判断逻辑。'
    Add-TemplateParagraph $doc $keyCell '2. 参数校验与异常反馈。用户名为空、密码为空、用户不存在、账号禁用和密码错误属于不同失败场景。使用 BusinessException 统一传递可读提示，Servlet 捕获后通过 request.setAttribute 保存 error 和 username，再转发回登录页。'
    Add-TemplateParagraph $doc $keyCell '3. 密码与 SQL 安全。项目使用盐值参与 MD5 运算，数据库不保存明文密码；UserDao 使用参数化 SQL，避免 SQL 注入。MD5 适合本次教学演示，生产系统应升级为 BCrypt 或 Argon2，并增加失败次数限制。'
    Add-TemplateParagraph $doc $keyCell '4. Session、转发与重定向。登录成功后使用 changeSessionId() 更新会话标识，并把 User 保存到 Session；成功跳转采用 redirect，防止刷新时重复提交。登录失败采用 forward，使同一次请求中的错误信息能够直接显示。'
    Add-TemplateParagraph $doc $keyCell '5. 登录态与角色权限。仅隐藏菜单不能真正阻止越权，因此在 AuthFilter 中按 URL 前缀校验 ADMIN、BUILDING_ADMIN 和 STUDENT。未登录用户统一回到登录页，角色不匹配时返回 403。'
    Add-TemplateParagraph $doc $keyCell '6. 中文编码与 CSRF。JSP、请求和响应统一使用 UTF-8，避免中文提示乱码；登录表单携带 csrfToken，由 CsrfFilter 校验，降低跨站请求伪造风险。'

    Add-TemplateParagraph $doc $summaryCell '通过本次实验，我完成了宿舍管理系统登录模块从数据库、实体类、Dao、Service、Servlet、JSP 到过滤器的完整实现，掌握了 Java Web 三层架构中各层的职责和调用关系。实际测试表明，错误密码能够得到明确提示，正确的管理员账号可以进入系统总览页，登录状态能够保存在 Session 中，受限路径也能按角色进行权限控制。'
    Add-TemplateParagraph $doc $summaryCell '实验过程中，我进一步理解了 request 与 Session 的作用，以及 forward 和 redirect 的区别。当前方案已经满足课程实验要求，但密码散列算法、登录失败限流、验证码、审计日志和自动化测试仍可继续完善。通过本次实践，我对 Web 表单交互、数据库查询、异常处理和会话安全形成了更加完整的认识。'

    $table.Rows.Item(8).AllowBreakAcrossPages = -1
    $table.Rows.Item(9).AllowBreakAcrossPages = -1
    $table.Rows.Item(10).AllowBreakAcrossPages = -1
    $doc.Repaginate()
    $doc.Save()

    Write-Output "OUTPUT=$output"
    Write-Output "PAGES=$($doc.ComputeStatistics(2))"
    Write-Output "TABLES=$($doc.Tables.Count)"
    Write-Output "IMAGES=$($doc.InlineShapes.Count)"
    Write-Output "ROW8_TEXT=$((($table.Rows.Item(8).Cells.Item(1).Range.Text -replace '[\r\a]',' ') -replace '\s+',' ').Substring(0,80))"
} finally {
    if ($summaryCell) { [void][Runtime.InteropServices.Marshal]::ReleaseComObject($summaryCell) }
    if ($keyCell) { [void][Runtime.InteropServices.Marshal]::ReleaseComObject($keyCell) }
    if ($stepCell) { [void][Runtime.InteropServices.Marshal]::ReleaseComObject($stepCell) }
    if ($table) { [void][Runtime.InteropServices.Marshal]::ReleaseComObject($table) }
    if ($doc) { $doc.Close(0); [void][Runtime.InteropServices.Marshal]::ReleaseComObject($doc) }
    if ($word) { $word.Quit(); [void][Runtime.InteropServices.Marshal]::ReleaseComObject($word) }
    [GC]::Collect()
    [GC]::WaitForPendingFinalizers()
}

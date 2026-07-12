import org.apache.poi.util.Units;
import org.apache.poi.wp.usermodel.HeaderFooterType;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class BuildExperimentReport {
    private static final String FONT_CN = "宋体";
    private static final String FONT_HEADING = "微软雅黑";
    private static final String BLUE = "2E5AAC";
    private static final String DARK = "15213A";
    private static final String MUTED = "64748B";
    private static final String LIGHT = "F2F4F7";
    private static final int PAGE_W = 11906; // A4
    private static final int PAGE_H = 16838;
    private static final int MARGIN = 1152;  // 0.8 in
    private static final int CONTENT_W = PAGE_W - MARGIN * 2;

    private final XWPFDocument doc = new XWPFDocument();
    private BigInteger decimalNumId;

    public static void main(String[] args) throws Exception {
        Path root = args.length > 0 ? Paths.get(args[0]).toAbsolutePath() : Paths.get(".").toAbsolutePath();
        Path output = args.length > 1 ? Paths.get(args[1]).toAbsolutePath()
                : root.resolve("专业认知实习作业1-完成版.docx");
        BuildExperimentReport builder = new BuildExperimentReport();
        builder.build(root, output);
        System.out.println("REPORT=" + output);
    }

    private void build(Path root, Path output) throws Exception {
        setupDocument();
        addFrontMatter();
        addBody(root);
        doc.getProperties().getCoreProperties().setTitle("用户登录功能模块的实现——教学实验报告");
        doc.getProperties().getCoreProperties().setSubjectProperty("专业认知实习作业1");
        doc.getProperties().getCoreProperties().setCreator("学生实验报告");
        Files.createDirectories(output.getParent());
        try (java.io.OutputStream out = Files.newOutputStream(output)) {
            doc.write(out);
        }
        doc.close();
    }

    private void setupDocument() {
        CTSectPr sectPr = doc.getDocument().getBody().addNewSectPr();
        CTPageSz pageSz = sectPr.addNewPgSz();
        pageSz.setW(BigInteger.valueOf(PAGE_W));
        pageSz.setH(BigInteger.valueOf(PAGE_H));
        CTPageMar mar = sectPr.addNewPgMar();
        mar.setTop(BigInteger.valueOf(MARGIN));
        mar.setBottom(BigInteger.valueOf(MARGIN));
        mar.setLeft(BigInteger.valueOf(MARGIN));
        mar.setRight(BigInteger.valueOf(MARGIN));
        mar.setHeader(BigInteger.valueOf(620));
        mar.setFooter(BigInteger.valueOf(620));

        XWPFStyles styles = doc.createStyles();
        addParagraphStyle(styles, "Normal", "正文", FONT_CN, 11, "1F2937", false, 0, 120, 264, null);
        addParagraphStyle(styles, "Heading1", "标题 1", FONT_HEADING, 16, BLUE, true, 260, 140, 264, 0);
        addParagraphStyle(styles, "Heading2", "标题 2", FONT_HEADING, 13, BLUE, true, 200, 100, 264, 1);
        addParagraphStyle(styles, "Heading3", "标题 3", FONT_HEADING, 11, DARK, true, 140, 80, 264, 2);
        addParagraphStyle(styles, "ReportTitle", "报告标题", FONT_HEADING, 24, DARK, true, 0, 100, 240, null);
        addParagraphStyle(styles, "Caption", "题注", FONT_CN, 9, MUTED, false, 60, 120, 240, null);
        decimalNumId = createDecimalNumbering();

        XWPFHeader header = doc.createHeader(HeaderFooterType.DEFAULT);
        XWPFParagraph hp = header.createParagraph();
        hp.setAlignment(ParagraphAlignment.RIGHT);
        hp.setSpacingAfter(0);
        XWPFRun hr = hp.createRun();
        formatRun(hr, 9, MUTED, false, FONT_HEADING);
        hr.setText("专业认知实习 · 用户登录功能模块");

        XWPFFooter footer = doc.createFooter(HeaderFooterType.DEFAULT);
        XWPFParagraph fp = footer.createParagraph();
        fp.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun fr = fp.createRun();
        formatRun(fr, 9, MUTED, false, FONT_CN);
        fr.setText("教学实验报告  |  第 ");
        CTSimpleField page = fp.getCTP().addNewFldSimple();
        page.setInstr("PAGE");
        page.addNewR().addNewT().setStringValue("1");
        XWPFRun fr2 = fp.createRun();
        formatRun(fr2, 9, MUTED, false, FONT_CN);
        fr2.setText(" 页");
    }

    private void addFrontMatter() {
        XWPFParagraph title = doc.createParagraph();
        title.setStyle("ReportTitle");
        title.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun tr = title.createRun();
        formatRun(tr, 24, DARK, true, FONT_HEADING);
        tr.setText("教 学 实 验 报 告");

        XWPFParagraph subtitle = doc.createParagraph();
        subtitle.setAlignment(ParagraphAlignment.CENTER);
        subtitle.setSpacingAfter(180);
        XWPFRun sr = subtitle.createRun();
        formatRun(sr, 11, MUTED, false, FONT_HEADING);
        sr.setText("专业认知实习作业 1 · 宿舍管理系统");

        XWPFTable meta = createTable(3, 4, new int[]{1500, 3300, 1500, 3302});
        setCell(meta, 0, 0, "实验名称", true, LIGHT, ParagraphAlignment.CENTER);
        setCell(meta, 0, 1, "用户登录功能模块的实现", false, null, ParagraphAlignment.LEFT);
        setCell(meta, 0, 2, "实验日期", true, LIGHT, ParagraphAlignment.CENTER);
        setCell(meta, 0, 3, "2026 年 7 月 10 日", false, null, ParagraphAlignment.CENTER);
        setCell(meta, 1, 0, "班级", true, LIGHT, ParagraphAlignment.CENTER);
        setCell(meta, 1, 1, "________________", false, null, ParagraphAlignment.CENTER);
        setCell(meta, 1, 2, "学号", true, LIGHT, ParagraphAlignment.CENTER);
        setCell(meta, 1, 3, "________________", false, null, ParagraphAlignment.CENTER);
        setCell(meta, 2, 0, "姓名", true, LIGHT, ParagraphAlignment.CENTER);
        setCell(meta, 2, 1, "________________", false, null, ParagraphAlignment.CENTER);
        setCell(meta, 2, 2, "指导老师", true, LIGHT, ParagraphAlignment.CENTER);
        setCell(meta, 2, 3, "胡健", false, null, ParagraphAlignment.CENTER);

        addHeading1("实验目标（建议教师填写，电子档）");
        addNumbered("理解三层架构的分层职责以及 JSP、Servlet、Service、Dao 之间的调用流程。");
        addNumbered("实现前后端表单数据交互，完成账号、密码、账号状态的校验。");
        addNumbered("掌握使用 Session 保存登录状态，并能区分请求转发与重定向的适用场景。");
        addNumbered("处理用户名为空、用户不存在、账号禁用、密码错误等异常，完成页面提示与界面美化。");

        addHeading1("实验环境（建议教师填写，电子档）");
        XWPFTable env = createTable(6, 2, new int[]{2500, 7102});
        String[][] envRows = {
                {"开发语言", "Java SE 8 语法与编译目标（本机 JDK 18）"},
                {"Web 技术", "Servlet 4.0、JSP、JSTL"},
                {"数据库", "MySQL 8.0"},
                {"数据访问", "C3P0 连接池、Commons DBUtils"},
                {"开发与构建", "IntelliJ IDEA、Maven 3.9.16"},
                {"运行环境", "Windows、Tomcat 9.0.120"}
        };
        for (int i = 0; i < envRows.length; i++) {
            setCell(env, i, 0, envRows[i][0], true, LIGHT, ParagraphAlignment.CENTER);
            setCell(env, i, 1, envRows[i][1], false, null, ParagraphAlignment.LEFT);
        }

        addHeading1("实验内容（建议教师填写，电子档）");
        addBody("基于宿舍管理系统现有 Java Web 项目，独立完成通用用户登录功能的完整开发与验证，具体内容如下：");
        addNumbered("设计 user 用户数据表并录入管理员、楼栋管理员和学生测试账号。");
        addNumbered("编写 User 实体类、UserDao 数据查询和 UserService 登录校验逻辑。");
        addNumbered("开发 LoginServlet 接收表单参数，完成身份校验、Session 保存和按角色跳转。");
        addNumbered("制作 login.jsp 登录页面，实现错误信息动态展示，并用 AuthFilter 保护受限资源。");
        addCallout("交付成果：功能核心源代码截图、登录页面截图、异常提示截图、登录成功运行截图。", "E8EEF8");
        addPageBreak();
    }

    private void addBody(Path root) throws Exception {
        Path assets = root.resolve("report_assets");
        addHeading1("教学实验报告正文（学生填写）");
        addCallout("请求链路：login.jsp → LoginServlet → UserService → UserDao → MySQL；登录成功后将 User 保存到 Session，再由 AuthFilter 统一校验访问权限。", "EEF4FF");

        addHeading1("一、实验步骤");
        addHeading2("1.1 项目分析与分层设计");
        addBody("本系统采用典型的 Java Web 分层结构。表示层由 JSP、CSS 和 JavaScript 构成；控制层由 Servlet 接收请求并组织跳转；业务层集中完成输入校验、密码匹配和角色首页选择；数据访问层通过带占位符的 SQL 查询 MySQL；过滤器负责登录态、权限和编码处理。这样可以降低页面与数据库之间的耦合，便于排错和后续扩展。");
        XWPFTable layers = createTable(5, 3, new int[]{1600, 2600, 5402});
        String[][] layerRows = {
                {"层次", "核心文件", "职责"},
                {"表示层", "login.jsp", "采集用户名与密码，显示错误信息和测试账号"},
                {"控制层", "LoginServlet", "接收请求，保存 Session，执行转发或重定向"},
                {"业务层", "UserService", "校验输入、账号状态、密码和角色首页"},
                {"数据层", "UserDao / MySQL", "按用户名查询用户并更新最后登录信息"}
        };
        for (int r = 0; r < layerRows.length; r++) {
            for (int c = 0; c < layerRows[r].length; c++) {
                setCell(layers, r, c, layerRows[r][c], r == 0, r == 0 ? "E8EEF5" : null,
                        c == 2 ? ParagraphAlignment.LEFT : ParagraphAlignment.CENTER);
            }
        }

        addHeading2("1.2 设计用户数据表");
        addBody("user 表以 username 作为唯一登录名，保存加密密码、盐值、真实姓名、角色、启用状态、最后登录时间和 IP。username、role、status 建立索引；初始化脚本提供 admin、zhanglou、lisi 等测试账号，初始密码为 admin123。角色字段取 ADMIN、BUILDING_ADMIN 或 STUDENT，为后续分角色跳转和权限校验提供依据。");
        addFigure(assets.resolve("04-user-table-code.png"), "图 1  user 用户表结构（真实源码截图）", 6.35, 6.2);

        addPageBreak();
        addHeading2("1.3 编写 User 实体类与 UserDao");
        addBody("User 实体类与 user 表字段一一对应，包含 id、username、password、salt、realName、role、status、lastLoginTime 等属性。UserDao 继承 BaseDao，通过 findByUsername 方法按用户名查询用户；SQL 使用 ? 占位符并由 DBUtils 绑定参数，避免直接拼接用户输入造成 SQL 注入。");
        addFigure(assets.resolve("05-user-dao-code.png"), "图 2  UserDao 按用户名/主键查询（真实源码截图）", 6.35, 5.0);

        addHeading2("1.4 实现 UserService 登录校验");
        addBody("业务层按“参数非空 → 用户存在 → 账号启用 → 密码匹配”的顺序校验。密码使用 salt 参与 MD5 运算，并兼容无盐的旧账号；验证通过后更新最后登录时间和 IP。homePath 方法根据角色返回对应首页，保证管理员、楼管和学生进入各自工作台。");
        addFigure(assets.resolve("06-user-service-code.png"), "图 3  UserService 登录校验、密码匹配与角色首页（真实源码截图）", 6.35, 6.5);

        addPageBreak();
        addHeading2("1.5 编写 LoginServlet 并保存 Session");
        addBody("LoginServlet 的 doPost 读取 username、password 并调用 UserService。成功时先执行 changeSessionId()，降低会话固定攻击风险，再把 User 以 loginUser 为键写入 Session，并使用重定向进入角色首页；失败时把错误信息和已输入的用户名放入 request，转发回 login.jsp，使页面能够显示本次错误且不产生重复跳转。");
        addFigure(assets.resolve("07-login-servlet-code.png"), "图 4  LoginServlet 请求处理与页面跳转（真实源码截图）", 6.35, 6.2);

        addPageBreak();
        addHeading2("1.6 制作登录页面并展示异常");
        addBody("login.jsp 使用 UTF-8 编码和 JSTL 标签。表单以 POST 方式提交到 /login，字段包含 username、password 和隐藏的 csrfToken；当 request 中存在 error 时，通过 c:if 输出错误提示。页面同时保留用户名、设置必填校验，并展示开发环境测试账号。");
        addFigure(assets.resolve("09-login-jsp-code.png"), "图 5  login.jsp 表单与错误信息展示（真实源码截图）", 6.35, 6.2);

        addPageBreak();
        addHeading2("1.7 使用 AuthFilter 保护受限资源");
        addBody("AuthFilter 对所有请求生效。登录页、登录接口和静态资源进入白名单；其余路径读取 Session 中的 loginUser。未登录时重定向到登录页；已登录但角色与 /admin/、/buildingadmin/、/student/ 路径不匹配时返回 403，从入口处统一阻止越权访问。");
        addFigure(assets.resolve("08-auth-filter-code.png"), "图 6  AuthFilter 登录态与角色权限校验（真实源码截图）", 6.35, 7.2);

        addPageBreak();
        addHeading2("1.8 启动系统并进行功能测试");
        addBody("执行 Maven 打包后，将 dormitory-system.war 部署到 Tomcat 9，连接本机 MySQL 8。浏览器访问 http://127.0.0.1:8080/dormitory-system/，系统自动进入登录页面。先输入错误密码验证异常分支，再输入 admin / admin123 验证成功分支。");
        addFigure(assets.resolve("01-login-page.png"), "图 7  宿舍管理系统登录页面（2026-07-10 实际运行截图）", 5.9, 4.15);
        addFigure(assets.resolve("02-login-error.png"), "图 8  错误密码提示“密码错误”（实际运行截图）", 5.9, 4.15);

        addPageBreak();
        addHeading2("1.9 验证登录成功与角色跳转");
        addBody("使用管理员账号登录后，地址跳转至 /admin/dashboard，页面右上角显示“系统管理员 / admin”，并可访问总览、用户管理、楼栋管理、宿舍管理、学生管理和报修监管等菜单，说明 Session 保存、角色识别和重定向均正常。");
        addFigure(assets.resolve("03-admin-dashboard.png"), "图 9  管理员登录成功后的总览页（实际运行截图）", 5.55, 7.6);

        addPageBreak();
        addHeading1("二、实验的关键点及解决方法");
        addHeading2("2.1 参数校验与统一异常提示");
        addBody("关键点：输入错误可能来自空值、未知用户、禁用账号或密码错误。解决方法：把判断集中在 UserService.login 中，并统一抛出 BusinessException；Servlet 只负责捕获异常、设置 error 属性并转发页面。这样避免多处重复判断，提示信息也保持一致。");

        addHeading2("2.2 密码校验与数据库安全");
        addBody("关键点：密码不能明文保存，用户输入也不能直接拼接进 SQL。解决方法：项目使用随机盐值参与 MD5 运算，UserDao 使用 ? 占位符传参。该方案满足本次教学实验要求，但 MD5 已不适合生产级密码存储，正式系统应升级为 BCrypt、scrypt 或 Argon2，并增加登录失败次数限制。");

        addHeading2("2.3 Session、转发与重定向");
        addBody("关键点：登录成功后需要跨请求保存身份，失败时又要保留错误信息。解决方法：成功时 changeSessionId 后写入 Session，并使用 sendRedirect 防止刷新页面重复提交；失败时使用 forward 保留同一次 request 中的 error 和 username。两种跳转方式各自用于最合适的场景。");

        addHeading2("2.4 角色路由与越权拦截");
        addBody("关键点：仅隐藏菜单不能真正阻止越权。解决方法：UserService.homePath 负责角色首页路由，AuthFilter 再按 URL 前缀检查 ADMIN、BUILDING_ADMIN、STUDENT 权限；未登录和角色不匹配都在进入业务 Servlet 前被处理。");

        addHeading2("2.5 页面编码与 CSRF 防护");
        addBody("关键点：中文提示需要稳定显示，登录表单也应防止跨站请求伪造。解决方法：页面、过滤器和响应统一使用 UTF-8；login.jsp 提交隐藏 csrfToken，由 CsrfFilter 完成令牌校验，同时静态资源和登录页保持可访问。");

        addHeading2("2.6 测试结果");
        XWPFTable tests = createTable(6, 4, new int[]{1500, 2400, 2800, 2902});
        String[][] testRows = {
                {"测试项", "输入/条件", "预期结果", "实际结果"},
                {"空值校验", "用户名或密码为空", "阻止提交或提示必填", "通过（HTML required + Service 校验）"},
                {"未知用户", "不存在的用户名", "提示“用户不存在”", "通过（业务分支检查）"},
                {"错误密码", "admin / 错误密码", "提示“密码错误”", "通过（图 8）"},
                {"成功登录", "admin / admin123", "进入管理员首页", "通过（图 9）"},
                {"未登录访问", "直接访问受限路径", "重定向到登录页", "通过（AuthFilter）"}
        };
        for (int r = 0; r < testRows.length; r++) {
            for (int c = 0; c < testRows[r].length; c++) {
                setCell(tests, r, c, testRows[r][c], r == 0, r == 0 ? "E8EEF5" : null,
                        c == 1 || c == 2 || c == 3 ? ParagraphAlignment.LEFT : ParagraphAlignment.CENTER);
            }
        }

        addHeading1("三、实验总结");
        addBody("本次实验完成了宿舍管理系统用户登录模块的完整闭环：从数据库账号表、实体对象和参数化查询，到业务层校验、Servlet 请求处理、Session 登录态保存、JSP 错误展示，再到过滤器权限保护和按角色跳转。实际测试表明，错误密码能够得到明确提示，正确管理员账号可以进入系统总览页，核心功能符合实验目标。");
        addBody("通过本次实验，我进一步理解了 Java Web 三层架构各层的职责，以及 request、Session、forward、redirect 之间的区别。后续可继续改进密码算法、登录限流、验证码、审计日志和自动化测试，使系统在安全性、可维护性和可靠性方面更接近生产环境要求。");

        addHeading1("实验（践）成绩与教师评语");
        XWPFTable review = createTable(3, 2, new int[]{1800, 7802});
        setCell(review, 0, 0, "实验成绩", true, LIGHT, ParagraphAlignment.CENTER);
        setCell(review, 0, 1, "", false, null, ParagraphAlignment.LEFT);
        setCell(review, 1, 0, "教师评语", true, LIGHT, ParagraphAlignment.CENTER);
        setCell(review, 1, 1, "\n\n\n", false, null, ParagraphAlignment.LEFT);
        setCell(review, 2, 0, "批阅信息", true, LIGHT, ParagraphAlignment.CENTER);
        setCell(review, 2, 1, "批阅人：________________    批阅日期：______年____月____日", false, null, ParagraphAlignment.LEFT);
    }

    private void addHeading1(String text) { addHeading(text, "Heading1", 16, BLUE); }
    private void addHeading2(String text) { addHeading(text, "Heading2", 13, BLUE); }

    private void addHeading(String text, String style, int size, String color) {
        XWPFParagraph p = doc.createParagraph();
        p.setStyle(style);
        XWPFRun r = p.createRun();
        formatRun(r, size, color, true, FONT_HEADING);
        r.setText(text);
    }

    private void addBody(String text) {
        XWPFParagraph p = doc.createParagraph();
        p.setStyle("Normal");
        p.setAlignment(ParagraphAlignment.BOTH);
        p.setIndentationFirstLine(440);
        p.setSpacingAfter(120);
        p.setSpacingBetween(1.15);
        XWPFRun r = p.createRun();
        formatRun(r, 11, "1F2937", false, FONT_CN);
        r.setText(text);
    }

    private void addNumbered(String text) {
        XWPFParagraph p = doc.createParagraph();
        p.setStyle("Normal");
        p.setNumID(decimalNumId);
        p.setIndentationLeft(720);
        p.setIndentationHanging(360);
        p.setSpacingAfter(80);
        p.setSpacingBetween(1.1);
        XWPFRun r = p.createRun();
        formatRun(r, 11, "1F2937", false, FONT_CN);
        r.setText(text);
    }

    private void addCallout(String text, String fill) {
        XWPFParagraph p = doc.createParagraph();
        p.setStyle("Normal");
        p.setSpacingBefore(80);
        p.setSpacingAfter(140);
        p.setIndentationLeft(180);
        p.setIndentationRight(180);
        CTPPr pPr = p.getCTP().isSetPPr() ? p.getCTP().getPPr() : p.getCTP().addNewPPr();
        CTShd shd = pPr.isSetShd() ? pPr.getShd() : pPr.addNewShd();
        shd.setFill(fill);
        XWPFRun r = p.createRun();
        formatRun(r, 10, DARK, true, FONT_HEADING);
        r.setText(text);
    }

    private void addPageBreak() {
        XWPFParagraph p = doc.createParagraph();
        p.setPageBreak(true);
        p.setSpacingAfter(0);
    }

    private void addFigure(Path image, String caption, double maxWidthIn, double maxHeightIn) throws Exception {
        if (!Files.exists(image)) throw new IllegalStateException("Missing image: " + image);
        BufferedImage bi = ImageIO.read(image.toFile());
        double width = maxWidthIn;
        double height = width * bi.getHeight() / (double) bi.getWidth();
        if (height > maxHeightIn) {
            height = maxHeightIn;
            width = height * bi.getWidth() / (double) bi.getHeight();
        }
        XWPFParagraph p = doc.createParagraph();
        p.setAlignment(ParagraphAlignment.CENTER);
        p.setSpacingBefore(80);
        p.setSpacingAfter(20);
        XWPFRun r = p.createRun();
        try (InputStream in = Files.newInputStream(image)) {
            r.addPicture(in, XWPFDocument.PICTURE_TYPE_PNG, image.getFileName().toString(),
                    Units.toEMU(width), Units.toEMU(height));
        }
        XWPFParagraph cp = doc.createParagraph();
        cp.setStyle("Caption");
        cp.setAlignment(ParagraphAlignment.CENTER);
        cp.setSpacingBefore(20);
        cp.setSpacingAfter(140);
        XWPFRun cr = cp.createRun();
        formatRun(cr, 9, MUTED, false, FONT_CN);
        cr.setText(caption);
    }

    private XWPFTable createTable(int rows, int cols, int[] widths) {
        if (Arrays.stream(widths).sum() != CONTENT_W) {
            throw new IllegalArgumentException("Table widths must total " + CONTENT_W);
        }
        XWPFTable table = doc.createTable(rows, cols);
        table.setWidth(CONTENT_W);
        table.setTableAlignment(TableRowAlign.LEFT);
        table.setCellMargins(80, 120, 80, 120);
        CTTblPr tblPr = table.getCTTbl().getTblPr();
        CTTblLayoutType layout = tblPr.isSetTblLayout() ? tblPr.getTblLayout() : tblPr.addNewTblLayout();
        layout.setType(STTblLayoutType.FIXED);
        CTTblWidth ind = tblPr.isSetTblInd() ? tblPr.getTblInd() : tblPr.addNewTblInd();
        ind.setType(STTblWidth.DXA);
        ind.setW(BigInteger.valueOf(120));
        CTTblGrid grid = table.getCTTbl().getTblGrid() != null ? table.getCTTbl().getTblGrid() : table.getCTTbl().addNewTblGrid();
        while (grid.sizeOfGridColArray() > 0) grid.removeGridCol(0);
        for (int width : widths) grid.addNewGridCol().setW(BigInteger.valueOf(width));
        for (XWPFTableRow row : table.getRows()) {
            for (int c = 0; c < cols; c++) {
                XWPFTableCell cell = row.getCell(c);
                CTTcPr tcPr = cell.getCTTc().isSetTcPr() ? cell.getCTTc().getTcPr() : cell.getCTTc().addNewTcPr();
                CTTblWidth tcW = tcPr.isSetTcW() ? tcPr.getTcW() : tcPr.addNewTcW();
                tcW.setType(STTblWidth.DXA);
                tcW.setW(BigInteger.valueOf(widths[c]));
                cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            }
        }
        XWPFParagraph spacer = doc.createParagraph();
        spacer.setSpacingAfter(40);
        return table;
    }

    private void setCell(XWPFTable table, int row, int col, String text, boolean bold, String fill,
                         ParagraphAlignment align) {
        XWPFTableCell cell = table.getRow(row).getCell(col);
        CTTcPr tcPr = cell.getCTTc().isSetTcPr() ? cell.getCTTc().getTcPr() : cell.getCTTc().addNewTcPr();
        if (fill != null) {
            CTShd shd = tcPr.isSetShd() ? tcPr.getShd() : tcPr.addNewShd();
            shd.setFill(fill);
        }
        XWPFParagraph p = cell.getParagraphs().get(0);
        for (int i = p.getRuns().size() - 1; i >= 0; i--) p.removeRun(i);
        p.setAlignment(align);
        p.setSpacingBefore(0);
        p.setSpacingAfter(0);
        p.setSpacingBetween(1.05);
        XWPFRun r = p.createRun();
        formatRun(r, 10, bold ? DARK : "334155", bold, bold ? FONT_HEADING : FONT_CN);
        String[] parts = text.split("\\n", -1);
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) r.addBreak();
            r.setText(parts[i]);
        }
    }

    private void addParagraphStyle(XWPFStyles styles, String id, String name, String font, int size,
                                   String color, boolean bold, int before, int after, int line,
                                   Integer outlineLevel) {
        CTStyle ct = CTStyle.Factory.newInstance();
        ct.setStyleId(id);
        ct.setType(STStyleType.PARAGRAPH);
        ct.addNewName().setVal(name);
        if (!"Normal".equals(id)) ct.addNewBasedOn().setVal("Normal");
        CTRPr rPr = ct.addNewRPr();
        CTFonts fonts = rPr.addNewRFonts();
        fonts.setAscii(font);
        fonts.setHAnsi(font);
        fonts.setEastAsia(font);
        rPr.addNewSz().setVal(BigInteger.valueOf(size * 2L));
        rPr.addNewSzCs().setVal(BigInteger.valueOf(size * 2L));
        rPr.addNewColor().setVal(color);
        if (bold) rPr.addNewB().setVal(true);
        CTPPrGeneral pPr = ct.addNewPPr();
        CTSpacing spacing = pPr.addNewSpacing();
        spacing.setBefore(BigInteger.valueOf(before));
        spacing.setAfter(BigInteger.valueOf(after));
        spacing.setLine(BigInteger.valueOf(line));
        spacing.setLineRule(STLineSpacingRule.AUTO);
        if (outlineLevel != null) pPr.addNewOutlineLvl().setVal(BigInteger.valueOf(outlineLevel));
        styles.addStyle(new XWPFStyle(ct));
    }

    private BigInteger createDecimalNumbering() {
        XWPFNumbering numbering = doc.createNumbering();
        CTAbstractNum abstractNum = CTAbstractNum.Factory.newInstance();
        abstractNum.setAbstractNumId(BigInteger.ZERO);
        CTLvl lvl = abstractNum.addNewLvl();
        lvl.setIlvl(BigInteger.ZERO);
        lvl.addNewStart().setVal(BigInteger.ONE);
        lvl.addNewNumFmt().setVal(STNumberFormat.DECIMAL);
        lvl.addNewLvlText().setVal("%1.");
        lvl.addNewLvlJc().setVal(STJc.LEFT);
        CTPPrGeneral pPr = lvl.addNewPPr();
        CTTabs tabs = pPr.addNewTabs();
        CTTabStop tab = tabs.addNewTab();
        tab.setVal(STTabJc.NUM);
        tab.setPos(BigInteger.valueOf(720));
        CTInd ind = pPr.addNewInd();
        ind.setLeft(BigInteger.valueOf(720));
        ind.setHanging(BigInteger.valueOf(360));
        BigInteger abstractId = numbering.addAbstractNum(new XWPFAbstractNum(abstractNum));
        return numbering.addNum(abstractId);
    }

    private void formatRun(XWPFRun run, int size, String color, boolean bold, String font) {
        run.setFontFamily(font);
        run.setFontFamily(font, XWPFRun.FontCharRange.eastAsia);
        run.setFontSize(size);
        run.setColor(color);
        run.setBold(bold);
    }
}

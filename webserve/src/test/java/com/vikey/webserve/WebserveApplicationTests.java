package com.vikey.webserve;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.rules.IColumnType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.vikey.webserve.config.PersonalConfig;
import com.vikey.webserve.entity.*;
import com.vikey.webserve.service.*;
import com.vikey.webserve.utils.DocxUtils;
import com.vikey.webserve.utils.ZipUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

@SpringBootTest
class WebserveApplicationTests {


    private static final Logger LOGGER = LoggerFactory.getLogger(WebserveApplicationTests.class);

    @Resource
    private IUserService iUserService;

    @Resource
    private IFast_taskService iFast_taskService;

    @Resource
    private IAnnexe_taskService iAnnexe_taskService;

    @Resource
    private IAnnexeService iAnnexeService;

    @Resource
    private PersonalConfig personalConfig;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource(name = "pingSoftTranslateService")
    private TranslateService translateService_pingsoft;

    @Resource
    private CacheManager cacheManager;


    @Test
    void test() {

        iUserService.getById(1l);
    }

    @Test
    void test0() {
        User u = iUserService.selectUserWithRoles(Long.valueOf(1l));
        System.out.println(u.toString());
    }

    @Test
    void test1() {
        List<Fast_task> fast_tasks = iFast_taskService.getLastFast_task(Long.valueOf(1));
        fast_tasks.stream().forEach(t -> LOGGER.info(t.toString()));
    }

    @Test
    void test2() {
        Map<String, List<Fast_task>> map = iFast_taskService.getFast_taskByDate(Long.valueOf(1), "a");
        map.forEach((k, v) -> {
            LOGGER.info(k);
            v.stream().forEach(t -> {
                LOGGER.info(t.toString());
            });
        });

    }

    @Test
    void test3() {
        Map<String, List<Annexe_task>> map = iAnnexe_taskService.getAnnexe_taskByDate(Long.valueOf(1), "e");
        map.forEach((k, v) -> {
            LOGGER.info(k);
            v.stream().forEach(t -> {
                LOGGER.info(t.toString());
            });
        });
    }

    @Test
    void test4() {
        IPage<Annexe> iPage = iAnnexeService.getAnnexeByPage(1, 2, 1l);
    }

    @Test
    void test5() {
        iUserService.selectUserWithRolesByAccount("admin");

    }

    @Test
    void tes6() {
        Fast_task t = iFast_taskService.getFast_TaskById(1l);
        LOGGER.info(t.toString());

    }

    @Test
    void test7() {
        QueryWrapper<Annexe_task> queryWrapper = new QueryWrapper();
        queryWrapper.select("id", "name").eq("id", Long.valueOf("1144"));
        List<Annexe_task> annexe_tasks = iAnnexe_taskService.getBaseMapper().selectList(queryWrapper);
        LOGGER.info(annexe_tasks.get(0).toString());
    }

    @Test
    @Transactional
    void test8() {
        String[] ids = {"36", "37"};
        List<Long> ids_ = new ArrayList<>();
        Arrays.stream(ids).forEach(i -> {
            ids_.add(Long.valueOf(i));
        });
        Predicate<Annexe> p = null;
        UpdateWrapper<Annexe> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("discard", Constant.DELETE).in("id", ids_);
        iAnnexeService.getBaseMapper().update(null, updateWrapper);
    }

    @Test
    void test9() throws IOException {
        List<Long> list = convert("44,45");
        QueryWrapper<Annexe> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("name", "path").in("id", list);
        List<Annexe> annexeList = iAnnexeService.getBaseMapper().selectList(queryWrapper);

        try {
            File file = new File(personalConfig.getMake_file_dir() + File.separator + "a.zip");
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ZipUtils.toZip(annexeList, fileOutputStream, personalConfig.getUpload_dir());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    void test10() {
        Page<User>
                page = new Page<>(1, 2);
        IPage<User> page1 = iUserService.selectUserWithRolesByName(page, null);
        LOGGER.debug(JSON.toJSONString(new RespPageBean(page1.getTotal(), page1.getRecords(), page1.getSize())));


    }

    @Test
    void test11() {
        Integer count =
                iUserService.countByAccount("admin", 1l);
        LOGGER.debug(count.toString());
    }

    @Test
    void test12() {
        LocalDateTime time = LocalDateTime.now();
        Integer count = iAnnexeService.getAnnexeCount(time.minusDays(30l), "0");
        System.out.println(count);
    }

    @Test
    void test13() {
        List<LocalDateTime> localDateTimes = new ArrayList<>();
        LocalDateTime time = LocalDateTime.of(2020, 5, 28, 14, 00, 0);
        for (int i = 23; i >= 0; i--) {
            localDateTimes.add(time.minusHours(Long.valueOf(i)));
        }
        List<Map> maps = iAnnexeService.getAnnexeCountByPeriod(localDateTimes, "txt", "%Y-%m-%d %H:00:00");
        maps.stream().forEach(System.out::print);

    }

    @Test
    void test14() {
//        Map map = iAnnexe_taskService.getAllTaskCount(2l);
//        System.out.println(map);
    }

    @Test
    void test15() {
//        String languege = iAnnexe_taskService.getMostUseLanguage(1l,LocalDateTime.now());
//        LOGGER.info(languege);
        String languege = iAnnexe_taskService.getLastUseLanguage(1l, LocalDateTime.now());
        LOGGER.info(languege);
    }

    @Test
    void test16() {
//        asyncService.batch_xiaoniu("12345678",null,null);

    }

    @Test
    void test17() {
        ArrayList<Integer> accResult_ = Stream.of(1, 2, 3, 4)
                .reduce(new ArrayList<>(),
                        (acc, item) -> {

                            acc.add(item);
                            System.out.println("item: " + item);
                            System.out.println("acc+ : " + acc);
                            System.out.println("BiFunction");
                            return acc;
                        }, (acc, item) -> {
                            System.out.println("BinaryOperator");
                            acc.addAll(item);
                            System.out.println("item: " + item);
                            System.out.println("acc+ : " + acc);
                            System.out.println("--------");
                            return acc;
                        });
        System.out.println("accResult_: " + accResult_);


    }

    @Test
    void test18() {
        List<Map> lit = iAnnexeService.getAnnexeCountByType(LocalDateTime.of(2020, 5, 28, 0, 0));
        LOGGER.info(lit.toString());
    }

    @Test
    void test19() {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00:00");
        LOGGER.info(df.format(LocalDateTime.now()));

    }

    @Test
    void test20() {
        User user = new User();
        user.setAccount("aaa");
        String aaa = Optional.ofNullable(user).map(User::getUsername).orElse("111");
        LOGGER.info(aaa);
    }

    @Test
    void test21() {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00:00");
        LocalDateTime n = LocalDateTime.now();

        String time = df.format(n);
        LOGGER.info(time);


    }

    @Test
    void test22() throws IOException {
        Annexe annexe = iAnnexeService.getAnnexeById(102l);
        LOGGER.info(annexe.toString());
    }

    private List<Long> convert(String content) {
        List<Long> list = new ArrayList<>();
        Arrays.stream(content.split(",")).forEach(i ->
        {
            list.add(Long.valueOf(i));
        });
        return list;
    }

    private static HttpClient HTTPCLIENT = HttpClientBuilder.create().build();


    @Test
    public void fun01() {
        for (int i = 0; i < 10; i++) {
            String url = "";
            if (i % 2 == 0) {
                url = "http://www.baidu.com";
            } else {
                url = "http://www.hao123.com";
            }
            HttpGet httpGet = new HttpGet(url);
            LOGGER.info("------url:{}-----", url);
            HttpResponse res = null;
            try {
                res = HTTPCLIENT.execute(httpGet);
            } catch (IOException e) {
                LOGGER.error("异常： ", e);
                return;
            }
            int code = res.getStatusLine().getStatusCode();
            LOGGER.info("---url:{}\tcode:{}\ti:{}", url, code, i + 1);

        }

    }

    @Test
    public void fun02() throws IOException {
        for (int i = 0; i < 10; i++) {
            String url = "";
            if (i % 2 == 0) {
                url = "http://www.baidu.com";
            } else {
                url = "http://www.hao123.com";
            }
            HttpGet httpGet = new HttpGet(url);
            LOGGER.info("------url:{}-----", url);
            HttpResponse res = null;
            try {
                res = HTTPCLIENT.execute(httpGet);
                int code = res.getStatusLine().getStatusCode();
                LOGGER.info("---url:{}\tcode:{}\ti:{}", url, code, i + 1);
            } catch (IOException e) {
                LOGGER.error("异常： ", e);
                return;
            } finally {
                if (res != null) {
                    if (res.getEntity() != null) {
                        if (res.getEntity().isStreaming()) {
                            InputStream inStream = res.getEntity().getContent();
                            if (inStream != null) {
                                inStream.close();
                            }
                        }

                    }

                }
            }
        }
    }


    @Test
    void test23() throws IOException, MessagingException {
        Content content = new DocxContent(new File("D:\\wkw\\越南语测试文件\\越南语.docx"));
        String c = content.getContent();
        content.write(c, new File("d:\\ss.docx"));


    }


    //    @Test
    void codeGenerator() {
        AutoGenerator mpg = new AutoGenerator();
        // 选择 freemarker 引擎
        mpg.setTemplateEngine(new FreemarkerTemplateEngine());

        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        gc.setAuthor("wkw");
        gc.setOutputDir("D:\\wkw\\translate_eee");
        gc.setFileOverride(false);// 是否覆盖同名文件，默认是false
        gc.setActiveRecord(false);// 不需要ActiveRecord特性的请改为false
        gc.setEnableCache(false);// XML 二级缓存
        gc.setBaseResultMap(true);// XML ResultMap
        gc.setBaseColumnList(false);// XML columList
        /* 自定义文件命名，注意 %s 会自动填充表实体属性！ */
        // gc.setMapperName("%sDao");
        // gc.setXmlName("%sDao");
        // gc.setServiceName("MP%sService");
        // gc.setServiceImplName("%sServiceDiy");
        // gc.setControllerName("%sAction");
        mpg.setGlobalConfig(gc);

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setDbType(DbType.MYSQL);
        dsc.setTypeConvert(new MySqlTypeConvert() {
            @Override
            // 自定义数据库表字段类型转换【可选】
            public IColumnType processTypeConvert(GlobalConfig globalConfig, String fieldType) {
                System.out.println("转换类型：" + fieldType);
                // 注意！！processTypeConvert 存在默认类型转换，如果不是你要的效果请自定义返回、非如下直接返回。
                return super.processTypeConvert(globalConfig, fieldType);
            }
        });
        dsc.setDriverName("com.mysql.jdbc.Driver");
        dsc.setUsername("root");
        dsc.setPassword("123456");
        dsc.setUrl("jdbc:mysql://localhost:3306/translate?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false");
        mpg.setDataSource(dsc);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        // strategy.setCapitalMode(true);// 全局大写命名 ORACLE 注意
//        strategy.setTablePrefix(new String[]{"user_"});// 此处可以修改为您的表前缀
        strategy.setNaming(NamingStrategy.no_change);// 表名生成策略
        strategy.setInclude(new String[]{"user"}); // 需要生成的表
        // strategy.setExclude(new String[]{"test"}); // 排除生成的表
        // 自定义实体父类
        // strategy.setSuperEntityClass("com.baomidou.demo.TestEntity");
        // 自定义实体，公共字段
        // strategy.setSuperEntityColumns(new String[] { "test_id", "age" });
        // 自定义 mapper 父类
        // strategy.setSuperMapperClass("com.baomidou.demo.TestMapper");
        // 自定义 service 父类
        // strategy.setSuperServiceClass("com.baomidou.demo.TestService");
        // 自定义 service 实现类父类
        // strategy.setSuperServiceImplClass("com.baomidou.demo.TestServiceImpl");
        // 自定义 controller 父类
        // strategy.setSuperControllerClass("com.baomidou.demo.TestController");
        // 【实体】是否生成字段常量（默认 false）
        // public static final String ID = "test_id";
        // strategy.setEntityColumnConstant(true);
        // 【实体】是否为构建者模型（默认 false）
        // public User setName(String name) {this.name = name; return this;}
        // strategy.setEntityBuilderModel(true);
        mpg.setStrategy(strategy);

        // 包配置
        PackageConfig pc = new PackageConfig();
        pc.setParent("com.vikey.webserve");
//        pc.setModuleName("test");
        mpg.setPackageInfo(pc);

        // 注入自定义配置，可以在 VM 中使用 cfg.abc 【可无】
//        InjectionConfig cfg = new InjectionConfig() {
//            @Override
//            public void initMap() {
//                Map<String, Object> map = new HashMap<String, Object>();
//                map.put("abc", this.getConfig().getGlobalConfig().getAuthor() + "-mp");
//                this.setMap(map);
//            }
//        };
//
//        // 自定义 xxList.jsp 生成
//        List<FileOutConfig> focList = new ArrayList<>();
//        focList.add(new FileOutConfig("/template/list.jsp.vm") {
//            @Override
//            public String outputFile(TableInfo tableInfo) {
//                // 自定义输入文件名称
//                return "D://my_" + tableInfo.getEntityName() + ".jsp";
//            }
//        });
//        cfg.setFileOutConfigList(focList);
//        mpg.setCfg(cfg);
//
//        // 调整 xml 生成目录演示
//        focList.add(new FileOutConfig("/templates/mapper.xml.vm") {
//            @Override
//            public String outputFile(TableInfo tableInfo) {
//                return "/develop/code/xml/" + tableInfo.getEntityName() + ".xml";
//            }
//        });
//        cfg.setFileOutConfigList(focList);
//        mpg.setCfg(cfg);
//
//        // 关闭默认 xml 生成，调整生成 至 根目录
//        TemplateConfig tc = new TemplateConfig();
//        tc.setXml(null);
//        mpg.setTemplate(tc);

        // 自定义模板配置，可以 copy 源码 mybatis-plus/src/main/resources/templates 下面内容修改，
        // 放置自己项目的 src/main/resources/templates 目录下, 默认名称一下可以不配置，也可以自定义模板名称
        // TemplateConfig tc = new TemplateConfig();
        // tc.setController("...");
        // tc.setEntity("...");
        // tc.setMapper("...");
        // tc.setXml("...");
        // tc.setService("...");
        // tc.setServiceImpl("...");
        // 如上任何一个模块如果设置 空 OR Null 将不生成该模块。
        // mpg.setTemplate(tc);

        // 执行生成
        mpg.execute();

        // 打印注入设置【可无】
        // System.err.println(mpg.getCfg().getMap().get("abc"));


    }

    @Test
    public void test66() throws Exception {
        String s = iFast_taskService.translate_xiaoniu("中华人民共和国", "zh", "en");
        System.out.println(s);
    }

    @Test
    public void test67() {
//        System.out.println(WebserveApplicationTests.class.getResource("/"));
        System.out.println(WebserveApplicationTests.class.getClassLoader().getResource("com/vikey/webserve/mapper/xml/Annexe_taskMapper.xml"));
    }

    @Test
    public void test68() throws Exception {
        String content = DocxUtils.docx2Html("D:\\docx2html\\[指舞如歌]从零开始,一步步安装虚拟机Mac10.6.x并完美.随意更新官方补丁(VM版)[AMD相.docx");
        LOGGER.info(content);
    }


    @Test
    public void test335() {
        double batchNum = Math.ceil(new Double(300l) / 1400);
        System.out.println(batchNum);
    }

    @Test
    public void test888() throws Exception {
        String s = batch_xiaoniu("sleep fish", "en", "zh");
        System.out.println(s);
    }

    private static final double LENGTH = 1400;

    /**
     * 小牛长文本文件翻译
     *
     * @param text
     * @param from
     * @param to
     * @return
     * @throws Exception
     */
    private String batch_xiaoniu(String text, String from, String to) throws Exception {
        double batchNum = Math.ceil(new Double(text.length()) / LENGTH);
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < batchNum; i++) {
            int end = (i + 1) * LENGTH > text.length() ? text.length() : (int) ((i + 1) * LENGTH);
            String batch_text = text.substring((int) (i * LENGTH), end);
            try {
                String batch_translate = translate_xiaoniu(batch_text, from, to);
                Thread.sleep(5100);
                result.append(batch_translate);
            } catch (Exception e) {
                throw e;
            }
        }
        return result.toString();
    }

    private String translate_xiaoniu(String text, String from, String to) throws Exception {
        HttpPost post = new HttpPost(personalConfig.getTranslate_api_url_xiaoniu());
        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("from", from));
        urlParameters.add(new BasicNameValuePair("to", to));
        urlParameters.add(new BasicNameValuePair("src_text", text));
        urlParameters.add(new BasicNameValuePair("apikey", personalConfig.getApiKey_xiaoniu()));
        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(3000).setConnectTimeout(3000).build();
        post.setConfig(requestConfig);
        post.setEntity(new UrlEncodedFormEntity(urlParameters, "utf-8"));

        HttpResponse response = HTTPCLIENT.execute(post);
        StringBuffer result = new StringBuffer();
        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent(), "utf-8"));
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        JSONObject result_obj = JSONObject.parseObject(result.toString());
        EntityUtils.consume(response.getEntity());
        if (result_obj.containsKey("tgt_text")) {
            return result_obj.getString("tgt_text");
        } else {
            throw new Exception("小牛接口翻译异常，错误代码" + result_obj.get("error_code"));
        }
    }




    @Test
    void test70() {
        Map<String, Object> translate = translateService_pingsoft.translate("Mới đây, Trung tâm phương tiện truyền thông mới kênh tin tức Đài " +
                "truyền hình CCTV của Đài Phát thanh và Truyền hình Trung ương Trung" +
                "Quốc đã đưa ra chương trình phát sóng trực tiếp quy mô lớn “Dạ du Trung " +
                "Quốc”, đã đi đến Bách Ích – Thượng Hà Thành của quận Giang Nam, thành phố Nam Ninh. " +
                "Chương trình này đã được phát trực tiếp và đồng bộ trên các kênh như Máy khách kênh t" +
                "in tức Đài truyền hình CCTV, Weibo chính thức của Đài truyền hình CCTV, Video Đài truyền " +
                "hình CCTV… đã thu hút được sự quan tâm của hơn 5 triệu cư dân mạng.", "vi", "zh");
        System.out.println(translate);
    }


    @Test
    public void test71() {
        Optional<String> optional = Optional.of("abcd");
        Optional<StringBuffer> result = optional.map(StringBuffer::new);
        System.out.println(result.get().toString());
    }


}
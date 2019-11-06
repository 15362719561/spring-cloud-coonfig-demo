package com.augurit;

import com.augurit.agcloud.agcom.agsupport.common.mongodb.MongoDbService;
import com.augurit.agcloud.agcom.agsupport.common.util.UploadUtil;
import com.augurit.agcloud.framework.ui.pager.PageArgumentResolver;
import com.augurit.agcloud.framework.util.JdkIdGenerator;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.util.List;
import java.util.Properties;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private HttpMessageConverters messageConverters;
    @Autowired
    private MongoDbService mongoDbService;
    private static Logger logger = LoggerFactory.getLogger(WebMvcConfig.class);

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }


    @Bean
    public PageArgumentResolver datatablePageArgumentResolver(){
        return new PageArgumentResolver();
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(pageArgumentResolver());
    }
    @Bean
    public PageArgumentResolver pageArgumentResolver() {
        return new PageArgumentResolver();
    }

    /**
     * 识别目前使用的是什么数据源
     * @return
     */
    @Bean
    public DatabaseIdProvider getDatabaseIdProvider() {
        DatabaseIdProvider databaseIdProvider = new VendorDatabaseIdProvider();
        Properties properties = new Properties();
        properties.setProperty("Oracle", "oracle");
        properties.setProperty("MySQL", "mysql");
        properties.setProperty("PostgreSQL", "postgresql");
        databaseIdProvider.setProperties(properties);
        return databaseIdProvider;
    }

    @Bean(name="validator")
    public LocalValidatorFactoryBean getLocalValidatorFactoryBean(){
        return new LocalValidatorFactoryBean();
    }

    //自动代理
    @Bean
    public DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator daap = new DefaultAdvisorAutoProxyCreator();
        daap.setProxyTargetClass(true);
        return daap;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }


    /**
     * MongoTemplate 使用agcloud配置的信息生成
     * @return
     */
    @Bean
    public MongoTemplate newTemplate() {
        MongoTemplate template = mongoDbService.getMongoTemplate();
        return template;
    }

    @Bean
    public JdkIdGenerator defaultIdGenerator() {
        return new JdkIdGenerator();
    }

    @Bean
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }

    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadRelativePath = UploadUtil.getUploadRelativePath();
        String uploadAbsolutePath = UploadUtil.getUploadAbsolutePath();
        File uploadFile = new File(uploadAbsolutePath);
        if (!uploadFile.exists()) {
            uploadFile.mkdir();
        }

        registry.addResourceHandler(new String[]{"/" + uploadRelativePath + "**"}).addResourceLocations(new String[]{"file:" + uploadAbsolutePath});
    }
}

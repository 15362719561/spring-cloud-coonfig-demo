package com.augurit.agsupport.map.util;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.util.Map;

/**
 * 应用Freemaker生成doc文件
 */
public class DocumentHandler {

    //配置实例：只需要一个实例（单例模式）
    private Configuration configuration = null;
    private String webPath;

    /**
     * @param webPath 应用根目录
     */
    public DocumentHandler(String webPath) {
        configuration = new Configuration();
        configuration.setDefaultEncoding("utf-8");
        this.webPath = webPath;
    }

    /**
     * @param dataMap          装载数据
     * @param templateFileName ftl文件名，如fourline.ftl
     * @param storagePath      生成文档存放路径 比如/upload/控制线检测/2015/07/201570722123.doc
     */
    @SuppressWarnings("unchecked")
    public void createDoc(Map dataMap, String templateFileName, String storagePath) throws Exception {
        /**
         * 模板加载方式
         * 1、setClassForTemplateLoading
         * 2、setServletContextForTemplateLoading  web上下文
         * 3、setDirectoryForTemplateLoading
         */
        try {
            configuration.setDirectoryForTemplateLoading(new File(webPath.concat("template")));
        } catch (IOException e2) {
            throw e2;
        }
        Template t = null;
        try {
            // fourline.ftl为要装载的模板
            t = configuration.getTemplate(templateFileName);
        } catch (IOException e) {
            throw e;
        }
        // 输出文档路径及名称
        File outFile = new File(webPath.concat(storagePath));
        //判断目标文件所在的目录是否存在
        if (!outFile.getParentFile().exists()) {
            outFile.getParentFile().mkdirs();
        }
        if (!outFile.exists()) {
            try {
                outFile.createNewFile();
            } catch (IOException e) {
                throw e;
            }
        }
        Writer out = null;
        try {
            try {
                out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw e;
            }
        } catch (FileNotFoundException e1) {
            throw e1;
        }
        try {
            t.process(dataMap, out);
            out.flush();
        } catch (TemplateException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    throw e;
                }
            }
        }
    }
}

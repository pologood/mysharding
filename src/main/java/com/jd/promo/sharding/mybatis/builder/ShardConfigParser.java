package com.jd.promo.sharding.mybatis.builder;

import com.jd.promo.sharding.mybatis.router.RouteCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;

/**
 * @Author: zhouchangjiang
 * @Date: 2016/08/24
 * @Version: 1.0.0
 * 实现配置文件解析
 */
public class ShardConfigParser {
    private static final Logger logger = LoggerFactory.getLogger(ShardConfigParser.class);

    public static ShardConfigHolder parse(InputStream input) throws Exception {
        final ShardConfigHolder configHolder = ShardConfigHolder.getInstance();
        SAXParserFactory spf = SAXParserFactory.newInstance();
        //spf.setValidating(true);是否校验配置文件格式，暂不校验
        spf.setNamespaceAware(true);
        SAXParser parser = spf.newSAXParser();
        final XMLReader reader = parser.getXMLReader();
        //XML文件解析
        DefaultHandler handler = new DefaultHandler() {
            private RouteCondition routeCondition;
            private StringBuffer sbValue;
            private boolean isInRouteCondition = false;

            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                if ("router".equals(qName)) {
                    String routename = attributes.getValue("name");
                    String className = attributes.getValue("class");
                    try {
                        Class<?> clazz = Class.forName(className);
                        configHolder.register(routename, clazz);
                    } catch (ClassNotFoundException e) {
                        logger.error("startElement parse router ClassNotFoundException,routename is {}", routename);
                        logger.error("startElement parse router ClassNotFoundException,className is {}", className, e);
                        throw new SAXException(e);
                    }
                } else if ("routeCondition".equals(qName)) {
                    //配置名称
                    String name = attributes.getValue("name");
                    //表名统一转换为大写
                    if (null != name && !"".equals(name.trim())) {
                        name = name.toUpperCase();
                    }
                    //路由算法名称
                    String routerName = attributes.getValue("routerName");
                    //父配置名称
                    String parent = attributes.getValue("parent");
                    if (null != parent && !"".equals(parent.trim())) {
                        parent = parent.toUpperCase();
                    }
                    this.routeCondition = new RouteCondition(name);
                    this.routeCondition.setRouterName(routerName);
                    this.routeCondition.setParent(parent);
                    isInRouteCondition = true;//进入routeCondition节点
                } else if (isInRouteCondition) {
                    sbValue = new StringBuffer();
                }
            }

            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException {
                if (!"routeCondition".equals(qName) && isInRouteCondition) {
                    //routeCondition 子元素
                    this.routeCondition.addCondition(qName, sbValue.toString());
                }
                if ("routeCondition".equals(qName)) {
                    configHolder.register(this.routeCondition.getLogicTableName(), this.routeCondition);
                    isInRouteCondition = false;//退出routeCondition节点
                }
            }

            @Override
            public void characters(char ch[], int start, int length) throws SAXException {
                String value = new String(ch, start, length).trim();
                if (sbValue != null && isInRouteCondition) {
                    sbValue.append(value);
                }
            }

            @Override
            public void error(SAXParseException e) throws SAXException {
                logger.error("parse config error", e);
                throw e;
            }
        };
        reader.setContentHandler(handler);
        reader.setEntityResolver(handler);
        reader.setErrorHandler(handler);
        reader.parse(new InputSource(input));

        return configHolder;
    }
}
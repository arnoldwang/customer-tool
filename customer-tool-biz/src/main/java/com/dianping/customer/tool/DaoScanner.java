package com.dianping.customer.tool;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class DaoScanner implements BeanFactoryPostProcessor,ResourceLoaderAware {
    private List<Map<String,String>> scanPackageList;
    private ResourcePatternResolver resourcePatternResolver;
    private CachingMetadataReaderFactory metadataReaderFactory;

    private static final String PACKAGE_NAME = "PACKAGE_NAME";
    private static final String PARENT_DAO_NAME = "PARENT_DAO_NAME";

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if(!scanPackageList.isEmpty()) {
            for(Map<String,String> scanPackage : scanPackageList) {
                String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                        resolveBasePackage(scanPackage.get(PACKAGE_NAME)) + "/" + "**/*Dao.class";
                try {
                    Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
                    if(null != resources){
                        for(Resource resource:resources){
                            GenericBeanDefinition dao = new GenericBeanDefinition();
                            MetadataReader metadataReader = this.metadataReaderFactory.getMetadataReader(resource);
                            String className = metadataReader.getClassMetadata().getClassName();
                            String simpleName = className.substring(className.lastIndexOf(".")+1);
                            dao.setParentName("parentDao");
                            String targetId = className.substring(0,className.length()-3)+"#1";
                            GenericBeanDefinition target = new GenericBeanDefinition();
                            target.setParentName(scanPackage.get(PARENT_DAO_NAME));
                            target.getConstructorArgumentValues().addGenericArgumentValue(simpleName.substring(0,simpleName.length()-3));
                            ((BeanDefinitionRegistry)beanFactory).registerBeanDefinition(targetId, target);
                            dao.getPropertyValues().addPropertyValue("proxyInterfaces", className);
                            dao.getPropertyValues().addPropertyValue("target",new RuntimeBeanReference(targetId));
                            ((BeanDefinitionRegistry)beanFactory).registerBeanDefinition(org.apache.commons.lang.StringUtils.uncapitalize(simpleName), dao);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
        this.metadataReaderFactory = new CachingMetadataReaderFactory(this.resourcePatternResolver);
    }

    public void setScanPackageList(List<Map<String,String>> scanPackageList) {
        this.scanPackageList = scanPackageList;
    }

    protected String resolveBasePackage(String basePackage) {
        return ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(basePackage));
    }
}

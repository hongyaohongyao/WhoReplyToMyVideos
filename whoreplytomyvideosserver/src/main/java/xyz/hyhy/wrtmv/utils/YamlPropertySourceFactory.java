package xyz.hyhy.wrtmv.utils;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.io.IOException;
import java.util.Properties;

public class YamlPropertySourceFactory implements PropertySourceFactory {

    @Override
    public PropertySource<?> createPropertySource(String s, EncodedResource encodedResource) throws IOException {
        Properties properties = loadFromYaml(encodedResource);
        String name = (s != null ? s : encodedResource.getResource().getFilename());

        return new PropertiesPropertySource(name, properties);
    }

    private Properties loadFromYaml(EncodedResource resource) {
        YamlPropertiesFactoryBean yamlPropertiesFactoryBean = new YamlPropertiesFactoryBean();
        yamlPropertiesFactoryBean.setResources(resource.getResource());
        yamlPropertiesFactoryBean.afterPropertiesSet();

        return yamlPropertiesFactoryBean.getObject();
    }
}

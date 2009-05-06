package org.impalaframework.spring.service.contribution;

import java.util.List;

import org.impalaframework.service.ServiceRegistryEntry;
import org.impalaframework.service.contribution.BaseServiceRegistryList;
import org.impalaframework.spring.service.proxy.DefaultServiceProxyFactoryCreator;
import org.impalaframework.spring.service.proxy.ServiceProxyFactoryCreator;
import org.impalaframework.spring.service.proxy.ServiceProxyFactoryCreatorAware;
import org.impalaframework.spring.service.proxy.StaticServiceReferenceProxyFactorySource;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Spring-based service registry {@link List} implementation which uses a possibly wired in 
 * {@link ServiceProxyFactoryCreator} to create a proxy for the backed object.
 * 
 * @author Phil Zoio
 */
public class ServiceRegistryList extends BaseServiceRegistryList
        implements InitializingBean, DisposableBean, BeanNameAware, ServiceProxyFactoryCreatorAware {

    private ServiceProxyFactoryCreator proxyFactoryCreator;
    
    private String beanName;
    
    public void afterPropertiesSet() throws Exception {
        this.init();
    }
    
    @Override
    public void init() {
        if (this.proxyFactoryCreator == null) {
            this.proxyFactoryCreator = new DefaultServiceProxyFactoryCreator();
        }
        super.init();
    }

    protected Object maybeGetProxy(ServiceRegistryEntry reference) {
        final StaticServiceReferenceProxyFactorySource proxyFactorySource = new StaticServiceReferenceProxyFactorySource(getProxyTypes(), reference);
        final ProxyFactory proxyFactory = this.proxyFactoryCreator.createProxyFactory(proxyFactorySource, beanName);
        return proxyFactory.getProxy();
    }

    /* ******************** ServiceProxyFactoryCreatorAware implementation ******************** */
    
    public void setServiceProxyFactoryCreator(ServiceProxyFactoryCreator serviceProxyFactoryCreator) {
        if (this.proxyFactoryCreator == null) {
            this.proxyFactoryCreator = serviceProxyFactoryCreator;
        }
    }
    
    /* ******************** BeanNameAware implementation ******************** */
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }
    
    /* ******************** Injection setters ******************** */

    /**
     * Sets the proxy types for the {@link ServiceRegistryList}. Simply delegates call
     * to superclass's {@link #setProxyTypes(Class[])} method. Allows both 
     * supportedTypes and proxyTypes to be used in populating this bean
     */
    public void setProxyTypes(Class<?>[] proxyTypes) {
        super.setProxyTypes(proxyTypes);
    }
    
}

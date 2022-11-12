package org.simpleframework.core;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.simpleframework.core.annotation.Component;
import org.simpleframework.core.annotation.Controller;
import org.simpleframework.core.annotation.Repository;
import org.simpleframework.core.annotation.Service;
import org.simpleframework.util.ClassUtil;
import org.simpleframework.util.ValidationUtil;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@NoArgsConstructor
public class BeanContainer {
    /**
     * 存放所有bean
     */
    private final Map<Class<?>, Object> beanMap = new ConcurrentHashMap<>();

    /**
     * 标识bean的注解
     */
    private final List<Class<? extends Annotation>> BEAN_ANNOTATIONS = Arrays.asList(Controller.class, Service.class, Component.class, Repository.class);

    private boolean loaded = false;

    public boolean isLoaded() {
        return loaded;
    }

    public static BeanContainer getInstance() {
        return ContainerHolder.HOLDER.instance;
    }

    private enum ContainerHolder {
        HOLDER;
        private BeanContainer instance;

        ContainerHolder() {
            this.instance = new BeanContainer();
        }
    }

    /**
     * 扫描加载所有bean
     */
    public synchronized void loadBeans(String packageName) {
        if (isLoaded()) {
            log.warn("BeanContainer has bean loaded");
            return;
        }
        Set<Class<?>> classes = ClassUtil.extractPackageClass(packageName);
        if (ValidationUtil.isEmpty(classes)) {
            log.error("extract nothing from packageName" + packageName);
            return;
        }
        for (Class<?> clazz : classes) {
            for (Class<? extends Annotation> bean_annotation : BEAN_ANNOTATIONS) {
                if (clazz.isAnnotationPresent(bean_annotation)) {
                    beanMap.put(clazz, ClassUtil.newInstance(clazz, true));
                }
            }
        }
        loaded = true;
    }

    public int size() {
        return beanMap.size();
    }

    /**
     * 添加一个bean实例
     *
     * @param clazz
     * @param bean
     * @return
     */
    public Object addBean(Class<?> clazz, Object bean) {
        return beanMap.put(clazz, bean);
    }

    /**
     * 移除一个bean实例
     *
     * @param clazz
     * @return
     */
    public Object removeBean(Class<?> clazz) {
        return beanMap.remove(clazz);
    }

    /**
     * 根据class对象获取bean实例
     */
    public Object getBean(Class<?> clazz) {
        return beanMap.get(clazz);
    }

    /**
     * 根据class获取
     */
    public Set<Class<?>> getClasses() {
        return beanMap.keySet();
    }

    public Set<Object> getBeans() {
        return new HashSet<>(beanMap.values());
    }

    /**
     * 根据注解筛选出所有Bean的Class集合
     *
     * @return
     */
    public Set<Class<?>> getClassesByAnnotation(Class<? extends Annotation> annotation) {
        Set<Class<?>> classes = getClasses();
        if (ValidationUtil.isEmpty(classes)) {
            log.error("nothing in beanMap");
            return null;
        }
        Set<Class<?>> set = classes.stream().filter(e -> e.isAnnotationPresent(annotation)).collect(Collectors.toSet());
        return set.isEmpty() ? null : set;
    }

    /**
     * 通过接口或者父类获取实现类
     */
    public Set<Class<?>> getClassesBySuper(Class<?> interfaceOrClass) {
        Set<Class<?>> classes = getClasses();
        if (ValidationUtil.isEmpty(classes)) {
            log.error("nothing in beanMap");
            return null;
        }
        Set<Class<?>> set = classes.stream().filter(clazz -> interfaceOrClass.isAssignableFrom(clazz) && !clazz.equals(interfaceOrClass)).collect(Collectors.toSet());
        return set.isEmpty() ? null : set;
    }
}

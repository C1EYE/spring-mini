package org.simpleframework.inject.annotation;

import lombok.extern.slf4j.Slf4j;
import org.simpleframework.core.BeanContainer;
import org.simpleframework.util.ClassUtil;
import org.simpleframework.util.ValidationUtil;

import java.lang.reflect.Field;
import java.util.Set;

@Slf4j
public class DependencyInjector {
    private BeanContainer beanContainer;

    public DependencyInjector() {
        this.beanContainer = BeanContainer.getInstance();
    }

    /**
     * 执行IOC
     */
    public void doIOC() {
        // 1.遍历容器中的所有Class对象
        Set<Class<?>> classes = beanContainer.getClasses();
        if (classes.isEmpty()) {
            log.warn("empty classes");
            return;
        }
        // 2.遍历Class对象的所有成员变量
        for (Class<?> clazz : classes) {
            Field[] fields = clazz.getDeclaredFields();
            if (ValidationUtil.isEmpty(fields)) {
                continue;
            }
            for (Field field : fields) {
                // 3. 找出被Autowired标记的变量
                if (field.isAnnotationPresent(Autowired.class)) {
                    Autowired annotation = field.getAnnotation(Autowired.class);
                    String autowiredValue = annotation.value();
                    // 4.获取这些成员变量的类型
                    Class<?> type = field.getType();
                    // 5.获取这些成员变量容器中对应的实例
                    Object fieldValue = getFileInstance(type, autowiredValue);
                    if (fieldValue == null) {
                        throw new RuntimeException("unable to inject relevant type,target field: " + type.getName());
                    } else {
                        // 6.反射注入
                        Object targetBean = beanContainer.getBean(clazz);
                        ClassUtil.setField(field, targetBean, fieldValue, true);

                    }
                }
            }
        }
    }

    /**
     * 根据class获取bean实例
     *
     * @param type
     * @param autowiredValue
     * @return
     */
    private Object getFileInstance(Class<?> type, String autowiredValue) {
        Object fieldValue = beanContainer.getBean(type);
        if (fieldValue != null) {
            return fieldValue;
        } else {
            Class<?> implementedClass = getImplementedClass(type, autowiredValue);
            if (implementedClass != null) {
                return beanContainer.getBean(implementedClass);
            } else {
                return null;
            }
        }
    }

    private Class<?> getImplementedClass(Class<?> type, String autowiredValue) {
        Set<Class<?>> classSet = beanContainer.getClassesBySuper(type);
        if (ValidationUtil.isEmpty(classSet)) {
            log.error("No cast bean found!");
            throw new RuntimeException();
        }
        if (ValidationUtil.isEmpty(autowiredValue)) {
            if (classSet.size() == 1) {
                return classSet.iterator().next();
            } else {
                log.error("multiple implemented classes for" + type.getName() + "please set value to pick one");
                throw new RuntimeException();
            }
        } else {
            for (Class<?> clazz : classSet) {
                if (autowiredValue.equals(clazz.getSimpleName())) {
                    return clazz;
                }
            }
        }
        return null;
    }

}

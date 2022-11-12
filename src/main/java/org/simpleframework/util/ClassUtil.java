package org.simpleframework.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class ClassUtil {

    public static final String FILE_PROTOCOL = "file";

    /**
     * 根据包名扫描包下类集合
     *
     * @param packageName
     * @return 类集合
     */
    public static Set<Class<?>> extractPackageClass(String packageName) {
        // 1.获取类的加载器
        ClassLoader classLoader = getClassLoader();
        // 2.类加载器加载资源
        URL url = classLoader.getResource(packageName.replace(".", File.separator));
        // 3.依据资源不同的类型，选择不同的方式获取资源的集合
        if (url == null) {
            log.warn("unable to retrieve anything from package" + packageName);
            return null;
        }
        Set<Class<?>> classSet = null;
        // 过滤文件类型
        if (url.getProtocol().equalsIgnoreCase(FILE_PROTOCOL)) {
            classSet = new HashSet<>();
            File packageDirectory = new File(url.getPath());
            extractClassFile(classSet, packageDirectory, packageName);
        }
        //TODO 对其他类型文件的处理
        return classSet;
    }

    /**
     * 递归获取目标package下的所有类文件
     *
     * @param classSet
     * @param fileSource
     * @param packageName
     */
    private static void extractClassFile(Set<Class<?>> classSet, File fileSource, String packageName) {
        if (!fileSource.isDirectory()) {
            return;
        }
        File[] files = fileSource.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.isDirectory()) {
                    return true;
                } else if (file.isFile()) {
                    // 获取绝对路径
                    File absoluteFile = file.getAbsoluteFile();
                    if (absoluteFile.getPath().endsWith(".class")) {
                        addToClassSet(absoluteFile.getPath());
                    }
                }
                return false;
            }

            /**
             * 根据绝对路径加载类文件到set
             * @param absolutePath
             */
            private void addToClassSet(String absolutePath) {
                // 1.提取出包含package的类名
                // 例如：/User/xxx/xxx/com/springframework/a.class -> com.springframework.a
                absolutePath = absolutePath.replace(File.separator, ".");
                String className = absolutePath.substring(absolutePath.indexOf(packageName));
                className = className.substring(0, className.lastIndexOf("."));
                // 2.反射获取对应的Class对象到classSet
                Class<?> targetClass = loadClass(className);
                classSet.add(targetClass);
            }
        });


        if (null != files) {
            for (File file : files) {
                extractClassFile(classSet, file, packageName);
            }
        }

    }

    private static Class<?> loadClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            log.error("load class error");
            throw new RuntimeException();
        }
    }


    private static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    public static <T> T newInstance(Class<?> clazz, boolean accessible) {
        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(accessible);
            return (T) constructor.newInstance();
        } catch (Exception e) {
            log.error("newInstance error", e);
            throw new RuntimeException();
        }
    }

    /**
     * 设置类的属性值
     */
    public static void setField(Field field, Object target, Object value, boolean accessible) {
        field.setAccessible(accessible);
        try {
            field.set(target, value);
        } catch (IllegalAccessException e) {
            log.error("fail to inject value");
            throw new RuntimeException();
        }
    }


}

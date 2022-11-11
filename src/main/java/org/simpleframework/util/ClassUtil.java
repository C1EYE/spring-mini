package org.simpleframework.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileFilter;
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
        URL url = classLoader.getResource(packageName.replace(".", "/"));
        // 3.依据资源不同的类型，选择不同的方式获取资源的集合
        if (url == null) {
            log.warn("unable to retrieve anything from package" + packageName);
            return null;
        }
        Set<Class<?>> classSet;
        // 过滤文件类型
        if (url.getProtocol().equalsIgnoreCase(FILE_PROTOCOL)) {
            classSet = new HashSet<>();
            File packageDirectory = new File(url.getPath());
            extractClassFile(classSet, packageDirectory, packageName);
        }
        return null;
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
                        addToClassSet(absoluteFile);
                    }
                }
                return false;
            }

            private void addToClassSet(File absoluteFile) {
//                classSet.add();
                System.out.println(absoluteFile.getName());
            }
        });

        if (null != files) {
            for (File file : files) {
                extractClassFile(classSet, file, packageName);
            }
        }

    }


    private static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    public static void main(String[] args) {
        extractPackageClass("org.simpleframework");
    }
}

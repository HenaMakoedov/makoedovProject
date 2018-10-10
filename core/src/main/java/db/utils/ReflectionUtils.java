package db.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by Makoiedov.H on 12/13/2017.
 */
public class ReflectionUtils {
    static class PackageInfo {
        private final String name;
        private final Package _package;
        private final boolean annotated;
        private final Set<String> classes;

        PackageInfo(String packageName) {
            name = packageName;
            try {
                Class.forName(packageName + ".package-info");
            } catch (ClassNotFoundException e) {
                // Intentionally left blank
            }
            _package = Package.getPackage(packageName);
            annotated = _package != null && _package.getAnnotations().length > 0;
            classes = new HashSet<>();
        }

        /*package*/
        void add(String className){
            classes.add(className);
        }

        public String getName(){
            return name;
        }

        public Package getPackage() {
            return _package;
        }

        public boolean isAnnotated() {
            return annotated;
        }

        public Stream<String> getClassNames(){
            return classes.stream();
        }

        public Stream<Class> getClasses(){
            ClassLoader classLoader = getClass().getClassLoader();
            return classes.stream().map(name -> {
                try {
                    return Class.forName(name, false, classLoader);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private static final String FILE_PREFIX = "file:";
    private static final String JAR_PATH_SEPARATOR = "!/";

    public static final String ROOT_PACKAGE_NAME = "db";

    private static List<PackageInfo> packages;

    public static synchronized List<PackageInfo> getPackages() {
        if(packages == null){

            Map<String, PackageInfo> packageMap = new HashMap<>();
            scanPackages(ROOT_PACKAGE_NAME, packageMap);
            packages = new ArrayList<>(packageMap.values());

        }
        return packages;
    }

    private static void scanPackages(String rootPackage, Map<String, PackageInfo> packages) {
        // We don't trust Package.getPackages() method, so we just do everything by ourselves.
        try {
            List<String> paths = getResources(rootPackage);
            for (String resourcePath : paths) {

                if (isJarResource(resourcePath))
                    scanJarForPackages(resourcePath, rootPackage, packages);
                else
                    scanDirForPackages(resourcePath, rootPackage, packages);
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static List<String> getResources(String packageName)
            throws ClassNotFoundException, IOException {

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');

        Enumeration resources = classLoader.getResources(path);

        List<String> dirs = new ArrayList<>();

        while (resources.hasMoreElements()) {
            URL resource = (URL) resources.nextElement();
            dirs.add(URLDecoder.decode(resource.getFile(), "UTF-8"));
        }
        return dirs;
    }

    private static boolean isJarResource(String resourcePath){
        return resourcePath.startsWith(FILE_PREFIX) && resourcePath.contains(JAR_PATH_SEPARATOR);
    }

    private static void scanJarForPackages(String resourcePath, String rootPackage, Map<String, PackageInfo> packages)
            throws IOException {
        String[] split = resourcePath.split(JAR_PATH_SEPARATOR, 2);
        URL jarUrl = new URL(split[0]);
        ZipInputStream zip = new ZipInputStream(jarUrl.openStream());

        ZipEntry zipEntry;
        while ((zipEntry = zip.getNextEntry()) != null) {
            String entryName = zipEntry.getName();
            if (isCollectibleClassPath(entryName)) {
                String className = entryName.substring(0, entryName.length() - 6).replace('/', '.');
                if (!className.startsWith(rootPackage))
                    continue;

                int lastDot = className.lastIndexOf('.');
                String packageName = className.substring(0, lastDot);
                packages.computeIfAbsent(packageName, PackageInfo::new).add(className);
            }
        }
    }

    private static void scanDirForPackages(String dirPath, String rootPackage, Map<String, PackageInfo> packages){
        File rootDir = new File(dirPath);
        if(!rootDir.exists())
            return;

        Queue<File> queue = new ArrayDeque<>();
        queue.add(rootDir);
        while(!queue.isEmpty()){
            File dir = queue.remove();
            File[] dirFiles = dir.listFiles();
            if(dirFiles == null)
                continue;

            for(File file : dirFiles){
                String fileName = file.getName();
                if (file.isDirectory()) {
                    assert !fileName.contains(".");
                    scanDirForPackages(file.getPath(), rootPackage + '.' + fileName, packages);
                } else if (isCollectibleClassPath(file.getPath())) {
                    String className = rootPackage + '.' + fileName.substring(0, fileName.length() - 6);
                    packages.computeIfAbsent(rootPackage, PackageInfo::new).add(className);
                }
            }
        }
    }

    private static boolean isCollectibleClassPath(String path){
        return path.endsWith(".class") && !path.contains("$");
    }

    public static List<Class> getAnnotatedClasses(Class annotation) throws ClassNotFoundException {
        List<PackageInfo> packages = getPackages();
        List<Class> result = new ArrayList<>();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        for(PackageInfo _package : packages) {
            for(String className : _package.classes) {
                Class clazz = loader.loadClass(className);
                if(clazz.getAnnotation(annotation) != null) {
                    result.add(clazz);
                }
            }
        }
        return result;
    }
}

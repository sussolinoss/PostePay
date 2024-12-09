package dev.sussolino.postepay.utils.reflection;

import dev.sussolino.postepay.PostePay;
import dev.sussolino.postepay.utils.command.AntiSocial;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@UtilityClass
public class ReflectionUtil {

    public void register(String pack, Plugin plugin) {
        for (AntiSocial c : ReflectionUtil.getCommands(pack)) {
            String commandName = c.getClass().getSimpleName().replace("Command", "");
            PluginCommand command = Bukkit.getPluginCommand(commandName);

            if (command != null) {
                command.setExecutor(c);
                if (c instanceof TabCompleter) {
                    command.setTabCompleter((TabCompleter) c);
                }
            } else Bukkit.getLogger().severe("Command '" + commandName + "' not registred.");
        }

        for (Listener listener : getSpartanChecks(pack)) {
            Bukkit.getPluginManager().registerEvents(listener, plugin);
        }
    }

    @SneakyThrows
    public List<AntiSocial> getCommands(String packageName) {
        List<AntiSocial> commands = new ArrayList<>();
        for (Class<?> clazz : getClassesInPackage(packageName)) {
            if (clazz == null) continue;
            if (!clazz.isAnnotationPresent(Grrpow.class)) continue;
            commands.add((AntiSocial) clazz.getConstructor().newInstance());
        }
        return commands;
    }

    @SneakyThrows
    public List<Listener> getSpartanChecks(String packageName) {
        List<Listener> commands = new ArrayList<>();
        for (Class<?> clazz : getClassesInPackage(packageName)) {
            if (clazz == null) continue;
            if (!clazz.isAnnotationPresent(SpartanAC.class)) continue;
            commands.add((Listener) clazz.getConstructor().newInstance());
        }
        return commands;
    }

    public Class<?>[] getClassesInPackage(String packageName) {
        try {
            Set<String> classnames = getClassNamesFromJarFile(Paths.get(ReflectionUtil.path()).toFile());
            List<Class<?>> classes = new ArrayList<>();

            for (String classname : classnames) {
                try {
                    if (classname.startsWith(packageName)) {
                        Class<?> clazz = Class.forName(classname);
                        classes.add(clazz);
                    }
                } catch (NoClassDefFoundError | ClassNotFoundException |
                         UnsupportedClassVersionError ignored) {
                }
            }

            return classes.toArray(new Class[0]);
        } catch (Exception exception) {

            File directory = getPackageDirectory(packageName);

            if (!directory.exists()) {
                throw new IllegalArgumentException("Could not get directory resource for package " + packageName);
            }

            return getClassesInPackage(packageName, directory);
        }
    }

    public Set<String> getClassNamesFromJarFile(File givenFile) throws IOException {
        Set<String> classNames = new HashSet<>();
        try (JarFile jarFile = new JarFile(givenFile)) {
            Enumeration<JarEntry> e = jarFile.entries();
            while (e.hasMoreElements()) {
                JarEntry jarEntry = e.nextElement();
                if (jarEntry.getName().endsWith(".class")) {
                    String className = jarEntry.getName()
                            .replace("/", ".")
                            .replace(".class", "");
                    classNames.add(className);
                }
            }
            return classNames;
        }
    }

    private Class<?>[] getClassesInPackage(String packageName, File directory) {
        List<Class<?>> classes = new ArrayList<>();

        for (String filename : Objects.requireNonNull(directory.list())) {
            if (filename.endsWith(".class")) {
                String classname = buildClassname(packageName, filename);
                try {
                    classes.add(Class.forName(classname));
                } catch (ClassNotFoundException e) {
                    System.err.println("Error creating class " + classname);
                }
            } else if (!filename.contains(".")) {
                String name = packageName + (packageName.endsWith(".") ? "" : ".") + filename;
                classes.addAll(Arrays.asList(getClassesInPackage(name, getPackageDirectory(name))));
            }
        }

        return classes.toArray(new Class[0]);
    }

    public String buildClassname(String packageName, String filename) {
        return packageName + '.' + filename.replace(".class", "");
    }

    private File getPackageDirectory(String packageName) {
        ClassLoader cld = Thread.currentThread().getContextClassLoader();
        if (cld == null) {
            throw new IllegalStateException("Can't get class loader.");
        }

        URL resource = cld.getResource(packageName.replace('.', '/'));

        if (resource == null) {
            throw new RuntimeException("Package " + packageName + " not found on classpath.");
        }

        return new File(resource.getFile());
    }

    public String path() throws URISyntaxException {
        return new File(PostePay.class.getProtectionDomain().getCodeSource().getLocation()
                .toURI()).getPath();
    }
}
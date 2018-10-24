package com.theah64.mvpize;

import com.theah64.mvpize.exceptions.InvalidComponentException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MvpIze {

    private static final String CORE_NAME_REGEX = "public class (.+)(Activity|Fragment) extends";
    private static final String CLASS_NAME_REGEX = "public class (.+) extends";
    private static final String PACKAGE_NAME_REGEXP = "package (.+);";

    private static final String PRESENTER_INTERFACE_FORMAT = "package {PACKAGE_NAME};\n" +
            "\n" +
            "public interface {PRESENTER_NAME} {\n" +
            "}\n";

    private static final String VIEW_INTERFACE_FORMAT = "package {PACKAGE_NAME};\n" +
            "\n" +
            "public interface {VIEW_NAME} {\n" +
            "}\n";

    /*private static final String PRESENTER_IMPL_FORMAT = "package {PACKAGE_NAME};\n" +
            "\n" +
            "public class {PRESENTER_IMPL_NAME} implements {PRESENTER_NAME} {\n" +
            "}\n";*/

    private static final String PRESENTER_IMPL_FORMAT = "package {PACKAGE_NAME};\n" +
            "\n" +
            "public class {PRESENTER_IMPL_NAME} implements {PRESENTER_NAME} {\n" +
            "    private final {VIEW_NAME} view;\n" +
            "\n" +
            "    public ChannelsPresenterImpl({VIEW_NAME} view) {\n" +
            "        this.view = view;\n" +
            "    }\n" +
            "}\n";

    private static final String IS_AUTO_INTEGRATABLE_REGEX = "public class .+(Fragment|Activity) extends .+ \\{";


    private final String fileContent;

    public MvpIze(String fileContent) {
        this.fileContent = fileContent;
    }

    public static void createPresenter(String packageName, String presenterName) throws IOException {

        final String presenterContent = PRESENTER_INTERFACE_FORMAT.replace("{PACKAGE_NAME}", packageName)
                .replace("{PRESENTER_NAME}", presenterName);

        createJavaFile(presenterName, presenterContent);
    }

    public static void createJavaFile(String fileName, String content) throws IOException {
        final String dir = System.getProperty("user.dir");
        final BufferedWriter bw = new BufferedWriter(new FileWriter(dir + File.separator + fileName + ".java"));
        bw.write(content);
        bw.flush();
        bw.close();
    }

    public static void createPresenterImpl(String packageName, String viewName, String presenterName, String presenterImplName) throws IOException {
        final String presenterImplContent = PRESENTER_IMPL_FORMAT
                .replaceAll("\\{PACKAGE_NAME}", packageName)
                .replaceAll("\\{PRESENTER_IMPL_NAME}", presenterImplName)
                .replaceAll("\\{VIEW_NAME}", viewName)
                .replaceAll("\\{PRESENTER_NAME}", presenterName);

        createJavaFile(presenterImplName, presenterImplContent);
    }

    public static void createView(String packageName, String viewName) throws IOException {
        final String presenterContent = VIEW_INTERFACE_FORMAT.replace("{PACKAGE_NAME}", packageName)
                .replace("{VIEW_NAME}", viewName);

        createJavaFile(viewName, presenterContent);
    }

    public String getCoreName() throws InvalidComponentException {
        final Matcher matcher = Pattern.compile(CORE_NAME_REGEX).matcher(fileContent);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new InvalidComponentException("Component name should end with either Activity or Fragment.");
    }

    public String getPackageName() throws InvalidComponentException {
        final Matcher matcher = Pattern.compile(PACKAGE_NAME_REGEXP).matcher(fileContent);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new InvalidComponentException("Component should have a package name");
    }

    public boolean isAutoIntegratable() {
        return Pattern.compile(IS_AUTO_INTEGRATABLE_REGEX).matcher(fileContent).find();
    }

    public String getClassName() throws InvalidComponentException {
        final Matcher matcher = Pattern.compile(CLASS_NAME_REGEX).matcher(fileContent);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new InvalidComponentException("Couldn't find class name");
    }


    public String getNewComponentContent(String viewName, String presenterName, String presenterImplName) {
        final StringBuilder sb = new StringBuilder();
        final String[] lines = fileContent.split("\n");
        for (String line : lines) {
            if (line.contains("extends")) {
                // it's header
                final String[] lineChunks = line.split("\\{");
                line = lineChunks[0] + "implements " + viewName + " {";
                line += String.format("\n\n    private %s presenter;", presenterName);
            }

            if (line.contains("setContentView")) {
                line += String.format("\n\n\t\tpresenter = new %s(this);\n", presenterImplName);
            }

            sb.append(line).append("\n");
        }
        return sb.toString();
    }
}

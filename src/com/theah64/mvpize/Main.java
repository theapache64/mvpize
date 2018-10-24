package com.theah64.mvpize;

import com.theah64.mvpize.exceptions.InvalidComponentException;
import com.theah64.mvpize.utils.FileUtils;

import java.io.File;
import java.io.IOException;

public class Main {


    public static void main(String[] args) {

        if (args.length == 2) {

            // write your code here
            final String fileName = args[1];
            final String curDir = System.getProperty("user.dir");

            final File file = new File(curDir + File.separator + fileName);
            if (file.exists()) {

                try {
                    final String fileContent = FileUtils.read(file);
                    final MvpIze mvpIze = new MvpIze(fileContent);

                    final String coreName = mvpIze.getCoreName();
                    final String packageName = mvpIze.getPackageName();

                    System.out.printf("Component name is %s\n", coreName);

                    final String presenterName = coreName + "Presenter";
                    final String presenterImplName = presenterName + "Impl";
                    final String viewName = coreName + "View";

                    MvpIze.createPresenter(packageName, presenterName);
                    MvpIze.createPresenterImpl(packageName, viewName, presenterName, presenterImplName);
                    MvpIze.createView(packageName, viewName);

                    System.out.println("MVP components created");

                    final boolean isAutoIntegratable = mvpIze.isAutoIntegratable();
                    if (isAutoIntegratable) {

                        System.out.println(isAutoIntegratable);

                        final String newComponentContent = mvpIze.getNewComponentContent(
                                viewName,
                                presenterName,
                                presenterImplName
                        );

                        MvpIze.createJavaFile(mvpIze.getClassName() + ".new", newComponentContent);

                        System.out.println("All done");

                    } else {
                        System.out.println("Manual edits found in the components, auto integration failed");
                    }


                } catch (IOException | InvalidComponentException e) {
                    error(e.getMessage());
                }

            } else {
                error("File not found :" + file.getAbsolutePath());
            }

        } else {
            error("File name not passed");
        }

    }

    private static void error(String message) {
        System.out.printf("ERROR: %s", message);
    }
}

package com.zeus

import com.android.build.gradle.TestedExtension
import javassist.ClassPool
import javassist.CtClass
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile

/**
 * Created by tianyang on 16/7/28.
 */
public class KeepFilter {

    private Project project

    public KeepFilter(Project project) {
        this.project = project;
    }

    public void filter(File inputDir, JavaCompile javaCompile) {
        ClassPool classes = new ClassPool(true)
        TestedExtension android = project.extensions.findByName("android")
        classes.appendClassPath(inputDir.getAbsolutePath());
        //def androidJar = android.getSdkDirectory().absolutePath + "/platforms/" + android.getCompileSdkVersion() + "/android.jar"

        android.bootClasspath.each {
            classes.appendClassPath(it.absolutePath)
        }

        javaCompile.classpath.each {
            classes.appendClassPath(it.absolutePath)
        }

        inputDir.eachFileRecurse {
            if (!it.isDirectory() && it.absolutePath.endsWith(".class")) {
                String path = it.absolutePath.substring(inputDir.absolutePath.length() + 1, it.absolutePath.length() - 6)
                String clsName = path.replaceAll("/", ".");

                CtClass ctcls = classes.getCtClass(clsName);

                if (ctcls.isFrozen()) {
                    ctcls.defrost()
                }

                boolean remove = !ctcls.hasAnnotation(Keep.class)

                if (remove) {
                    it.delete()
                } else {
                    println("Zeus keep:" + clsName)
                }

            }
        }
    }
}

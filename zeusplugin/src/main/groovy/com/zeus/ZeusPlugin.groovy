package com.zeus

import com.android.build.api.transform.Format
import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.TestedExtension
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.dsl.SigningConfig
import com.android.build.gradle.internal.pipeline.IntermediateFolderUtils
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.build.gradle.internal.pipeline.TransformTask
import com.android.build.gradle.tasks.ZipAlign
import org.gradle.api.DomainObjectSet
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.bundling.Zip
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.tasks.Jar

public class ZeusPlugin implements Plugin<Project> {

    TestedExtension android;
    private static final String PATCH_VARIANT = "patch"


    @Override
    void apply(Project project) {

        android = project.extensions.findByName("android")
        prepareVariant(project)

    }

    private void prepareVariant(Project project) {

        SigningConfig sign = android.signingConfigs.find {
            it.name.equals("debug")
        }

        android.buildTypes {
            patch {
                signingConfig sign
            }
        }

        project.afterEvaluate {

            DomainObjectSet<BaseVariant> variants

            if (android instanceof AppExtension) {
                AppExtension appExtension = android
                variants = appExtension.applicationVariants
            } else if (android instanceof LibraryExtension) {
                LibraryExtension libraryExtension = android
                variants = libraryExtension.libraryVariants
            }

            variants.each {
                def varName = it.name
                if (it.name.equals(PATCH_VARIANT)) {
                    JavaCompile compile = it.javaCompiler
                    TransformTask task = project.tasks.findByName('transformClassesWithDexFor' + it.name.capitalize())
                    if (task != null) {
                        task.doFirst {
                            it.inputs.files.files.each {

                                File mainJar = IntermediateFolderUtils.getContentLocation(it, "main", TransformManager.CONTENT_JARS, TransformManager.SCOPE_FULL_PROJECT, Format.JAR)
                                println("Zeus find jar:"+it.absolutePath+"--->"+mainJar.absolutePath)
                                if (mainJar.exists()) {
                                    File outputDir = new File(project.buildDir, it.name)
                                    if (!outputDir.exists()) {
                                        outputDir.mkdirs()
                                    }

                                    project.copy {
                                        from project.zipTree(mainJar)
                                        into outputDir
                                    }

                                    new KeepFilter(project).filter(outputDir, compile)

                                    Jar jar = project.task('mJar', type: Jar)
                                    jar.manifest = null
                                    jar.archiveName = "main.jar"
                                    jar.destinationDir = mainJar.parentFile
                                    jar.from(outputDir)
                                    jar.execute()

                                    project.delete(outputDir)
                                }
                            }
                        }
                    }

                    def zipalignTask = project.tasks.findByName('zipalign' + it.name.capitalize())
                    if (zipalignTask != null) {
                        zipalignTask.doFirst() {
                            ZipAlign zipAlign = it
                            zipAlign.setOutputFile(new File(zipAlign.outputFile.parentFile, "patch.apk"))
                            it.inputs.files.files.each {
                                if (it.name.endsWith(".apk")) {
                                    println("Zeus transfer zipalign:"+it.absolutePath)
                                    File outputDir = new File(project.buildDir, varName)
                                    if (!outputDir.exists()) {
                                        outputDir.mkdirs()
                                    }

                                    Zip zip = project.task('mZip', type: Zip)
                                    zip.destinationDir = outputDir
                                    zip.archiveName = "patch.apk"
                                    zip.from(project.zipTree(it))
                                    zip.include('classes.dex', 'META-INF/*')
                                    zip.execute()


                                    Copy cp = project.task('mCp', type: Copy)
                                    cp.from(new File(outputDir, "patch.apk"))
                                    cp.destinationDir = it.parentFile
                                    cp.rename('patch.apk', it.name)
                                    cp.execute()

                                    project.delete(outputDir)
                                }
                            }
                        }
                    }
                }
            }


        }
    }
}



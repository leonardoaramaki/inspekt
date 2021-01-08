package inspekt

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class CompilerPluginTest {
    @Test
    fun `should generate getAccessibilityClassName() method inside subclasses of View `() {
        val sourceDep1 = SourceFile.kotlin("View.kt", """
            package android.view
            
            open class View
        """)
        val sourceDep2 = SourceFile.kotlin("FlexboxLayout.kt", """
            package com.google.android.flexbox
            
            open class FlexboxLayout : android.view.View()
        """)
        val testSource = SourceFile.kotlin(
                "TestClass.kt", """
            package com.example
            
            interface Interface1
            interface Interface2
            class ColumnLayout : android.view.View(), Interface1, Interface2
            class RowLayout : com.google.android.flexbox.FlexboxLayout()
        """
        )
        val result = KotlinCompilation().apply {
            sources = listOf(sourceDep1, sourceDep2, testSource)
            compilerPlugins = listOf(InspektComponentRegistrar())
            inheritClassPath = true
            noOptimize = true
            verbose = false
            messageOutputStream = System.out
        }.compile()

        expectThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)
        val classloader = result.classLoader
        val columnLayoutClass = classloader.loadClass("com.example.ColumnLayout")
        val rowLayoutClass = classloader.loadClass("com.example.RowLayout")
        val method1 = assertDoesNotThrow { columnLayoutClass.getDeclaredMethod(METHOD_NAME) }
        val method2 = assertDoesNotThrow { rowLayoutClass.getDeclaredMethod(METHOD_NAME) }
        val columnLayout = columnLayoutClass.newInstance()
        val rowLayout = rowLayoutClass.newInstance()
        expectThat(method1.invoke(columnLayout)).isEqualTo("com.example.ColumnLayout")
        expectThat(method2.invoke(rowLayout)).isEqualTo("com.example.RowLayout")
    }

    companion object {
        const val METHOD_NAME = "getAccessibilityClassName"
    }
}
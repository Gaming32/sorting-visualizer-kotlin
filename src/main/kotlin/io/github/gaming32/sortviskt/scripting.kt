package io.github.gaming32.sortviskt

import java.io.File
import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.*
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost

private object SortCompilationConfiguration : ScriptCompilationConfiguration({
    baseClass(SortScript::class)
    jvm {
        dependenciesFromCurrentContext(wholeClasspath = true)
        defaultImports(VisualList::class)
    }
    ide {
        acceptedLocations(ScriptAcceptedLocation.Everywhere)
    }
})

@KotlinScript(
    fileExtension = "sort.kts",
    compilationConfiguration = SortCompilationConfiguration::class
)
abstract class SortScript : Sort

fun evalFile(scriptFile: File) =
    BasicJvmScriptingHost().eval(scriptFile.toScriptSource(), SortCompilationConfiguration, null)

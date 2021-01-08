package inspekt

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey

@AutoService(CommandLineProcessor::class)
class InspektCommandLineProcessor : CommandLineProcessor {
    override val pluginId: String = "inspekt"

    override val pluginOptions: Collection<CliOption> = listOf(
        CliOption(
            optionName = "enabled", valueDescription = "<true|false>",
            description = "whether to enable the inspekt plugin or not"
        )
    )

    override fun processOption(option: AbstractCliOption, value: String, configuration: CompilerConfiguration)
        = when (option.optionName) {
            "enabled" -> configuration.put(KEY_ENABLED, value.toBoolean())
            else -> error("Unexpected config option ${option.optionName}")
        }
}

val KEY_ENABLED = CompilerConfigurationKey<Boolean>("whether the plugin is enabled")
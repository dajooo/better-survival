package de.dajooo.bettersurvival.commands

import de.dajooo.bettersurvival.commands.suggestions.SuggestFeatures
import de.dajooo.bettersurvival.feature.FeatureRegistry
import de.dajooo.kommons.koin.getKoin
import de.dajooo.kommons.koin.withKoin
import revxrsal.commands.autocomplete.SuggestionProvider
import revxrsal.commands.bukkit.BukkitLamp
import revxrsal.commands.ktx.SuspendFunctionsSupport

fun registerCommands(): Unit = withKoin {
    val featureRegistry by inject<FeatureRegistry>()

    val lamp = BukkitLamp.builder(getKoin().get())
        .accept(SuspendFunctionsSupport)
        .suggestionProviders{ providers ->
            providers.addProviderForAnnotation(SuggestFeatures::class.java) { _ ->
                SuggestionProvider { _ ->
                    featureRegistry.map { it.name }
                }
            }
        }
        .build();

    lamp.register(FeaturesCommand)
}
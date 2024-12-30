package de.dajooo.bettersurvival.commands

import de.dajooo.bettersurvival.commands.suggestions.SuggestFeatures
import de.dajooo.bettersurvival.commands.suggestions.SuggestHomes
import de.dajooo.bettersurvival.database.model.Home
import de.dajooo.bettersurvival.database.model.Homes
import de.dajooo.bettersurvival.feature.FeatureRegistry
import de.dajooo.kommons.koin.getKoin
import de.dajooo.kommons.koin.withKoin
import kotlinx.coroutines.*
import kotlinx.coroutines.future.asCompletableFuture
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import revxrsal.commands.Lamp
import revxrsal.commands.autocomplete.SuggestionProvider
import revxrsal.commands.bukkit.BukkitLamp
import revxrsal.commands.bukkit.actor.BukkitCommandActor
import revxrsal.commands.ktx.SuspendFunctionsSupport
import kotlin.coroutines.CoroutineContext

object CommandScope : CoroutineScope {
    override val coroutineContext = Dispatchers.Default + SupervisorJob()
}

fun registerCommands(): Lamp<BukkitCommandActor> = withKoin {
    val featureRegistry by inject<FeatureRegistry>()

    val lamp = BukkitLamp.builder(getKoin().get())
        .accept(SuspendFunctionsSupport)
        .suggestionProviders{ providers ->
            providers.addProviderForAnnotation(SuggestFeatures::class.java) { _ ->
                SuggestionProvider { _ ->
                    featureRegistry.map { it.name }
                }
            }
            providers.addProviderForAnnotation(SuggestHomes::class.java) { _ ->
                SuggestionProvider.fromAsync { context ->
                    CommandScope.async { newSuspendedTransaction { Home.find(Homes.player.eq(context.actor().uniqueId())).map { it.name } } }.asCompletableFuture()
                }
            }
        }
        .build();

    lamp.register(FeaturesCommand)

    return@withKoin lamp
}
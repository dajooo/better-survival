package de.dajooo.bettersurvival.util.extensions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.future.asCompletableFuture
import revxrsal.commands.autocomplete.SuggestionProvider
import revxrsal.commands.autocomplete.SuggestionProviders
import revxrsal.commands.bukkit.actor.BukkitCommandActor
import revxrsal.commands.node.ExecutionContext

object CommandSuggestionScope : CoroutineScope {
    override val coroutineContext = Dispatchers.Default + SupervisorJob()
}

inline fun <reified T : Annotation> SuggestionProviders.Builder<BukkitCommandActor>.addProviderForAnnotation(
    crossinline function: (T, ExecutionContext<BukkitCommandActor>) -> Collection<String>
) =
    addProviderForAnnotation(T::class.java) { annotation ->
        SuggestionProvider { context ->
            function(annotation, context)
        }
    }

inline fun <reified T : Annotation> SuggestionProviders.Builder<BukkitCommandActor>.addSuspendingProviderForAnnotation(
    crossinline function: suspend (T, ExecutionContext<BukkitCommandActor>) -> Collection<String>
) =
    addProviderForAnnotation(T::class.java) { annotation ->
        SuggestionProvider.fromAsync { context ->
            CommandSuggestionScope.async { function(annotation, context) }.asCompletableFuture()
        }
    }
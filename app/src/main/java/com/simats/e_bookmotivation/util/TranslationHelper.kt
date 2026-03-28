package com.simats.e_bookmotivation.util

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

data class LanguageOption(
    val name: String,
    val code: String
)

object TranslationHelper {

    /** Popular languages for the app */
    val supportedLanguages: List<LanguageOption> = listOf(
        LanguageOption("Tamil", TranslateLanguage.TAMIL),
        LanguageOption("Hindi", TranslateLanguage.HINDI),
        LanguageOption("Telugu", TranslateLanguage.TELUGU),
        LanguageOption("Kannada", TranslateLanguage.KANNADA),
        LanguageOption("Bengali", TranslateLanguage.BENGALI),
        LanguageOption("Marathi", TranslateLanguage.MARATHI),
        LanguageOption("Gujarati", TranslateLanguage.GUJARATI),
        LanguageOption("Spanish", TranslateLanguage.SPANISH),
        LanguageOption("French", TranslateLanguage.FRENCH),
        LanguageOption("German", TranslateLanguage.GERMAN),
        LanguageOption("Japanese", TranslateLanguage.JAPANESE),
        LanguageOption("Korean", TranslateLanguage.KOREAN),
        LanguageOption("Chinese", TranslateLanguage.CHINESE),
        LanguageOption("Arabic", TranslateLanguage.ARABIC),
        LanguageOption("Portuguese", TranslateLanguage.PORTUGUESE),
        LanguageOption("Russian", TranslateLanguage.RUSSIAN),
        LanguageOption("Urdu", TranslateLanguage.URDU)
    )

    /**
     * Translates [text] from English to [targetLangCode].
     * Automatically downloads the language model if not already cached (~30MB).
     * Returns the translated text.
     */
    suspend fun translate(text: String, targetLangCode: String): String {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(targetLangCode)
            .build()
        val translator = Translation.getClient(options)

        try {
            // Ensure model is downloaded (wifi not required for better UX)
            suspendCancellableCoroutine { cont ->
                val conditions = DownloadConditions.Builder().build()
                translator.downloadModelIfNeeded(conditions)
                    .addOnSuccessListener { cont.resume(Unit) }
                    .addOnFailureListener { cont.resumeWithException(it) }
            }

            // Perform translation
            return suspendCancellableCoroutine { cont ->
                translator.translate(text)
                    .addOnSuccessListener { translated -> cont.resume(translated) }
                    .addOnFailureListener { cont.resumeWithException(it) }
            }
        } finally {
            translator.close()
        }
    }
}

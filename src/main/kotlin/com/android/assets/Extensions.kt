package com.android.assets

/** Characters that terminate an asset name. */
private const val ASSET_NAME_TERMINATING_PATTERN = "[^a-z0-9_]"
/** Regex for a valid asset name. */
private val ASSET_NAME_REGEX = Regex("[a-z0-9_]*")
/**
 * Pattern for valid replacements.
 *
 * Don't replace usages when that usage contains the desired asset name as a substring. For example, when replacing
 * `icn_person`, we don't want to also replace `icn_person_2` or `blue_icn_person` (because they are different assets).
 */
private const val REPLACEMENT_REGEX = "($ASSET_NAME_TERMINATING_PATTERN)%s($ASSET_NAME_TERMINATING_PATTERN)"

fun String.isValidAssetName(): Boolean {
    return matches(ASSET_NAME_REGEX)
}

fun String.containsAssetName(name: String): Boolean {
    return contains(Regex(REPLACEMENT_REGEX.format(name)))
}

fun String.replaceAssetName(old: String, new: String): String {
    return replace(Regex(REPLACEMENT_REGEX.format(old)), "$1$new$2")
}
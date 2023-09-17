package utils.extensions

import com.google.common.cache.Cache

operator fun <K, V> Cache<K, V>.set(key: K & Any, value: V & Any) =
    put(key, value)

operator fun <K, V> Cache<K, V>.get(key: K & Any): V? =
    getIfPresent(key)
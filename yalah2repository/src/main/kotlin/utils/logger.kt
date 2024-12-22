package utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Provides a logger for the current receiver.
 * Calling this function repeatedly will create new loggers every time.
 */
fun Any.logger(): Logger = LoggerFactory.getLogger(this::class.java)
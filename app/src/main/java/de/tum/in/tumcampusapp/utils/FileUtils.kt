package de.tum.`in`.tumcampusapp.utils

import java.io.File

/**
 * Utility functions to ease the work with files and file contents.
 */
object FileUtils {

    /**
     * Delete all files and folder contained in a folder
     */
    fun deleteRecursive(fileOrDirectory: File) {
        if (!fileOrDirectory.isDirectory) {
            fileOrDirectory.delete()
        }

        // When current item is a dir, then we need to delete all files inside
        fileOrDirectory
                .listFiles()
                .forEach(this::deleteRecursive)
    }

    /**
     * Read a file directly to a string
     * Fails silently
     */
    fun getStringFromFile(filePath: String) = File(filePath).readText()
}// FileUtils is a Utility class

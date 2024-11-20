package ru.smalljinn.domain.saving

interface FileNameGenerator {
    fun constructImageFileName(): String
}
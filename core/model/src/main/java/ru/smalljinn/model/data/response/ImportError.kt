package ru.smalljinn.model.data.response

enum class ImportError: RootError {
    FILE_NOT_FOUND,
    NOT_BACKUP_FILE,
    UNKNOWN
}
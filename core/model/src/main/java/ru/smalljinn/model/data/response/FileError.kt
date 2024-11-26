package ru.smalljinn.model.data.response

enum class FileError: RootError {
    FILE_NOT_FOUND,
    NO_PERMISSIONS,
    FILE_NOT_WRITTEN,
    FILE_NOT_READIED,
    FILE_NOT_DELETED,
    DECODE_ERROR,
    UNKNOWN
}
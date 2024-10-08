package ru.smalljinn.model.data.response

enum class PhotoError: Error {
    EMPTY_URIS,
    FILE_NOT_FOUND,
    HAVE_NOT_ACCESS,
    DECODE_FAILED,
    UNKNOWN
}
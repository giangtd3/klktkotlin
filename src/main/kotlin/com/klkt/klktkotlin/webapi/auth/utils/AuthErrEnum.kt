package com.klkt.klktkotlin.webapi.auth.utils

enum class AuthErrEnum {
    ACCEPT_DENIED,
    DEFAULT_EXCEPTION,

    TRUST_AUTH_TOKEN_NULL_OR_EMPTY,
    TRUST_AUTH_TOKEN_INVALID,
    TRUST_AUTH_TOKEN_NOT_ACCEPT
}
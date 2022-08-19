package io.notable.utils

import java.util.UUID

private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

fun getRandomId(): UUID = UUID.randomUUID()

fun getRandomString(length: Int): String = (1..length)
    .map { kotlin.random.Random.nextInt(0, charPool.size) }
    .map(charPool::get)
    .joinToString("")

fun getRandomEmail(): String = "${getRandomString(6)}@test.com"

object Kotlinx {
    private const val kotlinxDatetimeVersion = "0.4.0"
    const val datetime = "org.jetbrains.kotlinx:kotlinx-datetime:$kotlinxDatetimeVersion"

    private const val coroutinesCoreVersion = "1.6.4"
    const val coroutinesCore =
        "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesCoreVersion"

    // Need for tests. Plugin doesn't work.
    private const val serializationVersion = "1.3.3"
    const val serialization =
        "org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion"
}
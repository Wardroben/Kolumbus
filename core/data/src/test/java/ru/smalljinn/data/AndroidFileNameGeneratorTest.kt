package ru.smalljinn.data

import org.junit.Test
import ru.smalljinn.kolumbus.data.saving.AndroidFileNameGenerator

class AndroidFileNameGeneratorTest {
    @Test
    fun generateImageName() {
        val generator = AndroidFileNameGenerator()
        val name = generator.constructImageFileName()
        assert(name.isNotBlank())
    }
}
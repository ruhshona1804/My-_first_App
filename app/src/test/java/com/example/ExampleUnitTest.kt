package com.example

import com.example.data.model.AchievementLibrary
import com.example.data.model.HabitTemplates
import com.example.data.model.Localization
import com.example.data.model.QuoteLibrary
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testLocalization() {
        val uzTitle = Localization.get("app_title", "uz")
        assertEquals("NUVIA", uzTitle)

        val ruTitle = Localization.get("nav_habits", "ru")
        assertEquals("Привычки", ruTitle)

        val enTitle = Localization.get("nav_habits", "en")
        assertEquals("Habits", enTitle)

        val uzHome = Localization.get("nav_home", "uz")
        assertEquals("Bosh sahifa", uzHome)
    }

    @Test
    fun testHabitTemplates() {
        val templates = HabitTemplates.starterHabits
        assertTrue(templates.isNotEmpty())
        assertTrue(templates.any { it.titleUz == "Kitob o'qish" })
        assertTrue(templates.any { it.titleUz == "2 litr suv ichish" })
        assertTrue(templates.any { it.titleUz == "Sport bilan shug'ullanish" })
    }

    @Test
    fun testQuoteLibrary() {
        val quote = QuoteLibrary.getDailyQuote(10)
        assertNotNull(quote.uz)
        assertNotNull(quote.author)
        assertTrue(quote.uz.isNotBlank())
    }

    @Test
    fun testAchievements() {
        val badges = AchievementLibrary.badges
        assertEquals(5, badges.size)
        assertTrue(badges.any { it.requiredStreak == 7 })
        assertTrue(badges.any { it.requiredStreak == 30 })
    }
}

package com.bangkit.scantion.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.bangkit.scantion.data.database.SkinExamsDao
import com.bangkit.scantion.data.database.SkinExamsDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class SkinExamDaoTest{

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: SkinExamsDatabase
    private lateinit var dao: SkinExamsDao

    private val sampleCase = DataDummy.generateDummyCaseEntity()[0]
    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            SkinExamsDatabase::class.java
        ).build()
        dao = database.skinExamsDao()
    }

    @After
    fun closeDb() = database.close()
    @Test
    fun addSkinExam_success() = runTest {
        dao.addSkinExam(sampleCase)
        val actualCase = dao.getSkinExams().getOrAwaitValue()
        Assert.assertEquals(sampleCase.id, actualCase[0].id)
    }

    @Test
    fun deleteSkinCase_Success() = runTest {
        dao.addSkinExam(sampleCase)
        dao.deleteSkinExam(sampleCase)
        val actualCase = dao.getSkinExams().getOrAwaitValue()
        Assert.assertTrue(actualCase.isEmpty())
    }
}
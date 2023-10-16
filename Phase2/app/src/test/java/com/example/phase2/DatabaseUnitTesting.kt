package com.example.phase2

import com.example.phase2.database.FakePaintsRepository
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test

class DatabaseUnitTesting {

    private val fakePaintsRepository = FakePaintsRepository()

    @Test
    fun testInsertPaintsData(){

        // Inserting Single Data and Testing other SQL functions
        val fakePaintsData1 = PaintsData("fakeData1","Drawing Data 1",1)
        fakePaintsRepository.insertPaintsData(fakePaintsData1)
        assertFalse(fakePaintsRepository.getPaintingsByUserId(1).isEmpty())
        assertEquals(fakePaintsRepository.getPaintingsByUserId(1).get(0).drawingData,fakePaintsData1.drawingData)
        assertEquals(fakePaintsRepository.getPaintingNamesByUserId(1).get(0),fakePaintsData1.drawingName)
        val fakeUpdatePaintsData1 = PaintsData("fakeData1","Update Drawing Data 1",1)
        fakePaintsRepository.updatePaintsData(fakeUpdatePaintsData1)
        assertEquals(fakePaintsRepository.getPaintingsByUserId(1).get(0).drawingData,fakeUpdatePaintsData1.drawingData)
        assertEquals(fakePaintsRepository.getPaintingNamesByUserId(1).get(0),fakeUpdatePaintsData1.drawingName)

        assertTrue(fakePaintsRepository.getPaintingsByUserId(2).isEmpty())
        assertTrue(fakePaintsRepository.getPaintingsByUserId(3).isEmpty())
        assertTrue(fakePaintsRepository.getPaintingsByUserId(4).isEmpty())

        // Inserting Multiple Data and Testing other SQL functions
        val fakePaintsData2 = PaintsData("fakeData2","Drawing Data 2",1)
        val fakePaintsData3 = PaintsData("fakeData3","Drawing Data 3",1)
        val fakePaintsData4 = PaintsData("fakeData1","Drawing Data 2",2)
        val fakePaintsData5 = PaintsData("fakeData1","Drawing Data 2",3)
        val fakePaintsData6 = PaintsData("fakeData1","Drawing Data 2",4)
        fakePaintsRepository.insertPaintsData(fakePaintsData2)
        fakePaintsRepository.insertPaintsData(fakePaintsData3)
        fakePaintsRepository.insertPaintsData(fakePaintsData4)
        fakePaintsRepository.insertPaintsData(fakePaintsData5)
        fakePaintsRepository.insertPaintsData(fakePaintsData6)

        assertFalse(fakePaintsRepository.getPaintingsByUserId(2).isEmpty())
        assertFalse(fakePaintsRepository.getPaintingsByUserId(3).isEmpty())
        assertFalse(fakePaintsRepository.getPaintingsByUserId(4).isEmpty())

    }


}
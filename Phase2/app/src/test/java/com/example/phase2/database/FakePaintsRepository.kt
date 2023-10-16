package com.example.phase2.database

import com.example.phase2.PaintsData

class FakePaintsRepository {

    private  val fakeData = mutableListOf<PaintsData>();

    fun insertPaintsData(paintsData: PaintsData) {

        for(data in fakeData){
            if(data.userId.equals(paintsData.userId) && data.drawingName.equals(paintsData.drawingName)){
                return
            }
        }
        fakeData.add(paintsData)
    }


     fun getDrawingByDrawingName(drawingName: String, userId: Long): PaintsData? {
        for(data in fakeData){
            if(data.userId.equals(userId) && data.drawingName.equals(drawingName)){
                return data
            }
        }
         return null
    }


    fun updatePaintsData(paintsData: PaintsData) {
        for(data in fakeData){
            if(data.userId.equals(paintsData.userId) && data.drawingName.equals(paintsData.drawingName)){
                data.drawingData = paintsData.drawingData
            }
        }
    }


     fun getPaintingsByUserId(userId: Long): List<PaintsData> {
         val result = mutableListOf<PaintsData>();
         for(data in fakeData){
             if(data.userId.equals(userId)){
                 result.add(data)
             }
         }
         return result

    }


    fun getPaintingNamesByUserId(userId: Long): List<String> {
        val result = mutableListOf<String>();
        for(data in fakeData){
            if(data.userId.equals(userId)){
                result.add(data.drawingName)
            }
        }
        return result
    }

}
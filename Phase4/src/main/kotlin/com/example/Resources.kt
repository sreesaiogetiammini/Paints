package com.example


import com.example.plugins.getSessionData
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.Resources
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction



fun Application.configureResources() {
    install(Resources)
    routing{
        post("/upload") {
            try {
                val uploadedData = call.receive<PaintsData>()
                // Check if a record with the same userID and drawingName already exists
                val existingRecord = newSuspendedTransaction(Dispatchers.IO, DBSettings.db) {
                    Painting.select {
                       (Painting.drawingName eq uploadedData.drawingName)
                    }.singleOrNull()
                }

                if (existingRecord == null){
                    newSuspendedTransaction(Dispatchers.IO, DBSettings.db) {
                        Painting.insert {
                            it[drawingName] = uploadedData.drawingName
                            it[drawingData] = uploadedData.drawingData
                            it[drawingImages] = uploadedData.drawingImages
                            it[drawingTexts] = uploadedData.drawingTexts
                            it[userId] = uploadedData.userId
                        }
                    }

                    // Respond with a success message
                    call.respondText("Data uploaded successfully", ContentType.Text.Plain)
                }

                else{
                    call.respondText("Already Existing Data No Need to Upload", ContentType.Text.Plain)
                }

            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid data "+e.message)
            }
        }
        get("paintings") {
            val paintings =

                newSuspendedTransaction(Dispatchers.IO, DBSettings.db) {
                Painting.selectAll().map {
                    PaintsData(
                        it[Painting.userId],
                        it[Painting.drawingName],
                        it[Painting.drawingData],
                        it[Painting.drawingImages],
                        it[Painting.drawingTexts]

                    )
                }
            }

            if (paintings.isNotEmpty()) {
                call.respond(paintings)
            }
            else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}



@Serializable
data class PaintsData(
    var userId: String,
    var drawingName: String,
    var drawingData: String,
    var drawingImages: String,
    var drawingTexts: String

)


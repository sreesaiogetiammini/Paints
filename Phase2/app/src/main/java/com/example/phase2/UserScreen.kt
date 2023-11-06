package com.example.phase2

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.api.Logging
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.call.receive
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
//import io.ktor.client.statement.HttpResponse
//import io.ktor.client.statement.bodyAsText
//import io.ktor.http.ContentType
//import io.ktor.http.contentType
import io.ktor.client.plugins.resources.*
//import io.ktor.http.HttpMethod
//import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*


@Composable
fun UserScreen(navController: NavController, paintsRepository: PaintsRepository,userId: String) {

    val scope = rememberCoroutineScope()

    var drawingNames by remember {
        mutableStateOf<List<String>>(emptyList()) // Provide an initial empty list
    }

    var globalDrawings by remember {
        mutableStateOf<List<PaintsServerData>>(emptyList())
    }

// Inside a Composable
    LaunchedEffect(1){
        scope.launch {
            var id = userId // Replace with the actual user ID
            drawingNames = paintsRepository.getPaintingNamesByUserId(userId)
            globalDrawings = getAllDrawingsFromKtorServer()
            Log.e("Data drawing", globalDrawings.toString())
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Image(
            painter = painterResource(id = R.drawable.paints),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer), // Color.Purple
        )

        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer) // Replace Color.Red with the desired background color
                .padding(16.dp)
        ) {
            Text(
                text = "Your Drawings",
                style = TextStyle(fontWeight = FontWeight.Bold),
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f) // Ensure that the ScrollableList takes available space
        ) {
            ScrollableList(
                drawingNames = drawingNames,
                navController = navController,
                paintsRepository = paintsRepository,
                userId = userId,
            )

        }

        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer) // Replace Color.Red with the desired background color
                .padding(16.dp)
        ) {
            Text(
                text = "Global Feed",
                style = TextStyle(fontWeight = FontWeight.Bold),
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f) // Ensure that the ScrollableList takes available space
        ) {
            NewScrollableList(
                navController = navController,
                paintsRepository = paintsRepository,
                userId = userId,
                globalDrawings = globalDrawings
            )
        }




        FloatingActionButton(
            onClick = { navController.navigate(Screen.DrawScreen.route + "/$userId/dummy")  },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.End)
        ) {
            Icon(Icons.Filled.Add, "Floating action button.")

        }
    }
}

@Composable
fun ScrollableList(
    drawingNames: List<String>,
    navController: NavController,
    paintsRepository: PaintsRepository,
    userId: String,
//    globalDrawings: List<PaintsServerData>
) {
    val windowInfo= rememberWindowInfo()
    if(windowInfo.screenWidthInfo is WindowInfo.WindowType.Compact){
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            items(drawingNames) { drawingName ->
                // Each item in the list
                if(!drawingName.equals("dummy")){
                    ListItem(drawingName,navController,paintsRepository,userId)
                }
            }
        }
    }
    else{
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(drawingNames) { drawingName ->
                // Each drawing name in the list
                if(!drawingName.equals("dummy")){
                    ListItem(drawingName, navController,paintsRepository,userId)
                }

            }
        }
    }

}

@Composable
fun NewScrollableList(
    navController: NavController,
    paintsRepository: PaintsRepository,
    userId: String,
    globalDrawings: List<PaintsServerData>
){
    val windowInfo= rememberWindowInfo()
    if(windowInfo.screenWidthInfo is WindowInfo.WindowType.Compact){
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            items(globalDrawings) { drawing ->
                // Each item in the list
                if(true){
                    GlobalListItem(drawing = drawing,navController,paintsRepository,userId)
                }
            }
        }
    }
    else{
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(globalDrawings) { drawing ->
                // Each drawing name in the list
                if(!drawing.isGlobal){
                    GlobalListItem(drawing, navController,paintsRepository,userId)
                }

            }
        }
    }
}

@Composable
fun ListItem(drawingName: String, navController: NavController,paintsRepository:PaintsRepository,userId: String) {
    // Composable for rendering each list item
    // You can customize this based on your item's content
    var isDropDownVisible by remember {
        mutableStateOf(false)
    }

    var isImageUploadTask by remember {
        mutableStateOf(false)
    }

    var isDeleteDrawing by remember {
        mutableStateOf(false)
    }



    Row (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = drawingName,
            style = TextStyle.Default.copy(color = Color.Black),
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = "Dropdown",
            modifier = Modifier
                .clickable {
                    isDropDownVisible = !isDropDownVisible
                }
                .padding(8.dp)
        )

        DropdownMenu(expanded = isDropDownVisible, onDismissRequest = {
            isDropDownVisible = false
        },
            modifier = Modifier.widthIn(320.dp)
        )
        {
            DropdownMenuItem(text = {
                Text("Edit")
            }, onClick = {
                Log.e("Edit Name",drawingName)
                navController.navigate(Screen.DrawScreen.route + "/$userId" +"/$drawingName")
                isDropDownVisible = false;
            })
            DropdownMenuItem(text = {
                Text("Share Globally")
            },

                onClick = {
                    isDropDownVisible = false
                    isImageUploadTask = true


                })
            DropdownMenuItem(
                text = { Text("Delete") },
                onClick = {
                    isDeleteDrawing = true
                    isDropDownVisible = false;

                })

            if(isImageUploadTask){
                LaunchedEffect(Unit){
                    val  dataFromDB = paintsRepository.getDrawingByDrawingName(drawingName = drawingName,userId= userId)
                    Log.i("data from DB",dataFromDB.toString())

                    if (dataFromDB != null) {
                        val paintsServerData = dataFromDB.toPaintsServerData()
                        uploadPaintingDataToKtorServer(paintsServerData)
                        paintsRepository.setPaintingAsGlobal(drawingName, userId)
                    }

                    navController.navigate(Screen.UserScreen.route + "/$userId")
                }
            }

            if(isDeleteDrawing){
                LaunchedEffect(Unit){
                    paintsRepository.deletePaintingByDrawingName(drawingName, userId)
                    deletePaintingFromServer(PaintsServerData(drawingName,"assasa","assasa","sasaassasa",userId,true))
                    navController.navigate(Screen.UserScreen.route + "/$userId")
                }
            }
        }
    }
}

@Composable
fun GlobalListItem(drawing: PaintsServerData, navController: NavController, paintsRepository:PaintsRepository, userId: String){
    var isDropDownVisible by remember {
        mutableStateOf(false)
    }
    Row (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = drawing.drawingName,
            style = TextStyle.Default.copy(color = Color.Black),
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "by " + drawing.userId,
            style = TextStyle.Default.copy(color = Color.Gray),
            modifier = Modifier.weight(0.5f)
        )



        Icon(
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = "Dropdown",
            modifier = Modifier
                .clickable {
                    isDropDownVisible = !isDropDownVisible
                }
                .padding(8.dp)
        )

        DropdownMenu(expanded = isDropDownVisible, onDismissRequest = {
            isDropDownVisible = false
        },
            modifier = Modifier.widthIn(320.dp)
        )
        {
            DropdownMenuItem(text = {
                Text("Edit")
            }, onClick = {
                Log.e("Edit Name",drawing.drawingName)
                navController.navigate(Screen.DrawScreen.route + "/${userId}" +"/${drawing.drawingName}")
                isDropDownVisible = false;
            })
        }
    }
}

suspend fun uploadPaintingDataToKtorServer(dataFromDB: PaintsServerData) {
    runBlocking {
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                })
            }
        }

        println("Post Request is " + dataFromDB.toString())
        val response: HttpResponse = client.post("http://10.0.2.2:8080/upload") {
            contentType(ContentType.Application.Json)
            setBody(dataFromDB)
        }
        println("Response is " + response.bodyAsText())

        client.close()
    }
}

suspend fun getAllDrawingsFromKtorServer(): List<PaintsServerData> {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
    }

    val response: HttpResponse = client.request("http://10.0.2.2:8080/paintings") {
        method = HttpMethod.Get
    }

    val drawingsList = when (response.status) {
        HttpStatusCode.OK -> response.body<List<PaintsServerData>>()
        else -> emptyList()
    }

    client.close()
    return drawingsList
}

@Serializable
data class PaintsServerData(
    var drawingName: String,
    var drawingData: String,
    var drawingImages: String,
    var drawingTexts: String,
    var userId: String,
    var isGlobal: Boolean,
)

fun PaintsData.toPaintsServerData(): PaintsServerData {
    return PaintsServerData(
        drawingName = this.drawingName,
        drawingData = this.drawingData,
        drawingImages = this.drawingImages,
        drawingTexts = this.drawingTexts,
        userId = this.userId,
        isGlobal = true
    )
}

fun deletePaintingFromServer(paintsData: PaintsServerData){
    runBlocking {
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                })
            }
        }
        val response: HttpResponse = client.delete("http://10.0.2.2:8080/paints"){
            contentType(ContentType.Application.Json)
            setBody(paintsData)
        }
        println("Delete Painting Response is "+response.bodyAsText())
    }
}
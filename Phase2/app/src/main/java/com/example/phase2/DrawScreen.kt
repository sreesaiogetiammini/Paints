package com.example.phase2

import ImageDataTypeAdapter
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.util.InternalAPI
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun DrawScreen(navController: NavController, paintsRepository: PaintsRepository, userId: String,drawingName: String,sensorManager: SensorManager) {
    var expanded  by remember { mutableStateOf(false) }
    var sliderPosition by remember { mutableStateOf(10f) }
    var clickedBtn by remember { mutableStateOf(-1) }
    var isSliderDialogOpen by remember { mutableStateOf(false) }
    var isSaveDialogOpen by remember { mutableStateOf(false) }
    val myviewModel: PaintViewModel = viewModel()
    var isColorPickerDialogVisible by remember { mutableStateOf(false) }
    var paintingName by remember { mutableStateOf(drawingName) }
    var lines by remember { mutableStateOf(emptyList<Line>()) }
    val lineColor by rememberUpdatedState(myviewModel.lineColor)
    val lineStroke by rememberUpdatedState(myviewModel.lineStroke)
    var id by remember { mutableStateOf(userId) }
    val scope = rememberCoroutineScope()
    val icons = listOf(
        painterResource(R.drawable.baseline_draw_24),
        painterResource(R.drawable.baseline_line_style_24),
        painterResource(R.drawable.baseline_color_lens_24),
        painterResource(R.drawable.baseline_brush_24),
        painterResource(R.drawable.erasor),
        painterResource(R.drawable.baseline_fiber_new_24),
        painterResource(R.drawable.baseline_text_fields_24),
        painterResource(R.drawable.baseline_image_24)
    )
    val gson: Gson = GsonBuilder()
        .registerTypeAdapter(ImageData::class.java, ImageDataTypeAdapter())
        .create()
    var isCapDialogOpen by remember { mutableStateOf(false) }
    var imagesData by remember { mutableStateOf(mutableListOf<ImageData>()) }
    var imageUris by remember { mutableStateOf(mutableListOf<Uri>()) }
    val PhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = {uris ->
            imageUris = uris.toMutableList()
            imageUris.forEach{uri ->
                Log.e("uri", uri.toString())
                myviewModel.addImage(ImageData(uri, 0f,0f))
                for (image in myviewModel.getImages()){
                    Log.e("132", image.src.toString())
                }
            }
        }
    )
    var textList by remember { mutableStateOf(emptyList<TextBox>()) }
    var screenshotTaken by remember { mutableStateOf(0) }
    val context = LocalContext.current
    val activity = (LocalView.current.context as? ComponentActivity)
    val aiPaintingEnabled by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            topBarStuff(navController = navController, userId = id , aiPaintingEnabled)
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        for (i in 0 until icons.size) {
                            val isClicked = i == clickedBtn

                            AnimatedIconButton(
                                icon = icons[i],
                                onClick = {
                                    if (isClicked) {
                                        clickedBtn = -1
                                    } else {
                                        clickedBtn = i
                                    }

                                    if (i == 0) {
                                        isSliderDialogOpen = !isSliderDialogOpen
                                    }
                                    if (i == 1) {
                                        isCapDialogOpen = !isCapDialogOpen
                                    }
                                    if(i == 2){
                                        isColorPickerDialogVisible = !isColorPickerDialogVisible
                                    }
                                    if(i == 3){
                                        myviewModel.updateLineColor(Color.Black)
                                    }

                                    if(i == 4){
                                        myviewModel.updateLineColor(Color.White)
                                    }
                                    if(i == 5){
                                        navController.navigate(Screen.DrawScreen.route + "/$userId" +"/dummy")
                                    }
                                    if(i == 6){
                                        val text = TextBox(0f,0f,"Add Text")
                                        myviewModel.addTexts(text)
                                    }
                                    if(i == 7){
                                        PhotoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    }
                                },
                                isClicked = isClicked,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        if(isCapDialogOpen){
                            addCapDialog(myViewModel = myviewModel,
                                onDialogDismiss = {
                                    isCapDialogOpen = !isCapDialogOpen
                                })
                        }

                        if (isSliderDialogOpen)
                        {
                            addSliderDialog(
                                myViewModel = myviewModel,
                                sliderPosition = sliderPosition,
                                onDialogDismiss = {
                                    isSliderDialogOpen = !isSliderDialogOpen
                                }
                            )
                        }


                        if (isColorPickerDialogVisible) {
                            addColorPickerDialog(
                                myViewModel = myviewModel,
                                onDialogDismiss = {
                                    isColorPickerDialogVisible = !isColorPickerDialogVisible
                                }
                            )
                        }



                    }
                }
            }
        },
        floatingActionButton = {
            Column{
                FloatingActionButton(onClick = { expanded = !expanded }) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
                Spacer(modifier = Modifier.height(16.dp))
                if(expanded){
                    FloatingActionButton(onClick = {
                        isSaveDialogOpen = !isSaveDialogOpen
                    }) {
                        Icon(
                            painterResource(id = R.drawable.baseline_save_as_24),
                            contentDescription = "Save As"
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    FloatingActionButton(onClick = {
                        screenshotTaken++
                    }) {
                        Icon(
                            painterResource(id = R.drawable.baseline_share_24),
                            contentDescription = "Share"
                        )
                    }
                    if(screenshotTaken>0){
                        LaunchedEffect(Unit){
                            takeScreenshot(myviewModel,activity, context)
                        }
                    }

                }
            }
        }
    )

    { innerPadding ->
        var offset by remember { mutableStateOf(0f) }
        Box( modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .scrollable(
                orientation = Orientation.Vertical,
                // Scrollable state: describes how to consume
                // scrolling delta and update offset
                state = rememberScrollableState { delta ->
                    offset += delta
                    delta
                },
                reverseDirection = true
            )
            .border(5.dp, Color.Blue, RectangleShape)
        )
        {

            // Jin Work on this
//            if(aiPaintingEnabled){
//                MarbleRollingApp(myviewModel,sensorManager)
//            }

            if (drawingName.isNotBlank() && drawingName != "dummy") {
                LaunchedEffect(Unit) {
                    val drawingData = paintsRepository.getDrawingByDrawingName(
                        drawingName = paintingName,
                        userId = id.toLong()
                    )
                    if (drawingData != null) {
                        lines = deserializeDrawingData(drawingData.drawingData)
                        imagesData = deserializeDrawingImages(drawingData.drawingImages)
                        textList = deserializeDrawingTexts(drawingData.drawingTexts)
                        for (line in lines) {
                            myviewModel.addLine(line)
                        }
                        for(image in imagesData){
                            myviewModel.addImage(image)
                        }
                        for(text in textList){
                            myviewModel.addTexts(text)
                        }
                    }
                }
            }
            Canvas(modifier = Modifier
                .fillMaxSize()
                .pointerInput(true) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        val line_custom = Line(
                            start = change.position - dragAmount,
                            end = change.position,
                            color = lineColor.value,
                            strokeWidth = lineStroke.value,
                        )


                        lines =
                            lines + line_custom // Append the new line to the list of lines
                        myviewModel.addLine(line = line_custom)
                    }
                })
            {
                lines.forEach { line ->
                    drawLine(
                        color = line.color,
                        start = line.start,
                        end = line.end,
                        strokeWidth = line.strokeWidth.width,
                        cap = line.strokeWidth.cap
                    )
                }
            }
            // Save Drawing Confirmation Dialog
            if (isSaveDialogOpen) {
                SaveDrawingDialog(
                    onSave = {
                        scope.launch {
                            val lines = myviewModel.getLines()
                            val images = myviewModel.getImages()
                            val texts = myviewModel.getTexts()
                            val drawingData = Gson().toJson(lines) // Serialize the drawing data to JSON
                            val drawingImages = gson.toJson(images)
                            Log.e("drawing Data", drawingImages)
                            val drawingTexts= Gson().toJson(texts)

                            val userId = id // Replace with the actual user ID
                            if (paintingName.isNotBlank()) {
                                val paintsData = PaintsData(
                                    userId = userId.toLong(),
                                    drawingName = paintingName,
                                    drawingData = drawingData,
                                    drawingImages = drawingImages,
                                    drawingTexts = drawingTexts,
                                )

                                val existingDrawingData =
                                    paintsRepository.getDrawingByDrawingName(
                                        drawingName = paintingName,
                                        userId = userId.toLong()
                                    )

                                if (existingDrawingData != null) {
                                    // Update the existing painting
                                    existingDrawingData.drawingData = drawingData
                                    existingDrawingData.drawingImages = drawingImages
                                    existingDrawingData.drawingTexts = drawingTexts
                                    paintsRepository.updatePaintsData(existingDrawingData)

                                    // need to add update paints data.
                                } else {
                                    // Insert a new painting
                                    paintsRepository.insertPaintsData(paintsData)
                                }
                            }
                        }
                        isSaveDialogOpen = false
                    },
                    onDismiss = {
                        isSaveDialogOpen = false // Close the dialog on dismiss
                    },
                    onNameChange = { name ->
                        paintingName = name // Update the painting name
                    },
                    initialName = paintingName
                )
            }


            myviewModel.getImages().forEach { uri ->
                var imageData by remember {
                    mutableStateOf(uri)
                }
                addImageToCanvas(imageData = imageData, myviewModel = myviewModel)
            }
            textList = myviewModel.getTexts()

            for (text in textList) {
                addTextField( myviewModel, text)
            }

        }
    }

}



suspend fun takeScreenshot(myViewModel: PaintViewModel, activity: ComponentActivity?, context: Context) {
    if (activity == null) return


    val deviceWidth = Resources.getSystem().displayMetrics.widthPixels
    val deviceHeight = Resources.getSystem().displayMetrics.heightPixels

    val screenshot = myViewModel.captureCanvasAsBitmap(deviceWidth,deviceHeight, context = context)
    // Save the screenshot to a file
    val timestamp = SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(Date())
    val fileName = "Screenshot_$timestamp.png"
    val screenshotFile = File(context.filesDir, fileName)

    val uri = FileProvider.getUriForFile(
        context,
        "com.example.phase2.fileprovider",
        screenshotFile
    )

    val fos = context.contentResolver.openOutputStream(uri)
    screenshot.compress(Bitmap.CompressFormat.PNG,100, fos!!)
    fos!!.close()
    uploadImageUriToServer(uri.toString(), "http://0.0.0.0:8080/paints/share")
//    val shareIntent: Intent = Intent().apply {
//        action = Intent.ACTION_SEND
//        putExtra(Intent.EXTRA_STREAM, uri)
//        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
//        type = "image/png"
//    }
//
//    context.startActivity(Intent.createChooser(shareIntent, "Share Screenshot"))
}

@OptIn(InternalAPI::class)
suspend fun uploadImageUriToServer(imageUri: String, serverUrl: String) {
    val client = HttpClient(CIO)
    val response: io.ktor.client.statement.HttpResponse = client.post(serverUrl) {
        contentType(ContentType.Application.Json)
        body = ImageUriUploadRequest(imageUri)
    }
    UploadResponse(response.status.equals(HttpStatusCode.Created))
}
data class ImageUriUploadRequest(val imageUri: String)
data class UploadResponse(val success: Boolean)

@Composable
fun addImageToCanvas(imageData: ImageData,myviewModel: PaintViewModel){
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    Box(
        modifier = Modifier
            .size(200.dp)
            .offset { IntOffset((imageData.x + offsetX).roundToInt(), (imageData.y + offsetY).roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y

                    myviewModel.updateImageDataBySrc(imageData.src, ImageData(imageData.src, offsetX, offsetY))

                }
            }
    ) {
        AsyncImage(
            model = imageData.src,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        IconButton(
            onClick = {
                myviewModel.removeImage(imageData)
            },
            modifier = Modifier
                .padding(4.dp) // Adjust the padding as needed
                .align(Alignment.TopEnd) // Position the close button in the top-right corner
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close Image"
            )
        }
    }

}

@Composable
fun MarbleRollingApp(marbleViewModel: PaintViewModel, sensorManager: SensorManager) {
    val marbleOffset = marbleViewModel.gravityOffset.value
    val trail = marbleViewModel.trailPositions

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val maxWidth = constraints.maxWidth
        val maxHeight = constraints.maxHeight

        // Draw the trail
        trail.forEach { position ->
            Box(
                modifier = Modifier.offset {
                    IntOffset(position.x.roundToInt(), position.y.roundToInt())
                }.size(10.dp).background(marbleViewModel.getLineColor())
            )
        }

        LaunchedEffect(sensorManager) {
            val accelMagFlow = updateGravityData(sensorManager, marbleViewModel, dampingFactor = 2f, maxWidth = maxWidth, maxHeight = maxHeight)
            accelMagFlow.collect { offset ->
                marbleViewModel.gravityOffset.value = offset
            }
        }

        Marble(
            x = marbleOffset.x,
            y = marbleOffset.y,
            maxWidth = maxWidth,
            maxHeight = maxHeight,
            marbleViewModel = marbleViewModel
        )
    }
}

fun updateGravityData(
    sensorManager: SensorManager,
    marbleViewModel: PaintViewModel,
    dampingFactor: Float,
    maxWidth: Int,
    maxHeight: Int
): Flow<Offset> {
    return channelFlow {
        val gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
        var currentOffset = marbleViewModel.gravityOffset.value
        val radius = 50.dp.value
        val xMin = 0f
        val xMax = maxWidth - 2 * radius
        val yMin = 0f
        val yMax = maxHeight - 2 * radius

        val sensorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    val gravityX = it.values[0]
                    val gravityY = it.values[1]
                    val deltaX = gravityX * dampingFactor
                    val deltaY = gravityY * dampingFactor

                    val constrainedX = (currentOffset.x + deltaX).coerceIn(xMin, xMax)
                    val constrainedY = (currentOffset.y + deltaY).coerceIn(yMin, yMax)
                    currentOffset = Offset(constrainedX, constrainedY)

                    marbleViewModel.gravityOffset.value = currentOffset
                    marbleViewModel.trailPositions.add(currentOffset)
                    trySend(currentOffset)
                } ?: run {
                    Log.e("Event Null", "Gge")
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensorManager.registerListener(
            sensorListener,
            gravitySensor,
            SensorManager.SENSOR_DELAY_GAME
        )
        awaitClose {
            sensorManager.unregisterListener(sensorListener)
        }
    }
}

@Composable
fun  addTextField(myviewModel: PaintViewModel,text : TextBox ){

    var textValue by remember { mutableStateOf(text.value) }
    var offsetX by remember { mutableStateOf(text.x) }
    var offsetY by remember { mutableStateOf(text.y) }

    Box(
        modifier = Modifier
            .size(200.dp)
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                    myviewModel.updateTextCoordinates(text, offsetX, offsetY)
                }
            }
    ) {

        TextField(value = textValue, onValueChange = {
            textValue = it
            myviewModel.updateTextValue(text,textValue)
        })
        IconButton(
            onClick = {
                myviewModel.removeTexts(text)
            },
            modifier = Modifier
                .padding(4.dp) // Adjust the padding as needed
                .align(Alignment.TopEnd) // Position the close button in the top-right corner
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close Image"
            )
        }
    }

}




@Composable
fun topBarStuff(navController: NavController, userId: String, aiPaintingEnabled: Boolean){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.paints),
            contentDescription = "P.a.i.n.t.s image",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(MaterialTheme.colorScheme.primaryContainer), // Color.Purple
        )
        IconButton(
            onClick = {
                aiPaintingEnabled != aiPaintingEnabled
            },
        ) {
            Icon(
                imageVector = Icons.Default.Face,
                contentDescription = "AI Painter"
            )
        }


        IconButton(
            onClick = {
                navController.navigate(Screen.SplashScreen.route)

            },
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Account User"
            )
        }
        IconButton(
            onClick = {
                navController.navigate(Screen.UserScreen.route+"/$userId" )
            },
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Account Home"
            )
        }
    }
}



@Composable
fun SaveDrawingDialog(
    onSave: () -> Unit,
    onDismiss: () -> Unit,
    onNameChange: (String) -> Unit,
    initialName: String
) {
    // Create and display your custom dialog here
    // Include buttons for confirmation and dismissal
    // You can use a Dialog Composable or a custom AlertDialog
    // Example using Dialog Composable:
    var paintingName by remember { mutableStateOf(initialName) }
    Dialog(
        onDismissRequest = { onDismiss() }
    ) {

        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            TextField(
                value = paintingName,
                onValueChange = {
                    paintingName = it
                    onNameChange(it) // Callback to handle painting name changes
                },
                label = { Text("Painting Name") }
            )

            // Include a button to confirm the save action
            Button(
                onClick = {
                    onSave()
                    onDismiss()
                },
                enabled = paintingName.isNotBlank() && paintingName != "dummy" && paintingName.length>3
            ) {
                Text("Confirm Save")
            }

            // Include a button to dismiss the dialog
            Button(
                onClick = {
                    onDismiss()
                },

                ) {
                Text("Cancel")
            }
        }
    }
}




fun deserializeDrawingTexts(drawingTexts: String?): List<TextBox> {
    val gson = Gson()
    val listType = object : TypeToken<List<TextBox>>() {}.type
    return gson.fromJson(drawingTexts, listType)
}

fun deserializeDrawingData(drawingData: String?): List<Line> {
    val gson = Gson()
    val listType = object : TypeToken<List<Line>>() {}.type
    return gson.fromJson(drawingData, listType)
}

fun deserializeDrawingImages(imagesData: String?): MutableList<ImageData> {
    val gson: Gson = GsonBuilder()
        .registerTypeAdapter(ImageData::class.java, ImageDataTypeAdapter())
        .create()
    val imageDataType = object : TypeToken<MutableList<ImageData>>() {}.type
    return gson.fromJson(imagesData, imageDataType) ?: mutableListOf()
}
@Composable
fun AnimatedIconButton(
    icon: Painter,
    onClick: () -> Unit,
    isClicked: Boolean,
    modifier: Modifier = Modifier,
) {
    val size by animateDpAsState(targetValue = if (isClicked) 62.dp else 38.dp)

    Box(
        modifier = modifier
            .size(size)
            .clickable {
                onClick()
            }
    ) {
        Image(
            painter = icon,
            contentDescription = "AnimatedIconButtons",
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
fun addSliderDialog(myViewModel:PaintViewModel, onDialogDismiss: () -> Unit, sliderPosition:Float){
    var sliderPosition by remember { mutableFloatStateOf(sliderPosition) }

    Dialog(
        onDismissRequest = {
            onDialogDismiss()
        }
    )
    {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background), // Background color
            shape = MaterialTheme.shapes.medium,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = sliderPosition.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Slider(
                    modifier = Modifier.semantics {
                        contentDescription = "Localized Description"
                    },
                    value = sliderPosition,
                    onValueChange = { sliderPosition = it },
                    valueRange = 10f..100f,
                    onValueChangeFinished = {
                        myViewModel.updateLineStrokeWidth(sliderPosition)
                    },
                )
            }
        }
    }



}

@Composable
fun addCapDialog(myViewModel:PaintViewModel, onDialogDismiss: () -> Unit){
    Dialog(
        onDismissRequest = {   onDialogDismiss() }
    )
    {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background), // Background color
            shape = MaterialTheme.shapes.medium,
        ) {
            Column( modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                )
                {
                    Button(onClick = {myViewModel.updateLineStrokeCap(StrokeCap.Butt)}) {
                        Icon(
                            painterResource(id = R.drawable.baseline_butt_24),
                            contentDescription = "Butt Cap"
                        )
                    }
                    Button(onClick = {myViewModel.updateLineStrokeCap(StrokeCap.Square)}) {
                        Icon(
                            painterResource(id = R.drawable.baseline_square_24),
                            contentDescription = "Square Cap"
                        )
                    }
                    Button(onClick = {myViewModel.updateLineStrokeCap(StrokeCap.Round)}) {
                        Icon(
                            painterResource(id = R.drawable.baseline_circle_24),
                            contentDescription = "Round Cap"
                        )
                    }
                }
            }
        }
    }

}

@Composable
fun addColorPickerDialog(myViewModel:PaintViewModel,onDialogDismiss: () -> Unit){
    Dialog(
        onDismissRequest = {
            // Close the color picker dialog

        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(450.dp)
                .padding(10.dp)
        ) {
//            HsvColorPicker(
//                modifier = Modifier.fillMaxSize(),
//                controller = ColorPickerController(),
//                onColorChanged = { colorEnvelope: ColorEnvelope ->
//                    val selectedLineColor = Color(colorEnvelope.color.toArgb())
//                    myViewModel.updateLineColor(selectedLineColor)
//                }
//            )
            colorPicker(myViewModel)
//
            // Close button
            IconButton(
                onClick = {
                    onDialogDismiss()
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }

        }
    }

}



@Composable
fun colorPicker(myViewModel: PaintViewModel){
    val controller = rememberColorPickerController()

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 30.dp)
    ){
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AlphaTile(modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .clip(RoundedCornerShape(6.dp)),
                controller = controller
            )
        }
        HsvColorPicker(modifier = Modifier
            .fillMaxWidth()
            .height(450.dp)
            .padding(10.dp)
            ,
            controller = controller,
            onColorChanged = {
                    colorEnvelope: ColorEnvelope ->
                val selectedLineColor = Color(colorEnvelope.color.toArgb())
                myViewModel.updateLineColor(selectedLineColor)
            }
        )
        AlphaSlider(modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .height(35.dp)
            ,
            controller =controller,
            tileOddColor = Color.White,
            tileEvenColor = Color.Black
        )
        BrightnessSlider(modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .height(35.dp)
            , controller = controller)

    }
}


@Composable
fun Marble(x: Float, y: Float, maxWidth: Int, maxHeight: Int, marbleViewModel: PaintViewModel ) {
    val radius = 50.dp
    val xMin = 0f
    val xMax = (maxWidth - (radius.value * 4).toInt())
    val yMin = 0f
    val yMax = (maxHeight - (radius.value * 4).toInt())

    val xPosition = x.coerceIn(xMin, xMax.toFloat())
    val yPosition = y.coerceIn(yMin, yMax.toFloat())

    Box(
        modifier = Modifier
            .offset { IntOffset(xPosition.roundToInt(), yPosition.roundToInt()) }
            .size(radius)
            .border(2.dp, Color.Black, shape = CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(radius)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            marbleViewModel.getLineColor(),
                            marbleViewModel.getLineColor()
                        ),
                        center = Offset(xPosition, yPosition),
                        radius = radius.value
                    ),
                    shape = CircleShape,
                )
                .graphicsLayer {
                    translationX = xPosition
                    translationY = yPosition
                }
        )
    }
}
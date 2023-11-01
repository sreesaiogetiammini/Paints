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
import android.widget.Toast
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt
import androidx.compose.material3.LinearProgressIndicator
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.callbackFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawScreen(navController: NavController, paintsRepository: PaintsRepository, userId: String,drawingName: String,sensorManager: SensorManager) {
    var sliderPosition by remember { mutableStateOf(10f) }
    var clickedBtn by remember { mutableStateOf(-1) }
    var isSliderDialogOpen by remember { mutableStateOf(false) }
    var isSaveDialogOpen by remember { mutableStateOf(false) }
    val myViewModel: PaintViewModel = viewModel()
    var isColorPickerDialogVisible by remember { mutableStateOf(false) }
    var paintingName by remember { mutableStateOf(drawingName) }
    var lines by remember { mutableStateOf(emptyList<Line>()) }
    val lineColor by rememberUpdatedState(myViewModel.lineColor)
    val lineStroke by rememberUpdatedState(myViewModel.lineStroke)
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
        painterResource(R.drawable.baseline_image_24),
        painterResource(R.drawable.baseline_cloud_download_24)
    )
    val gson: Gson = GsonBuilder()
        .registerTypeAdapter(ImageData::class.java, ImageDataTypeAdapter())
        .create()
    var isCapDialogOpen by remember { mutableStateOf(false) }
    var imageDataList by remember { mutableStateOf(emptyList<ImageData>()) }
    var imageUris by remember { mutableStateOf(emptyList<Uri>()) }
    val PhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = {uris ->
            imageUris = uris.toMutableList()
            imageUris.forEach{uri ->
                Log.e("uri", uri.toString())
                myViewModel.addImage(ImageData(uri, 0f,0f))
            }
        }
    )
    var textList by remember { mutableStateOf(emptyList<TextBox>()) }
    var imageUploadedTpFireBase by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val activity = (LocalView.current.context as? ComponentActivity)
    val aiPaintingEnabled by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedImagesFromServer by remember { mutableStateOf(mutableListOf<Uri>()) }
    Scaffold(
        topBar = {
           topBarStuff(
               navController = navController,
               userId = userId,
               myViewModel = myViewModel,
               activity = activity,
               context = context
           )
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
                                        myViewModel.updateLineColor(Color.Black)
                                    }

                                    if(i == 4){
                                        myViewModel.updateLineColor(Color.White)
                                    }
                                    if(i == 5){
                                        navController.navigate(Screen.DrawScreen.route + "/$userId" +"/dummy")
                                    }
                                    if(i == 6){
                                        val text = TextBox(0f,0f,"Add Text")
                                        myViewModel.addTexts(text)
                                    }
                                    if(i == 7){
                                        PhotoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    }
                                    if(i == 8){
                                        showBottomSheet = !showBottomSheet
                                    }
                                },
                                isClicked = isClicked,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        if(isCapDialogOpen){
                            addCapDialog(myViewModel = myViewModel,
                                onDialogDismiss = {
                                    isCapDialogOpen = !isCapDialogOpen
                                })
                        }

                        if (isSliderDialogOpen)
                        {
                            addSliderDialog(
                                myViewModel = myViewModel,
                                sliderPosition = sliderPosition,
                                onDialogDismiss = {
                                    isSliderDialogOpen = !isSliderDialogOpen
                                }
                            )
                        }


                        if (isColorPickerDialogVisible) {
                            addColorPickerDialog(
                                myViewModel = myViewModel,
                                onDialogDismiss = {
                                    isColorPickerDialogVisible = !isColorPickerDialogVisible
                                }
                            )
                        }




                        if (showBottomSheet) {
                            var serverImages by remember { mutableStateOf(emptyList<Uri>()) }

                            // Load server images from Firebase Storage
                            LaunchedEffect(Unit) {
                                serverImages = loadServerImages()
                            }
                            if (serverImages.size == 0) {
                                // Show a progress indicator while images are loading
                                LinearProgressIndicator(
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }

                            ModalBottomSheet(
                                onDismissRequest = {
                                    showBottomSheet = false
                                },
                                sheetState = sheetState
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ){
                                    Button(onClick = {
                                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                                            if (!sheetState.isVisible) {
                                                showBottomSheet = false
                                            }
                                        }
                                    }) {
                                        Text("Hide bottom sheet")
                                    }

                                    Button(onClick = {
                                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                                            if (!sheetState.isVisible) {
                                                showBottomSheet = false
                                            }
                                        }
                                        val numSelectedImages = selectedImagesFromServer.size

                                        // Display the number of images as a Toast
                                        Toast.makeText(
                                            context,
                                            "Selected $numSelectedImages images",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        val storage: FirebaseStorage = Firebase.storage
                                        val localImageUris = mutableListOf<Uri>()
                                        val serverImageUris = selectedImagesFromServer.toMutableList()

                                        scope.launch {
                                            serverImageUris.forEach { uri ->
                                                val localFileName = "image_${System.currentTimeMillis()}.png"
                                                val localFile = File(context.filesDir, localFileName)
                                                val storageReference: StorageReference = storage.getReferenceFromUrl(uri.toString())

                                                try {
                                                    // Download the image from Firebase Storage to the local file
                                                    val fileDownloadTask: FileDownloadTask.TaskSnapshot? = storageReference.getFile(localFile).await()
                                                    Log.i("File Download", "Success: " + Uri.fromFile(localFile).toString())
                                                    localImageUris.add(Uri.fromFile(localFile))
                                                }
                                                catch (e: Exception) {
                                                    Log.e("File Download Failed", "Error: " + e.message, e)
                                                }
                                            }

                                            Log.i("Local Files Download",localImageUris.size.toString())

                                            localImageUris.forEach { uri ->
                                                myViewModel.addImage(ImageData(uri,0f,0f))
                                            }
                                            selectedImagesFromServer.clear()

                                        }

                                    })


                                    {
                                        Text("Add Images")
                                    }
                                }


                                // Display the list of images in a LazyColumn
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(4),
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                )
                                {
                                    items(serverImages) { imageUri ->
                                        val (checkedState, onStateChange) = remember { mutableStateOf(false) }



                                        Box(
                                            modifier = Modifier
                                                .padding(8.dp)
                                                .border(2.dp, Color.Black)
                                        ){
                                            AsyncImage(model = imageUri, contentDescription = "Server Images" )
                                            val isChecked = selectedImagesFromServer.contains(imageUri)
                                            Checkbox(
                                                checked = checkedState, // Set the initial state as needed
                                                onCheckedChange = {
                                                    onStateChange(!checkedState)
                                                    if (!isChecked) {
                                                        selectedImagesFromServer.add(imageUri)
                                                        Log.i("Size after add",selectedImagesFromServer.size.toString())
                                                        // Add to the list when checked
                                                    } else {
                                                        selectedImagesFromServer.remove(imageUri)
                                                        Log.i("Size after remove",selectedImagesFromServer.size.toString())// Remove from the list when unchecked
                                                    }
                                                },
                                                modifier = Modifier
                                                    .align(Alignment.TopEnd)
                                            )
                                            IconButton(
                                                onClick = {
                                                    deleteImageFromFirebase(imageUri,context)
                                                    serverImages = serverImages.filter { it != imageUri }
                                                },
                                                modifier = Modifier
                                                    .align(Alignment.TopStart) // Position the close button in the top-right corner
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = "Delete Server Image"
                                                )
                                            }
                                        }

                                    }
                                }
                            }
                        }

                    }
                }
            }
        },
        floatingActionButton = {
            Column{

                FloatingActionButton(onClick = {
                    isSaveDialogOpen = !isSaveDialogOpen
                }) {
                    Icon(
                        painterResource(id = R.drawable.baseline_save_as_24),
                        contentDescription = "Save As"
                    )
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
            MarbleRollingApp(myViewModel,sensorManager)
            if (drawingName.isNotBlank() && drawingName != "dummy") {
                LaunchedEffect(Unit) {
                    val drawingData = paintsRepository.getDrawingByDrawingName(
                        drawingName = paintingName,
                        userId = id
                    )
                    if (drawingData != null) {
                        lines = deserializeDrawingData(drawingData.drawingData)
                        imageDataList = deserializeDrawingImages(drawingData.drawingImages)
                        textList = deserializeDrawingTexts(drawingData.drawingTexts)
                        for (line in lines) {
                            myViewModel.addLine(line)
                        }
                        for(image in imageDataList){
                            myViewModel.addImage(image)
                        }
                        for(text in textList){
                            myViewModel.addTexts(text)
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
                        myViewModel.addLine(line = line_custom)
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
                            val lines = myViewModel.getLines()
                            val images = myViewModel.getImages()
                            val texts = myViewModel.getTexts()
                            val drawingData = Gson().toJson(lines) // Serialize the drawing data to JSON
                            val drawingImages = gson.toJson(images)
                            Log.e("drawing Data", drawingImages)
                            val drawingTexts= Gson().toJson(texts)

                            val userId = id // Replace with the actual user ID
                            if (paintingName.isNotBlank()) {
                                val paintsData = PaintsData(
                                    userId = userId,
                                    drawingName = paintingName,
                                    drawingData = drawingData,
                                    drawingImages = drawingImages,
                                    drawingTexts = drawingTexts,
                                )

                                val existingDrawingData =
                                    paintsRepository.getDrawingByDrawingName(
                                        drawingName = paintingName,
                                        userId = userId
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


            myViewModel.getImages().forEach { uri ->
                var imageData by remember {
                    mutableStateOf(uri)
                }
                addImageToCanvas(imageData = imageData, myViewModel = myViewModel)
            }
            textList = myViewModel.getTexts()

            for (text in textList) {
                addTextField( myViewModel, text)
            }

        }
    }

}


@Composable
fun topBarStuff(
    navController: NavController,
    userId: String,
    myViewModel: PaintViewModel,
    activity: ComponentActivity?,
    context: Context
){

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
        var imageLoaded by remember { mutableStateOf(false) }

        if (imageLoaded) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )
        }

        Button(
            onClick = {
                imageLoaded = true
                takeScreenshot(myViewModel,activity, context)
                imageLoaded = false
            },
        ) {
            Image(painter = painterResource(id = R.drawable.baseline_upload_24),
                contentDescription = "Upload Image")
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

            colorPicker(myViewModel)
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
                modifier = Modifier
                    .offset {
                        IntOffset(position.x.roundToInt(), position.y.roundToInt())
                    }
                    .size(10.dp)
                    .background(marbleViewModel.getLineColor())
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

@Composable
fun addImageToCanvas(imageData: ImageData,myViewModel: PaintViewModel){
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    val windowInfo = rememberWindowInfo()
    var size = 200.dp
    when (windowInfo.screenWidthInfo) {
        WindowInfo.WindowType.Compact -> {
            size = 100.dp
        }
        WindowInfo.WindowType.Medium -> {
            size = 150.dp
        }
        WindowInfo.WindowType.Expanded -> {
            size = 200.dp
        }
    }

    Box(
        modifier = Modifier
            .size(size)
            .offset {
                IntOffset(
                    (imageData.x + offsetX).roundToInt(),
                    (imageData.y + offsetY).roundToInt()
                )
            }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                    myViewModel.updateImageDataBySrc(
                        imageData.src,
                        ImageData(imageData.src, offsetX, offsetY)
                    )
                    // myViewModel.updateImageCoordinates(imageData, offsetX, offsetY)

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
                Log.e("Image Cross",myViewModel.getImages().size.toString())
                myViewModel.removeImage(imageData)
                Log.e("Image Cross After",myViewModel.getImages().size.toString())
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close Image"
            )
        }
    }

}

@Composable
fun  addTextField(myViewModel: PaintViewModel,text : TextBox ){

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
                    myViewModel.updateTextCoordinates(text, offsetX, offsetY)
                }
            }
    ) {

        TextField(value = textValue, onValueChange = {
            textValue = it
            myViewModel.updateTextValue(text,textValue)
        })
        IconButton(
            onClick = {
                myViewModel.removeTexts(text)
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


fun takeScreenshot(myViewModel: PaintViewModel, activity: ComponentActivity?, context: Context) {
    if (activity == null) return


    val deviceWidth = Resources.getSystem().displayMetrics.widthPixels
    val deviceHeight = Resources.getSystem().displayMetrics.heightPixels

    val screenshot = myViewModel.captureCanvasAsBitmap(deviceWidth,deviceHeight, context = context)
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
    fos.close()

//    val shareIntent: Intent = Intent().apply {
//        action = Intent.ACTION_SEND
//        putExtra(Intent.EXTRA_STREAM, uri)
//        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
//        type = "image/png"
//    }
//
//    context.startActivity(Intent.createChooser(shareIntent, "Share Screenshot"))

    uploadImageToFirebase(uri,fileName,context)



}

suspend fun loadServerImages(): List<Uri> {
    val storage: FirebaseStorage = Firebase.storage
    val storageReference: StorageReference = storage.getReference()
    val listResult = storageReference.listAll().await()
    val imageUris = listResult.items.mapNotNull { item ->
        try {
            val uri = item.downloadUrl.await()
            Uri.parse(uri.toString())
        } catch (e: Exception) {
            null
        }
    }

    return imageUris
}

fun uploadImageToFirebase(imageUri:Uri,storagePath: String,context: Context){
    var storageRef = FirebaseStorage.getInstance().getReference().child(storagePath)
    storageRef.putFile(imageUri)
        .addOnSuccessListener { taskSnapshot ->
            Toast.makeText(
                context,
                "Image Upload Sucess:: "+taskSnapshot.uploadSessionUri.toString(),
                Toast.LENGTH_LONG
            ).show()

        }
        .addOnFailureListener { exception ->
            Toast.makeText(
                context,
                "Image Upload Failed:: "+exception.message,
                Toast.LENGTH_LONG
            ).show()

        }
}



fun uploadImageToFirebase(imageUri: Uri, storagePath: String): Flow<UploadTask.TaskSnapshot?> {
    val storage: FirebaseStorage = Firebase.storage
    val storageRef: StorageReference = storage.getReference().child(storagePath)

    return callbackFlow {
        val uploadTask = storageRef.putFile(imageUri)

        uploadTask
            .addOnSuccessListener { taskSnapshot ->
                trySend(taskSnapshot).isSuccess
                close()
            }
            .addOnFailureListener { exception ->
                close(CancellationException("Image Upload Failed:: ${exception.message}"))
            }

        awaitClose { uploadTask.cancel() }
    }
}




// Function to delete an image in Firebase Storage
fun deleteImageFromFirebase(imageUri: Uri,context: Context){
        val storage: FirebaseStorage = Firebase.storage
        val imageReference = storage.getReferenceFromUrl(imageUri.toString())
        imageReference.delete().addOnSuccessListener {
            Toast.makeText(
                context,
                "Image Delete Success" ,
                Toast.LENGTH_LONG
            ).show()
        }.addOnFailureListener {
            Toast.makeText(
                context,
                "Image Delete Failure" ,
                Toast.LENGTH_LONG
            ).show()
        }

}

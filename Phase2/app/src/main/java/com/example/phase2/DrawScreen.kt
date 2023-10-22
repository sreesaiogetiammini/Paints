package com.example.phase2

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.util.Log
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
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
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun DrawScreen(navController: NavController, paintsRepository: PaintsRepository, userId: String,drawingName: String,sensorManager: SensorManager) {
    var presses by remember { mutableStateOf(0) }
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
    var isCapDialogOpen by remember { mutableStateOf(false) }
    var imageUris by remember { mutableStateOf(mutableListOf<Uri>()) }
    val PhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = {uris ->
            imageUris = uris.toMutableList()
            imageUris.forEach{uri ->
                myviewModel.addImage(uri)
            }
        }
    )




    Scaffold(
        topBar = {
            topBarStuff(navController = navController, userId = id)
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
                                        myviewModel.addTexts("Add Text")
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
            FloatingActionButton(onClick = { presses++ }) {
                Button(onClick = {
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
            MarbleRollingApp(myviewModel,sensorManager)
            if (drawingName.isNotBlank() && drawingName != "dummy") {
                        LaunchedEffect(Unit) {
                            val drawingData = paintsRepository.getDrawingByDrawingName(
                                drawingName = paintingName,
                                userId = id.toLong()
                            )
                            if (drawingData != null) {
                                lines = deserializeDrawingData(drawingData.drawingData)
                                for (line in lines) {
                                    myviewModel.addLine(line)
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
                            val drawingData = Gson().toJson(lines) // Serialize the drawing data to JSON
                            val userId = id // Replace with the actual user ID
                            if (paintingName.isNotBlank()) {
                                val paintsData = PaintsData(
                                    userId = userId.toLong(),
                                    drawingName = paintingName,
                                    drawingData = drawingData
                                )

                                val existingDrawingData =
                                    paintsRepository.getDrawingByDrawingName(
                                        drawingName = paintingName,
                                        userId = userId.toLong()
                                    )

                                if (existingDrawingData != null) {
                                    // Update the existing painting
                                    existingDrawingData.drawingData = drawingData
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
                var offsetX by remember { mutableStateOf(0f) }
                var offsetY by remember { mutableStateOf(0f) }
                AsyncImage(
                    model = uri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                        .size(200.dp)
                        .pointerInput(Unit) {

                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                offsetX += dragAmount.x
                                offsetY += dragAmount.y
                            }
                        },
                )

            }
            val textList = myviewModel.getTexts().toMutableList()
            for (index in textList.indices) {
                var offsetX by remember { mutableStateOf(0f) }
                var offsetY by remember { mutableStateOf(0f) }

                val textValue = remember { mutableStateOf(textList[index]) }

                TextField(
                    value = textValue.value,
                    onValueChange = {
                        textValue.value = it
                        textList[index] = it
                    },
                    modifier = Modifier
                        .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                        .size(200.dp,50.dp)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                offsetX += dragAmount.x
                                offsetY += dragAmount.y
                            }
                        }
                )
            }

        }
    }

}





@Composable
fun MarbleRollingApp(marbleViewModel: PaintViewModel, sensorManager: SensorManager) {
    val marbleOffset = marbleViewModel.gravityOffset.value

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val maxWidth = constraints.maxWidth
        val maxHeight = constraints.maxHeight

        LaunchedEffect(sensorManager) {
            val accelMagFlow = updateGravityData(sensorManager, marbleViewModel, dampingFactor = 2f)
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


fun updateGravityData(sensorManager: SensorManager, marbleViewModel: PaintViewModel, dampingFactor: Float): Flow<Offset> {

    return channelFlow {

        val alpha: Float = 0.8f
        val gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
        var currentOffset = Offset(10f, 10f)
        val sensorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (event !== null) {
                    Log.i("Gravity Event Changes",event.values[0].toString())
                    val gravityX =event.values[0]
                    val gravityY = event.values[1]
                    val deltaX = gravityX * dampingFactor
                    val deltaY = gravityY * dampingFactor

                    currentOffset = Offset(
                        currentOffset.x + deltaX,
                        currentOffset.y + deltaY,

                        )
                    marbleViewModel.gravityOffset.value = currentOffset
                    Log.i("Current LogSet",currentOffset.x.toString())
                    trySend(currentOffset).isSuccess
                }
                else{
                    Log.e("Event Null" ,"Gge")
                }
            }


            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

            }
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
fun topBarStuff(navController: NavController,userId:String){
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






fun deserializeDrawingData(drawingData: String?): List<Line> {
    val gson = Gson()
    val listType = object : TypeToken<List<Line>>() {}.type
    return gson.fromJson(drawingData, listType)
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
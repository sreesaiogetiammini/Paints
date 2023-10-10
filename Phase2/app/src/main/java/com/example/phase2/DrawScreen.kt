package com.example.phase2

import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.ColorPickerController
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.math.roundToInt


@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class,
)
@Composable
fun DrawScreen(navController: NavController, paintsRepository: PaintsRepository, drawingName: String) {
    var presses by remember { mutableStateOf(0) }
    var sliderPosition by remember { mutableStateOf(10f) }
    var clickedBtn by remember { mutableStateOf(-1) }
    var isSliderDialogOpen by remember { mutableStateOf(false) }
    var isSaveDialogOpen by remember { mutableStateOf(false) }
    val viewModel: PaintViewModel = viewModel()
    var isColorPickerDialogVisible by remember { mutableStateOf(false) }
    var selectedLineColor by remember { mutableStateOf(Color.Green) } // Default color
    var paintingName by remember { mutableStateOf(drawingName) }
    val icons = listOf(
        painterResource(R.drawable.baseline_draw_24),
        painterResource(R.drawable.baseline_line_style_24),
        painterResource(R.drawable.baseline_color_lens_24),
        painterResource(R.drawable.erasor),
        painterResource(R.drawable.baseline_text_fields_24),
        painterResource(R.drawable.baseline_image_24) ,
        painterResource(R.drawable.baseline_clear_all_24)
    )
    var isCapDialogOpen by remember { mutableStateOf(false) }
    val myViewModel: PaintViewModel = viewModel()

    Scaffold(
        topBar = {
            // Top app bar content
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF800080)),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.paints),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .background(Color(0xFF800080)), // Color.Purple
                    )
                    IconButton(
                        onClick = { navController.navigate(Screen.LoginScreen.route) },
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Account User"
                        )
                    }
                    IconButton(
                        onClick = { navController.navigate(Screen.UserScreen.route) },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Account Home"
                        )
                    }
                }
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
//                                        isColorPickerDialogVisible = true
                                        isCapDialogOpen = !isCapDialogOpen
                                    }
                                    if(i == 2){
                                        isColorPickerDialogVisible = true
                                    }
                                    if(i == 6){
                                        myViewModel.clearLines()
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
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { presses++ }) {
                Button(onClick = {
                    isSaveDialogOpen = true
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            var lines by remember { mutableStateOf(emptyList<Line>()) }
            val lineColor by rememberUpdatedState(viewModel.lineColor)
            val lineStroke by rememberUpdatedState(viewModel.lineStroke)
            val scope = rememberCoroutineScope()

            if(drawingName.isNotBlank() && drawingName != "dummy") {
                LaunchedEffect(Unit){
                    val drawingData = paintsRepository.getDrawingByDrawingName( drawingName = paintingName, userId = 1)

                    if (drawingData != null) {
                        lines = deserializeDrawingData(drawingData.drawingData)
                        for (line in lines) {
                            viewModel.addLine(line)
                        }
                    }
                }
            }


            Canvas(
                modifier = Modifier
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
                            lines = lines + line_custom // Append the new line to the list of lines
                            viewModel.addLine(line = line_custom)
                        }
                    }
            ) {
                lines.forEach { line ->
                    drawLine(
                        color = line.color,
                        start = line.start,
                        end = line.end,
                        strokeWidth = line.strokeWidth.width,
                        cap = StrokeCap.Round
                    )
                }
            }

            if (isColorPickerDialogVisible) {
                Dialog(
                    onDismissRequest = {
                        // Close the color picker dialog
                        isColorPickerDialogVisible = false
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(450.dp)
                            .padding(10.dp)
                    ) {
                        HsvColorPicker(
                            modifier = Modifier.fillMaxSize(),
                            controller = ColorPickerController(),
                            onColorChanged = { colorEnvelope: ColorEnvelope ->
                                selectedLineColor = Color(colorEnvelope.color.toArgb())
                                viewModel.updateLineColor(selectedLineColor)
                            }
                        )

                        // Close button
                        IconButton(
                            onClick = {
                                isColorPickerDialogVisible = false
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

            // Save Drawing Confirmation Dialog
            if (isSaveDialogOpen) {
                SaveDrawingDialog(
                    onSave = {
                        scope.launch {
                            val lines = viewModel.getLines()
                            val drawingData = Gson().toJson(lines) // Serialize the drawing data to JSON
                            val userId = 1 // Replace with the actual user ID
//                            val drawingName = "painting1"

                            if(paintingName.isNotBlank()) {
                                val paintsData = PaintsData(
                                    userId = userId.toLong(),
                                    drawingName = paintingName,
                                    drawingData = drawingData
                                )

                                val existingDrawingData = paintsRepository.getDrawingByDrawingName(
                                    drawingName = paintingName,
                                    userId = 1
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
                    initalName = drawingName
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveDrawingDialog(
    onSave: () -> Unit,
    onDismiss: () -> Unit,
    onNameChange: (String) -> Unit,
    initalName: String
) {
    // Create and display your custom dialog here
    // Include buttons for confirmation and dismissal
    // You can use a Dialog Composable or a custom AlertDialog
    // Example using Dialog Composable:
    var paintingName by remember { mutableStateOf(initalName) }
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
                }
            ) {
                Text("Confirm Save")
            }

            // Include a button to dismiss the dialog
            Button(
                onClick = {
                    onDismiss()
                }
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
            contentDescription = null,
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
                    style = MaterialTheme.typography.bodyMedium,
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
                        myViewModel.updatePathStrokeWidth(sliderPosition)
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
                    Button(onClick = {myViewModel.updatePathStrokeCap(StrokeCap.Butt)}) {
                        Icon(
                            painterResource(id = R.drawable.baseline_butt_24),
                            contentDescription = "Butt Cap"
                        )
                    }
                    Button(onClick = {myViewModel.updatePathStrokeCap(StrokeCap.Square)}) {
                        Icon(
                            painterResource(id = R.drawable.baseline_square_24),
                            contentDescription = "Square Cap"
                        )
                    }
                    Button(onClick = {myViewModel.updatePathStrokeCap(StrokeCap.Round)}) {
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



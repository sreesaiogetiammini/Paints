package com.example.phase2

import android.content.Context
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
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.runBlocking
import kotlin.math.roundToInt

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class,
)
@Composable
fun DrawScreen(navController: NavController, context: Context, paintsRepository: PaintsRepository) {
    var presses by remember { mutableStateOf(0) }
    var sliderPosition by remember { mutableStateOf(10f) }
    var clickedBtn by remember { mutableStateOf(-1) }
    var isSliderDialogOpen by remember { mutableStateOf(false) }
    var isSaveDialogOpen by remember { mutableStateOf(false) }
    val viewModel: PaintViewModel = viewModel()

    val icons = listOf(
        painterResource(R.drawable.baseline_draw_24),
        painterResource(R.drawable.baseline_line_style_24),
        painterResource(R.drawable.baseline_color_lens_24),
        painterResource(R.drawable.erasor),
        painterResource(R.drawable.baseline_text_fields_24),
        painterResource(R.drawable.baseline_image_24)
    )

    Scaffold(
        topBar = {
            // Top app bar content
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
                                        viewModel.updateLineColor(Color.Green)
                                    }
                                },
                                isClicked = isClicked,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        if (isSliderDialogOpen) {
                            Dialog(
                                onDismissRequest = { isSliderDialogOpen = false }
                            ) {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.background),
                                    shape = MaterialTheme.shapes.medium,
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = sliderPosition.roundToInt().toString(),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.Black,
                                            modifier = Modifier.padding(bottom = 16.dp)
                                        )
                                        Slider(
                                            modifier = Modifier.semantics { contentDescription = "Slider" },
                                            value = sliderPosition,
                                            onValueChange = {
                                                sliderPosition = it
                                            },
                                            valueRange = 10f..100f,
                                            onValueChangeFinished = {
                                                viewModel.updateLineStroke(Stroke(sliderPosition))
                                            },
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // Show a confirmation dialog
                    isSaveDialogOpen = true
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    )

    { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val lines = viewModel.getLines()
            val drawing = DatabaseHelper(context)
//            val dName = "painting1"
//            val lines = deserializeDrawingData(drawing.getDrawingByDrawingName(userID = 1, drawingName = "painting1"))
            val lineColor = viewModel.lineColor
            val lineStroke = viewModel.lineStroke

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

            // Save Drawing Confirmation Dialog
            if (isSaveDialogOpen) {
                SaveDrawingDialog(
                    onSave = {
                        // Save the drawing
                        val lines = viewModel.getLines()
                        val drawingData = Gson().toJson(lines) // Serialize the drawing data to JSON
                        val userId = 1 // Replace with the actual user ID
                        val dbHelper = DatabaseHelper(context)
                        val drawingName = "painting1"

                        Thread {
//                            val id = dbHelper.insertDrawing(userId, drawingName = drawingName, drawingData)
                            val paintsData = PaintsData(
                                userId = userId.toLong(),
                                drawingName = drawingName,
                                drawingData = drawingData
                            )

                            runBlocking {
                                paintsRepository.insertPaintsData(paintsData)
                            }
//                            Log.e("id", id.toString())
//                            if (id > 0) {
//                                // Drawing saved successfully
//                                Log.e("data saved", "true")
//
//                            } else {
//                                // Handle save failure
//                                Log.e("data saved", "failed")
//                            }
                        }.start()

                        isSaveDialogOpen = false
                    },
                    onDismiss = {
                        isSaveDialogOpen = false // Close the dialog on dismiss
                    }
                )
            }
        }
    }
}

@Composable
fun SaveDrawingDialog(
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    // Create and display your custom dialog here
    // Include buttons for confirmation and dismissal
    // You can use a Dialog Composable or a custom AlertDialog
    // Example using Dialog Composable:
    Dialog(
        onDismissRequest = { onDismiss() }
    ) {
        // Dialog content and buttons
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


fun deserializeDrawingData(drawingData: Any?): List<Line> {
    val gson = Gson()
    val listType = object : TypeToken<List<Line>>() {}.type
    return gson.fromJson(drawingData.toString(), listType)
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

package com.example.phase2

import android.graphics.Bitmap
import android.icu.text.ListFormatter.Width
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.createBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import kotlin.math.roundToInt

@Preview(showBackground = true)
@Composable
fun DrawScreenPreview() {
    val navController = rememberNavController() // Create a dummy NavController
    DrawScreen(navController = navController)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun DrawScreen(navController: NavController) {
    var presses by remember { mutableIntStateOf(0) }
    var sliderPosition by remember { mutableStateOf(10f) }
    var clickedBtn by remember { mutableStateOf(-1) }
    var isSliderDialogOpen by remember { mutableStateOf(false) }
    val viewModel: PaintViewModel = viewModel()
    var isSaveDialogOpen by remember {
        mutableStateOf(false)
    }

    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp
    val screenHeightDp = configuration.screenHeightDp.dp

    val density = LocalDensity.current.density
    val screenWidth = (screenWidthDp.value * density).roundToInt()
    val screenHeight = (screenHeightDp.value * density).roundToInt()


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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF800080))
                ,
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
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
            }

        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth(),
            )
            {
                Column(modifier = Modifier.fillMaxSize()){
                    Row(

                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                        // evenly space the icons
                    )
                    {
                        for (i in 0 until icons.size) {
                            val isClicked = i == clickedBtn

                            AnimatedIconButton(
                                icon = icons[i],
                                onClick = {
                                    // Toggle the button's state
                                    if (isClicked) {
                                        // Self-clicked, unclick all buttons
                                        clickedBtn = -1

                                    }
                                    else {
                                        // Clicked a different button, unclick others
                                        clickedBtn = i
                                    }

                                    if(i == 0){
                                        isSliderDialogOpen = !isSliderDialogOpen
                                    }
                                    if( i == 1){
                                        viewModel.updateLineColor(Color.Green)
                                    }

                                },

                                isClicked = isClicked,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        if (isSliderDialogOpen) {
                            Dialog(
                                onDismissRequest = { isSliderDialogOpen = false}
                            )
                            {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.background), // Background color
                                    shape = MaterialTheme.shapes.medium,
                                ){
                                    Column( modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = sliderPosition.toString() ,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.Black,
                                            modifier = Modifier.padding(bottom = 16.dp)
                                        )
                                        Slider(
                                            modifier = Modifier.semantics { contentDescription = "Localized Description" },
                                            value = sliderPosition,
                                            onValueChange = { sliderPosition = it },
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
            Column(
                verticalArrangement = Arrangement.Bottom
            )
            {
                FloatingActionButton(onClick = {
                    isSaveDialogOpen = true
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        }
    )

    { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ){
            val lines = viewModel.getLines()
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
            )
            {
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
        }



    }

    fun convertToBitMap(lines: List<Line>) : Bitmap {
        val bitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(bitmap)
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
            contentDescription = null, // Provide an appropriate content description
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
fun DrawCanvas(
    modifier: Modifier,
    lines: List<Line>,
    linesColor: Color,
    linesStroke: Dp
){
    val density = LocalDensity.current.density
    val strokePx = with(LocalDensity.current) {linesStroke.toPx()}
    val paint = remember {
        Paint().apply {
            color = linesColor
            strokeWidth = strokePx
            isAntiAlias = true
            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
        }
    }

    AndroidView(
        modifier = modifier,
        factory = {context ->
            val bitmapWidth = context.resources.displayMetrics.widthPixels
            val bitmapHeight = context.resources.displayMetrics.heightPixels
            createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888)
        },
        update = {canvasBitmap ->

            val canvas = canvasBitmap.nativeCanvas

            canvas.drawColor(Color.White.toArgb()) // Clear the canvas

            lines.forEach { line ->
                canvas.drawLine(
                    line.start.x,
                    line.start.y,
                    line.end.x,
                    line.end.y,
                    paint
                )
            }
        }
    )
}


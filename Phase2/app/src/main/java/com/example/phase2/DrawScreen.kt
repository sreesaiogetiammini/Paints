package com.example.phase2

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Picture
import android.os.Environment
import android.util.Log
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.net.toUri
import androidx.core.view.drawToBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


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
    var isCapDialogOpen by remember { mutableStateOf(false) }
    var addText by remember { mutableStateOf("") }
    val myViewModel: PaintViewModel = viewModel()

    val icons = listOf(
        painterResource(R.drawable.baseline_draw_24),
        painterResource(R.drawable.baseline_line_style_24),
        painterResource(R.drawable.baseline_color_lens_24),
        painterResource(R.drawable.erasor),
        painterResource(R.drawable.baseline_text_fields_24),
        painterResource(R.drawable.baseline_image_24) ,
        painterResource(R.drawable.baseline_clear_all_24)
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

                                    } else {
                                        // Clicked a different button, unclick others
                                        clickedBtn = i
                                    }

                                    if (i == 0) {
                                        isSliderDialogOpen = !isSliderDialogOpen
                                    }
                                    if(i == 1){
                                        isCapDialogOpen = !isCapDialogOpen
                                    }
                                    if (i == 2) {
                                        myViewModel.updatePathColor(Color.Green)
                                    }
                                    if (i == 3) {
                                        myViewModel.updatePathStrokeCap(StrokeCap.Round)
                                        myViewModel.updatePathColor(Color.White)
                                    }
//                                    if(i == 4){
//                                        myViewModel.updateText(addText)
//                                    }
//                                    if(i == 5){
//                                        myViewModel.updateText(addText)
//                                    }

                                    if(i == 6){
                                        myViewModel.clearPaths()
                                    }

                                },

                                isClicked = isClicked,
                                modifier = Modifier.weight(1f)
                            )
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


                        if(isCapDialogOpen){
                            addCapDialog(myViewModel = myViewModel,
                                onDialogDismiss = {
                                    isCapDialogOpen = !isCapDialogOpen
                                })
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { presses++ }) {
                Button(onClick = {


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

        ){
            DrawCanvas(viewModel = myViewModel)

        }



    }
}



@Composable
fun DrawCanvas(viewModel: PaintViewModel) {
    val paths = viewModel.getPaths()
    val pathColor = viewModel.pathColor
    val pathStroke = viewModel.pathStroke


    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .clipToBounds()
            .background(Color.White)
            .pointerInput(true) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    // Draw path
                    val customPath = MyPath(
                        color = pathColor.value,
                        stroke = pathStroke.value
                    )
                    val startPosition = change.position - dragAmount
                    customPath.path.moveTo(startPosition.x, startPosition.y)
                    customPath.path.lineTo(change.position.x, change.position.y)
                    viewModel.addPath(path = customPath)
                }
            }
    )
    {

        // Draw paths
        paths.forEach { path ->
            drawPath(
                path = path.path,
                color = path.color,
                style = path.stroke
            )
        }

    }

}

@Composable
fun AnimatedIconButton(icon: Painter, onClick: () -> Unit, isClicked: Boolean, modifier: Modifier) {
    val size by animateDpAsState(targetValue = if (isClicked) 62.dp else 38.dp)
    Box(
        modifier = modifier
            .size(size)
            .clickable {
                onClick()
            }
    )
    {
        Image(
            painter = icon,
            contentDescription = null, // Provide an appropriate content description
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

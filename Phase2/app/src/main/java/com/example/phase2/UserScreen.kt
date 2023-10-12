package com.example.phase2

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@Composable
fun UserScreen(navController: NavController, paintsRepository: PaintsRepository,userId: String) {

    val scope = rememberCoroutineScope()
    var drawingNames by remember {
        mutableStateOf<List<String>>(emptyList()) // Provide an initial empty list
    }

// Inside a Composable
    LaunchedEffect(1){
        scope.launch {
            var id = userId // Replace with the actual user ID
            drawingNames = paintsRepository.getPaintingNamesByUserId(userId.toLong())
        }
    }

//    val drawingNames = paintsRepository.getPaintingNamesByUserId(1)
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
                .fillMaxSize()
                .weight(1f) // Ensure that the ScrollableList takes available space
        ) {
            ScrollableList(
                drawingNames = drawingNames,
                navController = navController,
                paintsRepository = paintsRepository,
                userId = userId
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
    userId: String
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
fun ListItem(drawingName: String, navController: NavController,paintsRepository:PaintsRepository,userId: String) {
    // Composable for rendering each list item
    // You can customize this based on your item's content
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
        ) {
            DropdownMenuItem(text = {
                Text("Edit")
            }, onClick = {
                Log.e("Edit Name",drawingName)
                navController.navigate(Screen.DrawScreen.route + "/$userId" +"/$drawingName")
                isDropDownVisible = false;
            })
            DropdownMenuItem(text = {
                Text("Share")
            }, onClick = {
                isDropDownVisible = false;
            })
            DropdownMenuItem(
                text = { Text("Delete") },
               onClick = { isDropDownVisible = false; })

        }
    }
}



@Composable
fun deleteDrawingName(drawingName: String, userId: Long,paintsRepository:PaintsRepository) {
    val scope = rememberCoroutineScope()
    // Call the repository function to delete the drawing by name and user ID
    LaunchedEffect(1){
        scope.launch {
            paintsRepository.deletePaintingByDrawingName(drawingName, userId)
        }
    }
}
package com.example.phase2

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch


@Composable
fun UserScreen(navController: NavController, paintsRepository: PaintsRepository) {

    val scope = rememberCoroutineScope()

    var drawingNames by remember {
        mutableStateOf<List<String>>(emptyList()) // Provide an initial empty list
    }

// Inside a Composable
    LaunchedEffect(1){
        scope.launch {
            val userId = 1 // Replace with the actual user ID
            drawingNames = paintsRepository.getPaintingNamesByUserId(1)
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
                .height(200.dp)
                .background(Color(0xFF800080)), // Color.Purple
        )


        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f) // Ensure that the ScrollableList takes available space
        ) {
            ScrollableList(drawingNames = drawingNames, navController = navController)
        }


        FloatingActionButton(
            onClick = { navController.navigate(Screen.DrawScreen.route + "/dummy")  },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.End)
        ) {
            Icon(Icons.Filled.Add, "Floating action button.")

        }
    }
}

@Composable
fun ScrollableList(drawingNames: List<String>, navController: NavController) {
//    val items = (1..50).toList() // Replace with your list of items
    val windowInfo= rememberWindowInfo()
    if(windowInfo.screenWidthInfo is WindowInfo.WindowType.Compact){
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            items(drawingNames) { drawingName ->
                // Each item in the list
                ListItem(drawingName, navController = navController )
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
                ListItem(drawingName, navController)
            }
        }
    }

}

@Composable
fun ListItem(drawingName: String, navController: NavController) {
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
        BasicTextField(
            value = drawingName,
            onValueChange = { /* Handle text change if needed */ },
            modifier = Modifier
                .weight(1f)
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
                navController.navigate(Screen.DrawScreen.route + "/$drawingName" )
                isDropDownVisible = false;
            })
            DropdownMenuItem(text = {
                Text("Share")
            }, onClick = {
                isDropDownVisible = false;
            })
            DropdownMenuItem(text = {
                Text("Delete")
            }, onClick = {
                isDropDownVisible = false;
            })
        }
    }
}



//@Preview(showBackground = true)
//@Composable
//fun UserScreenPreview() {
//    val navController = rememberNavController() // Create a dummy NavController
//    UserScreen(navController = navController, paintsRepository = FakePa)
//}
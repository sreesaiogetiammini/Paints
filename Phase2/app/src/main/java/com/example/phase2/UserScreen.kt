package com.example.phase2

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController



@Composable
fun UserScreen(navController: NavController) {
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
            ScrollableList()
        }


        FloatingActionButton(
            onClick = { navController.navigate(Screen.DrawScreen.route) },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.End)
        ) {
            Icon(Icons.Filled.Add, "Floating action button.")

        }
    }
}

@Composable
fun ScrollableList() {
    val items = (1..50).toList() // Replace with your list of items
    val windowInfo= rememberWindowInfo()
    if(windowInfo.screenWidthInfo is WindowInfo.WindowType.Compact){
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            items(items) { item ->
                // Each item in the list
                ListItem(item)
            }
        }
    }
    else{
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(items) { item ->
                // Each item in the list
                ListItem(item)
            }
        }
    }

}

@Composable
fun ListItem(item: Int) {
    // Composable for rendering each list item
    // You can customize this based on your item's content
    BasicTextField(
        value = "Item $item",
        onValueChange = { /* Handle text change if needed */ },
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    )
}



@Preview(showBackground = true)
@Composable
fun UserScreenPreview() {
    val navController = rememberNavController() // Create a dummy NavController
    UserScreen(navController = navController)
}
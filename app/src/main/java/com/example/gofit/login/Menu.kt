package com.example.gofit.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun Menu( navigationController: NavHostController){

    Box(
        Modifier
            .fillMaxSize()
            .padding(8.dp)
            .background(Color.White)


    ) {
        Text(text = "Menu")
    }
}
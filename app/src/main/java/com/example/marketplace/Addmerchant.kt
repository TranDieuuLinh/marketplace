package com.example.marketplace

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.marketplace.ui.theme.marketplace_light_onPrimary
import com.example.marketplace.ui.theme.marketplace_light_onSurface
import com.example.marketplace.ui.theme.marketplace_light_onSurfaceVariant
import com.example.marketplace.ui.theme.marketplace_light_outline
import com.example.marketplace.ui.theme.marketplace_light_primary
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException


class Addmerchant {


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProduct(productViewModel: ProductViewModel,navController: NavController){

    val context = LocalContext.current
    var imageBase64: String? by remember { mutableStateOf(null) }
    val firestore = FirebaseFirestore.getInstance()
    val productsCollection = firestore.collection("products")
    val email: String? = navController.previousBackStackEntry?.savedStateHandle?.get("email")
    val username: String? = navController.previousBackStackEntry?.savedStateHandle?.get("username")
    var imageUri: Uri? by remember {
        mutableStateOf(null)
    }
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument()) {
        it?.let { uri ->
            imageUri = uri
            val bitmap = uriToBitmap(context, uri)
            imageBase64 = bitmap?.let { bitmapToBase64(it) }  // Update this line to set the base64 string
        }
    }
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val states = listOf("VIC", "QLD", "NSW", "SA", "TAS", "WA", "ACT", "NT")
    var isExpanded by remember { mutableStateOf(false) }
    var selectedState by remember { mutableStateOf(states[0]) }
    var showDialog by remember { mutableStateOf(false) }
    fun validateFields(): Boolean {
        if (name.isEmpty() || brand.isEmpty() || quantity.isEmpty() || price.isEmpty() || imageBase64 == null) {
            Toast.makeText(context, "Please enter all the information", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }
    Scaffold( topBar = {
        CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor =  marketplace_light_primary,
                titleContentColor =  marketplace_light_onPrimary,
            ),
            title = {
                Text(
                    "Add Product",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {
                IconButton(onClick = {
                    navController.currentBackStackEntry?.savedStateHandle?.set("email", email)
                    navController.currentBackStackEntry?.savedStateHandle?.set("username",username)
                    navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Localized description",
                        tint = marketplace_light_onPrimary,
                        )
                }
            },
        )
    },
        bottomBar = {
            BottomAppBar(
                containerColor =  marketplace_light_primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                actions = {
                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        modifier = Modifier
                            .fillMaxWidth()
                    ){
                        IconButton(onClick = {
                            //Maintain user identify during navigation
                            navController.currentBackStackEntry?.savedStateHandle?.set("email", email)
                            navController.currentBackStackEntry?.savedStateHandle?.set("username",username)
                            navController.navigate("home")}) {
                            Icon(Icons.Filled.Home,
                                contentDescription = "Localized description",
                                tint = marketplace_light_onPrimary)
                        }
                        IconButton(onClick = {
                            //Maintain user identify during navigation
                            navController.currentBackStackEntry?.savedStateHandle?.set("email", email)
                            navController.currentBackStackEntry?.savedStateHandle?.set("username",username)
                            navController.navigate("contact")
                        },) {
                            Icon(
                                Icons.Filled.MailOutline,
                                contentDescription = "Localized description",
                                tint = marketplace_light_onPrimary
                                )
                        }
                        IconButton(onClick = {
                            //Maintain user identify during navigation
                            navController.currentBackStackEntry?.savedStateHandle?.set("email", email)
                            navController.currentBackStackEntry?.savedStateHandle?.set("username",username)
                            navController.navigate("Addmerchant") }) {
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = "Localized description",
                                tint = marketplace_light_outline,
                                )
                        }
                        IconButton(onClick = {
                            //Maintain user identify during navigation
                            navController.currentBackStackEntry?.savedStateHandle?.set("email", email)
                            navController.currentBackStackEntry?.savedStateHandle?.set("username",username)
                            navController.navigate("Favourites") }) {
                            Icon(
                                Icons.Filled.Favorite,
                                contentDescription = "Localized description",
                                tint = marketplace_light_onPrimary,
                                )
                        }
                        LogoutButton(navController)

                    }
                          },

            )
        },
    )   { paddingValues ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
            ) {

            Box(
                modifier = Modifier
                    .size(370.dp, 120.dp)
                    .border(
                        BorderStroke(0.5.dp, color = marketplace_light_onSurfaceVariant), // Border stroke with color and width
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (imageUri != null) {
                        // Display the image
                        AsyncImage(
                            model = imageUri,
                            contentDescription = null,
                            modifier = Modifier.size(150.dp),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        // Show placeholder text
                        Text("+ Add the image here", fontSize = 18.sp)
                    }

                    // Button to pick image
                    Button(
                        onClick = {
                            launcher.launch(arrayOf("image/*"))
                        },
                        modifier = Modifier.padding(top = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = marketplace_light_primary),
                        ) {
                        Text(text = "Pick Image")
                    }
                }
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Product Name") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Price") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)

            )
            OutlinedTextField(
                value = quantity,
                onValueChange = {  quantity = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text("Quantity") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)

            )
            Row(modifier = Modifier.padding(start = 8.dp, top = 8.dp), horizontalArrangement= Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically) {
                ExposedDropdownMenuBox(expanded = isExpanded, onExpandedChange = { isExpanded = it }) {
                    TextField(
                        modifier = Modifier
                            .menuAnchor()
                            .height(65.dp)
                            .width(100.dp)
                            .focusProperties {
                                canFocus = false
                            }
                            .padding(bottom = 8.dp),
                        readOnly = true,
                        value = selectedState,
                        onValueChange = {},
                        label = { Text("State") },
                        //manages the arrow icon up and down
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
                        },
                    )
                    ExposedDropdownMenu(
                        expanded = isExpanded,
                        onDismissRequest = { isExpanded = false })
                    {
                        states.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    selectedState = selectionOption
                                    isExpanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            )
                        }
                    }
                }
                Box(modifier = Modifier
                    .padding(start = 8.dp)
                    .height(80.dp)){
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("address") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 8.dp)
                    )
                }
            }
            OutlinedTextField(
                value = brand,
                onValueChange = { brand = it },
                label = { Text("Brand") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)

            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)

            )
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp)) {

                Button(onClick = { navController.navigate("Map") },
                    colors = ButtonDefaults.buttonColors(containerColor = marketplace_light_primary),
                ) {
                    Text("   Map   ")
                }
                Text("                                               ")
                Button(onClick = {

                    if (validateFields()){showDialog = true}
                     },
                    colors = ButtonDefaults.buttonColors(containerColor = marketplace_light_onSurface),

                    ) {
                        Text("Confirm")}

                }
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Confirm Action") },
                    text = { Text("Do you want to confirm or cancel this action?") },
                    confirmButton = {
                        Button(onClick = {
                            productViewModel.insertProduct(
                                Product(name = name,photo = imageBase64!!,price = price, quantity = quantity, state = selectedState,address = address, description = description ))
                            val newProduct = hashMapOf(
                                "name" to name,
                                "photo" to imageBase64!!,
                                "price" to price,
                                "quantity" to quantity,
                                "state" to selectedState,
                                "address" to address,
                                "description" to description,
                                "ownerEmail" to email,
                                "ownerName" to username
                            )
                            productsCollection.add(newProduct)
                            Toast.makeText(context, "Product added successfully", Toast.LENGTH_SHORT).show()
                            showDialog = false
                        }) {
                            Text("Confirm")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }

    }

}
    fun uriToBitmap(context: Context, imageUri: Uri): Bitmap? {
        return try {
            context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            null
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
    fun bitmapToBase64(bitmap: Bitmap): String {
        ByteArrayOutputStream().apply {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, this)
            val byteArray = this.toByteArray()
            return Base64.encodeToString(byteArray, Base64.DEFAULT)
        }
    }
}

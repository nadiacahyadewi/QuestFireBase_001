package com.example.firebase.ui.home.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.firebase.model.Mahasiswa
import com.example.firebase.ui.PenyediaViewModel
import com.example.firebase.ui.home.viewmodel.HomeUiState
import com.example.firebase.ui.home.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToItemEntry: () -> Unit,
    modifier: Modifier = Modifier,
    onDetailClick: (String) -> Unit = {},
    viewModel: HomeViewModel = viewModel(factory = PenyediaViewModel.Factory)
){
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold (
        modifier = Modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text("Home")
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToItemEntry,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(18.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Kontak")
            }
        },
    ){innerPadding ->
        HomeStatus(
            homeUiState = viewModel.mhsUiState,
            retryAction = {viewModel.getMhs()}, modifier = Modifier.padding(innerPadding),
            onDetailClick = onDetailClick,
            onDeleteClick = { mahasiswa ->
                viewModel.deleteMhs(mahasiswa)
            }
        )
    }
}

@Composable
fun HomeStatus(
    homeUiState: HomeUiState,
    retryAction:() -> Unit,
    modifier: Modifier=Modifier,
    onDeleteClick: (Mahasiswa) -> Unit = {},
    onDetailClick: (String) -> Unit
){
    var deleteConfirmationRequired by rememberSaveable { mutableStateOf<Mahasiswa?>(null) }

    when(homeUiState){
        is HomeUiState.Loading -> OnLoading(modifier = modifier.fillMaxSize())

        is HomeUiState.Success -> {
            MhsLayout(
                mahasiswa = homeUiState.data, modifier = modifier.fillMaxWidth(), onDetailClick = {
                    onDetailClick(it)
                },
                onDeleteClick = { mahasiswa -> deleteConfirmationRequired = mahasiswa

                }
            )
            deleteConfirmationRequired?.let { data ->
                DeleteConfirmationDialog(
                    onDeleteConfirm = {
                        onDeleteClick(data)
                        deleteConfirmationRequired = null
                    },
                    onDeleteCancel = {
                        deleteConfirmationRequired = null
                    })
            }
        }

        is HomeUiState.Error -> OnError(
            retryAction,
            modifier = modifier.fillMaxSize(),
            message = homeUiState.e.message ?: "Error"
        )
    }
}

@Composable
fun OnLoading(modifier: Modifier = Modifier){
    Text("Loading......")
}

//The home screen displaying error message with re-attempt button

@Composable
fun OnError(retryAction: () -> Unit, modifier: Modifier = Modifier, message: String){
    Column (
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        Text(
            text = "terjadi kesalahan : $message",
        )
        Button(
            onClick = retryAction) {
            Text("Retry")
        }
    }
}

@Composable
fun MhsLayout(
    mahasiswa: List<Mahasiswa>,
    modifier: Modifier = Modifier,
    onDetailClick: (String) -> Unit,
    onDeleteClick: (Mahasiswa) -> Unit = {}
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp) // Mengurangi jarak antar card
    ) {
        items(
            items = mahasiswa,
            itemContent = { mhs ->
                MhsCard(
                    mahasiswa = mhs,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {  },
                    onDeleteClick = {
                        onDeleteClick(mhs)
                    }
                )
            }
        )
    }
}

@Composable
fun MhsCard(
    mahasiswa: Mahasiswa,
    modifier: Modifier = Modifier,
    onDeleteClick: (Mahasiswa) -> Unit = {}
) {
    Card (
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ){
        Column (
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ){
            Row (
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = mahasiswa.nama,
                    style = MaterialTheme.typography.titleLarge,
                )
                Spacer(Modifier.weight(1f))
                IconButton(onClick = { onDeleteClick(mahasiswa) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null
                    )
                }
                Text(
                    text = mahasiswa.nim,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Text(
                text = mahasiswa.alamat,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = mahasiswa.judulSkripsi,
                style = MaterialTheme.typography.titleMedium
            )

        }
    }
}

@Composable
private fun DeleteConfirmationDialog(
    onDeleteConfirm: () -> Unit, onDeleteCancel: () -> Unit, modifier: Modifier = Modifier
){
    AlertDialog(onDismissRequest = { /* Do nothing */ },
        title = { Text("Delete Data") },
        text = { Text("Apakah anda yakin ingin menghapus data?") },
        modifier = modifier,
        dismissButton = {
            TextButton(onClick = onDeleteCancel) {
                Text(text = "Cancel")
            }
        },
        confirmButton = {
            TextButton(onClick = onDeleteConfirm) {
                Text(text = "Yes")
            }
        }
    )
}


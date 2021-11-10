package com.example.wms.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.wms.app.viewModels.OperationalViewModel
import org.burnoutcrew.reorderable.*

@ExperimentalMaterialApi
@Composable
fun ReorderableList(operationalViewModel: OperationalViewModel) {
    val state = rememberReorderState()
    val data = operationalViewModel.fullPalletsList
    LazyColumn(
        state = state.listState,
        modifier = Modifier.reorderable(state, { from, to -> data.move(from.index, to.index) })
    ) {
        items(items = data, key = { it.index }) { pallet ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .draggedItem(state.offsetByKey(pallet))
                    .detectReorderAfterLongPress(state)
            ) {
                PalletIItemListSwipe(
                    pallet = pallet,
                    viewModel = operationalViewModel
                )

            }
        }
    }
}




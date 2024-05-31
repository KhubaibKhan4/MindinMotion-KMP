package org.mind.app.presentation.ui.screens.quiz.subcategory

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import org.mind.app.domain.model.subcategories.SubCategoriesItem
import org.mind.app.domain.model.subquestions.SubQuestionsItem
import org.mind.app.presentation.ui.screens.home.SubCategoryCard
import org.mind.app.presentation.ui.screens.home.TopCollectionCard

class ScreenAll(
    private val subCategoriesItem: List<SubCategoriesItem>,
    private val subQuestionsItem: List<SubQuestionsItem>,
    private val categoryName: String,
) : Screen {
    @Composable
    override fun Content() {
        SeeAllContent(subCategoriesItem, subQuestionsItem, categoryName)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeeAllContent(
    subCategoryItems: List<SubCategoriesItem>,
    subQuestionsItems: List<SubQuestionsItem>,
    categoryName: String,
) {
    val navigator = LocalNavigator.current
    Scaffold(
        modifier = Modifier.fillMaxWidth(),
        topBar = {
            TopAppBar(
                title = { Text(text = categoryName, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            navigator?.pop()
                        }
                    )
                }
            )
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(paddingValues = it)
                .padding(start = 8.dp, end = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(subCategoryItems) { subCategoryItem ->
                    if (categoryName.contains("Top Collections")) {
                        TopCollectionCard(subCategoryItem, subQuestionsItems)
                    } else {
                        SubCategoryCard(subCategoryItem, subQuestionsItems)
                    }
                }
            }
        }
    }

}
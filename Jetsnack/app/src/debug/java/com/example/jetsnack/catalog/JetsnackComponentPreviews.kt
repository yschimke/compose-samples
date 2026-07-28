/*
 * Copyright 2026 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jetsnack.catalog

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetsnack.R
import com.example.jetsnack.model.Filter
import com.example.jetsnack.model.SearchCategory as SearchCategoryModel
import com.example.jetsnack.model.SearchRepo
import com.example.jetsnack.model.SnackRepo
import com.example.jetsnack.model.snacks
import com.example.jetsnack.ui.LocalSharedTransitionScope
import com.example.jetsnack.ui.components.FilterBar
import com.example.jetsnack.ui.components.JetsnackButton
import com.example.jetsnack.ui.components.JetsnackPreviewWrapper
import com.example.jetsnack.ui.components.JetsnackSnackbar
import com.example.jetsnack.ui.components.JetsnackSurface
import com.example.jetsnack.ui.components.QuantitySelector
import com.example.jetsnack.ui.components.SnackCollection
import com.example.jetsnack.ui.components.SnackImage
import com.example.jetsnack.ui.components.SnackItem
import com.example.jetsnack.ui.components.VerticalGrid
import com.example.jetsnack.ui.home.HomeSections
import com.example.jetsnack.ui.home.JetsnackBottomBar
import com.example.jetsnack.ui.home.JetsnackBottomNavigationItem
import com.example.jetsnack.ui.home.cart.CartItem
import com.example.jetsnack.ui.home.cart.SummaryItem
import com.example.jetsnack.ui.home.cart.SwipeDismissItem
import com.example.jetsnack.ui.home.search.NoResults
import com.example.jetsnack.ui.home.search.SearchBar
import com.example.jetsnack.ui.home.search.SearchCategory
import com.example.jetsnack.ui.home.search.SearchCategories
import com.example.jetsnack.ui.home.search.SearchResult
import com.example.jetsnack.ui.home.search.SearchResults
import com.example.jetsnack.ui.home.search.SearchSuggestions
import com.example.jetsnack.ui.snackdetail.SnackDetail
import com.example.jetsnack.ui.theme.DarkColorPalette
import com.example.jetsnack.ui.theme.JetsnackColors
import com.example.jetsnack.ui.theme.JetsnackTheme
import com.example.jetsnack.ui.theme.LightColorPalette
import com.example.jetsnack.ui.theme.ProvideJetsnackColors

/*
 * `@Preview`s for Jetsnack's components.
 *
 * The sample ships previews for its four foundation atoms (button, card, divider, icon button) and
 * a handful of leaf items, but the composables in between — the bottom bar, the filter bar, the
 * cart row, the summary block, the search surfaces, the snackbar, the swipe-to-dismiss affordance
 * and the bespoke `VerticalGrid` layout — had none. Several of them are also the components whose
 * *state* is the interesting part (which tab is selected, whether the filter sheet is open, a
 * quantity of 1 versus 12), and a single preview cannot show that, so those are previewed as
 * explicit variant pairs.
 *
 * Everything is driven by the data the app itself ships (`SnackRepo`, `SearchRepo`, `snacks`), so
 * no fixture here can drift from the real content. These live in the `debug` source set, so
 * nothing reaches a release build.
 *
 * Note on wrapping: many Jetsnack components read `LocalSharedTransitionScope` /
 * `LocalNavAnimatedVisibilityScope` and throw "No shared element scope" if they are absent. The
 * sample's own `JetsnackPreviewWrapper` supplies both alongside `JetsnackTheme`, so it — not bare
 * `JetsnackTheme` — is the correct wrapper for anything below the atom level.
 */

private val cart = SnackRepo.getCart()
private val snack = snacks.first()

@Composable
private fun Wrap(content: @Composable () -> Unit) = JetsnackPreviewWrapper {
    JetsnackSurface { Box(Modifier.padding(8.dp)) { content() } }
}

// ------------------------------------------------------------------ surface

@Preview("default")
@Preview("dark theme", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun JetsnackSurfaceFlatPreview() = Wrap {
    JetsnackSurface(shape = RoundedCornerShape(12.dp), elevation = 0.dp) {
        Text("Flat surface — elevation 0dp", Modifier.padding(16.dp))
    }
}

@Preview("default")
@Preview("dark theme", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun JetsnackSurfaceElevatedPreview() = Wrap {
    JetsnackSurface(shape = RoundedCornerShape(12.dp), elevation = 8.dp) {
        Text("Elevated surface — elevation 8dp", Modifier.padding(16.dp))
    }
}

// ------------------------------------------------------------------ button states

@Preview("default")
@Preview("dark theme", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun JetsnackButtonDisabledPreview() = Wrap {
    JetsnackButton(onClick = {}, enabled = false) {
        Text("Add to cart")
    }
}

// ------------------------------------------------------------------ quantity stepper states

@Preview("default")
@Preview("dark theme", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun QuantitySelectorSinglePreview() = Wrap {
    QuantitySelector(count = 1, decreaseItemCount = {}, increaseItemCount = {})
}

@Preview("default")
@Preview("dark theme", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun QuantitySelectorManyPreview() = Wrap {
    QuantitySelector(count = 12, decreaseItemCount = {}, increaseItemCount = {})
}

// ------------------------------------------------------------------ snackbar

private class PreviewSnackbarVisuals(override val message: String, override val actionLabel: String?) : SnackbarVisuals {
    override val withDismissAction: Boolean = false
    override val duration: SnackbarDuration = SnackbarDuration.Short
}

private class PreviewSnackbarData(override val visuals: SnackbarVisuals) : SnackbarData {
    override fun performAction() = Unit
    override fun dismiss() = Unit
}

@Preview("default")
@Preview("dark theme", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun JetsnackSnackbarPreview() = Wrap {
    JetsnackSnackbar(
        snackbarData = PreviewSnackbarData(
            PreviewSnackbarVisuals("Added to your cart", actionLabel = null),
        ),
    )
}

@Preview("default")
@Preview("dark theme", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun JetsnackSnackbarWithActionPreview() = Wrap {
    JetsnackSnackbar(
        snackbarData = PreviewSnackbarData(
            PreviewSnackbarVisuals("Removed from your cart", actionLabel = "Undo"),
        ),
    )
}

// ------------------------------------------------------------------ vertical grid layout

@Composable
private fun GridCell(index: Int) {
    JetsnackSurface(
        shape = RoundedCornerShape(8.dp),
        color = JetsnackTheme.colors.uiFloated,
        modifier = Modifier.padding(4.dp),
    ) {
        Text("Cell $index", Modifier.padding(16.dp))
    }
}

@Preview("default")
@Composable
fun VerticalGridTwoColumnPreview() = Wrap {
    VerticalGrid(columns = 2) { repeat(6) { GridCell(it) } }
}

@Preview("default")
@Composable
fun VerticalGridThreeColumnPreview() = Wrap {
    VerticalGrid(columns = 3) { repeat(6) { GridCell(it) } }
}

// ------------------------------------------------------------------ snack surfaces

@Preview("default")
@Preview("dark theme", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun SnackItemPreview() = Wrap {
    SnackItem(snack = snack, snackCollectionId = 1L, onSnackClick = { _, _ -> })
}

@Preview("default")
@Composable
fun SnackImagePreview() = Wrap {
    SnackImage(
        imageRes = snack.imageRes,
        contentDescription = snack.name,
        elevation = 4.dp,
        modifier = Modifier.size(120.dp),
    )
}

@Preview("default", heightDp = 320)
@Preview("dark theme", uiMode = UI_MODE_NIGHT_YES, heightDp = 320)
@Composable
fun SnackCollectionHighlightPreview() = Wrap {
    SnackCollection(
        snackCollection = SnackRepo.getSnacks().first(),
        onSnackClick = { _, _ -> },
        highlight = true,
    )
}

@Preview("default", heightDp = 320)
@Composable
fun SnackCollectionNormalPreview() = Wrap {
    SnackCollection(
        snackCollection = SnackRepo.getSnacks().first(),
        onSnackClick = { _, _ -> },
        highlight = false,
    )
}

// ------------------------------------------------------------------ filter bar

@Preview("default")
@Preview("dark theme", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun FilterBarPreview() = Wrap {
    FilterBar(
        filters = previewFilters(),
        onShowFilters = {},
        filterScreenVisible = false,
        sharedTransitionScope = LocalSharedTransitionScope.current!!,
    )
}

@Preview("default")
@Composable
fun FilterBarSheetOpenPreview() = Wrap {
    FilterBar(
        filters = previewFilters(),
        onShowFilters = {},
        filterScreenVisible = true,
        sharedTransitionScope = LocalSharedTransitionScope.current!!,
    )
}

/**
 * Fresh [Filter] instances per preview. The shipped `filters` list holds `MutableState` that a
 * preview toggling selection would mutate for every *other* preview in the same render run.
 */
private fun previewFilters() = listOf(
    Filter(name = "Organic", enabled = true),
    Filter(name = "Gluten-free"),
    Filter(name = "Dairy-free"),
    Filter(name = "Sweet"),
    Filter(name = "Savory"),
)

// ------------------------------------------------------------------ cart rows

@Preview("default")
@Preview("dark theme", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun CartItemPreview() = Wrap {
    CartItem(
        orderLine = cart.first(),
        removeSnack = {},
        increaseItemCount = {},
        decreaseItemCount = {},
        onSnackClick = { _, _ -> },
    )
}

@Preview("default")
@Preview("dark theme", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun SummaryItemPreview() = Wrap {
    SummaryItem(subtotal = 134500L, shippingCosts = 36000L)
}

@Preview("default")
@Composable
fun SwipeDismissItemPreview() = Wrap {
    SwipeDismissItem(
        background = { progress ->
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .background(JetsnackTheme.colors.error.copy(alpha = 0.2f + progress * 0.8f)),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_delete_forever),
                    contentDescription = "Remove",
                    modifier = Modifier.padding(end = 24.dp),
                )
            }
        },
        content = {
            JetsnackSurface {
                Text("Swipe to remove", Modifier.padding(20.dp))
            }
        },
    )
}

// ------------------------------------------------------------------ search surfaces

@Preview("catalog")
@Composable
fun SearchBarCatalogPreview() = Wrap {
    SearchBar(
        query = TextFieldValue(),
        onQueryChange = {},
        searchFocused = false,
        onSearchFocusChange = {},
        onClearQuery = {},
        searching = false,
    )
}

@Preview("default")
@Preview("dark theme", uiMode = UI_MODE_NIGHT_YES)
@Preview("large font", fontScale = 2f, widthDp = 412, heightDp = 220)
@Composable
fun SearchCategoryCatalogPreview() = Wrap {
    SearchCategory(
        category = SearchCategoryModel(
            name = "Desserts",
            imageRes = R.drawable.desserts,
        ),
        gradient = JetsnackTheme.colors.gradient3_2,
    )
}

@Preview("default", heightDp = 400)
@Preview("dark theme", uiMode = UI_MODE_NIGHT_YES, heightDp = 400)
@Composable
fun SearchCategoriesPreview() = Wrap {
    SearchCategories(categories = SearchRepo.getCategories())
}

@Preview("default", heightDp = 400)
@Composable
fun SearchSuggestionsPreview() = Wrap {
    SearchSuggestions(
        suggestions = SearchRepo.getSuggestions(),
        onSuggestionSelect = {},
    )
}

@Preview("default")
@Preview("dark theme", uiMode = UI_MODE_NIGHT_YES)
@Preview("large font", fontScale = 2f, widthDp = 412, heightDp = 220)
@Composable
fun SearchResultCatalogPreview() = Wrap {
    SearchResult(
        snack = snacks[0],
        onSnackClick = { _, _ -> },
        showDivider = false,
    )
}

@Preview("default", heightDp = 400)
@Preview("dark theme", uiMode = UI_MODE_NIGHT_YES, heightDp = 400)
@Composable
fun SearchResultsPreview() = Wrap {
    SearchResults(searchResults = snacks.take(4), onSnackClick = { _, _ -> })
}

@Preview("default")
@Preview("dark theme", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun NoResultsPreview() = Wrap {
    NoResults(query = "kombucha")
}

@Preview("catalog", device = "spec:width=400dp,height=800dp,dpi=160")
@Composable
fun SnackDetailCatalogPreview() {
    JetsnackPreviewWrapper {
        SnackDetail(
            snackId = 1L,
            origin = "details",
            upPress = {},
        )
    }
}

// ------------------------------------------------------------------ bottom navigation

@Composable
private fun BottomBar(current: HomeSections) = Wrap {
    JetsnackBottomBar(
        tabs = HomeSections.entries.toTypedArray(),
        currentRoute = current.route,
        navigateToRoute = {},
    )
}

@Preview("default")
@Preview("dark theme", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun JetsnackBottomBarFeedPreview() = BottomBar(HomeSections.FEED)

@Preview("default")
@Composable
fun JetsnackBottomBarSearchPreview() = BottomBar(HomeSections.SEARCH)

@Preview("default")
@Composable
fun JetsnackBottomBarCartPreview() = BottomBar(HomeSections.CART)

@Preview("default")
@Composable
fun JetsnackBottomBarProfilePreview() = BottomBar(HomeSections.PROFILE)

@Composable
private fun NavItem(selected: Boolean) = Wrap {
    JetsnackBottomNavigationItem(
        icon = {
            Icon(
                painter = painterResource(HomeSections.SEARCH.icon),
                contentDescription = null,
                tint = JetsnackTheme.colors.iconInteractive,
            )
        },
        text = {
            Text(
                text = "Search",
                style = MaterialTheme.typography.labelLarge,
                color = JetsnackTheme.colors.iconInteractive,
            )
        },
        selected = selected,
        onSelected = {},
        animSpec = tween(0),
        modifier = Modifier.background(Brush.horizontalGradient(JetsnackTheme.colors.interactivePrimary)),
    )
}

@Preview("default", device = "spec:width=400dp,height=800dp,dpi=160")
@Composable
fun JetsnackBottomNavigationItemSelectedPreview() = NavItem(selected = true)

@Preview("default", device = "spec:width=400dp,height=800dp,dpi=160")
@Composable
fun JetsnackBottomNavigationItemUnselectedPreview() = NavItem(selected = false)

// ------------------------------------------------------------------ design tokens
//
// The `@ThemeCatalog` sheets in JetsnackThemeCatalogs.kt project the Jetsnack palette onto M3
// roles so the plugin's canned specimen grid can read it. These sheets instead show the palette
// as the app actually models it — including the seven gradient ramps, which have no M3 role to
// project onto and so appear nowhere else in the catalog.

@Composable
private fun Swatch(name: String, color: Color) = Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.padding(vertical = 2.dp),
) {
    Box(
        Modifier
            .size(28.dp)
            .background(color, RoundedCornerShape(4.dp)),
    )
    Spacer(Modifier.width(10.dp))
    Text(name, style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Monospace)
}

@Composable
private fun ColorTokens(colors: JetsnackColors) = ProvideJetsnackColors(colors) {
    JetsnackSurface {
        Column(Modifier.padding(12.dp)) {
            Swatch("brand", colors.brand)
            Swatch("brandSecondary", colors.brandSecondary)
            Swatch("uiBackground", colors.uiBackground)
            Swatch("uiBorder", colors.uiBorder)
            Swatch("uiFloated", colors.uiFloated)
            Swatch("textPrimary", colors.textPrimary)
            Swatch("textSecondary", colors.textSecondary)
            Swatch("textHelp", colors.textHelp)
            Swatch("textInteractive", colors.textInteractive)
            Swatch("textLink", colors.textLink)
            Swatch("iconPrimary", colors.iconPrimary)
            Swatch("iconSecondary", colors.iconSecondary)
            Swatch("iconInteractive", colors.iconInteractive)
            Swatch("iconInteractiveInactive", colors.iconInteractiveInactive)
            Swatch("error", colors.error)
        }
    }
}

@Preview("light tokens", heightDp = 560)
@Composable
fun JetsnackColorTokensLightPreview() = JetsnackTheme(darkTheme = false) {
    ColorTokens(LightColorPalette)
}

@Preview("dark tokens", heightDp = 560)
@Composable
fun JetsnackColorTokensDarkPreview() = JetsnackTheme(darkTheme = true) {
    ColorTokens(DarkColorPalette)
}

@Composable
private fun Ramp(name: String, ramp: List<Color>) = Column(Modifier.padding(vertical = 4.dp)) {
    Text(name, style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Monospace)
    Box(
        Modifier
            .padding(top = 2.dp)
            .fillMaxWidth()
            .height(24.dp)
            .background(Brush.horizontalGradient(ramp), RoundedCornerShape(4.dp)),
    )
}

@Composable
private fun GradientTokens(colors: JetsnackColors) = ProvideJetsnackColors(colors) {
    JetsnackSurface {
        Column(
            Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Ramp("gradient6_1", colors.gradient6_1)
            Ramp("gradient6_2", colors.gradient6_2)
            Ramp("gradient3_1", colors.gradient3_1)
            Ramp("gradient3_2", colors.gradient3_2)
            Ramp("gradient2_1 / interactivePrimary", colors.gradient2_1)
            Ramp("gradient2_2 / interactiveSecondary", colors.gradient2_2)
            Ramp("gradient2_3", colors.gradient2_3)
            Ramp("tornado1", colors.tornado1)
        }
    }
}

@Preview("light gradients", heightDp = 470)
@Composable
fun JetsnackGradientTokensPreview() = JetsnackTheme(darkTheme = false) {
    GradientTokens(LightColorPalette)
}

@Preview("dark gradients", heightDp = 470)
@Composable
fun JetsnackGradientTokensDarkPreview() = JetsnackTheme(darkTheme = true) {
    GradientTokens(DarkColorPalette)
}

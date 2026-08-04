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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.example.jetsnack.model.SnackRepo
import com.example.jetsnack.ui.components.JetsnackPreviewWrapper
import com.example.jetsnack.ui.home.Feed
import com.example.jetsnack.ui.home.FilterScreen
import com.example.jetsnack.ui.home.Profile
import com.example.jetsnack.ui.home.cart.Cart
import com.example.jetsnack.ui.home.search.Search

/*
 * Whole-screen `@Preview`s that must be captured early enough for strict catalog generation.
 */

@Preview("default", device = "spec:width=400dp,height=800dp,dpi=160")
@Preview(
    "large font",
    fontScale = 2f,
    device = "spec:width=700dp,height=800dp,dpi=160",
)
@Composable
@PreviewLightDark
fun FeedScreenPreview() = JetsnackPreviewWrapper {
    Feed(onSnackClick = { _, _ -> })
}

@Preview("default", device = "spec:width=400dp,height=800dp,dpi=160")
@Preview(
    "large font",
    fontScale = 2f,
    device = "spec:width=700dp,height=800dp,dpi=160",
)
@Composable
@PreviewLightDark
fun CartScreenPreview() = JetsnackPreviewWrapper {
    Cart(
        orderLines = SnackRepo.getCart(),
        removeSnack = {},
        increaseItemCount = {},
        decreaseItemCount = {},
        inspiredByCart = SnackRepo.getInspiredByCart(),
        onSnackClick = { _, _ -> },
    )
}

@Preview("empty", device = "spec:width=400dp,height=800dp,dpi=160")
@Composable
fun CartScreenEmptyPreview() = JetsnackPreviewWrapper {
    Cart(
        orderLines = emptyList(),
        removeSnack = {},
        increaseItemCount = {},
        decreaseItemCount = {},
        inspiredByCart = SnackRepo.getInspiredByCart(),
        onSnackClick = { _, _ -> },
    )
}

@Preview("default")
@Preview("large font", fontScale = 2f, widthDp = 412, heightDp = 800)
@Composable
@PreviewLightDark
fun ProfileScreenPreview() = JetsnackPreviewWrapper {
    Profile()
}

@Preview("search", device = "spec:width=400dp,height=800dp,dpi=160")
@Composable
@PreviewLightDark
fun SearchScreenPreview() = JetsnackPreviewWrapper {
    Search(onSnackClick = { _, _ -> })
}

@Preview("filter screen")
@Composable
@PreviewLightDark
fun FilterScreenCatalogPreview() = JetsnackPreviewWrapper {
    SharedTransitionLayout {
        AnimatedVisibility(true) {
            FilterScreen(
                animatedVisibilityScope = this,
                sharedTransitionScope = this@SharedTransitionLayout,
                onDismiss = {},
            )
        }
    }
}

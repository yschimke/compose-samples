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
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.jetsnack.model.SnackRepo
import com.example.jetsnack.ui.components.JetsnackPreviewWrapper
import com.example.jetsnack.ui.home.Feed
import com.example.jetsnack.ui.home.cart.Cart
import com.example.jetsnack.ui.home.search.Search

/*
 * Whole-screen `@Preview`s for the three Jetsnack destinations that had none that render.
 *
 * Jetsnack ships `HomePreview` (Feed.kt) and `CartPreview` (Cart.kt), but **both fail to render**:
 * they wrap the screen in bare `JetsnackTheme`, and every Jetsnack screen reaches
 * `DestinationBar`, which hard-throws `IllegalStateException("No shared element scope")` when
 * `LocalSharedTransitionScope` / `LocalNavAnimatedVisibilityScope` are absent. Those two previews
 * therefore produce `.error.json` sidecars rather than PNGs, which is why the catalog spec
 * previously described the feed and the cart as unrenderable and covered them only by their
 * constituent parts.
 *
 * They are renderable — the sample already ships the fix, it just is not used at those two call
 * sites. `JetsnackPreviewWrapper` (Snacks.kt) provides `JetsnackTheme` *plus* a
 * `SharedTransitionLayout` and an `AnimatedVisibility` scope bound into both composition locals,
 * which is exactly what `DestinationBar` demands. Wrapping the same screens in it renders them
 * fine, so these previews cover the feed, the cart and search as whole screens.
 *
 * The main-source `HomePreview` / `CartPreview` are deliberately left untouched: fixing them is a
 * change to shipped sample code and out of scope for the catalog, and their failure is documented
 * as pre-existing. These debug-source previews are what the catalog references instead.
 */

@Preview("feed", heightDp = 800)
@Preview("feed dark", uiMode = UI_MODE_NIGHT_YES, heightDp = 800)
@Composable
fun FeedScreenPreview() = JetsnackPreviewWrapper {
    Feed(onSnackClick = { _, _ -> })
}

@Preview("cart", heightDp = 800)
@Preview("cart dark", uiMode = UI_MODE_NIGHT_YES, heightDp = 800)
@Composable
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

@Preview("search", heightDp = 800)
@Preview("search dark", uiMode = UI_MODE_NIGHT_YES, heightDp = 800)
@Composable
fun SearchScreenPreview() = JetsnackPreviewWrapper {
    Search(onSnackClick = { _, _ -> })
}

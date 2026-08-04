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

package com.example.reply.catalog

import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.reply.data.local.LocalEmailsDataProvider
import com.example.reply.ui.ReplyEmailDetail
import com.example.reply.ui.ReplyEmailList
import com.example.reply.ui.components.EmailDetailAppBar
import com.example.reply.ui.components.ReplyDockedSearchBar
import com.example.reply.ui.components.ReplyEmailListItem
import com.example.reply.ui.components.ReplyEmailThreadItem
import com.example.reply.ui.components.ReplyProfileImage
import com.example.reply.ui.components.SelectedProfileImage
import com.example.reply.ui.navigation.ModalNavigationDrawerContent
import com.example.reply.ui.navigation.PermanentNavigationDrawerContent
import com.example.reply.ui.navigation.ReplyBottomNavigationBar
import com.example.reply.ui.navigation.ReplyNavigationRail
import com.example.reply.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.example.reply.ui.theme.ContrastAwareReplyTheme
import com.example.reply.ui.utils.ReplyNavigationContentPosition

/**
 * `@Preview`s for Reply's components and screens.
 *
 * Before this file the sample had previews for exactly two things: [
 * com.example.reply.ui.EmptyComingSoon] and the whole app at five window sizes. Every component and
 * screen composable in between — the list item, the thread item, the detail pane, the search bar,
 * and all four navigation surfaces — had none, so the only way to see any of them was to render the
 * entire app and hope the state you cared about happened to be on screen.
 *
 * These previews drive each component directly with the sample data the app itself ships
 * ([LocalEmailsDataProvider]), so the states that matter for review — an opened item, a selected
 * item, an email with attachments, a rail pinned top vs centre — are addressable one at a time.
 * They live in the `debug` source set so nothing here reaches a release build.
 */

private val email = LocalEmailsDataProvider.allEmails[0]
private val threadEmail = LocalEmailsDataProvider.allEmails[1]
private val emails = LocalEmailsDataProvider.allEmails

@Composable
private fun Wrap(dark: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) = ContrastAwareReplyTheme(darkTheme = dark) {
    Surface { Box(Modifier.padding(8.dp)) { content() } }
}

// ---------------------------------------------------------------- profile images

@Preview(name = "ProfileImage", showBackground = true)
@Composable
@PreviewLightDark
fun ReplyProfileImagePreview() = Wrap {
    ReplyProfileImage(email.sender.avatar, email.sender.fullName, Modifier.padding(4.dp))
}

@Preview(name = "ProfileImage — selected", showBackground = true)
@Composable
fun SelectedProfileImagePreview() = Wrap { SelectedProfileImage(Modifier.padding(4.dp)) }

// ---------------------------------------------------------------- email list item

@Preview(name = "EmailListItem", showBackground = true, widthDp = 400)
@Composable
@PreviewLightDark
fun ReplyEmailListItemPreview() = Wrap {
    ReplyEmailListItem(email = email, navigateToDetail = {}, toggleSelection = {})
}

@Preview(name = "EmailListItem — opened", showBackground = true, widthDp = 400)
@Composable
fun ReplyEmailListItemOpenedPreview() = Wrap {
    ReplyEmailListItem(email = email, navigateToDetail = {}, toggleSelection = {}, isOpened = true)
}

@Preview(name = "EmailListItem — selected", showBackground = true, widthDp = 400)
@Composable
fun ReplyEmailListItemSelectedPreview() = Wrap {
    ReplyEmailListItem(email = email, navigateToDetail = {}, toggleSelection = {}, isSelected = true)
}

@Preview(name = "EmailListItem — opened and selected", showBackground = true, widthDp = 400)
@Composable
fun ReplyEmailListItemOpenedSelectedPreview() = Wrap {
    ReplyEmailListItem(
        email = email,
        navigateToDetail = {},
        toggleSelection = {},
        isOpened = true,
        isSelected = true,
    )
}

@Preview(name = "EmailListItem — dark", showBackground = true, widthDp = 400, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ReplyEmailListItemDarkPreview() = Wrap(dark = true) {
    ReplyEmailListItem(email = email, navigateToDetail = {}, toggleSelection = {})
}

// ---------------------------------------------------------------- thread item

@Preview(name = "EmailThreadItem", showBackground = true, widthDp = 400)
@Composable
@PreviewLightDark
fun ReplyEmailThreadItemPreview() = Wrap { ReplyEmailThreadItem(email = threadEmail) }

@Preview(name = "EmailThreadItem — dark", showBackground = true, widthDp = 400, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ReplyEmailThreadItemDarkPreview() = Wrap(dark = true) {
    ReplyEmailThreadItem(email = threadEmail)
}

// ---------------------------------------------------------------- app bars

@Preview(name = "SearchBar", showBackground = true, widthDp = 400)
@Composable
@PreviewLightDark
fun ReplyDockedSearchBarPreview() = Wrap {
    ReplyDockedSearchBar(emails = emails, onSearchItemSelected = {})
}

@Preview(name = "SearchBar — expanded", showBackground = true, widthDp = 400, heightDp = 180)
@Composable
fun ReplyDockedSearchBarExpandedPreview() = Wrap {
    ReplyDockedSearchBar(
        emails = emails,
        onSearchItemSelected = {},
        initialExpanded = true,
    )
}

@Preview(name = "SearchBar — results", showBackground = true, widthDp = 400, heightDp = 420)
@Composable
fun ReplyDockedSearchBarResultsPreview() = Wrap {
    ReplyDockedSearchBar(
        emails = emails,
        onSearchItemSelected = {},
        initialQuery = email.subject.take(4),
        initialExpanded = true,
    )
}

@Preview(name = "DetailAppBar — full screen", showBackground = true, widthDp = 400)
@Composable
@PreviewLightDark
fun EmailDetailAppBarFullScreenPreview() = Wrap {
    EmailDetailAppBar(email = email, isFullScreen = true, onBackPressed = {})
}

@Preview(name = "DetailAppBar — pane", showBackground = true, widthDp = 400)
@Composable
fun EmailDetailAppBarPanePreview() = Wrap {
    EmailDetailAppBar(email = email, isFullScreen = false, onBackPressed = {})
}

// ---------------------------------------------------------------- screens

@Preview(name = "EmailList", showBackground = true, widthDp = 400, heightDp = 700)
@Composable
@PreviewLightDark
fun ReplyEmailListPreview() = Wrap {
    ReplyEmailList(
        emails = emails,
        openedEmail = null,
        selectedEmailIds = emptySet(),
        toggleEmailSelection = {},
        emailLazyListState = rememberLazyListState(),
        navigateToDetail = { _, _ -> },
    )
}

@Preview(name = "EmailList — selection mode", showBackground = true, widthDp = 400, heightDp = 700)
@Composable
fun ReplyEmailListSelectionPreview() = Wrap {
    ReplyEmailList(
        emails = emails,
        openedEmail = email,
        selectedEmailIds = setOf(emails[0].id, emails[2].id),
        toggleEmailSelection = {},
        emailLazyListState = rememberLazyListState(),
        navigateToDetail = { _, _ -> },
    )
}

@Preview(name = "EmailList — empty", showBackground = true, widthDp = 400, heightDp = 700)
@Composable
fun ReplyEmailListEmptyPreview() = Wrap {
    ReplyEmailList(
        emails = emptyList(),
        openedEmail = null,
        selectedEmailIds = emptySet(),
        toggleEmailSelection = {},
        emailLazyListState = rememberLazyListState(),
        navigateToDetail = { _, _ -> },
    )
}

@Preview(name = "EmailDetail — full screen", showBackground = true, widthDp = 400, heightDp = 700)
@Composable
@PreviewLightDark
fun ReplyEmailDetailPreview() = Wrap {
    ReplyEmailDetail(email = email, modifier = Modifier.fillMaxSize())
}

@Preview(name = "EmailDetail — pane", showBackground = true, widthDp = 500, heightDp = 700)
@Composable
fun ReplyEmailDetailPanePreview() = Wrap {
    ReplyEmailDetail(email = email, modifier = Modifier.fillMaxSize(), isFullScreen = false)
}

@Preview(name = "EmailDetail — dark", showBackground = true, widthDp = 400, heightDp = 700, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ReplyEmailDetailDarkPreview() = Wrap(dark = true) {
    ReplyEmailDetail(email = email, modifier = Modifier.fillMaxSize())
}

// ---------------------------------------------------------------- navigation surfaces

@Preview(name = "BottomNavigationBar", showBackground = true, widthDp = 400)
@Composable
@PreviewLightDark
fun ReplyBottomNavigationBarPreview() = Wrap {
    ReplyBottomNavigationBar(currentDestination = null, navigateToTopLevelDestination = {})
}

@Preview(name = "BottomNavigationBar — Inbox selected", showBackground = true, widthDp = 400)
@Composable
fun ReplyBottomNavigationBarInboxPreview() = Wrap {
    ReplyBottomNavigationBar(
        currentDestination = null,
        navigateToTopLevelDestination = {},
        selectedDestination = TOP_LEVEL_DESTINATIONS[0],
    )
}

@Preview(name = "BottomNavigationBar — Articles selected", showBackground = true, widthDp = 400)
@Composable
fun ReplyBottomNavigationBarArticlesPreview() = Wrap {
    ReplyBottomNavigationBar(
        currentDestination = null,
        navigateToTopLevelDestination = {},
        selectedDestination = TOP_LEVEL_DESTINATIONS[1],
    )
}

@Preview(name = "NavigationRail — top", showBackground = true, widthDp = 120, heightDp = 600)
@Composable
@PreviewLightDark
fun ReplyNavigationRailTopPreview() = Wrap {
    ReplyNavigationRail(
        currentDestination = null,
        navigationContentPosition = ReplyNavigationContentPosition.TOP,
        navigateToTopLevelDestination = {},
    )
}

@Preview(name = "NavigationRail — centre", showBackground = true, widthDp = 120, heightDp = 600)
@Composable
fun ReplyNavigationRailCenterPreview() = Wrap {
    ReplyNavigationRail(
        currentDestination = null,
        navigationContentPosition = ReplyNavigationContentPosition.CENTER,
        navigateToTopLevelDestination = {},
    )
}

@Preview(name = "NavigationRail — Inbox selected", showBackground = true, widthDp = 120, heightDp = 600)
@Composable
fun ReplyNavigationRailInboxPreview() = Wrap {
    ReplyNavigationRail(
        currentDestination = null,
        navigationContentPosition = ReplyNavigationContentPosition.TOP,
        navigateToTopLevelDestination = {},
        selectedDestination = TOP_LEVEL_DESTINATIONS[0],
    )
}

@Preview(name = "PermanentDrawer", showBackground = true, widthDp = 300, heightDp = 700)
@Composable
@PreviewLightDark
fun PermanentNavigationDrawerContentPreview() = Wrap {
    PermanentNavigationDrawerContent(
        currentDestination = null,
        navigationContentPosition = ReplyNavigationContentPosition.TOP,
        navigateToTopLevelDestination = {},
    )
}

@Preview(name = "PermanentDrawer — Inbox selected", showBackground = true, widthDp = 300, heightDp = 700)
@Composable
fun PermanentNavigationDrawerInboxPreview() = Wrap {
    PermanentNavigationDrawerContent(
        currentDestination = null,
        navigationContentPosition = ReplyNavigationContentPosition.TOP,
        navigateToTopLevelDestination = {},
        selectedDestination = TOP_LEVEL_DESTINATIONS[0],
    )
}

@Preview(name = "ModalDrawer", showBackground = true, widthDp = 300, heightDp = 700)
@Composable
fun ModalNavigationDrawerContentPreview() = Wrap {
    ModalNavigationDrawerContent(
        currentDestination = null,
        navigationContentPosition = ReplyNavigationContentPosition.TOP,
        navigateToTopLevelDestination = {},
    )
}

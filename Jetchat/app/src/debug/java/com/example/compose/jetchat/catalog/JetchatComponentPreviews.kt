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

@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.compose.jetchat.catalog

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.jetchat.FunctionalityNotAvailablePopup
import com.example.compose.jetchat.R
import com.example.compose.jetchat.components.AnimatingFabContent
import com.example.compose.jetchat.components.DividerItem
import com.example.compose.jetchat.components.JetchatDrawer
import com.example.compose.jetchat.components.JetchatDrawerContent
import com.example.compose.jetchat.components.JetchatIcon
import com.example.compose.jetchat.conversation.AuthorAndTextMessage
import com.example.compose.jetchat.conversation.ChannelNameBar
import com.example.compose.jetchat.conversation.ChatItemBubble
import com.example.compose.jetchat.conversation.ClickableMessage
import com.example.compose.jetchat.conversation.ConversationContent
import com.example.compose.jetchat.conversation.ConversationUiState
import com.example.compose.jetchat.conversation.DayHeader
import com.example.compose.jetchat.conversation.EmojiSelector
import com.example.compose.jetchat.conversation.EmojiTable
import com.example.compose.jetchat.conversation.ExtendedSelectorInnerButton
import com.example.compose.jetchat.conversation.FunctionalityNotAvailablePanel
import com.example.compose.jetchat.conversation.JumpToBottom
import com.example.compose.jetchat.conversation.Message
import com.example.compose.jetchat.conversation.Messages
import com.example.compose.jetchat.conversation.RecordButton
import com.example.compose.jetchat.conversation.UserInput
import com.example.compose.jetchat.data.colleagueProfile
import com.example.compose.jetchat.data.exampleUiState
import com.example.compose.jetchat.data.initialMessages
import com.example.compose.jetchat.data.meProfile
import com.example.compose.jetchat.profile.ProfileError
import com.example.compose.jetchat.profile.ProfileFab
import com.example.compose.jetchat.profile.ProfileProperty
import com.example.compose.jetchat.profile.ProfileScreen
import com.example.compose.jetchat.theme.JetchatTheme

/*
 * `@Preview`s for Jetchat's components and screens.
 *
 * Jetchat shipped previews for the whole conversation screen, the profile screen, the drawer
 * content, the app bar, the day header, the composer and the jump-to-bottom pill — but nothing for
 * the pieces the app actually spends its pixels on. A chat app's most reviewable unit is a single
 * message row, and [Message], [AuthorAndTextMessage], [ChatItemBubble] and [ClickableMessage] had no
 * preview at all: own-message vs other-message colouring, the avatar/name that only appear on the
 * last message by an author, the sticker attachment, and the formatted `@mention` / `code` / link
 * spans were only visible by rendering the entire conversation and hoping the row you cared about
 * was on screen. The same was true of the composer's expanded panels ([EmojiSelector],
 * [EmojiTable], [FunctionalityNotAvailablePanel]), the [RecordButton]'s two transition states, the
 * modal [JetchatDrawer] shell (as opposed to its content), and the profile leaf composables.
 *
 * Everything here is driven by the sample data the app itself ships — `initialMessages`,
 * `exampleUiState`, `meProfile`, `colleagueProfile` — so the states that matter for review are
 * addressable one at a time. The previews live in the `debug` source set so nothing reaches a
 * release build.
 *
 * [JetchatTheme] is entered with `isDynamicColor = false` throughout: on API 31+ the default path
 * resolves the wallpaper palette, which in a headless render is the generic system default rather
 * than Jetchat's own blue/yellow scheme. Pinning it keeps these sheets showing the sample's brand
 * colours. The dynamic paths are covered separately by the `@ThemeCatalog` providers in
 * `JetchatThemeCatalogs.kt`.
 */

// "Check it out!" — own message, plain text.
private val meMessage = initialMessages[0]

// Own message carrying the sticker attachment.
private val meStickerMessage = initialMessages[1]

// "You can use all the same stuff" — another author, plain text.
private val otherMessage = initialMessages[2]

// Contains an @mention and an inline `code` span.
private val richMessage = initialMessages[3]

// Long body with an emoji and a https link, i.e. the widest formatter coverage.
private val longMessage = initialMessages[4]

@Composable
private fun Wrap(dark: Boolean = false, content: @Composable () -> Unit) {
    JetchatTheme(isDarkTheme = dark, isDynamicColor = false) {
        Surface { Box(Modifier.padding(8.dp)) { content() } }
    }
}

// ---------------------------------------------------------------- message row

@Preview(name = "Message — other author", showBackground = true, widthDp = 400)
@Composable
fun JetchatMessageOtherPreview() = Wrap {
    Message(
        onAuthorClick = {},
        msg = otherMessage,
        isUserMe = false,
        isFirstMessageByAuthor = true,
        isLastMessageByAuthor = true,
    )
}

@Preview(name = "Message — me", showBackground = true, widthDp = 400)
@Composable
fun JetchatMessageMePreview() = Wrap {
    Message(
        onAuthorClick = {},
        msg = meMessage,
        isUserMe = true,
        isFirstMessageByAuthor = true,
        isLastMessageByAuthor = true,
    )
}

@Preview(name = "Message — continuation", showBackground = true, widthDp = 400)
@Composable
fun JetchatMessageContinuationPreview() = Wrap {
    // Neither first nor last in the group: no avatar, no author/timestamp header.
    Message(
        onAuthorClick = {},
        msg = otherMessage,
        isUserMe = false,
        isFirstMessageByAuthor = false,
        isLastMessageByAuthor = false,
    )
}

@Preview(name = "Message — group head", showBackground = true, widthDp = 400)
@Composable
fun JetchatMessageGroupHeadPreview() = Wrap {
    Message(
        onAuthorClick = {},
        msg = otherMessage,
        isUserMe = false,
        isFirstMessageByAuthor = false,
        isLastMessageByAuthor = true,
    )
}

@Preview(name = "Message — group tail", showBackground = true, widthDp = 400)
@Composable
fun JetchatMessageGroupTailPreview() = Wrap {
    Message(
        onAuthorClick = {},
        msg = otherMessage,
        isUserMe = false,
        isFirstMessageByAuthor = true,
        isLastMessageByAuthor = false,
    )
}

@Preview(name = "Message — with image", showBackground = true, widthDp = 400)
@Composable
fun JetchatMessageWithImagePreview() = Wrap {
    Message(
        onAuthorClick = {},
        msg = meStickerMessage,
        isUserMe = true,
        isFirstMessageByAuthor = true,
        isLastMessageByAuthor = true,
    )
}

@Preview(name = "Message — long text and link", showBackground = true, widthDp = 400)
@Composable
fun JetchatMessageLongTextPreview() = Wrap {
    Message(
        onAuthorClick = {},
        msg = longMessage,
        isUserMe = false,
        isFirstMessageByAuthor = true,
        isLastMessageByAuthor = true,
    )
}

@Preview(
    name = "Message — other author, dark",
    showBackground = true,
    widthDp = 400,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun JetchatMessageOtherDarkPreview() = Wrap(dark = true) {
    Message(
        onAuthorClick = {},
        msg = otherMessage,
        isUserMe = false,
        isFirstMessageByAuthor = true,
        isLastMessageByAuthor = true,
    )
}

@Preview(
    name = "Message — me, dark",
    showBackground = true,
    widthDp = 400,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun JetchatMessageMeDarkPreview() = Wrap(dark = true) {
    Message(
        onAuthorClick = {},
        msg = meMessage,
        isUserMe = true,
        isFirstMessageByAuthor = true,
        isLastMessageByAuthor = true,
    )
}

// ---------------------------------------------------------------- message internals

@Preview(name = "AuthorAndTextMessage — group head", showBackground = true, widthDp = 340)
@Composable
fun JetchatAuthorAndTextMessagePreview() = Wrap {
    AuthorAndTextMessage(
        msg = otherMessage,
        isUserMe = false,
        isFirstMessageByAuthor = true,
        isLastMessageByAuthor = true,
        authorClicked = {},
    )
}

@Preview(name = "AuthorAndTextMessage — continuation", showBackground = true, widthDp = 340)
@Composable
fun JetchatAuthorAndTextMessageContinuedPreview() = Wrap {
    AuthorAndTextMessage(
        msg = otherMessage,
        isUserMe = false,
        isFirstMessageByAuthor = false,
        isLastMessageByAuthor = false,
        authorClicked = {},
    )
}

@Preview(name = "ChatItemBubble — other author", showBackground = true, widthDp = 340)
@Composable
fun JetchatChatItemBubbleOtherPreview() = Wrap {
    ChatItemBubble(message = otherMessage, isUserMe = false, authorClicked = {})
}

@Preview(name = "ChatItemBubble — me", showBackground = true, widthDp = 340)
@Composable
fun JetchatChatItemBubbleMePreview() = Wrap {
    ChatItemBubble(message = meMessage, isUserMe = true, authorClicked = {})
}

@Preview(name = "ChatItemBubble — with image", showBackground = true, widthDp = 340)
@Composable
fun JetchatChatItemBubbleWithImagePreview() = Wrap {
    ChatItemBubble(message = meStickerMessage, isUserMe = true, authorClicked = {})
}

@Preview(name = "ClickableMessage — mention and code", showBackground = true, widthDp = 340)
@Composable
fun JetchatClickableMessagePreview() = Wrap {
    ClickableMessage(message = richMessage, isUserMe = false, authorClicked = {})
}

@Preview(name = "ClickableMessage — primary (own) styling", showBackground = true, widthDp = 340)
@Composable
fun JetchatClickableMessagePrimaryPreview() = Wrap {
    ClickableMessage(message = longMessage, isUserMe = true, authorClicked = {})
}

// ---------------------------------------------------------------- message list

@Preview(name = "Messages", showBackground = true, widthDp = 400, heightDp = 640)
@Composable
fun JetchatMessagesPreview() = Wrap {
    Messages(
        messages = initialMessages,
        navigateToProfile = {},
        scrollState = rememberLazyListState(),
        modifier = Modifier.fillMaxSize(),
    )
}

@Preview(
    name = "Messages — dark",
    showBackground = true,
    widthDp = 400,
    heightDp = 640,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun JetchatMessagesDarkPreview() = Wrap(dark = true) {
    Messages(
        messages = initialMessages,
        navigateToProfile = {},
        scrollState = rememberLazyListState(),
        modifier = Modifier.fillMaxSize(),
    )
}

@Preview(name = "DayHeader — dark", showBackground = true, widthDp = 400, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun JetchatDayHeaderDarkPreview() = Wrap(dark = true) { DayHeader("Today") }

@Preview(name = "JumpToBottom — light", showBackground = true, widthDp = 220, heightDp = 80)
@Composable
fun JetchatJumpToBottomLightPreview() = Wrap {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        JumpToBottom(enabled = true, onClicked = {}, modifier = Modifier.offset(y = 32.dp))
    }
}

@Preview(
    name = "JumpToBottom — dark",
    showBackground = true,
    widthDp = 220,
    heightDp = 80,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun JetchatJumpToBottomDarkPreview() = Wrap(dark = true) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        JumpToBottom(enabled = true, onClicked = {}, modifier = Modifier.offset(y = 32.dp))
    }
}

// ---------------------------------------------------------------- conversation screen

@Preview(name = "Conversation — light", showBackground = true, widthDp = 412, heightDp = 800)
@Composable
fun JetchatConversationContentLightPreview() {
    JetchatTheme(isDarkTheme = false, isDynamicColor = false) {
        ConversationContent(uiState = exampleUiState, navigateToProfile = {})
    }
}

@Preview(name = "Conversation — dark", showBackground = true, widthDp = 412, heightDp = 800, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun JetchatConversationContentDarkPreview() {
    JetchatTheme(isDarkTheme = true, isDynamicColor = false) {
        ConversationContent(uiState = exampleUiState, navigateToProfile = {})
    }
}

@Preview(name = "Conversation — medium width", showBackground = true, widthDp = 700, heightDp = 700)
@Composable
fun JetchatConversationContentMediumPreview() {
    JetchatTheme(isDynamicColor = false) {
        ConversationContent(uiState = exampleUiState, navigateToProfile = {})
    }
}

@Preview(name = "Conversation — empty", showBackground = true, widthDp = 412, heightDp = 800)
@Composable
fun JetchatConversationContentEmptyPreview() {
    JetchatTheme(isDarkTheme = false, isDynamicColor = false) {
        ConversationContent(
            uiState =
                ConversationUiState(
                    channelName = "#composers",
                    channelMembers = 42,
                    initialMessages = emptyList(),
                ),
            navigateToProfile = {},
        )
    }
}

// ---------------------------------------------------------------- channel bar

@Preview(name = "ChannelNameBar — dark", showBackground = true, widthDp = 412, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun JetchatChannelNameBarDarkPreview() = Wrap(dark = true) {
    ChannelNameBar(channelName = "#composers", channelMembers = 42)
}

@Preview(name = "ChannelNameBar — long channel name", showBackground = true, widthDp = 412)
@Composable
fun JetchatChannelNameBarLongNamePreview() = Wrap {
    ChannelNameBar(
        channelName = "#compose-multiplatform-adaptive-layouts",
        channelMembers = 1284,
    )
}

// ---------------------------------------------------------------- composer

@Preview(name = "UserInput — dark", showBackground = true, widthDp = 412, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun JetchatUserInputDarkPreview() = Wrap(dark = true) { UserInput(onMessageSent = {}) }

@Preview(name = "EmojiSelector", showBackground = true, widthDp = 412, heightDp = 320)
@Composable
fun JetchatEmojiSelectorPreview() = Wrap {
    EmojiSelector(onTextAdded = {}, focusRequester = remember { FocusRequester() })
}

@Preview(name = "EmojiTable", showBackground = true, widthDp = 412)
@Composable
fun JetchatEmojiTablePreview() = Wrap { EmojiTable(onTextAdded = {}) }

@Preview(name = "SelectorInnerButton — selected", showBackground = true, widthDp = 220)
@Composable
fun JetchatExtendedSelectorInnerButtonSelectedPreview() = Wrap {
    Row(Modifier.fillMaxWidth()) {
        ExtendedSelectorInnerButton(
            text = "Emojis",
            onClick = {},
            selected = true,
            modifier = Modifier.weight(1f),
        )
    }
}

@Preview(name = "SelectorInnerButton — unselected", showBackground = true, widthDp = 220)
@Composable
fun JetchatExtendedSelectorInnerButtonUnselectedPreview() = Wrap {
    Row(Modifier.fillMaxWidth()) {
        ExtendedSelectorInnerButton(
            text = "Stickers",
            onClick = {},
            selected = false,
            modifier = Modifier.weight(1f),
        )
    }
}

@Preview(name = "FunctionalityNotAvailablePanel", showBackground = true, widthDp = 412, heightDp = 340)
@Composable
fun JetchatFunctionalityNotAvailablePanelPreview() = Wrap {
    FunctionalityNotAvailablePanel(showImmediately = true)
}

@Preview(name = "FunctionalityNotAvailablePopup", showBackground = true, widthDp = 412, heightDp = 300)
@Composable
fun JetchatFunctionalityNotAvailablePopupPreview() = Wrap { FunctionalityNotAvailablePopup(onDismiss = {}) }

@Preview(name = "RecordButton — idle", showBackground = true, widthDp = 144, heightDp = 144)
@Composable
fun JetchatRecordButtonIdlePreview() = Wrap {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        RecordButton(
            recording = false,
            swipeOffset = { 0f },
            onSwipeOffsetChange = {},
            onStartRecording = { true },
            onFinishRecording = {},
            onCancelRecording = {},
            modifier = Modifier.size(56.dp),
        )
    }
}

@Preview(name = "RecordButton — recording", showBackground = true, widthDp = 144, heightDp = 144)
@Composable
fun JetchatRecordButtonRecordingPreview() = Wrap {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        RecordButton(
            recording = true,
            swipeOffset = { 0f },
            onSwipeOffsetChange = {},
            onStartRecording = { true },
            onFinishRecording = {},
            onCancelRecording = {},
            modifier = Modifier.size(56.dp),
        )
    }
}

// ---------------------------------------------------------------- chrome

@Preview(name = "JetchatIcon", showBackground = true, widthDp = 96, heightDp = 96)
@Composable
fun JetchatIconPreview() = Wrap {
    JetchatIcon(contentDescription = "Jetchat", modifier = Modifier.size(48.dp))
}

@Preview(name = "DividerItem", showBackground = true, widthDp = 300, heightDp = 48)
@Composable
fun JetchatDividerItemPreview() = Wrap {
    Column(Modifier.fillMaxWidth()) {
        Text("Chats", style = MaterialTheme.typography.bodySmall)
        DividerItem()
    }
}

@Preview(name = "Drawer shell — open", showBackground = true, widthDp = 412, heightDp = 800)
@Composable
fun JetchatDrawerOpenPreview() {
    // The modal drawer shell (scrim + sheet over the conversation), not just its content.
    JetchatDrawer(
        drawerState = rememberDrawerState(initialValue = DrawerValue.Open),
        selectedMenu = "composers",
        onProfileClicked = {},
        onChatClicked = {},
    ) {
        Surface(Modifier.fillMaxSize()) {
            ConversationContent(uiState = exampleUiState, navigateToProfile = {})
        }
    }
}

@Preview(name = "Drawer shell — closed", showBackground = true, widthDp = 412, heightDp = 800)
@Composable
fun JetchatDrawerClosedPreview() {
    JetchatDrawer(
        drawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
        selectedMenu = "composers",
        onProfileClicked = {},
        onChatClicked = {},
    ) {
        Surface(Modifier.fillMaxSize()) {
            ConversationContent(uiState = exampleUiState, navigateToProfile = {})
        }
    }
}

@Preview(name = "Drawer content — chat selected", showBackground = true, widthDp = 300, heightDp = 560)
@Composable
fun JetchatDrawerContentChatSelectedPreview() = Wrap {
    // `droidcon-nyc` selected rather than the default `composers`: the selected pill moves to the
    // second chat row, which is the only way to see that the selection is driven by `selectedMenu`.
    JetchatDrawerContent(onProfileClicked = {}, onChatClicked = {}, selectedMenu = "droidcon-nyc")
}

@Preview(name = "Drawer content — profile selected", showBackground = true, widthDp = 300, heightDp = 560)
@Composable
fun JetchatDrawerContentProfileSelectedPreview() = Wrap {
    // No chat row is highlighted at all when a profile is the current destination.
    JetchatDrawerContent(onProfileClicked = {}, onChatClicked = {}, selectedMenu = colleagueProfile.userId)
}

@Preview(
    name = "Drawer content — profile selected, dark",
    showBackground = true,
    widthDp = 300,
    heightDp = 560,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun JetchatDrawerContentProfileSelectedDarkPreview() = Wrap(dark = true) {
    JetchatDrawerContent(onProfileClicked = {}, onChatClicked = {}, selectedMenu = meProfile.userId)
}

// ---------------------------------------------------------------- profile

@Preview(
    name = "Profile — medium width",
    showBackground = true,
    device = "spec:width=700dp,height=700dp,dpi=160",
)
@Composable
fun JetchatProfileScreenMediumPreview() {
    // The sample's own profile previews stop at 480dp; the catalog's medium breakpoint is 700dp,
    // where the header photo and the property column lay out differently.
    JetchatTheme(isDynamicColor = false) { ProfileScreen(meProfile) }
}

@Preview(
    name = "Profile — medium width, dark",
    showBackground = true,
    device = "spec:width=700dp,height=700dp,dpi=160",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun JetchatProfileScreenMediumDarkPreview() {
    JetchatTheme(isDarkTheme = true, isDynamicColor = false) { ProfileScreen(colleagueProfile) }
}

@Preview(name = "ProfileProperty", showBackground = true, widthDp = 340)
@Composable
fun JetchatProfilePropertyPreview() = Wrap {
    Column {
        ProfileProperty(label = "Display name", value = meProfile.displayName)
        ProfileProperty(label = "Status", value = meProfile.status)
    }
}

@Preview(name = "ProfileProperty — link", showBackground = true, widthDp = 340)
@Composable
fun JetchatProfilePropertyLinkPreview() = Wrap {
    ProfileProperty(label = "Twitter", value = meProfile.twitter, isLink = true)
}

@Preview(name = "ProfileProperty — away status", showBackground = true, widthDp = 340)
@Composable
fun JetchatProfilePropertyAwayPreview() = Wrap {
    Column {
        ProfileProperty(label = "Status", value = colleagueProfile.status)
        ProfileProperty(label = "Timezone", value = colleagueProfile.timeZone ?: "")
    }
}

@Preview(name = "ProfileError", showBackground = true, widthDp = 340, heightDp = 80)
@Composable
fun JetchatProfileErrorPreview() = Wrap { ProfileError() }

@Preview(name = "ProfileFab — me, extended", showBackground = true, widthDp = 220, heightDp = 96)
@Composable
fun JetchatProfileFabMeExtendedPreview() = Wrap {
    ProfileFab(extended = true, userIsMe = true)
}

@Preview(name = "ProfileFab — me, collapsed", showBackground = true, widthDp = 140, heightDp = 96)
@Composable
fun JetchatProfileFabMeCollapsedPreview() = Wrap {
    ProfileFab(extended = false, userIsMe = true)
}

@Preview(name = "ProfileFab — other, collapsed", showBackground = true, widthDp = 140, heightDp = 96)
@Composable
fun JetchatProfileFabOtherCollapsedPreview() = Wrap {
    ProfileFab(extended = false, userIsMe = false)
}

@Preview(name = "AnimatingFabContent — extended", showBackground = true, widthDp = 220, heightDp = 64)
@Composable
fun JetchatAnimatingFabContentExtendedPreview() = Wrap {
    AnimatingFabContent(
        icon = { Icon(painterResource(id = R.drawable.ic_create), contentDescription = null) },
        text = { Text("Edit Profile") },
        extended = true,
        modifier = Modifier.size(width = 180.dp, height = 48.dp),
    )
}

@Preview(name = "AnimatingFabContent — collapsed", showBackground = true, widthDp = 140, heightDp = 64)
@Composable
fun JetchatAnimatingFabContentCollapsedPreview() = Wrap {
    AnimatingFabContent(
        icon = { Icon(painterResource(id = R.drawable.ic_create), contentDescription = null) },
        text = { Text("Edit Profile") },
        extended = false,
        modifier = Modifier.size(width = 120.dp, height = 48.dp),
    )
}

package com.infotech.wishmaplus;

import java.util.Arrays;
import java.util.List;

public class StickerData {

    public static List<String> getCategory(int index) {
        switch (index) {
            case 0: return TRENDING;
            case 1: return TEXT_STICKERS;
            case 2: return FUN;
            case 3: return GLITTER;
            case 4: return VIBES;
            default: return TRENDING;
        }
    }

    // Trending - combo stickers
    private static final List<String> TRENDING = Arrays.asList(
        "🔥🔥", "💯", "✅", "❌", "⚡💥", "🌊🏄",
        "👑✨", "💎💎", "🎯🎯", "🏆🥇", "💪🔥", "😤💢",
        "🤙🏽", "👀👀", "🫶🏽", "🙌🏽", "🤌🏽", "💅🏽",
        "🫡", "🥹", "🤯💥", "😈🔥", "👻💀", "🦋✨"
    );

    // Text stickers (emoji + text combos shown as emoji)
    private static final List<String> TEXT_STICKERS = Arrays.asList(
        "💬", "🗨️", "💭", "📢", "📣", "🔔",
        "📌", "📍", "🏷️", "📎", "📏", "📐",
        "✏️", "📝", "📖", "📚", "🖊️", "🖋️",
        "💡", "🔑", "🗝️", "🔐", "🔒", "🔓"
    );

    // Fun
    private static final List<String> FUN = Arrays.asList(
        "🤣😂", "😜🤪", "🥳🎉", "🎭🎪", "🤹🎠", "🎮🕹️",
        "🃏🎲", "🎰🎯", "🎨🖼️", "🎬🎥", "📸🤳", "🎤🎵",
        "🎸🎶", "🥁🎼", "🪄✨", "🎩🐇", "🃏♠️", "🎲🎯"
    );

    // Glitter vibes
    private static final List<String> GLITTER = Arrays.asList(
        "✨✨", "⭐🌟", "💫✨", "🌠⭐", "🌟💫", "✨🎇",
        "🎆✨", "💥⭐", "🌈✨", "🦄✨", "👑💫", "💎✨",
        "🪩✨", "🌸✨", "🦋💫", "🌺⭐", "🌻💫", "💐✨"
    );

    // Vibes
    private static final List<String> VIBES = Arrays.asList(
        "☮️🌈", "🌊🏄", "🌿🍃", "🌙⭐", "☀️🌈", "🌸🌺",
        "🦋🌸", "🍃🌿", "🌊💙", "🔮🌙", "🌙✨", "🌌⭐",
        "🏔️🌄", "🌅🌊", "🌃🌉", "🌆🌇", "🏝️🌴", "🌋🔥"
    );
}

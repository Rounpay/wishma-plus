package com.infotech.wishmaplus;

import java.util.Arrays;
import java.util.List;

public class EmojiData {

    public static List<String> getCategory(int index) {
        switch (index) {
            case 0: return SMILEYS;
            case 1: return HEARTS;
            case 2: return CELEBRATION;
            case 3: return ANIMALS;
            case 4: return FOOD;
            case 5: return SPORTS;
            case 6: return NATURE;
            case 7: return TRAVEL;
            default: return SMILEYS;
        }
    }

    // Smileys & People
    private static final List<String> SMILEYS = Arrays.asList(
        "😀","😁","😂","🤣","😃","😄","😅","😆",
        "😇","😈","😉","😊","😋","😌","😍","🥰",
        "😎","😏","😐","😑","😒","😓","😔","😕",
        "🙃","🤑","🤗","🤔","🤐","😷","🤒","🤕",
        "🥳","🥺","🤩","😭","😢","😤","😠","😡",
        "🤬","😱","😨","😰","😥","😓","🤫","🤭",
        "🧐","🤓","😜","🤪","😝","🤑","🤠","🥸",
        "😺","😸","😹","😻","😼","😽","🙀","😿"
    );

    // Hearts & Love
    private static final List<String> HEARTS = Arrays.asList(
        "❤️","🧡","💛","💚","💙","💜","🖤","🤍",
        "🤎","💔","❣️","💕","💞","💓","💗","💖",
        "💘","💝","💟","☮️","✝️","☪️","🕉️","☯️",
        "🫀","💋","💌","💐","🌹","🌺","🌸","🌼",
        "🌻","💒","👑","💍","💎","🎁","🎀","🎊"
    );

    // Celebration
    private static final List<String> CELEBRATION = Arrays.asList(
        "🎉","🎊","🎈","🎁","🎀","🎂","🎆","🎇",
        "🧨","✨","🌟","⭐","💫","🔥","🎯","🏆",
        "🥇","🥈","🥉","🎖️","🏅","🎗️","🎫","🎟️",
        "🎪","🤹","🎭","🎨","🎬","🎤","🎧","🎼",
        "🎵","🎶","🎹","🎸","🎺","🎻","🪘","🥁"
    );

    // Animals
    private static final List<String> ANIMALS = Arrays.asList(
        "🐶","🐱","🐭","🐹","🐰","🦊","🐻","🐼",
        "🐨","🐯","🦁","🐮","🐷","🐸","🐵","🙈",
        "🙉","🙊","🐔","🐧","🐦","🐤","🦆","🦅",
        "🦉","🦇","🐺","🐗","🐴","🦄","🐝","🐛",
        "🦋","🐌","🐞","🐜","🦟","🦗","🦂","🐢"
    );

    // Food
    private static final List<String> FOOD = Arrays.asList(
        "🍕","🍔","🍟","🌭","🍿","🧂","🥓","🥚",
        "🍳","🥞","🧇","🥐","🍞","🥖","🥨","🧀",
        "🥗","🥙","🌮","🌯","🫔","🥫","🍱","🍘",
        "🍣","🍤","🍙","🍚","🍛","🍜","🦪","🍦",
        "🍧","🍨","🍩","🍪","🎂","🍰","🧁","🥧",
        "🍫","🍬","🍭","🍮","🧃","🥤","🧋","☕"
    );

    // Sports
    private static final List<String> SPORTS = Arrays.asList(
        "⚽","🏀","🏈","⚾","🥎","🎾","🏐","🏉",
        "🥏","🎱","🏓","🏸","🏒","🥊","🥋","🎯",
        "🏹","🎣","🤿","🎽","🎿","🛷","🛼","⛸️",
        "🏋️","🤼","🤸","⛹️","🏌️","🏇","🧘","🏄",
        "🚴","🤾","🧗","🏊","🤽","🚣","🧜","🏆"
    );

    // Nature
    private static final List<String> NATURE = Arrays.asList(
        "🌍","🌎","🌏","🌑","🌒","🌓","🌔","🌕",
        "🌖","🌗","🌘","🌙","🌚","🌛","🌜","☀️",
        "🌝","🌞","⭐","🌟","💫","✨","☁️","⛅",
        "🌤️","🌥️","🌦️","🌧️","⛈️","🌩️","🌨️","🌬️",
        "🌀","🌈","🌂","☂️","☔","⛱️","⚡","❄️",
        "🌊","💧","💦","🫧","🌿","🍀","🌱","🌾"
    );

    // Travel
    private static final List<String> TRAVEL = Arrays.asList(
        "✈️","🚀","🛸","🚁","🛺","🚗","🚕","🚙",
        "🚌","🚎","🏎️","🚓","🚑","🚒","🚐","🛻",
        "🚚","🚛","🚜","🏍️","🛵","🚲","🛴","🛹",
        "🚨","🚥","🚦","🛑","🚧","⚓","⛵","🛥️",
        "🚢","🏝️","🏔️","⛰️","🗻","🏕️","🏖️","🏗️",
        "🏰","🏯","🗼","🗽","🗿","🏟️","🎡","🎢"
    );
}

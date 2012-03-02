package com.codisimus.plugins.turnstile;

/**
 * Holds messages that are displayed to users of this plugin
 *
 * @author Codisimus
 */
public class TurnstileMessages {
    static String permission;
    static String locked;
    static String free;
    static String oneWay;
    static String correct;
    static String wrong;
    static String notEnoughMoney;
    static String displayCost;
    static String open;
    static String balanceCleared;
    static String privateTurnstile;
    static String inUse;
    static String occupied;
    
    /**
     * Formats all Turnstile messages
     * 
     */
    static void formatAll() {
        permission = format(permission);
        locked = format(locked);
        free = format(free);
        oneWay = format(oneWay);
        correct = format(correct);
        wrong = format(wrong);
        notEnoughMoney = format(notEnoughMoney);
        displayCost = format(displayCost);
        open = format(open);
        balanceCleared = format(balanceCleared);
        privateTurnstile = format(privateTurnstile);
        inUse = format(inUse);
        occupied = format(occupied);
    }
    
    /**
     * Adds various Unicode characters and colors to a string
     * 
     * @param string The string being formated
     * @return The formatted String
     */
    private static String format(String string) {
        return string.replaceAll("&", "§").replaceAll("<ae>", "æ").replaceAll("<AE>", "Æ")
                .replaceAll("<o/>", "ø").replaceAll("<O/>", "Ø")
                .replaceAll("<a>", "å").replaceAll("<A>", "Å");
    }
}

package com.codisimus.plugins.turnstile;

import org.bukkit.event.server.ServerListener;
import com.codisimus.plugins.turnstile.register.payment.Methods;
import org.bukkit.event.server.PluginEnableEvent;
import ru.tehkode.permissions.bukkit.PermissionsEx;

/**
 * Checks for plugins whenever one is enabled
 *
 */
public class PluginListener extends ServerListener {
    public PluginListener() { }
    protected static Boolean useOP;

    @Override
    public void onPluginEnable(PluginEnableEvent event) {
        linkPermissions();
        linkEconomy();
//        if (TurnstileMain.TextPlayer == null) {
//            Plugin TextPlayer = TurnstileMain.pm.getPlugin("TextPlayer");
//            if (TextPlayer != null) {
//                TurnstileMain.TextPlayer = (TextPlayer)TextPlayer;
//                System.out.println("Turnstile Successfully linked with TextPlayer!");
//            }
//        }
    }

    /**
     * Find and link a Permission plugin
     *
     */
    private void linkPermissions() {
        //Return if we have already have a permissions plugin
        if (TurnstileMain.permissions != null)
            return;

        //Return if PermissionsEx is not enabled
        if (!TurnstileMain.pm.isPluginEnabled("PermissionsEx"))
            return;

        //Return if OP permissions will be used
        if (useOP)
            return;

        TurnstileMain.permissions = PermissionsEx.getPermissionManager();
        System.out.println("[Turnstile] Successfully linked with PermissionsEx!");
    }

    /**
     * Find and link an Economy plugin
     *
     */
    private void linkEconomy() {
        //Return if we already have an Economy plugin
        if (Methods.hasMethod())
            return;

        //Return if no Economy is wanted
        if (Register.economy.equalsIgnoreCase("none"))
            return;

        //Set preferred plugin if there is one
        if (!Register.economy.equalsIgnoreCase("auto"))
            Methods.setPreferred(Register.economy);

        Methods.setMethod(TurnstileMain.pm);

        //Reset Methods if the preferred Economy was not found
        if (!Register.economy.equalsIgnoreCase("auto") && !Methods.getMethod().getName().equalsIgnoreCase(Register.economy)) {
            Methods.reset();
            return;
        }

        Register.econ = Methods.getMethod();
        System.out.println("[Turnstile] Successfully linked with "+Register.econ.getName()+" "+Register.econ.getVersion()+"!");
    }
}
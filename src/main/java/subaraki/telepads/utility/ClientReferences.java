package subaraki.telepads.utility;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import subaraki.telepads.capability.player.TelepadData;
import subaraki.telepads.network.client.CPacketEditWhiteListEntry;
import subaraki.telepads.network.client.CPacketRequestTeleportScreen;
import subaraki.telepads.screen.MissingEntryScreen;
import subaraki.telepads.screen.NameTelepadScreen;
import subaraki.telepads.screen.TeleportScreen;

public class ClientReferences {

    public static Player getClientPlayer() {

        return Minecraft.getInstance().player;
    }

    public static void openNamingScreen(BlockPos pos) {

        Minecraft.getInstance().setScreen(new NameTelepadScreen(pos));
    }

    public static void openMissingScreen(TelepadEntry entry) {

        Minecraft.getInstance().setScreen(new MissingEntryScreen(entry));
    }

    public static Level getClientWorld() {

        return Minecraft.getInstance().level;
    }

    public static void displayScreen(Screen screen) {

        Minecraft.getInstance().setScreen(screen);
    }

    public static void displayTeleportScreen(boolean has_transmitter) {

        Minecraft.getInstance().setScreen(new TeleportScreen(has_transmitter));
    }

    public static void handlePacket(CPacketRequestTeleportScreen packet) {

        TelepadData.get(getClientPlayer()).ifPresent(data -> {

            data.getEntries().clear();
            if (packet.entries != null && !packet.entries.isEmpty())
                packet.entries.forEach(data.getEntries()::add);

            data.setCounter(TelepadData.getMaxTime());
            data.setInTeleportGui(false);
            // use a layer of indirection when subscribing client events to avoid
            // classloading client classes on server
            if (!data.getEntries().isEmpty())
                displayTeleportScreen(packet.has_transmitter);

        });
    }

    public static void handlePacket(CPacketEditWhiteListEntry packet) {

        TelepadData.get(getClientPlayer()).ifPresent(data -> {
            if (packet.add)
                data.addWhiteListEntryClient(packet.name, packet.id);
            else
                data.removeWhiteListEntryClient(packet.name);
        });
    }
}
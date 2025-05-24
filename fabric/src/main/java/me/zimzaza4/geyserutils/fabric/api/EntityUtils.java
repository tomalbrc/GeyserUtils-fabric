package me.zimzaza4.geyserutils.fabric.api;

import com.google.common.cache.Cache;
import me.zimzaza4.geyserutils.common.channel.GeyserUtilsChannels;
import me.zimzaza4.geyserutils.common.packet.*;
import me.zimzaza4.geyserutils.fabric.GeyserUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.level.ServerPlayer;
import org.geysermc.geyser.api.GeyserApi;

import java.awt.*;
import java.util.Map;

import static me.zimzaza4.geyserutils.geyser.GeyserUtils.CUSTOM_ENTITIES;

public class EntityUtils {

    public static void sendCustomHitBox(ServerPlayer player, int id, float height, float width) {
        CustomEntityDataPacket packet = new CustomEntityDataPacket();
        packet.setEntityId(id);
        packet.setWidth(width);
        packet.setHeight(height);
        GeyserUtils.send(player, GeyserUtilsChannels.MAIN, GeyserUtils.getPacketManager().encodePacket(packet));

    }

    public static void sendCustomScale(ServerPlayer player, int id, float scale) {
        CustomEntityDataPacket packet = new CustomEntityDataPacket();
        packet.setEntityId(id);
        packet.setScale(scale);
        GeyserUtils.send(player, GeyserUtilsChannels.MAIN, GeyserUtils.getPacketManager().encodePacket(packet));

    }

    public static void sendCustomColor(ServerPlayer player, int id, Color color) {
        CustomEntityDataPacket packet = new CustomEntityDataPacket();
        packet.setEntityId(id);
        packet.setColor(color.getRGB());
        GeyserUtils.send(player, GeyserUtilsChannels.MAIN, GeyserUtils.getPacketManager().encodePacket(packet));

    }

    public static void sendVariant(ServerPlayer player, int id, int variant) {
        CustomEntityDataPacket packet = new CustomEntityDataPacket();
        packet.setEntityId(id);
        packet.setVariant(variant);
        GeyserUtils.send(player, GeyserUtilsChannels.MAIN, GeyserUtils.getPacketManager().encodePacket(packet));
    }

    public static void setCustomEntity(ServerPlayer player, int entityId, String def) {
        if (!FabricLoader.getInstance().isModLoaded("geyser-fabric")) {
            CustomEntityPacket packet = new CustomEntityPacket(entityId, def);
            GeyserUtils.send(player, GeyserUtilsChannels.MAIN, GeyserUtils.getPacketManager().encodePacket(packet));
        }
        else {
            Cache<Integer, String> cache = CUSTOM_ENTITIES.get(GeyserApi.api().connectionByUuid(player.getUUID()));
            cache.put(entityId, def);
        }
    }

    // (yes I'm aware it's "horrible" code), also this aint player packets at all lmao
    // right, so this part needs to be refactored xD
    // the plugin didn't have this much functionality in its earliest days (it even just have camera shakes),
    // so I didn't think too much about it

    public static void registerProperty(ServerPlayer player, int id, String identifier, Class<?> type) {
        EntityPropertyRegisterPacket packet = new EntityPropertyRegisterPacket();
        packet.setEntityId(id);
        packet.setIdentifier(identifier);
        packet.setType(type);
        GeyserUtils.send(player, GeyserUtilsChannels.MAIN, GeyserUtils.getPacketManager().encodePacket(packet));
    }

    public static void sendBoolProperty(ServerPlayer player, int id, String identifier, Boolean value) {
        EntityPropertyPacket<Boolean> packet = new EntityPropertyPacket<>();
        packet.setEntityId(id);
        packet.setIdentifier(identifier);
        packet.setValue(value);
        GeyserUtils.send(player, GeyserUtilsChannels.MAIN, GeyserUtils.getPacketManager().encodePacket(packet));
    }

    public static void sendBoolProperties(ServerPlayer player, int id, Map<String, Boolean> bundle) {
        BundlePacket packet = new BundlePacket();
        bundle.forEach((identifier, value) -> {
            EntityPropertyPacket<Boolean> propertyPacket = new EntityPropertyPacket<>();
            propertyPacket.setEntityId(id);
            propertyPacket.setIdentifier(identifier);
            propertyPacket.setValue(value);
            packet.addPacket(propertyPacket);
        });

        GeyserUtils.send(player, GeyserUtilsChannels.MAIN, GeyserUtils.getPacketManager().encodePacket(packet));
    }

    public static void sendFloatProperty(ServerPlayer player, int id, String identifier, Float value) {
        EntityPropertyPacket<Float> packet = new EntityPropertyPacket<>();
        packet.setEntityId(id);
        packet.setIdentifier(identifier);
        packet.setValue(value);
        GeyserUtils.send(player, GeyserUtilsChannels.MAIN, GeyserUtils.getPacketManager().encodePacket(packet));
    }

    public static void sendFloatProperties(ServerPlayer player, int id, Map<String, Float> bundle) {
        BundlePacket packet = new BundlePacket();
        bundle.forEach((identifier, value) -> {
            EntityPropertyPacket<Float> propertyPacket = new EntityPropertyPacket<>();
            propertyPacket.setEntityId(id);
            propertyPacket.setIdentifier(identifier);
            propertyPacket.setValue(value);
            packet.addPacket(propertyPacket);
        });

        GeyserUtils.send(player, GeyserUtilsChannels.MAIN, GeyserUtils.getPacketManager().encodePacket(packet));
    }

    public static void sendIntProperty(ServerPlayer player, int id, String identifier, Integer value) {
        EntityPropertyPacket<Integer> packet = new EntityPropertyPacket<>();
        packet.setEntityId(id);
        packet.setIdentifier(identifier);
        packet.setValue(value);
        GeyserUtils.send(player, GeyserUtilsChannels.MAIN, GeyserUtils.getPacketManager().encodePacket(packet));
    }

    public static void sendIntProperties(ServerPlayer player, int id, Map<String, Integer> bundle) {
        BundlePacket packet = new BundlePacket();
        bundle.forEach((identifier, value) -> {
            EntityPropertyPacket<Integer> propertyPacket = new EntityPropertyPacket<>();
            propertyPacket.setEntityId(id);
            propertyPacket.setIdentifier(identifier);
            propertyPacket.setValue(value);
            packet.addPacket(propertyPacket);
        });

        GeyserUtils.send(player, GeyserUtilsChannels.MAIN, GeyserUtils.getPacketManager().encodePacket(packet));
    }
}

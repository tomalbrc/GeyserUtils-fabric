package me.zimzaza4.geyserutils.fabric.api;

import me.zimzaza4.geyserutils.common.animation.Animation;
import me.zimzaza4.geyserutils.common.camera.instruction.Instruction;
import me.zimzaza4.geyserutils.common.channel.GeyserUtilsChannels;
import me.zimzaza4.geyserutils.common.packet.*;
import me.zimzaza4.geyserutils.common.particle.CustomParticle;
import me.zimzaza4.geyserutils.common.util.Pos;
import me.zimzaza4.geyserutils.fabric.GeyserUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlayerUtils {

    public static void shakeCamera(ServerPlayer player, float intensity, float duration, int type) {
        GeyserUtils.send(player, GeyserUtilsChannels.MAIN, GeyserUtils.getPacketManager().encodePacket(new CameraShakeCustomPayloadPacket(intensity, duration, type)));
    }

    public static void playEntityAnimation(ServerPlayer player, Animation animation, Entity... entityList) {
        List<Integer> idList = new ArrayList<>();
        for (Entity entity : entityList) {
            idList.add(entity.getId());
        }

        playEntityAnimation(player, animation, idList);
    }

    public static void playEntityAnimation(ServerPlayer player, Animation animation, List<Integer> entityList) {
        AnimateEntityCustomPayloadPacket packet = new AnimateEntityCustomPayloadPacket();
        packet.parseFromAnimation(animation);
        packet.setEntityJavaIds(entityList);
        GeyserUtils.send(player, GeyserUtilsChannels.MAIN, GeyserUtils.getPacketManager().encodePacket(packet));

    }

    public static void sendCameraInstruction(ServerPlayer player, Instruction instruction) {
        CameraInstructionCustomPayloadPacket packet = new CameraInstructionCustomPayloadPacket();
        packet.setInstruction(instruction);
        GeyserUtils.send(player, GeyserUtilsChannels.MAIN, GeyserUtils.getPacketManager().encodePacket(packet));

    }

    public static void sendCustomParticle(ServerPlayer player, Vec3 location, CustomParticle particle) {
        CustomParticleEffectPayloadPacket packet = new CustomParticleEffectPayloadPacket();
        packet.setParticle(particle);
        packet.setPos(new Pos((float) location.x(), (float) location.y(), (float) location.z()));
        GeyserUtils.send(player, GeyserUtilsChannels.MAIN, GeyserUtils.getPacketManager().encodePacket(packet));
    }

    public static void sendCustomSkin(ServerPlayer player, Entity entity, String skin) {
        CustomSkinPayloadPacket skinPayloadPacket = new CustomSkinPayloadPacket();
        skinPayloadPacket.setSkinId(skin);
        skinPayloadPacket.setEntityId(entity.getId());
        GeyserUtils.send(player, GeyserUtilsChannels.MAIN, GeyserUtils.getPacketManager().encodePacket(skinPayloadPacket));

    }

    public static void sendCustomHitBox(ServerPlayer player, Entity entity, float height, float width) {
        EntityUtils.sendCustomHitBox(player, entity.getId(), height, width);
    }

    public static void sendCustomScale(ServerPlayer player, Entity entity, float scale) {
        EntityUtils.sendCustomScale(player, entity.getId(), scale);
    }

    public static void sendCustomColor(ServerPlayer player, Entity entity, Color color) {
        EntityUtils.sendCustomColor(player, entity.getId(), color);
    }

    public static void setCustomVariant(ServerPlayer player, int id, int variant) {
        EntityUtils.sendVariant(player, id, variant);
    }

    public static void setCustomEntity(ServerPlayer player, int id, String def) {
        EntityUtils.setCustomEntity(player, id, def);
    }

    // (yes I'm aware it's "horrible" code), also this aint player packets at all lmao
    // right, so this part needs to be refactored xD
    // the plugin didn't have this much functionality in its earliest days (it even just have camera shakes),
    // so I didn't think too much about it

    public static void registerProperty(ServerPlayer player, Entity entity, String identifier, Class<?> type) {
        EntityUtils.registerProperty(player, entity.getId(), identifier, type);
    }

    public static void sendBoolProperty(ServerPlayer player, Entity entity, String identifier, Boolean value) {
        EntityUtils.sendBoolProperty(player, entity.getId(), identifier, value);
    }

    public static void sendBoolProperties(ServerPlayer player, Entity entity, Map<String, Boolean> bundle) {
        EntityUtils.sendBoolProperties(player, entity.getId(), bundle);
    }

    public static void sendFloatProperty(ServerPlayer player, Entity entity, String identifier, Float value) {
        EntityUtils.sendFloatProperty(player, entity.getId(), identifier, value);
    }

    public static void sendFloatProperties(ServerPlayer player, Entity entity, Map<String, Float> bundle) {
        EntityUtils.sendFloatProperties(player, entity.getId(), bundle);
    }

    public static void sendIntProperty(ServerPlayer player, Entity entity, String identifier, Integer value) {
        EntityUtils.sendIntProperty(player, entity.getId(), identifier, value);
    }

    public static void sendIntProperties(ServerPlayer player, Entity entity, Map<String, Integer> bundle) {
        EntityUtils.sendIntProperties(player, entity.getId(), bundle);
    }
}

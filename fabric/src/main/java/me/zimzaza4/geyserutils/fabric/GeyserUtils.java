package me.zimzaza4.geyserutils.fabric;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.pokeskies.fabricpluginmessaging.PluginMessageEvent;
import com.pokeskies.fabricpluginmessaging.PluginMessagePacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import me.zimzaza4.geyserutils.common.camera.data.CameraPreset;
import me.zimzaza4.geyserutils.common.channel.GeyserUtilsChannels;
import me.zimzaza4.geyserutils.common.manager.PacketManager;
import me.zimzaza4.geyserutils.common.packet.CustomPayloadPacket;
import me.zimzaza4.geyserutils.common.packet.NpcFormResponseCustomPayloadPacket;
import me.zimzaza4.geyserutils.fabric.api.form.NpcDialogueForm;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class GeyserUtils implements ModInitializer {

    private static MinecraftServer server;

    @Getter
    private static GeyserUtils instance;

    @Getter
    private static PacketManager packetManager;

    @Override
    public void onInitialize() {
        // Plugin startup logic
        instance = this;
        packetManager = new PacketManager();

        CameraPreset.load();

        StreamCodec<RegistryFriendlyByteBuf, GeyserUtilPluginMessagePacket> CODEC = StreamCodec.ofMember((value, buf) -> buf.writeBytes(value.getData()), GeyserUtilPluginMessagePacket::new);

        PayloadTypeRegistry.playS2C().register(MAIN, CODEC);

        ServerLifecycleEvents.SERVER_STARTING.register(minecraftServer -> server = minecraftServer);

        PluginMessageEvent.EVENT.register((pluginMessagePacket, context) -> {
            ByteArrayDataInput inputStream  = ByteStreams.newDataInput(pluginMessagePacket.getData());
            String channel = inputStream.readUTF();
            byte[] message = new byte[inputStream.readInt()];
            inputStream.readFully(message);
            if (channel.equals(GeyserUtilsChannels.MAIN)) {
                CustomPayloadPacket packet = packetManager.decodePacket(message);
                if (packet instanceof NpcFormResponseCustomPayloadPacket) {
                    NpcFormResponseCustomPayloadPacket response = (NpcFormResponseCustomPayloadPacket) packet;
                    if (NpcDialogueForm.FORMS.containsKey(response.getFormId())) {

                        NpcDialogueForm form = NpcDialogueForm.FORMS.get(response.getFormId());

                        if (form.handler() != null) {
                            if (response.getButtonId() != -1) {
                                form.handler().accept(response.getFormId(), response.getButtonId());
                            }
                        }
                        if (response.getButtonId() == -1) {
                            if (form.closeHandler() != null) {
                                form.closeHandler().accept(response.getFormId());
                            }
                            NpcDialogueForm.FORMS.remove(response.getFormId());
                        }
                    }
                }
            }
        });
    }

    public static void send(ServerPlayer serverPlayer, String channel, byte[] payload) {
        ByteBuf rawBuf = Unpooled.buffer();
        FriendlyByteBuf buf = new FriendlyByteBuf(rawBuf);
        buf.writeUtf(GeyserUtilsChannels.MAIN);
        buf.writeShort(payload.length);
        buf.writeBytes(payload);
        ServerPlayNetworking.send(serverPlayer, new GeyserUtilPluginMessagePacket(payload));
    }
    static CustomPacketPayload.Type<GeyserUtilPluginMessagePacket> MAIN = new CustomPacketPayload.Type<>(ResourceLocation.parse(GeyserUtilsChannels.MAIN));

    public static ServerPlayer getPlayer(UUID uuid) {
        if (server == null)
            return null;

        return server.getPlayerList().getPlayer(uuid);
    }

    private static class GeyserUtilPluginMessagePacket extends PluginMessagePacket {

        public GeyserUtilPluginMessagePacket(byte[] payload) {
            super(payload);
        }

        public GeyserUtilPluginMessagePacket(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
            super(registryFriendlyByteBuf);
        }

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return MAIN;
        }
    }
}

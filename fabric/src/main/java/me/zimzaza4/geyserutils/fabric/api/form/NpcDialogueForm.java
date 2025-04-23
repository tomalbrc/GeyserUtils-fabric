package me.zimzaza4.geyserutils.fabric.api.form;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.zimzaza4.geyserutils.common.channel.GeyserUtilsChannels;
import me.zimzaza4.geyserutils.common.form.element.NpcDialogueButton;
import me.zimzaza4.geyserutils.common.packet.NpcDialogueFormDataCustomPayloadPacket;
import me.zimzaza4.geyserutils.fabric.GeyserUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Accessors(fluent = true)
public class NpcDialogueForm {
    public static Map<String, NpcDialogueForm> FORMS = new HashMap<>();

    String title;
    String dialogue;
    String skinData;
    Entity bindEntity;
    boolean hasNextForm = false;
    List<NpcDialogueButton> buttons;
    BiConsumer<String, Integer> handler;
    Consumer<String> closeHandler;

    FormHandler formHandler = new FormHandler();

    public static void closeForm(FloodgatePlayer floodgatePlayer) {
        NpcDialogueFormDataCustomPayloadPacket data = new NpcDialogueFormDataCustomPayloadPacket(null, null, null, null, -1, null, "CLOSE", false);
        ServerPlayer p = GeyserUtils.getPlayer(floodgatePlayer.getCorrectUniqueId());
        if (p != null) {
            GeyserUtils.send(p, GeyserUtilsChannels.MAIN, GeyserUtils.getPacketManager().encodePacket(data));
        }
    }

    public void send(FloodgatePlayer floodgatePlayer) {
        formHandler.send(floodgatePlayer);
    }

    public class FormHandler {
        private static final ScheduledExecutorService EXECUTOR = Executors.newScheduledThreadPool(1);
        private static final Map<UUID, ScheduledFuture<?>> RUNNING_TASKS = new ConcurrentHashMap<>();

        public void send(FloodgatePlayer floodgatePlayer) {
            UUID formId = UUID.randomUUID();

            NpcDialogueFormDataCustomPayloadPacket data = new NpcDialogueFormDataCustomPayloadPacket(
                    formId.toString(), title, dialogue, skinData, bindEntity.getId(), buttons, "OPEN", hasNextForm
            );

            ServerPlayer p = GeyserUtils.getPlayer(floodgatePlayer.getCorrectUniqueId());

            if (p != null) {
                FORMS.put(formId.toString(), NpcDialogueForm.this);

                GeyserUtils.send(p,
                        GeyserUtilsChannels.MAIN,
                        GeyserUtils.getPacketManager().encodePacket(data)
                );

                // Schedule repeating task
                ScheduledFuture<?> task = EXECUTOR.scheduleAtFixedRate(() -> {
                    if (!FORMS.containsKey(formId.toString())) {
                        cancelTask(formId);
                        return;
                    }
                    if (p.hasDisconnected()) {
                        FORMS.remove(formId.toString());
                    }
                }, 10, 10, TimeUnit.MILLISECONDS); // adjust time unit to match desired delay

                RUNNING_TASKS.put(formId, task);
            }
        }

        private void cancelTask(UUID formId) {
            ScheduledFuture<?> task = RUNNING_TASKS.remove(formId);
            if (task != null) {
                task.cancel(false);
            }
        }
    }
}

package dzwdz.chat_heads.mixin;

import dzwdz.chat_heads.EntryPoint;
import java.util.UUID;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatListenerHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.network.MessageType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatListenerHud.class)
public class ChatListenerHudMixin {
    @Inject(
            at = @At("HEAD"),
            method = "onChatMessage(Lnet/minecraft/network/MessageType;Lnet/minecraft/text/Text;Ljava/util/UUID;)V"
    )
    public void onChatMessage(MessageType messageType, Text message, UUID senderUuid, CallbackInfo callbackInfo) {
        EntryPoint.lastSender = MinecraftClient.getInstance().getNetworkHandler().getPlayerListEntry(senderUuid);
        String textString = message.getString();
        if (EntryPoint.lastSender == null) {
            for (String part : textString.split("(§.)|[^\\w]")) {
                if (part.isEmpty()) continue;
                PlayerListEntry p = MinecraftClient.getInstance().getNetworkHandler().getPlayerListEntry(part);
                if (p != null) {
                    EntryPoint.lastSender = p;
                    return;
                }
            }
        }
        for (PlayerListEntry p: MinecraftClient.getInstance().getNetworkHandler().getPlayerList()) {
            if (textString.contains(p.getDisplayName().getString())) {
                EntryPoint.lastSender = p;
                return;
            }
        }
    }
}

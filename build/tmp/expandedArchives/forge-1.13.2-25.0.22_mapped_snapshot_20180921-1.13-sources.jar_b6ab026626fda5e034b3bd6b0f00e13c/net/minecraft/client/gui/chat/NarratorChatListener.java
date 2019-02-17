package net.minecraft.client.gui.chat;

import com.mojang.text2speech.Narrator;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.toasts.GuiToast;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NarratorChatListener implements IChatListener {
   public static final NarratorChatListener INSTANCE = new NarratorChatListener();
   private final Narrator narrator = Narrator.getNarrator();

   /**
    * Called whenever this listener receives a chat message, if this listener is registered to the given type in {@link
    * net.minecraft.client.gui.GuiIngame#chatListeners chatListeners}
    */
   public void say(ChatType chatTypeIn, ITextComponent message) {
      int i = Minecraft.getInstance().gameSettings.narrator;
      if (i != 0 && this.narrator.active()) {
         if (i == 1 || i == 2 && chatTypeIn == ChatType.CHAT || i == 3 && chatTypeIn == ChatType.SYSTEM) {
            if (message instanceof TextComponentTranslation && "chat.type.text".equals(((TextComponentTranslation)message).getKey())) {
               this.narrator.say((new TextComponentTranslation("chat.type.text.narrate", ((TextComponentTranslation)message).getFormatArgs())).getString());
            } else {
               this.narrator.say(message.getString());
            }
         }

      }
   }

   public void announceMode(int p_193641_1_) {
      this.narrator.clear();
      this.narrator.say((new TextComponentTranslation("options.narrator")).getString() + " : " + (new TextComponentTranslation(GameSettings.NARRATOR_MODES[p_193641_1_])).getString());
      GuiToast guitoast = Minecraft.getInstance().getToastGui();
      if (this.narrator.active()) {
         if (p_193641_1_ == 0) {
            SystemToast.addOrUpdate(guitoast, SystemToast.Type.NARRATOR_TOGGLE, new TextComponentTranslation("narrator.toast.disabled"), (ITextComponent)null);
         } else {
            SystemToast.addOrUpdate(guitoast, SystemToast.Type.NARRATOR_TOGGLE, new TextComponentTranslation("narrator.toast.enabled"), new TextComponentTranslation(GameSettings.NARRATOR_MODES[p_193641_1_]));
         }
      } else {
         SystemToast.addOrUpdate(guitoast, SystemToast.Type.NARRATOR_TOGGLE, new TextComponentTranslation("narrator.toast.disabled"), new TextComponentTranslation("options.narrator.notavailable"));
      }

   }

   public boolean isActive() {
      return this.narrator.active();
   }

   public void clear() {
      this.narrator.clear();
   }
}
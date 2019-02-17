package net.minecraft.command;

import net.minecraft.util.text.ITextComponent;

public interface ICommandSource {
   /**
    * Send a chat message to the CommandSender
    */
   void sendMessage(ITextComponent component);

   boolean shouldReceiveFeedback();

   boolean shouldReceiveErrors();

   boolean allowLogging();
}
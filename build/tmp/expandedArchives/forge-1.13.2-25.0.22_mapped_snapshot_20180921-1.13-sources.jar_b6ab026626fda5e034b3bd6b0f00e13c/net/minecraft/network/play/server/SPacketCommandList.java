package net.minecraft.network.play.server;

import com.google.common.collect.Maps;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketCommandList implements Packet<INetHandlerPlayClient> {
   private RootCommandNode<ISuggestionProvider> root;

   public SPacketCommandList() {
   }

   public SPacketCommandList(RootCommandNode<ISuggestionProvider> p_i47940_1_) {
      this.root = p_i47940_1_;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      SPacketCommandList.Entry[] aspacketcommandlist$entry = new SPacketCommandList.Entry[buf.readVarInt()];
      Deque<SPacketCommandList.Entry> deque = new ArrayDeque<>(aspacketcommandlist$entry.length);

      for(int i = 0; i < aspacketcommandlist$entry.length; ++i) {
         aspacketcommandlist$entry[i] = this.func_197692_c(buf);
         deque.add(aspacketcommandlist$entry[i]);
      }

      while(!deque.isEmpty()) {
         boolean flag = false;
         Iterator<SPacketCommandList.Entry> iterator = deque.iterator();

         while(iterator.hasNext()) {
            SPacketCommandList.Entry spacketcommandlist$entry = iterator.next();
            if (spacketcommandlist$entry.func_197723_a(aspacketcommandlist$entry)) {
               iterator.remove();
               flag = true;
            }
         }

         if (!flag) {
            throw new IllegalStateException("Server sent an impossible command tree");
         }
      }

      this.root = (RootCommandNode)aspacketcommandlist$entry[buf.readVarInt()].field_197730_e;
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      Map<CommandNode<ISuggestionProvider>, Integer> map = Maps.newHashMap();
      Deque<CommandNode<ISuggestionProvider>> deque = new ArrayDeque<>();
      deque.add(this.root);

      while(!deque.isEmpty()) {
         CommandNode<ISuggestionProvider> commandnode = deque.pollFirst();
         if (!map.containsKey(commandnode)) {
            int i = map.size();
            map.put(commandnode, i);
            deque.addAll(commandnode.getChildren());
            if (commandnode.getRedirect() != null) {
               deque.add(commandnode.getRedirect());
            }
         }
      }

      CommandNode<ISuggestionProvider>[] commandnode2 = new CommandNode[map.size()];

      for(Map.Entry<CommandNode<ISuggestionProvider>, Integer> entry : map.entrySet()) {
         commandnode2[entry.getValue()] = entry.getKey();
      }

      buf.writeVarInt(commandnode2.length);

      for(CommandNode<ISuggestionProvider> commandnode1 : commandnode2) {
         this.func_197696_a(buf, commandnode1, map);
      }

      buf.writeVarInt(map.get(this.root));
   }

   private SPacketCommandList.Entry func_197692_c(PacketBuffer p_197692_1_) {
      byte b0 = p_197692_1_.readByte();
      int[] aint = p_197692_1_.readVarIntArray();
      int i = (b0 & 8) != 0 ? p_197692_1_.readVarInt() : 0;
      ArgumentBuilder<ISuggestionProvider, ?> argumentbuilder = this.func_197695_a(p_197692_1_, b0);
      return new SPacketCommandList.Entry(argumentbuilder, b0, i, aint);
   }

   @Nullable
   private ArgumentBuilder<ISuggestionProvider, ?> func_197695_a(PacketBuffer p_197695_1_, byte p_197695_2_) {
      int i = p_197695_2_ & 3;
      if (i == 2) {
         String s = p_197695_1_.readString(32767);
         ArgumentType<?> argumenttype = ArgumentTypes.deserialize(p_197695_1_);
         if (argumenttype == null) {
            return null;
         } else {
            RequiredArgumentBuilder<ISuggestionProvider, ?> requiredargumentbuilder = RequiredArgumentBuilder.argument(s, argumenttype);
            if ((p_197695_2_ & 16) != 0) {
               requiredargumentbuilder.suggests(SuggestionProviders.get(p_197695_1_.readResourceLocation()));
            }

            return requiredargumentbuilder;
         }
      } else {
         return i == 1 ? LiteralArgumentBuilder.literal(p_197695_1_.readString(32767)) : null;
      }
   }

   private void func_197696_a(PacketBuffer p_197696_1_, CommandNode<ISuggestionProvider> p_197696_2_, Map<CommandNode<ISuggestionProvider>, Integer> p_197696_3_) {
      byte b0 = 0;
      if (p_197696_2_.getRedirect() != null) {
         b0 = (byte)(b0 | 8);
      }

      if (p_197696_2_.getCommand() != null) {
         b0 = (byte)(b0 | 4);
      }

      if (p_197696_2_ instanceof RootCommandNode) {
         b0 = (byte)(b0 | 0);
      } else if (p_197696_2_ instanceof ArgumentCommandNode) {
         b0 = (byte)(b0 | 2);
         if (((ArgumentCommandNode)p_197696_2_).getCustomSuggestions() != null) {
            b0 = (byte)(b0 | 16);
         }
      } else {
         if (!(p_197696_2_ instanceof LiteralCommandNode)) {
            throw new UnsupportedOperationException("Unknown node type " + p_197696_2_);
         }

         b0 = (byte)(b0 | 1);
      }

      p_197696_1_.writeByte(b0);
      p_197696_1_.writeVarInt(p_197696_2_.getChildren().size());

      for(CommandNode<ISuggestionProvider> commandnode : p_197696_2_.getChildren()) {
         p_197696_1_.writeVarInt(p_197696_3_.get(commandnode));
      }

      if (p_197696_2_.getRedirect() != null) {
         p_197696_1_.writeVarInt(p_197696_3_.get(p_197696_2_.getRedirect()));
      }

      if (p_197696_2_ instanceof ArgumentCommandNode) {
         ArgumentCommandNode<ISuggestionProvider, ?> argumentcommandnode = (ArgumentCommandNode)p_197696_2_;
         p_197696_1_.writeString(argumentcommandnode.getName());
         ArgumentTypes.serialize(p_197696_1_, argumentcommandnode.getType());
         if (argumentcommandnode.getCustomSuggestions() != null) {
            p_197696_1_.writeResourceLocation(SuggestionProviders.getId(argumentcommandnode.getCustomSuggestions()));
         }
      } else if (p_197696_2_ instanceof LiteralCommandNode) {
         p_197696_1_.writeString(((LiteralCommandNode)p_197696_2_).getLiteral());
      }

   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleCommandList(this);
   }

   @OnlyIn(Dist.CLIENT)
   public RootCommandNode<ISuggestionProvider> getRoot() {
      return this.root;
   }

   static class Entry {
      @Nullable
      private final ArgumentBuilder<ISuggestionProvider, ?> field_197726_a;
      private final byte field_197727_b;
      private final int field_197728_c;
      private final int[] field_197729_d;
      private CommandNode<ISuggestionProvider> field_197730_e;

      private Entry(@Nullable ArgumentBuilder<ISuggestionProvider, ?> p_i48139_1_, byte p_i48139_2_, int p_i48139_3_, int[] p_i48139_4_) {
         this.field_197726_a = p_i48139_1_;
         this.field_197727_b = p_i48139_2_;
         this.field_197728_c = p_i48139_3_;
         this.field_197729_d = p_i48139_4_;
      }

      public boolean func_197723_a(SPacketCommandList.Entry[] p_197723_1_) {
         if (this.field_197730_e == null) {
            if (this.field_197726_a == null) {
               this.field_197730_e = new RootCommandNode<>();
            } else {
               if ((this.field_197727_b & 8) != 0) {
                  if (p_197723_1_[this.field_197728_c].field_197730_e == null) {
                     return false;
                  }

                  this.field_197726_a.redirect(p_197723_1_[this.field_197728_c].field_197730_e);
               }

               if ((this.field_197727_b & 4) != 0) {
                  this.field_197726_a.executes((p_197724_0_) -> {
                     return 0;
                  });
               }

               this.field_197730_e = this.field_197726_a.build();
            }
         }

         for(int i : this.field_197729_d) {
            if (p_197723_1_[i].field_197730_e == null) {
               return false;
            }
         }

         for(int j : this.field_197729_d) {
            CommandNode<ISuggestionProvider> commandnode = p_197723_1_[j].field_197730_e;
            if (!(commandnode instanceof RootCommandNode)) {
               this.field_197730_e.addChild(commandnode);
            }
         }

         return true;
      }
   }
}
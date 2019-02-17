package net.minecraft.server.gui;

import java.util.Vector;
import javax.swing.JList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ITickable;

public class PlayerListComponent extends JList<String> implements ITickable {
   private final MinecraftServer server;
   private int ticks;

   public PlayerListComponent(MinecraftServer server) {
      this.server = server;
      server.registerTickable(this);
   }

   public void tick() {
      if (this.ticks++ % 20 == 0) {
         Vector<String> vector = new Vector<>();

         for(int i = 0; i < this.server.getPlayerList().getPlayers().size(); ++i) {
            vector.add(this.server.getPlayerList().getPlayers().get(i).getGameProfile().getName());
         }

         this.setListData(vector);
      }

   }
}
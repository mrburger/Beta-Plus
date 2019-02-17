package net.minecraft.scoreboard;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketDisplayObjective;
import net.minecraft.network.play.server.SPacketScoreboardObjective;
import net.minecraft.network.play.server.SPacketTeams;
import net.minecraft.network.play.server.SPacketUpdateScore;
import net.minecraft.server.MinecraftServer;

public class ServerScoreboard extends Scoreboard {
   private final MinecraftServer server;
   private final Set<ScoreObjective> addedObjectives = Sets.newHashSet();
   private Runnable[] dirtyRunnables = new Runnable[0];

   public ServerScoreboard(MinecraftServer mcServer) {
      this.server = mcServer;
   }

   public void onScoreUpdated(Score scoreIn) {
      super.onScoreUpdated(scoreIn);
      if (this.addedObjectives.contains(scoreIn.getObjective())) {
         this.server.getPlayerList().sendPacketToAllPlayers(new SPacketUpdateScore(ServerScoreboard.Action.CHANGE, scoreIn.getObjective().getName(), scoreIn.getPlayerName(), scoreIn.getScorePoints()));
      }

      this.markSaveDataDirty();
   }

   public void broadcastScoreUpdate(String scoreName) {
      super.broadcastScoreUpdate(scoreName);
      this.server.getPlayerList().sendPacketToAllPlayers(new SPacketUpdateScore(ServerScoreboard.Action.REMOVE, (String)null, scoreName, 0));
      this.markSaveDataDirty();
   }

   public void broadcastScoreUpdate(String scoreName, ScoreObjective objective) {
      super.broadcastScoreUpdate(scoreName, objective);
      if (this.addedObjectives.contains(objective)) {
         this.server.getPlayerList().sendPacketToAllPlayers(new SPacketUpdateScore(ServerScoreboard.Action.REMOVE, objective.getName(), scoreName, 0));
      }

      this.markSaveDataDirty();
   }

   /**
    * 0 is tab menu, 1 is sidebar, 2 is below name
    */
   public void setObjectiveInDisplaySlot(int objectiveSlot, @Nullable ScoreObjective objective) {
      ScoreObjective scoreobjective = this.getObjectiveInDisplaySlot(objectiveSlot);
      super.setObjectiveInDisplaySlot(objectiveSlot, objective);
      if (scoreobjective != objective && scoreobjective != null) {
         if (this.getObjectiveDisplaySlotCount(scoreobjective) > 0) {
            this.server.getPlayerList().sendPacketToAllPlayers(new SPacketDisplayObjective(objectiveSlot, objective));
         } else {
            this.sendDisplaySlotRemovalPackets(scoreobjective);
         }
      }

      if (objective != null) {
         if (this.addedObjectives.contains(objective)) {
            this.server.getPlayerList().sendPacketToAllPlayers(new SPacketDisplayObjective(objectiveSlot, objective));
         } else {
            this.addObjective(objective);
         }
      }

      this.markSaveDataDirty();
   }

   public boolean addPlayerToTeam(String p_197901_1_, ScorePlayerTeam p_197901_2_) {
      if (super.addPlayerToTeam(p_197901_1_, p_197901_2_)) {
         this.server.getPlayerList().sendPacketToAllPlayers(new SPacketTeams(p_197901_2_, Arrays.asList(p_197901_1_), 3));
         this.markSaveDataDirty();
         return true;
      } else {
         return false;
      }
   }

   /**
    * Removes the given username from the given ScorePlayerTeam. If the player is not on the team then an
    * IllegalStateException is thrown.
    */
   public void removePlayerFromTeam(String username, ScorePlayerTeam playerTeam) {
      super.removePlayerFromTeam(username, playerTeam);
      this.server.getPlayerList().sendPacketToAllPlayers(new SPacketTeams(playerTeam, Arrays.asList(username), 4));
      this.markSaveDataDirty();
   }

   /**
    * Called when a score objective is added
    */
   public void onScoreObjectiveAdded(ScoreObjective scoreObjectiveIn) {
      super.onScoreObjectiveAdded(scoreObjectiveIn);
      this.markSaveDataDirty();
   }

   public void func_199869_b(ScoreObjective p_199869_1_) {
      super.func_199869_b(p_199869_1_);
      if (this.addedObjectives.contains(p_199869_1_)) {
         this.server.getPlayerList().sendPacketToAllPlayers(new SPacketScoreboardObjective(p_199869_1_, 2));
      }

      this.markSaveDataDirty();
   }

   public void onScoreObjectiveRemoved(ScoreObjective objective) {
      super.onScoreObjectiveRemoved(objective);
      if (this.addedObjectives.contains(objective)) {
         this.sendDisplaySlotRemovalPackets(objective);
      }

      this.markSaveDataDirty();
   }

   /**
    * This packet will notify the players that this team is created, and that will register it on the client
    */
   public void broadcastTeamCreated(ScorePlayerTeam playerTeam) {
      super.broadcastTeamCreated(playerTeam);
      this.server.getPlayerList().sendPacketToAllPlayers(new SPacketTeams(playerTeam, 0));
      this.markSaveDataDirty();
   }

   /**
    * This packet will notify the players that this team is updated
    */
   public void broadcastTeamInfoUpdate(ScorePlayerTeam playerTeam) {
      super.broadcastTeamInfoUpdate(playerTeam);
      this.server.getPlayerList().sendPacketToAllPlayers(new SPacketTeams(playerTeam, 2));
      this.markSaveDataDirty();
   }

   public void broadcastTeamRemove(ScorePlayerTeam playerTeam) {
      super.broadcastTeamRemove(playerTeam);
      this.server.getPlayerList().sendPacketToAllPlayers(new SPacketTeams(playerTeam, 1));
      this.markSaveDataDirty();
   }

   public void addDirtyRunnable(Runnable runnable) {
      this.dirtyRunnables = Arrays.copyOf(this.dirtyRunnables, this.dirtyRunnables.length + 1);
      this.dirtyRunnables[this.dirtyRunnables.length - 1] = runnable;
   }

   protected void markSaveDataDirty() {
      for(Runnable runnable : this.dirtyRunnables) {
         runnable.run();
      }

   }

   public List<Packet<?>> getCreatePackets(ScoreObjective objective) {
      List<Packet<?>> list = Lists.newArrayList();
      list.add(new SPacketScoreboardObjective(objective, 0));

      for(int i = 0; i < 19; ++i) {
         if (this.getObjectiveInDisplaySlot(i) == objective) {
            list.add(new SPacketDisplayObjective(i, objective));
         }
      }

      for(Score score : this.getSortedScores(objective)) {
         list.add(new SPacketUpdateScore(ServerScoreboard.Action.CHANGE, score.getObjective().getName(), score.getPlayerName(), score.getScorePoints()));
      }

      return list;
   }

   public void addObjective(ScoreObjective objective) {
      List<Packet<?>> list = this.getCreatePackets(objective);

      for(EntityPlayerMP entityplayermp : this.server.getPlayerList().getPlayers()) {
         for(Packet<?> packet : list) {
            entityplayermp.connection.sendPacket(packet);
         }
      }

      this.addedObjectives.add(objective);
   }

   public List<Packet<?>> getDestroyPackets(ScoreObjective p_96548_1_) {
      List<Packet<?>> list = Lists.newArrayList();
      list.add(new SPacketScoreboardObjective(p_96548_1_, 1));

      for(int i = 0; i < 19; ++i) {
         if (this.getObjectiveInDisplaySlot(i) == p_96548_1_) {
            list.add(new SPacketDisplayObjective(i, p_96548_1_));
         }
      }

      return list;
   }

   public void sendDisplaySlotRemovalPackets(ScoreObjective p_96546_1_) {
      List<Packet<?>> list = this.getDestroyPackets(p_96546_1_);

      for(EntityPlayerMP entityplayermp : this.server.getPlayerList().getPlayers()) {
         for(Packet<?> packet : list) {
            entityplayermp.connection.sendPacket(packet);
         }
      }

      this.addedObjectives.remove(p_96546_1_);
   }

   public int getObjectiveDisplaySlotCount(ScoreObjective p_96552_1_) {
      int i = 0;

      for(int j = 0; j < 19; ++j) {
         if (this.getObjectiveInDisplaySlot(j) == p_96552_1_) {
            ++i;
         }
      }

      return i;
   }

   public static enum Action {
      CHANGE,
      REMOVE;
   }
}
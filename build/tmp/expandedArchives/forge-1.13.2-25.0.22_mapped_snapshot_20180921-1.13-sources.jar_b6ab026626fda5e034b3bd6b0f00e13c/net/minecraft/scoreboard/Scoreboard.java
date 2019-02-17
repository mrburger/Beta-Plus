package net.minecraft.scoreboard;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Scoreboard {
   /** Map of objective names to ScoreObjective objects. */
   private final Map<String, ScoreObjective> scoreObjectives = Maps.newHashMap();
   /** Map of IScoreObjectiveCriteria objects to ScoreObjective objects. */
   private final Map<ScoreCriteria, List<ScoreObjective>> scoreObjectiveCriterias = Maps.newHashMap();
   /** Map of entities name to ScoreObjective objects. */
   private final Map<String, Map<ScoreObjective, Score>> entitiesScoreObjectives = Maps.newHashMap();
   /** Index 0 is tab menu, 1 is sidebar, and 2 is below name */
   private final ScoreObjective[] objectiveDisplaySlots = new ScoreObjective[19];
   /** Map of teamnames to ScorePlayerTeam instances */
   private final Map<String, ScorePlayerTeam> teams = Maps.newHashMap();
   /** Map of usernames to ScorePlayerTeam objects. */
   private final Map<String, ScorePlayerTeam> teamMemberships = Maps.newHashMap();
   private static String[] displaySlots;

   @OnlyIn(Dist.CLIENT)
   public boolean hasObjective(String p_197900_1_) {
      return this.scoreObjectives.containsKey(p_197900_1_);
   }

   public ScoreObjective getOrCreateObjective(String p_197899_1_) {
      return this.scoreObjectives.get(p_197899_1_);
   }

   /**
    * Returns a ScoreObjective for the objective name
    */
   @Nullable
   public ScoreObjective getObjective(@Nullable String name) {
      return this.scoreObjectives.get(name);
   }

   public ScoreObjective addObjective(String p_199868_1_, ScoreCriteria p_199868_2_, ITextComponent p_199868_3_, ScoreCriteria.RenderType p_199868_4_) {
      if (p_199868_1_.length() > 16) {
         throw new IllegalArgumentException("The objective name '" + p_199868_1_ + "' is too long!");
      } else if (this.scoreObjectives.containsKey(p_199868_1_)) {
         throw new IllegalArgumentException("An objective with the name '" + p_199868_1_ + "' already exists!");
      } else {
         ScoreObjective scoreobjective = new ScoreObjective(this, p_199868_1_, p_199868_2_, p_199868_3_, p_199868_4_);
         this.scoreObjectiveCriterias.computeIfAbsent(p_199868_2_, (p_197903_0_) -> {
            return Lists.newArrayList();
         }).add(scoreobjective);
         this.scoreObjectives.put(p_199868_1_, scoreobjective);
         this.onScoreObjectiveAdded(scoreobjective);
         return scoreobjective;
      }
   }

   public final void forAllObjectives(ScoreCriteria p_197893_1_, String p_197893_2_, Consumer<Score> p_197893_3_) {
      this.scoreObjectiveCriterias.getOrDefault(p_197893_1_, Collections.emptyList()).forEach((p_197906_3_) -> {
         p_197893_3_.accept(this.getOrCreateScore(p_197893_2_, p_197906_3_));
      });
   }

   /**
    * Returns if the entity has the given ScoreObjective
    */
   public boolean entityHasObjective(String name, ScoreObjective objective) {
      Map<ScoreObjective, Score> map = this.entitiesScoreObjectives.get(name);
      if (map == null) {
         return false;
      } else {
         Score score = map.get(objective);
         return score != null;
      }
   }

   /**
    * Get a player's score or create it if it does not exist
    */
   public Score getOrCreateScore(String username, ScoreObjective objective) {
      if (username.length() > 40) {
         throw new IllegalArgumentException("The player name '" + username + "' is too long!");
      } else {
         Map<ScoreObjective, Score> map = this.entitiesScoreObjectives.computeIfAbsent(username, (p_197898_0_) -> {
            return Maps.newHashMap();
         });
         return map.computeIfAbsent(objective, (p_197904_2_) -> {
            Score score = new Score(this, p_197904_2_, username);
            score.setScorePoints(0);
            return score;
         });
      }
   }

   /**
    * Returns an array of Score objects, sorting by Score.getScorePoints()
    */
   public Collection<Score> getSortedScores(ScoreObjective objective) {
      List<Score> list = Lists.newArrayList();

      for(Map<ScoreObjective, Score> map : this.entitiesScoreObjectives.values()) {
         Score score = map.get(objective);
         if (score != null) {
            list.add(score);
         }
      }

      Collections.sort(list, Score.SCORE_COMPARATOR);
      return list;
   }

   public Collection<ScoreObjective> getScoreObjectives() {
      return this.scoreObjectives.values();
   }

   public Collection<String> func_197897_d() {
      return this.scoreObjectives.keySet();
   }

   public Collection<String> getObjectiveNames() {
      return Lists.newArrayList(this.entitiesScoreObjectives.keySet());
   }

   /**
    * Remove the given ScoreObjective for the given Entity name.
    */
   public void removeObjectiveFromEntity(String name, @Nullable ScoreObjective objective) {
      if (objective == null) {
         Map<ScoreObjective, Score> map = this.entitiesScoreObjectives.remove(name);
         if (map != null) {
            this.broadcastScoreUpdate(name);
         }
      } else {
         Map<ScoreObjective, Score> map2 = this.entitiesScoreObjectives.get(name);
         if (map2 != null) {
            Score score = map2.remove(objective);
            if (map2.size() < 1) {
               Map<ScoreObjective, Score> map1 = this.entitiesScoreObjectives.remove(name);
               if (map1 != null) {
                  this.broadcastScoreUpdate(name);
               }
            } else if (score != null) {
               this.broadcastScoreUpdate(name, objective);
            }
         }
      }

   }

   /**
    * Returns all the objectives for the given entity
    */
   public Map<ScoreObjective, Score> getObjectivesForEntity(String name) {
      Map<ScoreObjective, Score> map = this.entitiesScoreObjectives.get(name);
      if (map == null) {
         map = Maps.newHashMap();
      }

      return map;
   }

   public void removeObjective(ScoreObjective objective) {
      this.scoreObjectives.remove(objective.getName());

      for(int i = 0; i < 19; ++i) {
         if (this.getObjectiveInDisplaySlot(i) == objective) {
            this.setObjectiveInDisplaySlot(i, (ScoreObjective)null);
         }
      }

      List<ScoreObjective> list = this.scoreObjectiveCriterias.get(objective.getCriteria());
      if (list != null) {
         list.remove(objective);
      }

      for(Map<ScoreObjective, Score> map : this.entitiesScoreObjectives.values()) {
         map.remove(objective);
      }

      this.onScoreObjectiveRemoved(objective);
   }

   /**
    * 0 is tab menu, 1 is sidebar, 2 is below name
    */
   public void setObjectiveInDisplaySlot(int objectiveSlot, @Nullable ScoreObjective objective) {
      this.objectiveDisplaySlots[objectiveSlot] = objective;
   }

   /**
    * 0 is tab menu, 1 is sidebar, 2 is below name
    */
   @Nullable
   public ScoreObjective getObjectiveInDisplaySlot(int slotIn) {
      return this.objectiveDisplaySlots[slotIn];
   }

   /**
    * Retrieve the ScorePlayerTeam instance identified by the passed team name
    */
   public ScorePlayerTeam getTeam(String teamName) {
      return this.teams.get(teamName);
   }

   public ScorePlayerTeam createTeam(String name) {
      if (name.length() > 16) {
         throw new IllegalArgumentException("The team name '" + name + "' is too long!");
      } else {
         ScorePlayerTeam scoreplayerteam = this.getTeam(name);
         if (scoreplayerteam != null) {
            throw new IllegalArgumentException("A team with the name '" + name + "' already exists!");
         } else {
            scoreplayerteam = new ScorePlayerTeam(this, name);
            this.teams.put(name, scoreplayerteam);
            this.broadcastTeamCreated(scoreplayerteam);
            return scoreplayerteam;
         }
      }
   }

   /**
    * Removes the team from the scoreboard, updates all player memberships and broadcasts the deletion to all players
    */
   public void removeTeam(ScorePlayerTeam playerTeam) {
      this.teams.remove(playerTeam.getName());

      for(String s : playerTeam.getMembershipCollection()) {
         this.teamMemberships.remove(s);
      }

      this.broadcastTeamRemove(playerTeam);
   }

   public boolean addPlayerToTeam(String p_197901_1_, ScorePlayerTeam p_197901_2_) {
      if (p_197901_1_.length() > 40) {
         throw new IllegalArgumentException("The player name '" + p_197901_1_ + "' is too long!");
      } else {
         if (this.getPlayersTeam(p_197901_1_) != null) {
            this.removePlayerFromTeams(p_197901_1_);
         }

         this.teamMemberships.put(p_197901_1_, p_197901_2_);
         return p_197901_2_.getMembershipCollection().add(p_197901_1_);
      }
   }

   public boolean removePlayerFromTeams(String playerName) {
      ScorePlayerTeam scoreplayerteam = this.getPlayersTeam(playerName);
      if (scoreplayerteam != null) {
         this.removePlayerFromTeam(playerName, scoreplayerteam);
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
      if (this.getPlayersTeam(username) != playerTeam) {
         throw new IllegalStateException("Player is either on another team or not on any team. Cannot remove from team '" + playerTeam.getName() + "'.");
      } else {
         this.teamMemberships.remove(username);
         playerTeam.getMembershipCollection().remove(username);
      }
   }

   /**
    * Retrieve all registered ScorePlayerTeam names
    */
   public Collection<String> getTeamNames() {
      return this.teams.keySet();
   }

   /**
    * Retrieve all registered ScorePlayerTeam instances
    */
   public Collection<ScorePlayerTeam> getTeams() {
      return this.teams.values();
   }

   /**
    * Gets the ScorePlayerTeam object for the given username.
    */
   @Nullable
   public ScorePlayerTeam getPlayersTeam(String username) {
      return this.teamMemberships.get(username);
   }

   /**
    * Called when a score objective is added
    */
   public void onScoreObjectiveAdded(ScoreObjective scoreObjectiveIn) {
   }

   public void func_199869_b(ScoreObjective p_199869_1_) {
   }

   public void onScoreObjectiveRemoved(ScoreObjective objective) {
   }

   public void onScoreUpdated(Score scoreIn) {
   }

   public void broadcastScoreUpdate(String scoreName) {
   }

   public void broadcastScoreUpdate(String scoreName, ScoreObjective objective) {
   }

   /**
    * This packet will notify the players that this team is created, and that will register it on the client
    */
   public void broadcastTeamCreated(ScorePlayerTeam playerTeam) {
   }

   /**
    * This packet will notify the players that this team is updated
    */
   public void broadcastTeamInfoUpdate(ScorePlayerTeam playerTeam) {
   }

   public void broadcastTeamRemove(ScorePlayerTeam playerTeam) {
   }

   /**
    * Returns 'list' for 0, 'sidebar' for 1, 'belowName for 2, otherwise null.
    */
   public static String getObjectiveDisplaySlot(int id) {
      switch(id) {
      case 0:
         return "list";
      case 1:
         return "sidebar";
      case 2:
         return "belowName";
      default:
         if (id >= 3 && id <= 18) {
            TextFormatting textformatting = TextFormatting.fromColorIndex(id - 3);
            if (textformatting != null && textformatting != TextFormatting.RESET) {
               return "sidebar.team." + textformatting.getFriendlyName();
            }
         }

         return null;
      }
   }

   /**
    * Returns 0 for (case-insensitive) 'list', 1 for 'sidebar', 2 for 'belowName', otherwise -1.
    */
   public static int getObjectiveDisplaySlotNumber(String name) {
      if ("list".equalsIgnoreCase(name)) {
         return 0;
      } else if ("sidebar".equalsIgnoreCase(name)) {
         return 1;
      } else if ("belowName".equalsIgnoreCase(name)) {
         return 2;
      } else {
         if (name.startsWith("sidebar.team.")) {
            String s = name.substring("sidebar.team.".length());
            TextFormatting textformatting = TextFormatting.getValueByName(s);
            if (textformatting != null && textformatting.getColorIndex() >= 0) {
               return textformatting.getColorIndex() + 3;
            }
         }

         return -1;
      }
   }

   public static String[] getDisplaySlotStrings() {
      if (displaySlots == null) {
         displaySlots = new String[19];

         for(int i = 0; i < 19; ++i) {
            displaySlots[i] = getObjectiveDisplaySlot(i);
         }
      }

      return displaySlots;
   }

   public void removeEntity(Entity entityIn) {
      if (entityIn != null && !(entityIn instanceof EntityPlayer) && !entityIn.isAlive()) {
         String s = entityIn.getCachedUniqueIdString();
         this.removeObjectiveFromEntity(s, (ScoreObjective)null);
         this.removePlayerFromTeams(s);
      }
   }

   protected NBTTagList func_197902_i() {
      NBTTagList nbttaglist = new NBTTagList();
      this.entitiesScoreObjectives.values().stream().map(Map::values).forEach((p_197894_1_) -> {
         p_197894_1_.stream().filter((p_209546_0_) -> {
            return p_209546_0_.getObjective() != null;
         }).forEach((p_197896_1_) -> {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setString("Name", p_197896_1_.getPlayerName());
            nbttagcompound.setString("Objective", p_197896_1_.getObjective().getName());
            nbttagcompound.setInt("Score", p_197896_1_.getScorePoints());
            nbttagcompound.setBoolean("Locked", p_197896_1_.isLocked());
            nbttaglist.add((INBTBase)nbttagcompound);
         });
      });
      return nbttaglist;
   }

   protected void func_197905_a(NBTTagList p_197905_1_) {
      for(int i = 0; i < p_197905_1_.size(); ++i) {
         NBTTagCompound nbttagcompound = p_197905_1_.getCompound(i);
         ScoreObjective scoreobjective = this.getOrCreateObjective(nbttagcompound.getString("Objective"));
         String s = nbttagcompound.getString("Name");
         if (s.length() > 40) {
            s = s.substring(0, 40);
         }

         Score score = this.getOrCreateScore(s, scoreobjective);
         score.setScorePoints(nbttagcompound.getInt("Score"));
         if (nbttagcompound.hasKey("Locked")) {
            score.setLocked(nbttagcompound.getBoolean("Locked"));
         }
      }

   }
}
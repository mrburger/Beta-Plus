package net.minecraft.scoreboard;

import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.storage.WorldSavedData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScoreboardSaveData extends WorldSavedData {
   private static final Logger LOGGER = LogManager.getLogger();
   private Scoreboard scoreboard;
   private NBTTagCompound delayedInitNbt;

   public ScoreboardSaveData() {
      this("scoreboard");
   }

   public ScoreboardSaveData(String name) {
      super(name);
   }

   public void setScoreboard(Scoreboard scoreboardIn) {
      this.scoreboard = scoreboardIn;
      if (this.delayedInitNbt != null) {
         this.read(this.delayedInitNbt);
      }

   }

   /**
    * reads in data from the NBTTagCompound into this MapDataBase
    */
   public void read(NBTTagCompound nbt) {
      if (this.scoreboard == null) {
         this.delayedInitNbt = nbt;
      } else {
         this.readObjectives(nbt.getList("Objectives", 10));
         this.scoreboard.func_197905_a(nbt.getList("PlayerScores", 10));
         if (nbt.contains("DisplaySlots", 10)) {
            this.readDisplayConfig(nbt.getCompound("DisplaySlots"));
         }

         if (nbt.contains("Teams", 9)) {
            this.readTeams(nbt.getList("Teams", 10));
         }

      }
   }

   protected void readTeams(NBTTagList tagList) {
      for(int i = 0; i < tagList.size(); ++i) {
         NBTTagCompound nbttagcompound = tagList.getCompound(i);
         String s = nbttagcompound.getString("Name");
         if (s.length() > 16) {
            s = s.substring(0, 16);
         }

         ScorePlayerTeam scoreplayerteam = this.scoreboard.createTeam(s);
         ITextComponent itextcomponent = ITextComponent.Serializer.fromJson(nbttagcompound.getString("DisplayName"));
         if (itextcomponent != null) {
            scoreplayerteam.setDisplayName(itextcomponent);
         }

         if (nbttagcompound.contains("TeamColor", 8)) {
            scoreplayerteam.setColor(TextFormatting.getValueByName(nbttagcompound.getString("TeamColor")));
         }

         if (nbttagcompound.contains("AllowFriendlyFire", 99)) {
            scoreplayerteam.setAllowFriendlyFire(nbttagcompound.getBoolean("AllowFriendlyFire"));
         }

         if (nbttagcompound.contains("SeeFriendlyInvisibles", 99)) {
            scoreplayerteam.setSeeFriendlyInvisiblesEnabled(nbttagcompound.getBoolean("SeeFriendlyInvisibles"));
         }

         if (nbttagcompound.contains("MemberNamePrefix", 8)) {
            ITextComponent itextcomponent1 = ITextComponent.Serializer.fromJson(nbttagcompound.getString("MemberNamePrefix"));
            if (itextcomponent1 != null) {
               scoreplayerteam.setPrefix(itextcomponent1);
            }
         }

         if (nbttagcompound.contains("MemberNameSuffix", 8)) {
            ITextComponent itextcomponent2 = ITextComponent.Serializer.fromJson(nbttagcompound.getString("MemberNameSuffix"));
            if (itextcomponent2 != null) {
               scoreplayerteam.setSuffix(itextcomponent2);
            }
         }

         if (nbttagcompound.contains("NameTagVisibility", 8)) {
            Team.EnumVisible team$enumvisible = Team.EnumVisible.getByName(nbttagcompound.getString("NameTagVisibility"));
            if (team$enumvisible != null) {
               scoreplayerteam.setNameTagVisibility(team$enumvisible);
            }
         }

         if (nbttagcompound.contains("DeathMessageVisibility", 8)) {
            Team.EnumVisible team$enumvisible1 = Team.EnumVisible.getByName(nbttagcompound.getString("DeathMessageVisibility"));
            if (team$enumvisible1 != null) {
               scoreplayerteam.setDeathMessageVisibility(team$enumvisible1);
            }
         }

         if (nbttagcompound.contains("CollisionRule", 8)) {
            Team.CollisionRule team$collisionrule = Team.CollisionRule.getByName(nbttagcompound.getString("CollisionRule"));
            if (team$collisionrule != null) {
               scoreplayerteam.setCollisionRule(team$collisionrule);
            }
         }

         this.loadTeamPlayers(scoreplayerteam, nbttagcompound.getList("Players", 8));
      }

   }

   protected void loadTeamPlayers(ScorePlayerTeam playerTeam, NBTTagList tagList) {
      for(int i = 0; i < tagList.size(); ++i) {
         this.scoreboard.addPlayerToTeam(tagList.getString(i), playerTeam);
      }

   }

   protected void readDisplayConfig(NBTTagCompound compound) {
      for(int i = 0; i < 19; ++i) {
         if (compound.contains("slot_" + i, 8)) {
            String s = compound.getString("slot_" + i);
            ScoreObjective scoreobjective = this.scoreboard.getObjective(s);
            this.scoreboard.setObjectiveInDisplaySlot(i, scoreobjective);
         }
      }

   }

   protected void readObjectives(NBTTagList nbt) {
      for(int i = 0; i < nbt.size(); ++i) {
         NBTTagCompound nbttagcompound = nbt.getCompound(i);
         ScoreCriteria scorecriteria = ScoreCriteria.byName(nbttagcompound.getString("CriteriaName"));
         if (scorecriteria != null) {
            String s = nbttagcompound.getString("Name");
            if (s.length() > 16) {
               s = s.substring(0, 16);
            }

            ITextComponent itextcomponent = ITextComponent.Serializer.fromJson(nbttagcompound.getString("DisplayName"));
            ScoreCriteria.RenderType scorecriteria$rendertype = ScoreCriteria.RenderType.byId(nbttagcompound.getString("RenderType"));
            this.scoreboard.addObjective(s, scorecriteria, itextcomponent, scorecriteria$rendertype);
         }
      }

   }

   public NBTTagCompound write(NBTTagCompound compound) {
      if (this.scoreboard == null) {
         LOGGER.warn("Tried to save scoreboard without having a scoreboard...");
         return compound;
      } else {
         compound.setTag("Objectives", this.objectivesToNbt());
         compound.setTag("PlayerScores", this.scoreboard.func_197902_i());
         compound.setTag("Teams", this.teamsToNbt());
         this.fillInDisplaySlots(compound);
         return compound;
      }
   }

   protected NBTTagList teamsToNbt() {
      NBTTagList nbttaglist = new NBTTagList();

      for(ScorePlayerTeam scoreplayerteam : this.scoreboard.getTeams()) {
         NBTTagCompound nbttagcompound = new NBTTagCompound();
         nbttagcompound.setString("Name", scoreplayerteam.getName());
         nbttagcompound.setString("DisplayName", ITextComponent.Serializer.toJson(scoreplayerteam.getDisplayName()));
         if (scoreplayerteam.getColor().getColorIndex() >= 0) {
            nbttagcompound.setString("TeamColor", scoreplayerteam.getColor().getFriendlyName());
         }

         nbttagcompound.setBoolean("AllowFriendlyFire", scoreplayerteam.getAllowFriendlyFire());
         nbttagcompound.setBoolean("SeeFriendlyInvisibles", scoreplayerteam.getSeeFriendlyInvisiblesEnabled());
         nbttagcompound.setString("MemberNamePrefix", ITextComponent.Serializer.toJson(scoreplayerteam.getPrefix()));
         nbttagcompound.setString("MemberNameSuffix", ITextComponent.Serializer.toJson(scoreplayerteam.getSuffix()));
         nbttagcompound.setString("NameTagVisibility", scoreplayerteam.getNameTagVisibility().internalName);
         nbttagcompound.setString("DeathMessageVisibility", scoreplayerteam.getDeathMessageVisibility().internalName);
         nbttagcompound.setString("CollisionRule", scoreplayerteam.getCollisionRule().name);
         NBTTagList nbttaglist1 = new NBTTagList();

         for(String s : scoreplayerteam.getMembershipCollection()) {
            nbttaglist1.add((INBTBase)(new NBTTagString(s)));
         }

         nbttagcompound.setTag("Players", nbttaglist1);
         nbttaglist.add((INBTBase)nbttagcompound);
      }

      return nbttaglist;
   }

   protected void fillInDisplaySlots(NBTTagCompound compound) {
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      boolean flag = false;

      for(int i = 0; i < 19; ++i) {
         ScoreObjective scoreobjective = this.scoreboard.getObjectiveInDisplaySlot(i);
         if (scoreobjective != null) {
            nbttagcompound.setString("slot_" + i, scoreobjective.getName());
            flag = true;
         }
      }

      if (flag) {
         compound.setTag("DisplaySlots", nbttagcompound);
      }

   }

   protected NBTTagList objectivesToNbt() {
      NBTTagList nbttaglist = new NBTTagList();

      for(ScoreObjective scoreobjective : this.scoreboard.getScoreObjectives()) {
         if (scoreobjective.getCriteria() != null) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setString("Name", scoreobjective.getName());
            nbttagcompound.setString("CriteriaName", scoreobjective.getCriteria().getName());
            nbttagcompound.setString("DisplayName", ITextComponent.Serializer.toJson(scoreobjective.getDisplayName()));
            nbttagcompound.setString("RenderType", scoreobjective.getRenderType().getId());
            nbttaglist.add((INBTBase)nbttagcompound);
         }
      }

      return nbttaglist;
   }
}
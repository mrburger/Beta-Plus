package net.minecraft.command.arguments;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.command.CommandSource;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCollection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextComponentTranslation;

public class NBTPathArgument implements ArgumentType<NBTPathArgument.NBTPath> {
   private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo.bar", "foo[0]", "[0]", ".");
   private static final DynamicCommandExceptionType field_197153_a = new DynamicCommandExceptionType((p_208665_0_) -> {
      return new TextComponentTranslation("arguments.nbtpath.child.invalid", p_208665_0_);
   });
   private static final DynamicCommandExceptionType field_197154_b = new DynamicCommandExceptionType((p_208666_0_) -> {
      return new TextComponentTranslation("arguments.nbtpath.element.invalid", p_208666_0_);
   });
   private static final SimpleCommandExceptionType field_201948_d = new SimpleCommandExceptionType(new TextComponentTranslation("arguments.nbtpath.node.invalid"));

   public static NBTPathArgument nbtPath() {
      return new NBTPathArgument();
   }

   public static NBTPathArgument.NBTPath getNBTPath(CommandContext<CommandSource> context, String name) {
      return context.getArgument(name, NBTPathArgument.NBTPath.class);
   }

   public NBTPathArgument.NBTPath parse(StringReader p_parse_1_) throws CommandSyntaxException {
      List<NBTPathArgument.INode> list = Lists.newArrayList();
      int i = p_parse_1_.getCursor();

      while(p_parse_1_.canRead() && p_parse_1_.peek() != ' ') {
         switch(p_parse_1_.peek()) {
         case '"':
            list.add(new NBTPathArgument.ChildNode(p_parse_1_.readString()));
            break;
         case '[':
            p_parse_1_.skip();
            list.add(new NBTPathArgument.ElementNode(p_parse_1_.readInt()));
            p_parse_1_.expect(']');
            break;
         default:
            list.add(new NBTPathArgument.ChildNode(this.func_197151_a(p_parse_1_)));
         }

         if (p_parse_1_.canRead()) {
            char c0 = p_parse_1_.peek();
            if (c0 != ' ' && c0 != '[') {
               p_parse_1_.expect('.');
            }
         }
      }

      return new NBTPathArgument.NBTPath(p_parse_1_.getString().substring(i, p_parse_1_.getCursor()), list.toArray(new NBTPathArgument.INode[0]));
   }

   private String func_197151_a(StringReader p_197151_1_) throws CommandSyntaxException {
      int i = p_197151_1_.getCursor();

      while(p_197151_1_.canRead() && func_197146_a(p_197151_1_.peek())) {
         p_197151_1_.skip();
      }

      if (p_197151_1_.getCursor() == i) {
         throw field_201948_d.createWithContext(p_197151_1_);
      } else {
         return p_197151_1_.getString().substring(i, p_197151_1_.getCursor());
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   private static boolean func_197146_a(char p_197146_0_) {
      return p_197146_0_ != ' ' && p_197146_0_ != '"' && p_197146_0_ != '[' && p_197146_0_ != ']' && p_197146_0_ != '.';
   }

   static class ChildNode implements NBTPathArgument.INode {
      private final String field_197139_a;

      public ChildNode(String p_i47788_1_) {
         this.field_197139_a = p_i47788_1_;
      }

      public INBTBase func_197137_a(INBTBase p_197137_1_) throws CommandSyntaxException {
         if (p_197137_1_ instanceof NBTTagCompound) {
            return ((NBTTagCompound)p_197137_1_).getTag(this.field_197139_a);
         } else {
            throw NBTPathArgument.field_197153_a.create(this.field_197139_a);
         }
      }

      public INBTBase func_197135_a(INBTBase p_197135_1_, Supplier<INBTBase> p_197135_2_) throws CommandSyntaxException {
         if (p_197135_1_ instanceof NBTTagCompound) {
            NBTTagCompound nbttagcompound = (NBTTagCompound)p_197135_1_;
            if (nbttagcompound.hasKey(this.field_197139_a)) {
               return nbttagcompound.getTag(this.field_197139_a);
            } else {
               INBTBase inbtbase = p_197135_2_.get();
               nbttagcompound.setTag(this.field_197139_a, inbtbase);
               return inbtbase;
            }
         } else {
            throw NBTPathArgument.field_197153_a.create(this.field_197139_a);
         }
      }

      public INBTBase func_197134_a() {
         return new NBTTagCompound();
      }

      public void func_197136_a(INBTBase p_197136_1_, INBTBase p_197136_2_) throws CommandSyntaxException {
         if (p_197136_1_ instanceof NBTTagCompound) {
            NBTTagCompound nbttagcompound = (NBTTagCompound)p_197136_1_;
            nbttagcompound.setTag(this.field_197139_a, p_197136_2_);
         } else {
            throw NBTPathArgument.field_197153_a.create(this.field_197139_a);
         }
      }

      public void func_197133_b(INBTBase p_197133_1_) throws CommandSyntaxException {
         if (p_197133_1_ instanceof NBTTagCompound) {
            NBTTagCompound nbttagcompound = (NBTTagCompound)p_197133_1_;
            if (nbttagcompound.hasKey(this.field_197139_a)) {
               nbttagcompound.removeTag(this.field_197139_a);
               return;
            }
         }

         throw NBTPathArgument.field_197153_a.create(this.field_197139_a);
      }
   }

   static class ElementNode implements NBTPathArgument.INode {
      private final int field_197138_a;

      public ElementNode(int p_i47787_1_) {
         this.field_197138_a = p_i47787_1_;
      }

      public INBTBase func_197137_a(INBTBase p_197137_1_) throws CommandSyntaxException {
         if (p_197137_1_ instanceof NBTTagCollection) {
            NBTTagCollection<?> nbttagcollection = (NBTTagCollection)p_197137_1_;
            if (nbttagcollection.size() > this.field_197138_a) {
               return nbttagcollection.getTag(this.field_197138_a);
            }
         }

         throw NBTPathArgument.field_197154_b.create(this.field_197138_a);
      }

      public INBTBase func_197135_a(INBTBase p_197135_1_, Supplier<INBTBase> p_197135_2_) throws CommandSyntaxException {
         return this.func_197137_a(p_197135_1_);
      }

      public INBTBase func_197134_a() {
         return new NBTTagList();
      }

      public void func_197136_a(INBTBase p_197136_1_, INBTBase p_197136_2_) throws CommandSyntaxException {
         if (p_197136_1_ instanceof NBTTagCollection) {
            NBTTagCollection<?> nbttagcollection = (NBTTagCollection)p_197136_1_;
            if (nbttagcollection.size() > this.field_197138_a) {
               nbttagcollection.setTag(this.field_197138_a, p_197136_2_);
               return;
            }
         }

         throw NBTPathArgument.field_197154_b.create(this.field_197138_a);
      }

      public void func_197133_b(INBTBase p_197133_1_) throws CommandSyntaxException {
         if (p_197133_1_ instanceof NBTTagCollection) {
            NBTTagCollection<?> nbttagcollection = (NBTTagCollection)p_197133_1_;
            if (nbttagcollection.size() > this.field_197138_a) {
               nbttagcollection.removeTag(this.field_197138_a);
               return;
            }
         }

         throw NBTPathArgument.field_197154_b.create(this.field_197138_a);
      }
   }

   interface INode {
      INBTBase func_197137_a(INBTBase p_197137_1_) throws CommandSyntaxException;

      INBTBase func_197135_a(INBTBase p_197135_1_, Supplier<INBTBase> p_197135_2_) throws CommandSyntaxException;

      INBTBase func_197134_a();

      void func_197136_a(INBTBase p_197136_1_, INBTBase p_197136_2_) throws CommandSyntaxException;

      void func_197133_b(INBTBase p_197133_1_) throws CommandSyntaxException;
   }

   public static class NBTPath {
      private final String field_197144_a;
      private final NBTPathArgument.INode[] field_197145_b;

      public NBTPath(String p_i47786_1_, NBTPathArgument.INode[] p_i47786_2_) {
         this.field_197144_a = p_i47786_1_;
         this.field_197145_b = p_i47786_2_;
      }

      public INBTBase func_197143_a(INBTBase p_197143_1_) throws CommandSyntaxException {
         for(NBTPathArgument.INode nbtpathargument$inode : this.field_197145_b) {
            p_197143_1_ = nbtpathargument$inode.func_197137_a(p_197143_1_);
         }

         return p_197143_1_;
      }

      public INBTBase func_197142_a(INBTBase p_197142_1_, INBTBase p_197142_2_) throws CommandSyntaxException {
         for(int i = 0; i < this.field_197145_b.length; ++i) {
            NBTPathArgument.INode nbtpathargument$inode = this.field_197145_b[i];
            if (i < this.field_197145_b.length - 1) {
               int j = i + 1;
               p_197142_1_ = nbtpathargument$inode.func_197135_a(p_197142_1_, () -> {
                  return this.field_197145_b[j].func_197134_a();
               });
            } else {
               nbtpathargument$inode.func_197136_a(p_197142_1_, p_197142_2_);
            }
         }

         return p_197142_1_;
      }

      public String toString() {
         return this.field_197144_a;
      }

      public void func_197140_b(INBTBase p_197140_1_) throws CommandSyntaxException {
         for(int i = 0; i < this.field_197145_b.length; ++i) {
            NBTPathArgument.INode nbtpathargument$inode = this.field_197145_b[i];
            if (i < this.field_197145_b.length - 1) {
               p_197140_1_ = nbtpathargument$inode.func_197137_a(p_197140_1_);
            } else {
               nbtpathargument$inode.func_197133_b(p_197140_1_);
            }
         }

      }
   }
}